package it.unicam.cs.ids.filieraids.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "carrelli")
public class Carrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- Eccolo qui!

    private double prezzoTotale;

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<RigaCarrello> contenuti;

    public Carrello() {
        this.prezzoTotale = 0.0;
        this.contenuti = new ArrayList<>();
    }

    public Carrello(Carrello carrelloDaCopiare) {
        this.prezzoTotale = carrelloDaCopiare.getPrezzoTotale();
        this.contenuti = new ArrayList<>();
        //è necessario copiare le righe e non solo la lista
        for (RigaCarrello rigaDaCopiare : carrelloDaCopiare.getContenuti()) {
            RigaCarrello nuovaRiga = new RigaCarrello(
                    rigaDaCopiare.getProdotto(),
                    rigaDaCopiare.getQuantita(),
                    rigaDaCopiare.getPrezzoUnitarioSnapshot()
            );
            this.addRiga(nuovaRiga);
        }
    }

    public void addRiga(RigaCarrello riga) {
        this.contenuti.add(riga);
        riga.setCarrello(this);
        this.ricalcolaTotale();
    }

    public void removeRiga(RigaCarrello riga) {
        this.contenuti.remove(riga);
        riga.setCarrello(null);
        this.ricalcolaTotale();
    }

    public void ricalcolaTotale() {
        this.prezzoTotale = 0.0;
        for (RigaCarrello riga : contenuti) {
            this.prezzoTotale += riga.getPrezzoTotaleRiga();
        }
    }

    public void svuota() {
        for (RigaCarrello riga : new ArrayList<>(contenuti)) {
            this.removeRiga(riga);
        }
        this.prezzoTotale = 0.0;
    }

    public Long getId() { return id; }
    public double getPrezzoTotale() { return prezzoTotale; }
    public void setPrezzoTotale(double prezzoTotale) { this.prezzoTotale = prezzoTotale; }
    public List<RigaCarrello> getContenuti() { return contenuti; }

    @Override
    public String toString() {
        return "Carrello [id=" + id + ", totale=" + prezzoTotale + ", contenuti=" + contenuti.size() + " righe]";
    }
}