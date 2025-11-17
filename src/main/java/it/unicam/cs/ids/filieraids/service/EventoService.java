package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.AttoreRepository;
import it.unicam.cs.ids.filieraids.repository.EventoRepository;
import it.unicam.cs.ids.filieraids.repository.PrenotazioneRepository;
import it.unicam.cs.ids.filieraids.repository.VenditoreRepository;
import it.unicam.cs.ids.filieraids.repository.InvitoRepository; // Import aggiunto
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final AttoreRepository attoreRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final VenditoreRepository venditoreRepository;
    private final InvitoRepository invitoRepository; // Campo aggiunto

    public EventoService(EventoRepository eventoRepository,
                         AttoreRepository attoreRepository,
                         PrenotazioneRepository prenotazioneRepository,
                         VenditoreRepository venditoreRepository,
                         InvitoRepository invitoRepository) { // Costruttore aggiornato
        this.eventoRepository = eventoRepository;
        this.attoreRepository = attoreRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.venditoreRepository = venditoreRepository;
        this.invitoRepository = invitoRepository;
    }

    private Attore getAttoreByEmail(String email) {
        return attoreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Attore non trovato con email: " + email));
    }

    private Evento getEventoIfOwner(Long eventoId, String animatoreEmail) {
        Attore animatore = getAttoreByEmail(animatoreEmail);
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + eventoId));

        if (!evento.getAnimatore().equals(animatore)) {
            throw new SecurityException("Accesso negato: puoi gestire solo i tuoi eventi.");
        }
        return evento;
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

        // Elimina anche gli inviti associati
        List<Invito> inviti = invitoRepository.findByEvento(evento);
        invitoRepository.deleteAll(inviti);

        eventoRepository.delete(evento);
        System.out.println("Evento " + eventoId + " eliminato da " + animatoreEmail);
    }

    @Transactional(readOnly = true)
    public List<Evento> getEventiVisibili() {
        return eventoRepository.findByStatoConferma(Conferma.APPROVATO);
    }

    @Transactional(readOnly = true)
    public Evento getEventoVisibileById(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + id));

        if (evento.getStatoConferma() != Conferma.APPROVATO) {
            throw new RuntimeException("Evento non trovato con ID: " + id);
        }
        return evento;
    }

    @Transactional(readOnly = true)
    public List<Evento> getMieiEventi(String animatoreEmail) {
        Attore animatore = getAttoreByEmail(animatoreEmail);
        if (!animatore.getRuoli().contains(Ruolo.ANIMATORE)) {
            throw new SecurityException("Azione consentita solo agli Animatori.");
        }
        return eventoRepository.findByAnimatore(animatore);
    }

    @Transactional(readOnly = true)
    public List<Prenotazione> getPrenotazioniPerEvento(Long eventoId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);
        return prenotazioneRepository.findByEvento(evento);
    }

    @Transactional
    public void invitaVenditore(Long eventoId, Long venditoreId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);

        Venditore venditore = venditoreRepository.findById(venditoreId)
                .orElseThrow(() -> new RuntimeException("Venditore non trovato con ID: " + venditoreId));

        if (evento.getStatoConferma() == Conferma.RIFIUTATO) {
            throw new IllegalStateException("Impossibile invitare a un evento rifiutato.");
        }

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
}