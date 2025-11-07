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
    @JoinColumn(name = "prodotto_id")
    private Prodotto prodotto;

    private int quantita;
    private double prezzoUnitarioSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrello_id")
    @JsonBackReference
    private Carrello carrello;

    public RigaCarrello() {}

    public RigaCarrello(Prodotto prodotto, int quantita, double prezzoUnitarioSnapshot) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.prezzoUnitarioSnapshot = prezzoUnitarioSnapshot;
    }

    public double getPrezzoTotaleRiga() {
        return this.prezzoUnitarioSnapshot * this.quantita;
    }

    public Long getId() { return id; }
    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    public double getPrezzoUnitarioSnapshot() { return prezzoUnitarioSnapshot; }
    public void setPrezzoUnitarioSnapshot(double prezzoUnitarioSnapshot) { this.prezzoUnitarioSnapshot = prezzoUnitarioSnapshot; }
    public Carrello getCarrello() { return carrello; }
    public void setCarrello(Carrello carrello) { this.carrello = carrello; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RigaCarrello other)) return false;
        return prodotto != null && prodotto.equals(other.getProdotto());
    }

    @Override
    public int hashCode() {
        return (prodotto != null ? prodotto.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "RigaCarrello [prodotto=" + (prodotto != null ? prodotto.getNome() : "N/D") +
                ", quantita=" + quantita +
                ", prezzoTotale=" + getPrezzoTotaleRiga() + "]";
    }
}