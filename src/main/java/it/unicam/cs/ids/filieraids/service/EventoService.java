package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final AttoreRepository attoreRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final VenditoreRepository venditoreRepository;
    private final InvitoRepository invitoRepository;
    private final AutorizzazioneRepository autorizzazioneRepository;


    public EventoService(EventoRepository eventoRepository,
                         AttoreRepository attoreRepository,
                         PrenotazioneRepository prenotazioneRepository,
                         VenditoreRepository venditoreRepository,
                         InvitoRepository invitoRepository,
                         AutorizzazioneRepository autorizzazioneRepository) {
        this.eventoRepository = eventoRepository;
        this.attoreRepository = attoreRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.venditoreRepository = venditoreRepository;
        this.invitoRepository = invitoRepository;
        this.autorizzazioneRepository = autorizzazioneRepository;
    }


    private Attore getAttoreByEmail(String email) {
        return attoreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Attore non trovato con email: " + email));
    }

    private Evento getEventoIfOwner(Long eventoId, String animatoreEmail) {
        Attore animatore = getAttoreByEmail(animatoreEmail);
        Evento evento = getEventoById(eventoId);

        if (!evento.getAnimatore().equals(animatore)) {
            throw new SecurityException("Accesso negato: puoi gestire solo i tuoi eventi.");
        }
        return evento;
    }


    public Evento getEventoById(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Evento> getEventiVisibili() {
        return eventoRepository.findByStatoConferma(Conferma.APPROVATO);
    }

    @Transactional(readOnly = true)
    public List<Evento> getEventiInAttesa() {
        return eventoRepository.findByStatoConferma(Conferma.ATTESA);
    }

    @Transactional(readOnly = true)
    public Evento getEventoVisibileById(Long id) {
        Evento evento = getEventoById(id);

        if (evento.getStatoConferma() != Conferma.APPROVATO) {
            // Meglio un messaggio chiaro che "non trovato" se esiste ma non è approvato
            throw new RuntimeException("Evento non disponibile (non approvato) con ID: " + id);
        }
        return evento;
    }

    @Transactional(readOnly = true)
    public List<Evento> getMieiEventi(String animatoreEmail) {
        Attore animatore = getAttoreByEmail(animatoreEmail);
        // Il controllo del ruolo qui è ridondante se il controller è protetto, ma male non fa
        if (!animatore.getRuoli().contains(Ruolo.ANIMATORE)) {
            throw new SecurityException("Azione consentita solo agli Animatori.");
        }
        return eventoRepository.findByAnimatore(animatore);
    }


    @Transactional
    public Evento creaEvento(Evento eventoInput, String animatoreEmail) {
        Attore animatore = getAttoreByEmail(animatoreEmail);

        if (!animatore.getRuoli().contains(Ruolo.ANIMATORE)) {
            throw new SecurityException("L'utente non ha il ruolo di ANIMATORE");
        }

        Evento evento = new Evento(
                eventoInput.getNome(),
                eventoInput.getDescrizione(),
                animatore,
                eventoInput.getDataEvento(),
                eventoInput.getIndirizzo(),
                eventoInput.getPostiDisponibili()
        );

        return eventoRepository.save(evento);
    }

    @Transactional
    public void eliminaEvento(Long eventoId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);

        prenotazioneRepository.deleteByEvento(evento);
        List<Invito> inviti = invitoRepository.findByEvento(evento);
        invitoRepository.deleteAll(inviti);
        autorizzazioneRepository.deleteByContenutoDaApprovare(evento);

        eventoRepository.delete(evento);
        System.out.println("Evento " + eventoId + " eliminato da " + animatoreEmail);
    }

    //metodi per curatore

    @Transactional
    public void approvaEvento(Long id, String noteCuratore) {
        Evento evento = getEventoById(id);

        // GUARD-CHECK: Si può approvare solo se è in ATTESA
        if (evento.getStatoConferma() != Conferma.ATTESA) {
            throw new IllegalStateException("Impossibile approvare: l'evento " + id +
                    " non è in attesa. Stato attuale: " + evento.getStatoConferma());
        }

        evento.setStatoConferma(Conferma.APPROVATO);
        System.out.println("Evento " + id + " APPROVATO dal curatore. Note: " + noteCuratore);
        eventoRepository.save(evento);
    }

    @Transactional
    public void rifiutaEvento(Long id, String motivoRifiuto) {
        Evento evento = getEventoById(id);

        // GUARD-CHECK: Si può rifiutare solo se è in ATTESA
        if (evento.getStatoConferma() != Conferma.ATTESA) {
            throw new IllegalStateException("Impossibile rifiutare: l'evento " + id +
                    " non è in attesa. Stato attuale: " + evento.getStatoConferma());
        }

        evento.setStatoConferma(Conferma.RIFIUTATO);
        System.out.println("Evento " + id + " RIFIUTATO dal curatore. Motivo: " + motivoRifiuto);
        eventoRepository.save(evento);
    }

    //gestione inviti
    @Transactional
    public void invitaVenditore(Long eventoId, Long venditoreId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);

        // Controllo stato evento PRIMA di cercare il venditore
        if (evento.getStatoConferma() == Conferma.RIFIUTATO) {
            throw new IllegalStateException("Impossibile invitare a un evento rifiutato.");
        }

        Venditore venditore = venditoreRepository.findById(venditoreId)
                .orElseThrow(() -> new RuntimeException("Venditore non trovato con ID: " + venditoreId));

        if (invitoRepository.findByEventoAndVenditore(evento, venditore).isPresent()) {
            throw new IllegalStateException("Il venditore è già stato invitato a questo evento.");
        }

        Invito invito = new Invito(evento, venditore);
        invitoRepository.save(invito);
    }

    @Transactional(readOnly = true)
    public List<Invito> getInvitatiPerEvento(Long eventoId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);
        return invitoRepository.findByEvento(evento);
    }

    //gestione prenotazioni

    @Transactional(readOnly = true)
    public List<Prenotazione> getPrenotazioniPerEvento(Long eventoId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);
        return prenotazioneRepository.findByEvento(evento);
    }
}