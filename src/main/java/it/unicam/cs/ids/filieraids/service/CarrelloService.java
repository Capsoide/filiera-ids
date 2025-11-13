package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.CarrelloRepository;
import it.unicam.cs.ids.filieraids.repository.ProdottoRepository;
import it.unicam.cs.ids.filieraids.repository.RigaCarrelloRepository;
import it.unicam.cs.ids.filieraids.repository.UtenteRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CarrelloService {
    private final ProdottoRepository prodottoRepository;
    private final CarrelloRepository carrelloRepository;
    private final RigaCarrelloRepository rigaCarrelloRepository;
    private final UtenteRepository utenteRepository;

    public CarrelloService(ProdottoRepository prodottoRepository,
                           CarrelloRepository carrelloRepository,
                           RigaCarrelloRepository rigaCarrelloRepository,
                           UtenteRepository utenteRepository) {
        this.prodottoRepository = prodottoRepository;
        this.carrelloRepository = carrelloRepository;
        this.rigaCarrelloRepository = rigaCarrelloRepository;
        this.utenteRepository = utenteRepository;
    }
    //metodo privato per trovare l'utente
    private Utente getUtenteByEmail(String email) {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));
    }
    //metodo privato per trovare il prodotto
    private Prodotto getProdottoById(Long prodottoId) {
        return prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));
    }
    //recupero carrello per l'utente loggato
    @Transactional(readOnly = true)
    public Carrello getCarrelloByEmail(String email) {
        Utente utente = getUtenteByEmail(email);
        return utente.getCarrello();
    }

    //aggiungo un prodotto al carrello per l'utente loggato
    @Transactional
    public Carrello aggiungiAlCarrelloByEmail(String email, Long prodottoId, int quantita) {
        Utente utente = getUtenteByEmail(email);
        Prodotto prodotto = getProdottoById(prodottoId);
        aggiungiAlCarrello(utente.getCarrello(), prodotto, quantita);
        return utente.getCarrello();
    }

    //diminuisce la quantità di un prodotto per l'utente loggato
    @Transactional
    public Carrello diminuisciDalCarrelloByEmail(String email, Long prodottoId, int quantita) {
        Utente utente = getUtenteByEmail(email);
        Prodotto prodotto = getProdottoById(prodottoId);

        diminuisciQuantita(utente.getCarrello(), prodotto, quantita);
        return utente.getCarrello();
    }

    //svuoto il carrello per l'utente loggato
    @Transactional
    public Carrello svuotaCarrelloByEmail(String email) {
        Utente utente = getUtenteByEmail(email);

        svuotaCarrello(utente.getCarrello());
        return utente.getCarrello();
    }


    @Transactional
    public void aggiungiAlCarrello(Carrello carrello, Prodotto prodotto, int quantita) {

        if (prodotto == null || quantita <= 0) {
            System.out.println("Errore: Dati prodotto non validi.");
            return;
        }
        Prodotto prodottoDB = prodottoRepository.findById(prodotto.getId()).orElse(null);
        if (prodottoDB == null || prodottoDB.getStatoConferma() != Conferma.APPROVATO) {
            throw new IllegalStateException("Impossibile aggiungere: Prodotto \"" + (prodottoDB != null ? prodottoDB.getNome() : "N/D") + "\" non approvato o non trovato.");
        }

        Optional<RigaCarrello> rigaEsistenteOpt = rigaCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);

        if (rigaEsistenteOpt.isPresent()) {
            RigaCarrello rigaDaAggiornare = rigaEsistenteOpt.get();
            int quantitaTotaleRichiesta = rigaDaAggiornare.getQuantita() + quantita;

            if (prodottoDB.getQuantita() < quantitaTotaleRichiesta) {
                throw new IllegalStateException("Impossibile aggiornare " + quantita + " unità: Stock non sufficiente per " + prodotto.getNome() + " (Disponibili: " + prodottoDB.getQuantita() + ")");
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
            throw new RuntimeException("Prodotto " + prodotto.getNome() + " non trovato nel carrello.");
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
            throw new RuntimeException("Prodotto " + prodotto.getNome() + " non trovato nel carrello.");
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