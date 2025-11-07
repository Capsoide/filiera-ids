package it.unicam.cs.ids.filieraids.model;
import jakarta.persistence.Embeddable;

@Embeddable
public class Pagamento {

    private String circuito;      //visa, mastercard,...
    private String numeroCarta;   //simulato
    private String intestatario;  //nome sulla carta

    public Pagamento() {}

    public Pagamento(String circuito, String numeroCarta, String intestatario) {
        this.circuito = circuito;
        this.numeroCarta = numeroCarta;
        this.intestatario = intestatario;
    }

    public String getCircuito() { return circuito; }

    public void setCircuito(String circuito) { this.circuito = circuito; }

    public String getNumeroCarta() { return numeroCarta; }

    public void setNumeroCarta(String numeroCarta) { this.numeroCarta = numeroCarta; }

    public String getIntestatario() { return intestatario; }

    public void setIntestatario(String intestatario) { this.intestatario = intestatario; }

    @Override
    public String toString() {
        return "Pagamento { circuito=" + circuito + ", intestatario='" + intestatario + "' }";
    }
}
