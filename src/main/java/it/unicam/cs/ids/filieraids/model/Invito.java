package it.unicam.cs.ids.filieraids.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <--- IMPORTA QUESTO
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "inviti")
public class Invito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    // Questa annotazione dice a Jackson di ignorare i campi interni del proxy Hibernate
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venditore_id", nullable = false)
    // Anche qui, per evitare errori se il venditore viene caricato lazy
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Venditore venditore;

    @Enumerated(EnumType.STRING)
    private StatoInvito stato;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInvito;

    public Invito() {}

    public Invito(Evento evento, Venditore venditore) {
        this.evento = evento;
        this.venditore = venditore;
        this.stato = StatoInvito.ATTESA;
        this.dataInvito = new Date();
    }

    public Long getId() { return id; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public Venditore getVenditore() { return venditore; }
    public void setVenditore(Venditore venditore) { this.venditore = venditore; }
    public StatoInvito getStato() { return stato; }
    public void setStato(StatoInvito stato) { this.stato = stato; }
    public Date getDataInvito() { return dataInvito; }
}
