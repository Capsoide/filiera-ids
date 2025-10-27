package it.unicam.cs.ids.filieraids.model;

import java.util.*;

public class Carrello {

    private double prezzoTotale;
    private List<RigaCarrello> contenuti;

    public Carrello(){
        this.prezzoTotale = 0.0;
        this.contenuti = new ArrayList<>();
    }
    public Carrello(Carrello carrelloDaCopiare) {
        this.prezzoTotale = carrelloDaCopiare.getPrezzoTotale();
        this.contenuti = new ArrayList<>(carrelloDaCopiare.getContenuti());
    }

    public double getPrezzoTotale() {
        return prezzoTotale;
    }

    public void setPrezzoTotale(double prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }

    public List<RigaCarrello> getContenuti() {
        return contenuti;
    }

    public void ricalcolaTotale(){
        this.prezzoTotale = 0.0;
        for (RigaCarrello rigaCarrello : contenuti){
            this.prezzoTotale += rigaCarrello.getPrezzoTotaleRiga();
        }
    }

    public void svuota(){
        contenuti.clear();
        prezzoTotale = 0.0;
    }

    @Override
    public String toString() {
        return "Carrello [totale=" + prezzoTotale + ", contenuti=" + contenuti + "]";
    }
}
