package it.unicam.cs.ids.filieraids.service;
import it.unicam.cs.ids.filieraids.repository.*;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.AutorizzazioneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuratoreService {

    private final AutorizzazioneRepository autorizzazioneRepository;
    private final ContenutoRepository contenutoRepository;

    public CuratoreService(AutorizzazioneRepository autorizzazioneRepository, ContenutoRepository contenutoRepository) {
        this.autorizzazioneRepository = autorizzazioneRepository;
        this.contenutoRepository = contenutoRepository;
    }

    @Transactional
    public Autorizzazione approvaContenuto(Attore curatore, Contenuto contenuto, String motivo) {
        if (!curatore.getRuoli().contains(Ruolo.CURATORE)) {
            throw new SecurityException("Solo i CURATORI possono approvare contenuti.");
        }

        Contenuto contenutoDB = contenutoRepository.findById(contenuto.getId()).orElse(null);
        if (contenutoDB == null) throw new RuntimeException("Contenuto non trovato");

        contenutoDB.setStatoConferma(Conferma.APPROVATO);
        contenutoRepository.save(contenutoDB);
        Autorizzazione log = new Autorizzazione(curatore, contenutoDB, motivo, true);
        autorizzazioneRepository.save(log);
        System.out.println("Contenuto ID " + contenutoDB.getId() + " APPROVATO dal curatore: " + curatore.getEmail());
        return log;
    }

    @Transactional
    public Autorizzazione rifiutaContenuto(Attore curatore, Contenuto contenuto, String motivo) {
        if (!curatore.getRuoli().contains(Ruolo.CURATORE)) {
            throw new SecurityException("Solo i CURATORI possono rifiutare contenuti.");
        }

        Contenuto contenutoDB = contenutoRepository.findById(contenuto.getId()).orElse(null);
        if (contenutoDB == null) throw new RuntimeException("Contenuto non trovato");

        contenutoDB.setStatoConferma(Conferma.RIFIUTATO);
        contenutoRepository.save(contenutoDB); //salvataggio nuovo stato aggiornato
        Autorizzazione log = new Autorizzazione(curatore, contenutoDB, motivo, false);
        autorizzazioneRepository.save(log);
        System.out.println("Contenuto ID " + contenutoDB.getId() + " RIFIUTATO dal curatore: " + curatore.getEmail());
        return log;
    }

    public List<Autorizzazione> getStoricoAutorizzazioni() {
        return autorizzazioneRepository.findAll();
    }
}