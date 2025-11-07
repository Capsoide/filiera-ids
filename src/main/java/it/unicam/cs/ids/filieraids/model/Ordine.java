package it.unicam.cs.ids.filieraids.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "ordini")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOrdine;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrello_id", referencedColumnName = "id")
    private Carrello carrello;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    @JsonBackReference
    private Utente utente;

    @Embedded
    private Pagamento pagamento;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "via", column = @Column(name = "indirizzo_via")),
            @AttributeOverride(name = "numCivico", column = @Column(name = "indirizzo_numCivico")),
            @AttributeOverride(name = "comune", column = @Column(name = "indirizzo_comune")),
            @AttributeOverride(name = "CAP", column = @Column(name = "indirizzo_CAP")),
            @AttributeOverride(name = "regione", column = @Column(name = "indirizzo_regione"))
    })
    private Indirizzo indirizzoDiFatturazione;

    private double totale;
    private boolean evaso;

    @Enumerated(EnumType.STRING)
    private StatoOrdine statoOrdine;

    public Ordine() {
    }

    public Ordine(Date dataOrdine, Carrello carrello, Pagamento pagamento, Indirizzo indirizzo, Utente utente){
        this.dataOrdine = (dataOrdine != null) ? dataOrdine : new Date();
        this.carrello = new Carrello(carrello);
        this.pagamento = pagamento;
        this.indirizzoDiFatturazione = indirizzo;
        this.utente = utente;
        this.carrello = new Carrello(carrello);
        this.evaso = false;
        this.totale = this.carrello.getPrezzoTotale();
        this.statoOrdine = StatoOrdine.ATTESA;
    }

    public Long getId() { return id; }
    public Date getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Date dataOrdine) { this.dataOrdine = dataOrdine; }
    public Carrello getCarrello() { return carrello; }
    public void setCarrello(Carrello carrello) { this.carrello = carrello; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }
    public Indirizzo getIndirizzoDiFatturazione() { return indirizzoDiFatturazione; }
    public void setIndirizzoDiFatturazione(Indirizzo indirizzoDiFatturazione) { this.indirizzoDiFatturazione = indirizzoDiFatturazione; }
    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }
    public boolean isEvaso() { return evaso; }
    public void setEvaso(boolean evaso) { this.evaso = evaso; }
    public StatoOrdine getStatoOrdine() { return statoOrdine; }
    public void setStatoOrdine(StatoOrdine statoOrdine) { this.statoOrdine = statoOrdine; }

    @Override
    public String toString() {
        return "Ordine {" +
                "id=" + id +
                ", dataOrdine=" + dataOrdine +
                ", utente_id=" + (utente != null ? utente.getId() : "N/D") + // <-- Stampa solo l'ID
                ", totale=" + totale +
                ", statoOrdine=" + statoOrdine +
                '}';
    }
}