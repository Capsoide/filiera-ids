package it.unicam.cs.ids.filieraids.service;
import java.util.*;
import it.unicam.cs.ids.filieraids.model.*;

public class CarrelloService {

    // Dipendenza iniettata (per controllare lo stock)
    private final ProdottoService prodottoService;

    // Costruttore per la Dependency Injection
    public CarrelloService(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    //aggiunge un prodotto al carrello di un utente,
    public void aggiungiAlCarrello(Carrello carrello, Prodotto prodotto, int quantita) {

        // 1. Validazione (che era in RigaCarrello)
        if (prodotto == null) {
            System.out.println("Errore: Prodotto nullo.");
            return;
        }
        if (quantita <= 0) {
            System.out.println("Errore: Quantità non valida.");
            return;
        }

        // 2. Logica di business (che era in Carrello)
        if (prodotto.getStatoConferma() != Conferma.APPROVATO) {
            System.out.println("Impossibile aggiungere: Prodotto \"" + prodotto.getNome() + "\" non approvato.");
            return;
        }

        //controlli generici
        int quantitaEsistente = 0;
        for (RigaCarrello riga : carrello.getContenuti()) {
            if (riga.getProdotto().equals(prodotto)) {
                quantitaEsistente = riga.getQuantita();
                break;
            }
        }
        int quantitaTotaleRichiesta = quantitaEsistente + quantita;

        if (!prodottoService.verificaDisponibilita(prodotto, quantitaTotaleRichiesta)) {
            System.out.println("Impossibile aggiungere " + quantita + " unità: Stock non sufficiente per " + prodotto.getNome() + " (Disponibili: " + prodotto.getQuantita() + ")");
            return;
        }

        if (quantitaEsistente > 0) {
            for (RigaCarrello riga : carrello.getContenuti()) {
                if (riga.getProdotto().equals(prodotto)) {
                    riga.setQuantita(quantitaTotaleRichiesta);
                    carrello.ricalcolaTotale();
                    System.out.println("Aggiornata quantità per " + prodotto.getNome() + ". Nuova qta: " + quantitaTotaleRichiesta);
                    return;
                }
            }
        }

        double prezzoUnitario = prodotto.getPrezzo();
        RigaCarrello nuovaRiga = new RigaCarrello(prodotto, quantita, prezzoUnitario);

        carrello.getContenuti().add(nuovaRiga);
        carrello.ricalcolaTotale();
        System.out.println("Aggiunto al carrello: " + prodotto.getNome() + " (Qta: " + quantita + ")");
    }

    public void diminuisciQuantita(Carrello carrello, Prodotto prodotto, int quantitaDaRimuovere) {
        if (carrello == null || prodotto == null || quantitaDaRimuovere <= 0) return;

        RigaCarrello rigaDaModificare = null;
        for (RigaCarrello riga : carrello.getContenuti()) {
            if (riga.getProdotto().equals(prodotto)) {
                rigaDaModificare = riga;
                break;
            }
        }

        if (rigaDaModificare != null) {
            int nuovaQuantita = rigaDaModificare.getQuantita() - quantitaDaRimuovere;

            if (nuovaQuantita <= 0) {
                carrello.getContenuti().remove(rigaDaModificare);
                System.out.println("Rimosso prodotto dal carrello: " + prodotto.getNome());
            } else {
                rigaDaModificare.setQuantita(nuovaQuantita);
                System.out.println("Diminuita quantità per " + prodotto.getNome() + ". Nuova qta: " + nuovaQuantita);
            }
            carrello.ricalcolaTotale();
        } else {
            System.out.println("Prodotto " + prodotto.getNome() + " non trovato nel carrello.");
        }
    }

    public void rimuoviRigaDalCarrello(Carrello carrello, Prodotto prodotto) {
        if (carrello == null || prodotto == null) return;

        boolean rimosso = carrello.getContenuti().removeIf(riga -> riga.getProdotto().equals(prodotto));

        if (rimosso) {
            carrello.ricalcolaTotale();
            System.out.println("Rimosso prodotto (tutta la riga) dal carrello: " + prodotto.getNome());
        } else {
            System.out.println("Prodotto " + prodotto.getNome() + " non trovato nel carrello.");
        }
    }

    //svuota completamente il carrello.

    public void svuotaCarrello(Carrello carrello) {
        if (carrello == null) return;

        carrello.svuota();

        System.out.println("Carrello svuotato.");
    }
}
