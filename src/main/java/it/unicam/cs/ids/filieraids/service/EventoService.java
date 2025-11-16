package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.AttoreRepository;
import it.unicam.cs.ids.filieraids.repository.EventoRepository;
import it.unicam.cs.ids.filieraids.repository.PrenotazioneRepository;
import it.unicam.cs.ids.filieraids.repository.VenditoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final AttoreRepository attoreRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final VenditoreRepository venditoreRepository;

    public EventoService(EventoRepository eventoRepository,
                         AttoreRepository attoreRepository,
                         PrenotazioneRepository prenotazioneRepository,
                         VenditoreRepository venditoreRepository) {
        this.eventoRepository = eventoRepository;
        this.attoreRepository = attoreRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.venditoreRepository = venditoreRepository;
    }

    private Attore getAttoreByEmail(String email) {
        return attoreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Attore non trovato con email: " + email));
    }

    private Evento getEventoIfOwner(Long eventoId, String animatoreEmail) {
        Attore animatore = getAttoreByEmail(animatoreEmail);
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + eventoId));

        //controllo che l'animatore loggato sia il proprietario dell'evento
        if (!evento.getAnimatore().equals(animatore)) {
            throw new SecurityException("Accesso negato: puoi gestire solo i tuoi eventi.");
        }
        return evento;
    }

    @Transactional
    public Evento creaEvento(Evento eventoInput, String animatoreEmail) {

        //cerca e verifica il ruolo dell'attore
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
        Attore animatore = getAttoreByEmail(animatoreEmail);
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);

        //elimina automaticamente tutte le prenotazioni associate a questo evento
        prenotazioneRepository.deleteByEvento(evento);

        //evento eliminato
        eventoRepository.delete(evento);

        System.out.println("Evento " + eventoId + " e relative prenotazioni eliminati da " + animatoreEmail);
    }



    //restituisco gli eventi approvati
    @Transactional(readOnly = true)
    public List<Evento> getEventiVisibili() {
        return eventoRepository.findByStatoConferma(Conferma.APPROVATO);
    }

    //restituisce un evento specifico per id solo se è approvato
    @Transactional(readOnly = true)
    public Evento getEventoVisibileById(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + id));

        if (evento.getStatoConferma() != Conferma.APPROVATO) {
            throw new RuntimeException("Evento non trovato con ID: " + id);
        }
        return evento;
    }

    //restituisce tutti gli eventi dell'animatore loggato
    @Transactional(readOnly = true)
    public List<Evento> getMieiEventi(String animatoreEmail) {
        Attore animatore = getAttoreByEmail(animatoreEmail);
        if (!animatore.getRuoli().contains(Ruolo.ANIMATORE)) {
            throw new SecurityException("Azione consentita solo agli Animatori.");
        }
        return eventoRepository.findByAnimatore(animatore);
    }

    //mostra all'animatore chi si è prenotato al suo evento
    @Transactional(readOnly = true)
    public List<Prenotazione> getPrenotazioniPerEvento(Long eventoId, String animatoreEmail) {
        // Verifica che l'animatore sia il proprietario dell'evento
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);
        return prenotazioneRepository.findByEvento(evento);
    }

    //invita venditore ad un evento
    @Transactional
    public void invitaVenditore(Long eventoId, Long venditoreId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);

        Venditore venditore = venditoreRepository.findById(venditoreId)
                .orElseThrow(() -> new RuntimeException("Venditore non trovato con ID: " + venditoreId));

        //controllo se l'evento è stato eliminato
        if (evento.getStatoConferma() == Conferma.RIFIUTATO) {
            throw new IllegalStateException("Impossibile invitare a un evento rifiutato.");
        }

        evento.addInvitato(venditore);
        eventoRepository.save(evento);
    }

    //vedi Venditori invitati
    @Transactional(readOnly = true)
    public Set<Venditore> getInvitatiPerEvento(Long eventoId, String animatoreEmail) {
        Evento evento = getEventoIfOwner(eventoId, animatoreEmail);
        return evento.getVenditoriInvitati();
    }
}