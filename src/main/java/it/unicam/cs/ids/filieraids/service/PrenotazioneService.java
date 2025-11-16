package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.EventoRepository;
import it.unicam.cs.ids.filieraids.repository.PrenotazioneRepository;
import it.unicam.cs.ids.filieraids.repository.UtenteRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PrenotazioneService {

    private final EventoRepository eventoRepository;
    private final UtenteRepository utenteRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository,
                               EventoRepository eventoRepository,
                               UtenteRepository utenteRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.eventoRepository = eventoRepository;
        this.utenteRepository = utenteRepository;
    }

    private Utente getUtenteByEmail(String email) {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));
    }

    @Transactional
    public Prenotazione creaPrenotazione(Long eventoId, int numeroPosti, String utenteEmail) {
        Utente utente = getUtenteByEmail(utenteEmail);
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        //controllo se l'evento è approvato
        if (evento.getStatoConferma() != Conferma.APPROVATO) {
            throw new IllegalStateException("Impossibile prenotare: l'evento non è ancora stato approvato.");
        }

        //controllo i posti disponibili
        if (evento.getPostiDisponibili() < numeroPosti) {
            throw new IllegalStateException("Impossibile prenotare: posti non sufficienti. Disponibili: " + evento.getPostiDisponibili());
        }

        //scalo i posti e salvo l'evento
        evento.setPostiDisponibili(evento.getPostiDisponibili() - numeroPosti);
        eventoRepository.save(evento);

        //creo e salvo la prenotazione
        Prenotazione prenotazione = new Prenotazione(utente, evento, numeroPosti);
        return prenotazioneRepository.save(prenotazione);
    }

    //viusalizza prenotazioni per l'utente loggato
    @Transactional(readOnly = true)
    public List<Prenotazione> getMiePrenotazioni(String utenteEmail) {
        Utente utente = getUtenteByEmail(utenteEmail);
        return prenotazioneRepository.findByUtente(utente);
    }

    @Transactional
    public void annullaPrenotazione(Long prenotazioneId, String utenteEmail) {

        //trova utente e la prenotazione
        Utente utente = getUtenteByEmail(utenteEmail);
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata con ID: " + prenotazioneId));

        //controllo che l'utente sia il proprietario della prenotazione
        if (!prenotazione.getUtente().equals(utente)) {
            throw new SecurityException("Accesso negato: puoi annullare solo le tue prenotazioni.");
        }

        //trova evento associato
        Evento evento = prenotazione.getEvento();

        //ripristina i posti disponibili sull'evento
        evento.setPostiDisponibili(evento.getPostiDisponibili() + prenotazione.getNumeroPostiPrenotati());
        eventoRepository.save(evento);

        //elimina prenotazione
        prenotazioneRepository.delete(prenotazione);

        System.out.println("Prenotazione " + prenotazioneId + " annullata. Posti ripristinati per evento " + evento.getId());
    }


}
