package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
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
    private final PacchettoRepository pacchettoRepository;

    public CarrelloService(ProdottoRepository prodottoRepository,
                           CarrelloRepository carrelloRepository,
                           RigaCarrelloRepository rigaCarrelloRepository,
                           UtenteRepository utenteRepository,
                           PacchettoRepository pacchettoRepository) {
        this.prodottoRepository = prodottoRepository;
        this.carrelloRepository = carrelloRepository;
        this.rigaCarrelloRepository = rigaCarrelloRepository;
        this.utenteRepository = utenteRepository;
        this.pacchettoRepository = pacchettoRepository;
    }

    private Utente getUtenteByEmail(String email) {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));
    }

    private Prodotto getProdottoById(Long prodottoId) {
        return prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));
    }

    @Transactional(readOnly = true)
    public Carrello getCarrelloByEmail(String email) {
        return getUtenteByEmail(email).getCarrello();
    }

    @Transactional
    public Carrello aggiungiAlCarrelloByEmail(String email, Long prodottoId, int quantita) {
        Utente utente = getUtenteByEmail(email);
        Prodotto prodotto = getProdottoById(prodottoId);
        aggiungiAlCarrello(utente.getCarrello(), prodotto, quantita);
        return utente.getCarrello();
    }

    @Transactional
    public Carrello aggiungiPacchettoAlCarrelloByEmail(String email, Long pacchettoId, int quantita) {
        Utente utente = getUtenteByEmail(email);
        Pacchetto pacchetto = pacchettoRepository.findById(pacchettoId)
                .orElseThrow(() -> new RuntimeException("Pacchetto non trovato"));

        if (pacchetto.getStatoConferma() != Conferma.APPROVATO) {
            throw new IllegalStateException("Pacchetto non disponibile (Non approvato).");
        }

        for (PacchettoItem item : pacchetto.getItems()) {
            int totaleRichiesto = item.getQuantita() * quantita;
            if (item.getProdotto().getQuantita() < totaleRichiesto) {
                throw new IllegalStateException("Stock insufficiente per il prodotto '" + item.getProdotto().getNome() +
                        "' contenuto nel pacchetto. Disponibili: " + item.getProdotto().getQuantita());
            }
        }

        aggiungiPacchettoLogica(utente.getCarrello(), pacchetto, quantita);
        return utente.getCarrello();
    }

    private void aggiungiPacchettoLogica(Carrello carrello, Pacchetto pacchetto, int quantita) {

        Optional<RigaCarrello> rigaEsistente = carrello.getContenuti().stream()
                .filter(r -> r.getPacchetto() != null && r.getPacchetto().getId().equals(pacchetto.getId()))
                .findFirst();

        if (rigaEsistente.isPresent()) {
            RigaCarrello riga = rigaEsistente.get();
            riga.setQuantita(riga.getQuantita() + quantita);
            rigaCarrelloRepository.save(riga);
        } else {
            RigaCarrello nuovaRiga = new RigaCarrello();
            nuovaRiga.setPacchetto(pacchetto);
            nuovaRiga.setProdotto(null);
            nuovaRiga.setQuantita(quantita);
            nuovaRiga.setPrezzoUnitarioSnapshot(pacchetto.getPrezzo());
            nuovaRiga.setCarrello(carrello);
            rigaCarrelloRepository.save(nuovaRiga);
            carrello.addRiga(nuovaRiga);
        }
        carrello.ricalcolaTotale();
        carrelloRepository.save(carrello);
    }

    @Transactional
    public Carrello diminuisciDalCarrelloByEmail(String email, Long prodottoId, int quantita) {
        Utente utente = getUtenteByEmail(email);
        Prodotto prodotto = getProdottoById(prodottoId);
        diminuisciQuantita(utente.getCarrello(), prodotto, quantita);
        return utente.getCarrello();
    }

    @Transactional
    public Carrello svuotaCarrelloByEmail(String email) {
        Utente utente = getUtenteByEmail(email);
        svuotaCarrello(utente.getCarrello());
        return utente.getCarrello();
    }

    @Transactional
    public void aggiungiAlCarrello(Carrello carrello, Prodotto prodotto, int quantita) {
        if (prodotto == null || quantita <= 0) return;

        Prodotto prodottoDB = prodottoRepository.findById(prodotto.getId()).orElse(null);
        if (prodottoDB == null || prodottoDB.getStatoConferma() != Conferma.APPROVATO) {
            throw new IllegalStateException("Prodotto non approvato o non trovato.");
        }

        Optional<RigaCarrello> rigaEsistenteOpt = rigaCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);

        if (rigaEsistenteOpt.isPresent()) {
            RigaCarrello riga = rigaEsistenteOpt.get();
            if (prodottoDB.getQuantita() < (riga.getQuantita() + quantita)) {
                throw new IllegalStateException("Stock insufficiente.");
            }
            riga.setQuantita(riga.getQuantita() + quantita);
            rigaCarrelloRepository.save(riga);
        } else {
            if (prodottoDB.getQuantita() < quantita) {
                throw new IllegalStateException("Stock insufficiente.");
            }
            RigaCarrello riga = new RigaCarrello(prodotto, quantita, prodotto.getPrezzo());
            carrello.addRiga(riga);
            rigaCarrelloRepository.save(riga);
        }
        carrello.ricalcolaTotale();
        carrelloRepository.save(carrello);
    }

    @Transactional
    public void diminuisciQuantita(Carrello carrello, Prodotto prodotto, int quantita) {

        Optional<RigaCarrello> rigaOpt = rigaCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (rigaOpt.isPresent()) {
            RigaCarrello riga = rigaOpt.get();
            int nuovaQta = riga.getQuantita() - quantita;
            if (nuovaQta <= 0) {
                carrello.removeRiga(riga);
                rigaCarrelloRepository.delete(riga);
            } else {
                riga.setQuantita(nuovaQta);
                rigaCarrelloRepository.save(riga);
            }
            carrello.ricalcolaTotale();
            carrelloRepository.save(carrello);
        }
    }

    @Transactional
    public void svuotaCarrello(Carrello carrello) {
        if (carrello == null) return;
        List<RigaCarrello> righe = new ArrayList<>(carrello.getContenuti());
        for(RigaCarrello r : righe) {
            carrello.removeRiga(r);
            rigaCarrelloRepository.delete(r);
        }
        carrello.svuota();
        carrelloRepository.save(carrello);
    }
}