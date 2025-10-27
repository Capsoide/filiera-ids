package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class OrdineService {
    private final List<Ordine> ordini = new ArrayList<>();

    private final ProdottoService prodottoService;

    public OrdineService(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    public Ordine creaOrdine(Utente utente, Carrello carrello, Pagamento pagamento, Indirizzo indirizzo){

        if(!utente.getRuoli().contains(Ruolo.ACQUIRENTE)){
            throw new IllegalArgumentException("L'utente non ha il ruolo di ACQUIRENTE e non può acquistare");
        }

        if (carrello.getContenuti().isEmpty()) {
            throw new IllegalStateException("Impossibile creare un ordine con un carrello vuoto.");
        }

        for (RigaCarrello riga : carrello.getContenuti()) {
            if (!prodottoService.verificaDisponibilita(riga.getProdotto(), riga.getQuantita())) {
                //se un prodotto non è disponibile, l'intero ordine fallisce --> DA CAMBIARE NON VA BENE COSI MA L'HO MESSO PER TESTARE
                throw new IllegalStateException("Stock non sufficiente per: " +
                        riga.getProdotto().getNome() + ". Richiesti: " +
                        riga.getQuantita() + ", Disponibili: " +
                        riga.getProdotto().getQuantita());
            }
        }

        for (RigaCarrello riga : carrello.getContenuti()){
            prodottoService.scalaQuantita(riga.getProdotto(), riga.getQuantita());
        }

        Ordine ordine = new Ordine(
                null, //data inserita in modo automatico
                carrello,
                pagamento,
                indirizzo,
                utente
        );

        utente.addOrdine(ordine);
        ordini.add(ordine);
        utente.getCarrello().svuota();

        System.out.println("Ordine creato : " + ordine.getId());
        return ordine;
    }


    //annulla un ordine e ripristina lo stock
    public boolean annullaOrdine(Ordine ordine){
        if(ordine == null) return false;

        //impossibile annullare un ordine già spedito o consegnato
        if (ordine.getStatoOrdine() == StatoOrdine.SPEDITO || ordine.getStatoOrdine() == StatoOrdine.CONSEGNATO) {
            System.out.println("ERRORE: Impossibile annullare un ordine già spedito o consegnato. ID: " + ordine.getId());
            return false;
        }

        //se è già annullato non fa nulla
        if (ordine.getStatoOrdine() == StatoOrdine.ANNULLATO) {
            System.out.println("Ordine " + ordine.getId() + " è già annullato.");
            return true;
        }

        for(RigaCarrello riga : ordine.getCarrello().getContenuti()){
            prodottoService.ripristinaQuantita(riga.getProdotto(), riga.getQuantita());
        }

        ordine.setStatoOrdine(StatoOrdine.ANNULLATO);
        ordine.setEvaso(false);

        ordine.getUtente().removeOrdine(ordine);
        boolean removed = ordini.remove(ordine);

        if(removed){
            System.out.println("Ordine annullato e rimosso correttamente con ID: " + ordine.getId());
        }
        return removed;
    }

    //restituisce la lista di ordini per un utente specifico.
    public List<Ordine> getOrdiniByUtente(Utente utente){
        return utente.getOrdini();
    }

    //trova ordine tramite id
    public Ordine getOrdineById(int id){
        for(Ordine ordine : ordini){
            if(ordine.getId() == id){
                return ordine;
            }
        }
        return null; //non trovato
    }

    //restituisce tutti gli ordini effettuati nel sistema
    public List<Ordine> getTuttiGliOrdini() {
        //gli faccio restiuire una copia per evitare modifiche esterne alla lista del service
        return new ArrayList<>(ordini);
    }


    //restituisce tutti gli ordini con uno stato specifico
    public List<Ordine> getOrdiniByStato(StatoOrdine stato) {
        return ordini.stream()
                .filter(ordine -> ordine.getStatoOrdine() == stato)
                .collect(Collectors.toList());
    }

    //aggiorna lo stato di un ordine
    public void aggiornaStatoOrdine(Ordine ordine, StatoOrdine nuovoStato) {
        if (ordine == null || nuovoStato == null) return;

        if (nuovoStato == StatoOrdine.ANNULLATO) {
            System.out.println("ERRORE: Usare il metodo annullaOrdine() per annullare.");
            return;
        }

        //non si può modificare uno stato CONSEGNATO o ANNULLATO
        if (ordine.getStatoOrdine() == StatoOrdine.CONSEGNATO || ordine.getStatoOrdine() == StatoOrdine.ANNULLATO) {
            System.out.println("INFO: L'ordine " + ordine.getId() + " è già concluso e non può cambiare stato.");
            return;
        }

        ordine.setStatoOrdine(nuovoStato);
        System.out.println("Stato ordine ID " + ordine.getId() + " aggiornato a: " + nuovoStato);

        //se ordine è CONSEGNATO lo conrassegno come evaso
        if (nuovoStato == StatoOrdine.CONSEGNATO) {
            ordine.setEvaso(true);
        }
    }
}