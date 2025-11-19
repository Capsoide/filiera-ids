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
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato con ID: " + id));

        if (!prodotto.getVenditore().equals(venditore)) {
            throw new SecurityException("Accesso negato: puoi gestire solo i tuoi prodotti.");
        }
        return prodotto;
    }

    //modifica prodotto
    @Transactional
    public Prodotto modificaProdotto(Long id, Prodotto datiAggiornati, String venditoreEmail) {
        Prodotto prodotto = getProdottoIfOwner(id, venditoreEmail);
        //la quantita non aggiornata non puo essere negativa
        if (datiAggiornati.getQuantita() < 0) {
            throw new IllegalArgumentException("Errore: La quantità non può essere negativa.");
        }

        //aggiorno i dati nome, descrizione, quantita e metodo di coltivazione (campi base)
        prodotto.setNome(datiAggiornati.getNome());
        prodotto.setDescrizione(datiAggiornati.getDescrizione());
        prodotto.setQuantita(datiAggiornati.getQuantita());
        prodotto.setMetodoDiColtivazione(datiAggiornati.getMetodoDiColtivazione());

        //gestione cambio prezzo
        if (prodotto.getPrezzo() != datiAggiornati.getPrezzo()) {
            double vecchioPrezzo = prodotto.getPrezzo();
            prodotto.setPrezzo(datiAggiornati.getPrezzo());

            //aggiornamento carrelli: trova chi ha quel prodotto nel carrello e aggiorna il prezzo
            List<RigaCarrello> righeCoinvolte = rigaCarrelloRepository.findByProdotto(prodotto);
            for (RigaCarrello riga : righeCoinvolte) {
                riga.setPrezzoUnitarioSnapshot(datiAggiornati.getPrezzo());
                //ricalcolo il totale del carrello di quell'utente
                Carrello c = riga.getCarrello();
                c.ricalcolaTotale();
                carrelloRepository.save(c);
            }
            System.out.println("Prezzo aggiornato da " + vecchioPrezzo + " a " + datiAggiornati.getPrezzo() + ". Aggiornati " + righeCoinvolte.size() + " carrelli.");
        }

        //il prodotto rimane approvato, non richiede una nuova revisione e approvazione da parte del curatore
        if (prodotto.getStatoConferma() == Conferma.APPROVATO) {
            prodotto.setStatoConferma(Conferma.APPROVATO);
            System.out.println("Prodotto modificato. Stato prodotto: APPROVATO.");
        }

        return prodottoRepository.save(prodotto);
    }

    //elimina prodotto
    @Transactional
    public void eliminaProdotto(Long id, String venditoreEmail) {
        Prodotto prodotto = getProdottoIfOwner(id, venditoreEmail);

        //la quantità del prodotto da eliminare deve essere 0
        if (prodotto.getQuantita() > 0) {
            throw new IllegalStateException("Impossibile eliminare: ci sono ancora " + prodotto.getQuantita() + " unità in magazzino. Porta la quantità a 0 prima di eliminare.");
        }

        //rimuove il prodotto eliminato dai carrelli attivi
        List<RigaCarrello> righe = rigaCarrelloRepository.findByProdotto(prodotto);
        for (RigaCarrello riga : righe) {
            Carrello c = riga.getCarrello();
            c.removeRiga(riga); //rimuove dalla lista e ricalcola totale
            //non serve cancellare la riga manualmente, orphanRemoval=true su Carrello ci penserà (o salvando il carrello)
            carrelloRepository.save(c);
        }
        //cancella le righe vuote se necessario
        rigaCarrelloRepository.deleteAll(righe);

        //cancella approvazione prodotto
        autorizzazioneRepository.deleteByContenutoDaApprovare(prodotto);

        //cancellazione finale
        prodottoRepository.delete(prodotto);
        System.out.println("Prodotto " + id + " eliminato definitivamente.");
    }

    /**
     * crea un nuovo prodotto per il venditore loggato
     * accetta i dati base del prodotto e l'email del venditore
     */
    @Transactional
    public Prodotto creaProdottoPerVenditore(Prodotto prodottoInput, String venditoreEmail) {

        Venditore venditore = getVenditoreByEmail(venditoreEmail);

        return creaProdotto(
                new Date(),
                prodottoInput.getDescrizione(),
                prodottoInput.getNome(),
                prodottoInput.getMetodoDiColtivazione(),
                prodottoInput.getPrezzo(),
                venditore,
                prodottoInput.getCertificazioni(),
                new Date(),
                prodottoInput.getQuantita()
        );
    }


    //recupera tutti i prodotti (inclusi quelli in attesa) per uno specifico venditore, identificato dalla sua email.
    @Transactional(readOnly = true) //in sola lettura
    public List<Prodotto> getProdottiPerVenditoreEmail(String venditoreEmail) {
        Venditore venditore = getVenditoreByEmail(venditoreEmail);
        return prodottoRepository.findByVenditore(venditore);
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

        System.out.println("Prodotto creato: " + prodottoSalvato.getNome() + " (ID: " + prodottoSalvato.getId() + ")");
        return prodottoSalvato;
    }
    //metodo pubblico per gli acquirenti
    public List<Prodotto> getProdottiVisibili(){
        return prodottoRepository.findByStatoConferma(Conferma.APPROVATO);
    }

    //metodo privato per i curatori
    public List<Prodotto> getProdottiPerStato(Conferma stato){
        return prodottoRepository.findByStatoConferma(stato);
    }

    public List<Prodotto> getProdottiDelVenditore(Venditore venditore) {
        return prodottoRepository.findByVenditore(venditore);
    }

    public Prodotto getProdottoById(Long id) {
        return prodottoRepository.findById(id).orElse(null);
    }

    public boolean verificaDisponibilita(Prodotto prodotto, int quantita){
        Prodotto p = prodottoRepository.findById(prodotto.getId()).orElse(null);
        if (p == null) return false;
        return p.getQuantita() >= quantita;
    }

    @Transactional
    public void scalaQuantita(Prodotto prodotto, int quantita){
        Prodotto p = prodottoRepository.findById(prodotto.getId()).orElse(null);
        if (p != null){
            p.setQuantita(p.getQuantita() - quantita);
            prodottoRepository.save(p);
            System.out.println("Scalato stock: " + p.getNome() + " -> Rimasti: " + p.getQuantita());
        }
    }

    @Transactional
    public void ripristinaQuantita(Prodotto prodotto, int quantita) {
        Prodotto p = prodottoRepository.findById(prodotto.getId()).orElse(null);
        if (p != null) {
            p.setQuantita(p.getQuantita() + quantita);
            prodottoRepository.save(p);
            System.out.println("Ripristinato stock: " + p.getNome() + " -> Rimasti: " + p.getQuantita());
        }
    }
}