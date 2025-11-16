package it.unicam.cs.ids.filieraids.model;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "autorizzazioni")
public class Autorizzazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curatore_id")
    private Attore curatore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenuto_id")
    private Contenuto contenutoDaApprovare;

    private String motivo;
    private boolean autorizzato;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAutorizzazione;

    public Autorizzazione() {}

    public Autorizzazione(Attore curatore, Contenuto contenutoDaApprovare, String motivo, boolean autorizzato) {
        this.dataAutorizzazione = new Date();
        this.curatore = curatore;
        this.contenutoDaApprovare = contenutoDaApprovare;
        this.motivo = motivo;
        this.autorizzato = autorizzato;
    }

    public Long getId() { return id; }
    public Attore getCuratore() { return curatore; }
    public void setCuratore(Attore curatore) { this.curatore = curatore; }
    public Contenuto getContenutoDaApprovare() { return contenutoDaApprovare; }
    public void setContenutoDaApprovare(Contenuto contenuto) { this.contenutoDaApprovare = contenuto; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public boolean isAutorizzato() { return autorizzato; }
    public void setAutorizzato(boolean autorizzato) { this.autorizzato = autorizzato; }
    public Date getDataAutorizzazione() { return dataAutorizzazione; }
    public void setDataAutorizzazione(Date dataAutorizzazione) { this.dataAutorizzazione = dataAutorizzazione; }

    @Override
    public String toString() {
        return "Autorizzazione {" +
                "id=" + id +
                ", contenuto=" + (contenutoDaApprovare != null ? contenutoDaApprovare.getDescrizione() : "n/d") +
                ", autorizzato=" + (autorizzato ? "APPROVATO" : "RIFIUTATO") +
                '}';
    }
}
