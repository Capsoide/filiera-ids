package it.unicam.cs.ids.filieraids.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "righe_carrello")
public class RigaCarrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prodotto_id", nullable = true)
    private Prodotto prodotto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pacchetto_id", nullable = true)
    private Pacchetto pacchetto;

    private int quantita;
    private double prezzoUnitarioSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrello_id")
    @JsonBackReference
    private Carrello carrello;

    public RigaCarrello() {}

    //Costruttore per PRODOTTI
    public RigaCarrello(Prodotto prodotto, int quantita, double prezzoUnitarioSnapshot) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.prezzoUnitarioSnapshot = prezzoUnitarioSnapshot;
    }

    //Costruttore per PACCHETTI
    public RigaCarrello(Pacchetto pacchetto, int quantita, double prezzoUnitarioSnapshot) {
        this.pacchetto = pacchetto;
        this.quantita = quantita;
        this.prezzoUnitarioSnapshot = prezzoUnitarioSnapshot;
    }

    public double getPrezzoTotaleRiga() {
        return this.prezzoUnitarioSnapshot * this.quantita;
    }

    public Long getId() { return id; }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public Pacchetto getPacchetto() { return pacchetto; }
    public void setPacchetto(Pacchetto pacchetto) { this.pacchetto = pacchetto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public double getPrezzoUnitarioSnapshot() { return prezzoUnitarioSnapshot; }
    public void setPrezzoUnitarioSnapshot(double prezzoUnitarioSnapshot) { this.prezzoUnitarioSnapshot = prezzoUnitarioSnapshot; }

    public Carrello getCarrello() { return carrello; }
    public void setCarrello(Carrello carrello) { this.carrello = carrello; }

    @Override
    public String toString() {
        String nome = (prodotto != null) ? prodotto.getNome() : (pacchetto != null ? pacchetto.getNome() : "N/D");
        return "RigaCarrello [oggetto=" + nome + ", quantita=" + quantita + "]";
    }
}