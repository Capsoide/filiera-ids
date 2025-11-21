package it.unicam.cs.ids.filieraids.dto;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Contenuto;

import java.util.Date;

public class AutorizzazioneDTO {

    private int id;

    private Attore curatore;

    private Contenuto contenutoDaApprovare;

    private String motivazione;

    private boolean autorizzato;

    private final Date dataAutorizzazione;

    public AutorizzazioneDTO(int id, Attore curatore, Contenuto contenutoDaApprovare,
                             boolean autorizzato, String motivo, Date dataAutorizzazione) {
        this.contenutoDaApprovare = contenutoDaApprovare;
        this.curatore = curatore;
        this.id = id;
        this.autorizzato = autorizzato;
        this.motivazione = motivazione;
        this.dataAutorizzazione = dataAutorizzazione;
    }

    public int getId() {
        return id;
    }

    public Attore getCuratore() {
        return curatore;
    }

    public void setCuratore(Attore curatore) {
        this.curatore = curatore;
    }

    public Contenuto getContenutoDaApprovare() {
        return contenutoDaApprovare;
    }

    public void setContenutoDaApprovare(Contenuto contenutoDaApprovare) {
        this.contenutoDaApprovare = contenutoDaApprovare;
    }

    public String getMotivazione() {
        return motivazione;
    }

    public void setMotivazione(String motivazione) {
        this.motivazione = motivazione;
    }

    public boolean isAutorizzato() {
        return autorizzato;
    }

    public void setAutorizzato(boolean autorizzato) {
        this.autorizzato = autorizzato;
    }

    public Date getDataAutorizzazione() {
        return dataAutorizzazione;
    }
}
