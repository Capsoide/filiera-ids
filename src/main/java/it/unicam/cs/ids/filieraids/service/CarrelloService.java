package it.unicam.cs.ids.filieraids.service;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.CarrelloRepository;
import it.unicam.cs.ids.filieraids.repository.ProdottoRepository;
import it.unicam.cs.ids.filieraids.repository.RigaCarrelloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CarrelloService {
    private final ProdottoRepository prodottoRepository;
    private final CarrelloRepository carrelloRepository;
    private final RigaCarrelloRepository rigaCarrelloRepository;

    public CarrelloService(ProdottoRepository prodottoRepository,
                           CarrelloRepository carrelloRepository,
                           RigaCarrelloRepository rigaCarrelloRepository) {
        this.prodottoRepository = prodottoRepository;
        this.carrelloRepository = carrelloRepository;
        this.rigaCarrelloRepository = rigaCarrelloRepository;
    }

    @Transactional
    public void aggiungiAlCarrello(Carrello carrello, Prodotto prodotto, int quantita) {

        if (prodotto == null || quantita <= 0) {
            System.out.println("Errore: Dati prodotto non validi.");
            return;
        }
        Prodotto prodottoDB = prodottoRepository.findById(prodotto.getId()).orElse(null);
        if (prodottoDB == null || prodottoDB.getStatoConferma() != Conferma.APPROVATO) {
            System.out.println("Impossibile aggiungere: Prodotto \"" + (prodottoDB != null ? prodottoDB.getNome() : "N/D") + "\" non approvato o non trovato.");
            return;
        }

        Optional<RigaCarrello> rigaEsistenteOpt = rigaCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);

        if (rigaEsistenteOpt.isPresent()) {
            RigaCarrello rigaDaAggiornare = rigaEsistenteOpt.get();
            int quantitaTotaleRichiesta = rigaDaAggiornare.getQuantita() + quantita;

            if (prodottoDB.getQuantita() < quantitaTotaleRichiesta) {
                System.out.println("Impossibile aggiornare " + quantita + " unità: Stock non sufficiente per " + prodotto.getNome() + " (Disponibili: " + prodottoDB.getQuantita() + ")");
                return;
            }

            rigaDaAggiornare.setQuantita(quantitaTotaleRichiesta);
            rigaCarrelloRepository.save(rigaDaAggiornare);
            System.out.println("Aggiornata quantità per " + prodotto.getNome() + ". Nuova qta: " + quantitaTotaleRichiesta);
        } else {
            if (prodottoDB.getQuantita() < quantita) {
                System.out.println("Impossibile aggiungere " + quantita + " unità: Stock non sufficiente per " + prodotto.getNome() + " (Disponibili: " + prodottoDB.getQuantita() + ")");
                return;
            }
            RigaCarrello rigaDaSalvare = new RigaCarrello(prodotto, quantita, prodotto.getPrezzo());
            carrello.addRiga(rigaDaSalvare);
            rigaCarrelloRepository.save(rigaDaSalvare);
            System.out.println("Aggiunto al carrello: " + prodotto.getNome() + " (Qta: " + quantita + ")");
        }
        carrello.ricalcolaTotale();
        carrelloRepository.save(carrello);
    }

    @Transactional
    public void diminuisciQuantita(Carrello carrello, Prodotto prodotto, int quantitaDaRimuovere) {
        if (carrello == null || prodotto == null || quantitaDaRimuovere <= 0) return;
        Optional<RigaCarrello> rigaOpt = rigaCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);

        if (rigaOpt.isPresent()) {
            RigaCarrello rigaDaModificare = rigaOpt.get();
            int nuovaQuantita = rigaDaModificare.getQuantita() - quantitaDaRimuovere;

            if (nuovaQuantita <= 0) {
                carrello.removeRiga(rigaDaModificare);
                rigaCarrelloRepository.delete(rigaDaModificare);
                System.out.println("Rimosso prodotto dal carrello: " + prodotto.getNome());
            } else {
                rigaDaModificare.setQuantita(nuovaQuantita);
                rigaCarrelloRepository.save(rigaDaModificare);
                System.out.println("Diminuita quantità per " + prodotto.getNome() + ". Nuova qta: " + nuovaQuantita);
            }
            carrello.ricalcolaTotale();
            carrelloRepository.save(carrello);
        } else {
            System.out.println("Prodotto " + prodotto.getNome() + " non trovato nel carrello.");
        }
    }

    @Transactional
    public void rimuoviRigaDalCarrello(Carrello carrello, Prodotto prodotto) {
        if (carrello == null || prodotto == null) return;
        Optional<RigaCarrello> rigaOpt = rigaCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);

        if (rigaOpt.isPresent()) {
            RigaCarrello rigaDaRimuovere = rigaOpt.get();
            carrello.removeRiga(rigaDaRimuovere);
            rigaCarrelloRepository.delete(rigaDaRimuovere);
            carrello.ricalcolaTotale();
            carrelloRepository.save(carrello);
            System.out.println("Rimosso prodotto (tutta la riga) dal carrello: " + prodotto.getNome());
        } else {
            System.out.println("Prodotto " + prodotto.getNome() + " non trovato nel carrello.");
        }
    }

    @Transactional
    public void svuotaCarrello(Carrello carrello) {
        if (carrello == null) return;

        List<RigaCarrello> righeDaRimuovere = new ArrayList<>(carrello.getContenuti());
        for(RigaCarrello riga : righeDaRimuovere) {
            carrello.removeRiga(riga);
            rigaCarrelloRepository.delete(riga);
        }
        carrello.svuota(); //svuoto lista e azzero totale
        carrelloRepository.save(carrello);
        System.out.println("Carrello svuotato.");
    }
}