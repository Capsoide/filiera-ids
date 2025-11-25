package it.unicam.cs.ids.filieraids.model;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@Embeddable
public class Indirizzo {

    private String via;
    private String numCivico;
    private String comune;
    private String CAP;
    private String regione;
    @DecimalMin(value = "-90", message = "Latitudine non valida.")
    @DecimalMax(value = "90", message = "Latitudine non valida.")
    private Double latitudine;
    @DecimalMin(value = "-180", message = "Longitudine non valida.")
    @DecimalMax(value = "180", message = "Longitudine non valida.")
    private Double longitudine;

    public Indirizzo(){}

    public Indirizzo(String via, String numCivico, String comune, String CAP, String regione, Double latitudine, Double longitudine) {
        this.via = via;
        this.numCivico = numCivico;
        this.comune = comune;
        this.CAP = CAP;
        this.regione = regione;
    }

    public Indirizzo(String via, String numCivico, String comune, String CAP, String regione) {
        this(via, numCivico, comune, CAP, regione, null, null);
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

    public Double getLatitudine(){ return latitudine; }

    public void setLatitudine(double latitudine) { this.latitudine = latitudine; }

    public Double getLongitudine(){ return longitudine; }

    public void setLongitudine(double longitudine) { this.longitudine = longitudine; }

    @Transient
    public String getIndirizzoCompleto(){
        return via + " " + numCivico + ", " + CAP + " " + comune + " (" + regione + ")";
    }

    @Override
    public String toString() {
        String coordinate = (latitudine != null && longitudine != null)
                ? (" [GPS: " + latitudine + ", " + longitudine + "]") : " ";
        return getIndirizzoCompleto() + coordinate;
    }

}
