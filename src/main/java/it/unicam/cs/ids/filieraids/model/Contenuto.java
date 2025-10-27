package it.unicam.cs.ids.filieraids.model;

import java.util.Date;

public abstract class Contenuto {

    private int id;
    private Conferma statoConferma;
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Conferma getStatoConferma() { return statoConferma; }
    public void setStatoConferma(Conferma statoConferma) { this.statoConferma = statoConferma; }
    public Date getDataCaricamento() { return dataCaricamento; }
    public void setDataCaricamento(Date dataCaricamento) { this.dataCaricamento = dataCaricamento; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}
