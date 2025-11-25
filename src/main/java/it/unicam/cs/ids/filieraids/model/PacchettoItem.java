package it.unicam.cs.ids.filieraids.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pacchetto_items")
public class PacchettoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pacchetto_id")
    private Pacchetto pacchetto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prodotto_id")
    private Prodotto prodotto;

    private int quantita;

    public PacchettoItem() {}

    public PacchettoItem(Prodotto prodotto, int quantita) {
        this.prodotto = prodotto;
        this.quantita = quantita;
    }

    public Long getId() { return id; }

    public Pacchetto getPacchetto() { return pacchetto; }
    public void setPacchetto(Pacchetto pacchetto) { this.pacchetto = pacchetto; }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    @Override
    public String toString() {
        return "PacchettoItem { prodotto=" +
                (prodotto != null ? prodotto.getNome() : "N/D") +
                ", quantita=" + quantita + " }";
    }
}