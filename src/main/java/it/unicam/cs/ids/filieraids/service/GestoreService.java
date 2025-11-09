package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Conferma;
import it.unicam.cs.ids.filieraids.model.RichiestaRuolo;
import it.unicam.cs.ids.filieraids.repository.AttoreRepository;
import it.unicam.cs.ids.filieraids.repository.RichiestaRuoloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GestoreService {

    private final RichiestaRuoloRepository richiestaRuoloRepository;
    private final AttoreRepository attoreRepository;


    public GestoreService(RichiestaRuoloRepository richiestaRuoloRepository, AttoreRepository attoreRepository) {
        this.richiestaRuoloRepository = richiestaRuoloRepository;
        this.attoreRepository = attoreRepository;
    }

    //Recupera tutte le RichiesteRuolo che si trovano in ATTESA
    public List<RichiestaRuolo> getRichiesteInAttesa() {
        return richiestaRuoloRepository.findByStato(Conferma.ATTESA);
    }

    @Transactional
    public void rifiutaRichiesta(Long id) {
        RichiestaRuolo richiesta = richiestaRuoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Richiesta non trovata con ID: " + id));

        if(richiesta.getStato() != Conferma.ATTESA) {
            throw new IllegalStateException("Richiesta con Id " + richiesta.getId() + " già processata");
        }

        richiesta.setStato(Conferma.RIFIUTATO);
        richiestaRuoloRepository.save(richiesta);
    }

    @Transactional
    public void approvaRichiesta(Long id) {
        RichiestaRuolo richiesta = richiestaRuoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Richiesta non trovata con ID: " + id));

        if(richiesta.getStato() != Conferma.ATTESA) {
            throw new IllegalStateException("Richiesta con Id " + richiesta.getId() + " già processata");
        }

        //Trova l'attore che ha eseguito la richiesta
        Attore attore = richiesta.getAttoreRichiedente();
        //Setta ruoli dell'attore richiedente
        attore.setRuoli(richiesta.getRuoliRichiesti());
        //Abilita l'account dell'attore richiedente
        attore.setEnabled(true);
        attoreRepository.save(attore);
        richiesta.setStato(Conferma.APPROVATO);
        richiestaRuoloRepository.save(richiesta);
    }


}
