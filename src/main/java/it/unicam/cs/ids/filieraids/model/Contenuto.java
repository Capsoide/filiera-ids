package it.unicam.cs.ids.filieraids.model;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "contenuti")
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Contenuto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Conferma statoConferma;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCaricamento;

    private String descrizione;

    public Contenuto() {
        this.dataCaricamento = new Date();
        this.statoConferma = Conferma.ATTESA;
    }

    public Contenuto(Conferma statoConferma, Date dataCaricamento, String descrizione) {
        this.statoConferma = statoConferma;
        this.dataCaricamento = dataCaricamento;
        this.descrizione = descrizione;
    }

    public Long getId() { return id; }

    public Conferma getStatoConferma() { return statoConferma; }
    public void setStatoConferma(Conferma statoConferma) { this.statoConferma = statoConferma; }
    public Date getDataCaricamento() { return dataCaricamento; }
    public void setDataCaricamento(Date dataCaricamento) { this.dataCaricamento = dataCaricamento; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}