package it.unicam.cs.ids.filieraids.model;

import java.util.Date;

public class Ordine {

    private static int counter = 1;
    private int id;
    private Date dataOrdine;
    private Carrello carrello;
    private Utente utente;
    private Pagamento pagamento;
    private Indirizzo indirizzoFatturazione;
    private double totale;
    private boolean evaso;
    private StatoOrdine statoOrdine;

    public Ordine() {
        this.id = counter++;
        this.dataOrdine = new Date();
        this.evaso = false;
        this.statoOrdine = StatoOrdine.ATTESA;
    }

    public Ordine(Date dataOrdine, Carrello carrello, Pagamento pagamento, Indirizzo indirizzo, Utente utente) {

        // Controlli e logica di relazione (utente.addOrdine) rimossi

        this.id = counter++;
        this.dataOrdine = (dataOrdine != null) ? dataOrdine : new Date();
        this.carrello = carrello;
        this.pagamento = pagamento;
        this.indirizzoFatturazione = indirizzo;
        this.utente = utente;
        this.carrello = new Carrello(carrello);
        this.evaso = false;
        this.totale = this.carrello.getPrezzoTotale();
        this.statoOrdine = StatoOrdine.ATTESA;
    }

    // --- GETTER E SETTER ---
    public int getId() { return id; }
    public Date getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Date dataOrdine) { this.dataOrdine = dataOrdine; }
    public Carrello getCarrello() { return carrello; }
    public void setCarrello(Carrello carrello) { this.carrello = carrello; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }
    public Indirizzo getIndirizzoDiFatturazione() { return indirizzoFatturazione; }
    public void setIndirizzoDiFatturazione(Indirizzo indirizzoDiFatturazione) { this.indirizzoFatturazione = indirizzoDiFatturazione; }
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
                ", utente=" + (utente != null ? utente.getEmail() : "N/D") +
                ", totale=" + totale +
                ", statoOrdine=" + statoOrdine +
                '}';
    }
}