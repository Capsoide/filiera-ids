package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.ProdottoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;

    public ProdottoService(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
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