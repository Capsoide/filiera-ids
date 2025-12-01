package it.unicam.cs.ids.filieraids.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "pacchetti")
public class Pacchetto extends Contenuto {

    private String nome;
    private double prezzo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venditore_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Venditore venditore;

    @OneToMany(mappedBy = "pacchetto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PacchettoItem> items = new ArrayList<>();

    public Pacchetto() {
        super();
    }

    public Pacchetto(String nome, String descrizione, double prezzo, Venditore venditore) {
        super(Conferma.ATTESA, new Date(), descrizione, false);
        this.nome = nome;
        this.prezzo = prezzo;
        this.venditore = venditore;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public Venditore getVenditore() { return venditore; }
    public void setVenditore(Venditore venditore) { this.venditore = venditore; }

    public List<PacchettoItem> getItems() { return items; }

    public void addItem(PacchettoItem item) {
        items.add(item);
        item.setPacchetto(this);
    }

    @Override
    public String toString() {
        return "Pacchetto { nome='" + nome + "', prezzo=" + prezzo +
                ", stato=" + getStatoConferma() + " }";
    }
}