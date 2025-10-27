package it.unicam.cs.ids.filieraids.model;

import org.springframework.stereotype.Service;

public class Indirizzo {

    private String via;
    private String numCivico;
    private String comune;
    private String CAP;
    private String regione;

    public Indirizzo(String via, String numCivico, String comune, String CAP, String regione) {
        this.via = via;
        this.numCivico = numCivico;
        this.comune = comune;
        this.CAP = CAP;
        this.regione = regione;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getNumCivico() {
        return numCivico;
    }

    public void setNumCivico(String numCivico) {
        this.numCivico = numCivico;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getCAP() {
        return CAP;
    }

    public void setCAP(String CAP) {
        this.CAP = CAP;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getIndirizzoCompleto(){
        return via + " " + numCivico + ", " + CAP + " " + comune + " (" + regione + ")";
    }

    @Override
    public String toString() {
        return getIndirizzoCompleto();
    }

}
