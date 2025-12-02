package it.unicam.cs.ids.filieraids.service;
import it.unicam.cs.ids.filieraids.repository.*;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.AutorizzazioneRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CuratoreService implements ContenutoSubject {

    private final AutorizzazioneRepository autorizzazioneRepository;
    private final ContenutoRepository contenutoRepository;
    private final AttoreRepository attoreRepository;

    private final List<ContenutoObserver> observers = new ArrayList<>();

    public CuratoreService(AutorizzazioneRepository autorizzazioneRepository,
                           ContenutoRepository contenutoRepository,
                           AttoreRepository attoreRepository,
                           List<ContenutoObserver> existingObservers) { //springboot trova automaticamente SocialService e lo mette nella lista
        this.autorizzazioneRepository = autorizzazioneRepository;
        this.contenutoRepository = contenutoRepository;
        this.attoreRepository = attoreRepository;

        //riempio la lista con gli observer trovati
        this.observers.addAll(existingObservers);
    }

    //metodi interfaccia subject
    @Override
    public void addObserver(ContenutoObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(ContenutoObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(Contenuto contenuto) {
        for (ContenutoObserver observer : observers) {
            observer.update(contenuto);
        }
    }

    public List<Contenuto> getContenutiInAttesa() {
        return contenutoRepository.findByStatoConferma(Conferma.ATTESA);
    }

    private Attore getCuratoreByEmail(String email) {
        return attoreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));
    }

    private Contenuto getContenutoById(Long id) {
        return contenutoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenuto non trovato"));
    }

    @Transactional
    public Autorizzazione approvaContenuto(Long contenutoId, String curatoreEmail, String motivo) {
        Attore curatore = getCuratoreByEmail(curatoreEmail);
        Contenuto contenutoDB = getContenutoById(contenutoId);

        if (!curatore.getRuoli().contains(Ruolo.CURATORE)) {
            throw new SecurityException("Solo i CURATORI possono approvare contenuti.");
        }

        contenutoDB.setStatoConferma(Conferma.APPROVATO);
        contenutoRepository.save(contenutoDB);

        //chiamata notifica manuale
        System.out.println("DEBUG CURATORE: Notifico " + observers.size() + " osservatori...");
        notifyObservers(contenutoDB);

        Autorizzazione log = new Autorizzazione(curatore, contenutoDB, motivo, true);
        autorizzazioneRepository.save(log);

        System.out.println("Contenuto ID " + contenutoDB.getId() + " APPROVATO dal curatore.");
        return log;
    }

    @Transactional
    public Autorizzazione rifiutaContenuto(Long contenutoId, String curatoreEmail, String motivo) {
        // Cerca l'attore e il contenuto qui nel service
        Attore curatore = getCuratoreByEmail(curatoreEmail);
        Contenuto contenutoDB = getContenutoById(contenutoId);

        if (!curatore.getRuoli().contains(Ruolo.CURATORE)) {
            throw new SecurityException("Solo i CURATORI possono rifiutare contenuti.");
        }

        contenutoDB.setStatoConferma(Conferma.RIFIUTATO);
        contenutoRepository.save(contenutoDB);

        Autorizzazione log = new Autorizzazione(curatore, contenutoDB, motivo, false);
        autorizzazioneRepository.save(log);

        System.out.println("Contenuto ID " + contenutoDB.getId() + " RIFIUTATO dal curatore: " + curatore.getEmail());
        return log;
    }

    public List<Autorizzazione> getStoricoAutorizzazioni() {
        return autorizzazioneRepository.findAll();
    }
}