package it.unicam.cs.ids.filieraids.service;
import it.unicam.cs.ids.filieraids.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ProdottoService {

    private final List<Prodotto> prodotti = new ArrayList<>();
    private static int idCounter = 1;       //simula l'autoincremento del id nel db

    public Prodotto creaProdotto(
            Date datacaricamento, String descrizione, String nome,
            String metodoDiColtivazione, double prezzo, Venditore produttore,
            List<String> certificazioni, Date dataProduzione, int quantita){

        Prodotto p = new Prodotto(datacaricamento, descrizione, nome,
                metodoDiColtivazione, prezzo, produttore, certificazioni,
                dataProduzione, quantita);

        p.setId(idCounter++);  //simulazione assegna id

        produttore.addProdotto(p);

        this.prodotti.add(p);

        System.out.println("Prodotto creato: " + p.getNome());
        return p;
    }
    //mostra solo i prodotti approvati e visibili
    public List<Prodotto> getProdottiVisibili(){
        return prodotti.stream()
                .filter(p -> p.getStatoConferma() == Conferma.APPROVATO)
                .collect(Collectors.toList());
    }

    //restituisce i prdotti di un venditore specifico
    public List<Prodotto> getProdottidelVenditore(Venditore venditore) {
        return prodotti.stream()
                .filter(p -> p.getVenditore() != null && p.getVenditore().equals(venditore))
                .collect(Collectors.toList());
    }

    //verifica la disponibiltà
    public boolean verificaDisponibilita(Prodotto prodotto, int quantita){
        return prodotto != null && prodotto.getQuantita() >= quantita;
    }

    //scala quantità, è usato da OrdineService
    public void scalaQuantita(Prodotto prodotto, int quantita){
        if (prodotto != null){
            prodotto.setQuantita(prodotto.getQuantita() - quantita);
            System.out.println("Scalato stock: " + prodotto.getNome() + " -> Rimasti: " + prodotto.getQuantita());
        }
    }

    //ripristina lo stock, è usato da OrdineService per annullamento
    public void ripristinaQuantita(Prodotto prodotto, int quantita) {
        if (prodotto != null) {
            prodotto.setQuantita(prodotto.getQuantita() + quantita);
            System.out.println("Ripristinato stock: " + prodotto.getNome() + " -> Rimasti: " + prodotto.getQuantita());
        }
    }
}


