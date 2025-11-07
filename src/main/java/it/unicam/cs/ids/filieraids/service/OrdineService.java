package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.CarrelloRepository;
import it.unicam.cs.ids.filieraids.repository.OrdineRepository;
import it.unicam.cs.ids.filieraids.repository.ProdottoRepository;
import it.unicam.cs.ids.filieraids.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrdineService {

    private final OrdineRepository ordineRepository;
    private final ProdottoRepository prodottoRepository;
    private final ProdottoService prodottoService;
    private final UtenteRepository utenteRepository;
    private final CarrelloService carrelloService;

    public OrdineService(OrdineRepository ordineRepository,
                         ProdottoRepository prodottoRepository,
                         ProdottoService prodottoService,
                         UtenteRepository utenteRepository,
                         CarrelloService carrelloService) {
        this.ordineRepository = ordineRepository;
        this.prodottoRepository = prodottoRepository;
        this.prodottoService = prodottoService;
        this.utenteRepository = utenteRepository;
        this.carrelloService = carrelloService;
    }

    @Transactional
    public Ordine creaOrdine(Utente utente, Carrello carrello, Pagamento pagamento, Indirizzo indirizzo){

        if(!utente.getRuoli().contains(Ruolo.ACQUIRENTE)){
            throw new IllegalArgumentException("L'utente non ha il ruolo di ACQUIRENTE e non può acquistare");
        }
        if (carrello.getContenuti().isEmpty()) {
            throw new IllegalStateException("Impossibile creare un ordine con un carrello vuoto.");
        }

        for (RigaCarrello riga : carrello.getContenuti()) {
            Prodotto prodottoDB = prodottoRepository.findById(riga.getProdotto().getId())
                    .orElseThrow(() -> new IllegalStateException("Prodotto non trovato: " + riga.getProdotto().getNome()));

            if (prodottoDB.getQuantita() < riga.getQuantita()) {
                throw new IllegalStateException("Stock non sufficiente per: " +
                        prodottoDB.getNome() + ". Richiesti: " +
                        riga.getQuantita() + ", Disponibili: " +
                        prodottoDB.getQuantita());
            }
        }
        for (RigaCarrello riga : carrello.getContenuti()){
            prodottoService.scalaQuantita(riga.getProdotto(), riga.getQuantita());
        }

        Ordine ordine = new Ordine(null, carrello, pagamento, indirizzo, utente);
        utente.addOrdine(ordine);
        Ordine ordineSalvato = ordineRepository.save(ordine);
        carrelloService.svuotaCarrello(utente.getCarrello());

        System.out.println("Ordine creato : " + ordineSalvato.getId());
        return ordineSalvato;
    }

    @Transactional
    public boolean annullaOrdine(Ordine ordine){
        if(ordine == null) return false;
        Ordine ordineDB = ordineRepository.findById(ordine.getId()).orElse(null);
        if (ordineDB == null) {
            System.out.println("Ordine non trovato per l'annullamento.");
            return false;
        }

        if (ordineDB.getStatoOrdine() == StatoOrdine.SPEDITO || ordineDB.getStatoOrdine() == StatoOrdine.CONSEGNATO) {
            System.out.println("ERRORE: Impossibile annullare un ordine già spedito o consegnato. ID: " + ordineDB.getId());
            return false;
        }
        if (ordineDB.getStatoOrdine() == StatoOrdine.ANNULLATO) {
            System.out.println("Ordine " + ordineDB.getId() + " è già annullato.");
            return true;
        }

        for(RigaCarrello riga : ordineDB.getCarrello().getContenuti()){
            prodottoService.ripristinaQuantita(riga.getProdotto(), riga.getQuantita());
        }

        ordineDB.setStatoOrdine(StatoOrdine.ANNULLATO);
        ordineDB.setEvaso(false);
        Utente utenteDellOrdine = ordineDB.getUtente();
        utenteDellOrdine.removeOrdine(ordineDB);
        utenteRepository.save(utenteDellOrdine);

        ordineRepository.delete(ordineDB); //ora posso eliminare l'ordine

        System.out.println("Ordine annullato e rimosso correttamente con ID: " + ordineDB.getId());
        return true;
    }
    public List<Ordine> getOrdiniByUtente(Utente utente){
        return ordineRepository.findByUtente(utente);
    }
    public Ordine getOrdineById(Long id){
        return ordineRepository.findById(id).orElse(null);
    }
    public List<Ordine> getTuttiGliOrdini() {
        return ordineRepository.findAll();
    }
    public List<Ordine> getOrdiniByStato(StatoOrdine stato) {
        return ordineRepository.findByStatoOrdine(stato);
    }
    @Transactional
    public void aggiornaStatoOrdine(Ordine ordine, StatoOrdine nuovoStato) {
        Ordine ordineDB = ordineRepository.findById(ordine.getId()).orElse(null);
        if (ordineDB == null || nuovoStato == null) return;
        if (nuovoStato == StatoOrdine.ANNULLATO) {
            System.out.println("ERRORE: Usare il metodo annullaOrdine() per annullare.");
            return;
        }
        if (ordineDB.getStatoOrdine() == StatoOrdine.CONSEGNATO || ordineDB.getStatoOrdine() == StatoOrdine.ANNULLATO) {
            System.out.println("INFO: L'ordine " + ordineDB.getId() + " è già concluso e non può cambiare stato.");
            return;
        }
        ordineDB.setStatoOrdine(nuovoStato);
        if (nuovoStato == StatoOrdine.CONSEGNATO) {
            ordineDB.setEvaso(true);
        }
        ordineRepository.save(ordineDB);
        System.out.println("Stato ordine ID " + ordineDB.getId() + " aggiornato a: " + nuovoStato);
    }
}