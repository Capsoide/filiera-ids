package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.ProdottoRepository;
import it.unicam.cs.ids.filieraids.repository.VenditoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final VenditoreRepository venditoreRepository;

    public ProdottoService(ProdottoRepository prodottoRepository,
                            VenditoreRepository venditoreRepository) {
        this.prodottoRepository = prodottoRepository;
        this.venditoreRepository = venditoreRepository;
    }

    private Venditore getVenditoreByEmail(String email) {
        return venditoreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Venditore non trovato: " + email));
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

    /**
     * recupera tutti i prodotti (inclusi quelli in attesa)
     * per uno specifico venditore, identificato dalla sua email.
     */
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