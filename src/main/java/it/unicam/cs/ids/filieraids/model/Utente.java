package it.unicam.cs.ids.filieraids.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.*;
import jakarta.persistence.*;


@Entity
@Table(name = "utenti")
@PrimaryKeyJoinColumn(name = "attore_id")
public class Utente extends Attore {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrello_id", referencedColumnName = "id")
    private Carrello carrello;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Ordine> ordini = new ArrayList<>();

    public Utente(String email, String password, String nome, String cognome) {
        super(email, password, nome, cognome);
        this.carrello = new Carrello();
        addRuolo(Ruolo.ACQUIRENTE);
    }

    public Utente() {
        super();
        this.carrello = new Carrello();
    }

    public Carrello getCarrello() {
        return carrello;
    }

    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
    }

    public List<Ordine> getOrdini() {
        return ordini;
    }

    public void addOrdine(Ordine ordine) {
        if (ordine != null && !ordini.contains(ordine)) {
            ordini.add(ordine);
            ordine.setUtente(this);
        }
    }

    public void removeOrdine(Ordine ordine) {
        ordini.remove(ordine);
        ordine.setUtente(null);
    }

    @Override
    public String toString() {
        return "Utente [" + getNomeCompleto() + ", email=" + getEmail() + ", ruoli=" + getRuoli() + "]";
    }
}