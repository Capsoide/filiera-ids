package it.unicam.cs.ids.filieraids.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "venditori")
@PrimaryKeyJoinColumn(name = "attore_id")
public class Venditore extends Attore {
    private String PIVA;
    private String descrizione;

    @OneToMany(mappedBy = "venditore", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Prodotto> prodotti = new ArrayList<>();

    @OneToMany(mappedBy = "venditore", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Invito> invitiRicevuti = new ArrayList<>();

    public Venditore(String email, String password, String nome, String cognome, String PIVA, String descrizione, Set<Ruolo> ruoli) {
        super(email, password, nome, cognome);
        this.PIVA = PIVA;
        this.descrizione = descrizione;
        setRuoli(ruoli);
    }
    public Venditore() { super(); }

    public List <Prodotto> getProdotti() { return prodotti; }
    public void addProdotto(Prodotto prodotto) { if(prodotto != null && !prodotti.contains(prodotto)) { prodotti.add(prodotto); prodotto.setVenditore(this); } }
    public void removeProdotto(Prodotto prodotto) { prodotti.remove(prodotto); prodotto.setVenditore(null); }
    public String getPIVA() { return PIVA; }
    public void setPIVA(String PIVA) { this.PIVA = PIVA; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public List<Invito> getInvitiRicevuti() { return invitiRicevuti; }
    public void setInvitiRicevuti(List<Invito> invitiRicevuti) { this.invitiRicevuti = invitiRicevuti; }

    @Override public String toString() { return "Venditore [" + getNomeCompleto() + ", P.IVA=" + PIVA + ", ruoli=" + getRuoli() + "]"; }
}