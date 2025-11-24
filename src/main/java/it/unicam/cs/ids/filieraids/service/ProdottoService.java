package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final VenditoreRepository venditoreRepository;
    private final RigaCarrelloRepository rigaCarrelloRepository;
    private final CarrelloRepository carrelloRepository;
    private final AutorizzazioneRepository autorizzazioneRepository;

    public ProdottoService(ProdottoRepository prodottoRepository,
                           VenditoreRepository venditoreRepository,
                           RigaCarrelloRepository rigaCarrelloRepository,
                           CarrelloRepository carrelloRepository,
                           AutorizzazioneRepository autorizzazioneRepository) {
        this.prodottoRepository = prodottoRepository;
        this.venditoreRepository = venditoreRepository;
        this.rigaCarrelloRepository = rigaCarrelloRepository;
        this.carrelloRepository = carrelloRepository;
        this.autorizzazioneRepository = autorizzazioneRepository;
    }

    private Venditore getVenditoreByEmail(String email) {
        return venditoreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Venditore non trovato: " + email));
    }

    private Prodotto getProdottoIfOwner(Long id, String venditoreEmail) {
        Venditore venditore = getVenditoreByEmail(venditoreEmail);
        Prodotto prodotto = getProdottoById(id); // Usa il metodo pubblico sicuro

        if (!prodotto.getVenditore().equals(venditore)) {
            throw new SecurityException("Accesso negato: puoi gestire solo i tuoi prodotti.");
        }
        return prodotto;
    }

    public Prodotto getProdottoById(Long id) {
        return prodottoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiPerVenditoreEmail(String venditoreEmail) {
        Venditore venditore = getVenditoreByEmail(venditoreEmail);
        return prodottoRepository.findByVenditore(venditore);
    }

    //metodo pubblico per gli acquirenti
    public List<Prodotto> getProdottiVisibili(){
        return prodottoRepository.findByStatoConferma(Conferma.APPROVATO);
    }

    //metodo per i curatori
    public List<Prodotto> getProdottiInAttesa(){
        return prodottoRepository.findByStatoConferma(Conferma.ATTESA);
    }

    public boolean verificaDisponibilita(Prodotto prodotto, int quantita){
        Prodotto p = prodottoRepository.findById(prodotto.getId()).orElse(null);
        if (p == null) return false;
        return p.getQuantita() >= quantita;
    }

    @Transactional
    public Prodotto creaProdottoPerVenditore(Prodotto prodottoInput, String venditoreEmail) {
        Venditore venditore = getVenditoreByEmail(venditoreEmail);

        prodottoInput.setDataCaricamento(new Date());
        if (prodottoInput.getDataProduzione() == null) {
            prodottoInput.setDataProduzione(new Date());
        }
        prodottoInput.setVenditore(venditore);

        venditore.addProdotto(prodottoInput);
        Prodotto prodottoSalvato = prodottoRepository.save(prodottoInput);
        System.out.println("Prodotto creato: " + prodottoSalvato.getNome() + " (ID: " + prodottoSalvato.getId() + ")");
        return prodottoSalvato;
    }

    @Transactional
    public Prodotto creaProdotto(
            Date datacaricamento, String descrizione, String nome,
            String metodoDiColtivazione, double prezzo, Venditore produttore,
            List<String> certificazioni, Date dataProduzione, int quantita){

        Prodotto p = new Prodotto(datacaricamento, descrizione, nome,
                metodoDiColtivazione, prezzo, produttore, certificazioni,
                dataProduzione, quantita);

        produttore.addProdotto(p);

        Prodotto prodottoSalvato = prodottoRepository.save(p);

        System.out.println("Prodotto creato (metodo legacy): " + prodottoSalvato.getNome() + " (ID: " + prodottoSalvato.getId() + ")");
        return prodottoSalvato;
    }


    //modifica prodotto
    @Transactional
    public Prodotto modificaProdotto(Long id, Prodotto datiAggiornati, String venditoreEmail) {
        Prodotto prodotto = getProdottoIfOwner(id, venditoreEmail);
        if (datiAggiornati.getQuantita() < 0) {
            throw new IllegalArgumentException("Errore: La quantità non può essere negativa.");
        }

        prodotto.setNome(datiAggiornati.getNome());
        prodotto.setDescrizione(datiAggiornati.getDescrizione());
        prodotto.setQuantita(datiAggiornati.getQuantita());
        prodotto.setMetodoDiColtivazione(datiAggiornati.getMetodoDiColtivazione());

        if (prodotto.getPrezzo() != datiAggiornati.getPrezzo()) {
            double vecchioPrezzo = prodotto.getPrezzo();
            prodotto.setPrezzo(datiAggiornati.getPrezzo());

            List<RigaCarrello> righeCoinvolte = rigaCarrelloRepository.findByProdotto(prodotto);
            for (RigaCarrello riga : righeCoinvolte) {
                riga.setPrezzoUnitarioSnapshot(datiAggiornati.getPrezzo());
                Carrello c = riga.getCarrello();
                c.ricalcolaTotale();
                carrelloRepository.save(c);
            }
            System.out.println("Prezzo aggiornato da " + vecchioPrezzo + " a " + datiAggiornati.getPrezzo() + ". Aggiornati " + righeCoinvolte.size() + " carrelli.");
        }

        return prodottoRepository.save(prodotto);
    }

    //elimina prodotto
    @Transactional
    public void eliminaProdotto(Long id, String venditoreEmail) {
        Prodotto prodotto = getProdottoIfOwner(id, venditoreEmail);

        if (prodotto.getQuantita() > 0) {
            throw new IllegalStateException("Impossibile eliminare: ci sono ancora " + prodotto.getQuantita() + " unità in magazzino.");
        }

        List<RigaCarrello> righe = rigaCarrelloRepository.findByProdotto(prodotto);
        for (RigaCarrello riga : righe) {
            Carrello c = riga.getCarrello();
            c.removeRiga(riga);
            carrelloRepository.save(c);
        }
        rigaCarrelloRepository.deleteAll(righe);

        autorizzazioneRepository.deleteByContenutoDaApprovare(prodotto);
        prodottoRepository.delete(prodotto);
        System.out.println("Prodotto " + id + " eliminato definitivamente.");
    }

    //gestione stock
    @Transactional
    public void scalaQuantita(Prodotto prodotto, int quantita){
        Prodotto p = getProdottoById(prodotto.getId());
        p.setQuantita(p.getQuantita() - quantita);
        prodottoRepository.save(p);
        System.out.println("Scalato stock: " + p.getNome() + " -> Rimasti: " + p.getQuantita());
    }

    @Transactional
    public void ripristinaQuantita(Prodotto prodotto, int quantita) {
        Prodotto p = getProdottoById(prodotto.getId());
        p.setQuantita(p.getQuantita() + quantita);
        prodottoRepository.save(p);
        System.out.println("Ripristinato stock: " + p.getNome() + " -> Rimasti: " + p.getQuantita());
    }


    @Transactional
    public void approvaProdotto(Long id, String noteCuratore) {
        Prodotto prodotto = getProdottoById(id);

        //si può approvare solo se è in ATTESA
        if (prodotto.getStatoConferma() != Conferma.ATTESA) {
            throw new IllegalStateException("Impossibile approvare: il prodotto " + id +
                    " non è in attesa. Stato attuale: " + prodotto.getStatoConferma());
        }

        prodotto.setStatoConferma(Conferma.APPROVATO);
        System.out.println("Prodotto " + id + " APPROVATO dal curatore. Note: " + noteCuratore);
        prodottoRepository.save(prodotto);
    }

    @Transactional
    public void rifiutaProdotto(Long id, String motivoRifiuto) {
        Prodotto prodotto = getProdottoById(id);

        //si può rifiutare solo se è in ATTESA
        if (prodotto.getStatoConferma() != Conferma.ATTESA) {
            throw new IllegalStateException("Impossibile rifiutare: il prodotto " + id +
                    " non è in attesa. Stato attuale: " + prodotto.getStatoConferma());
        }

        prodotto.setStatoConferma(Conferma.RIFIUTATO);
        System.out.println("Prodotto " + id + " RIFIUTATO dal curatore. Motivo: " + motivoRifiuto);
        prodottoRepository.save(prodotto);
    }
}