package it.unicam.cs.ids.filieraids.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "prenotazioni")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente; // L'acquirente che si è prenotato

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento; // L'evento a cui si è prenotato

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPrenotazione;

    private int numeroPostiPrenotati;

    public Prenotazione() {}

    public Prenotazione(Utente utente, Evento evento, int numeroPostiPrenotati) {
        this.utente = utente;
        this.evento = evento;
        this.numeroPostiPrenotati = numeroPostiPrenotati;
        this.dataPrenotazione = new Date();
    }

    public Long getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Date getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(Date dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione;
    }

    public int getNumeroPostiPrenotati() {
        return numeroPostiPrenotati;
    }

    public void setNumeroPostiPrenotati(int numeroPostiPrenotati) {
        this.numeroPostiPrenotati = numeroPostiPrenotati;
    }
}
