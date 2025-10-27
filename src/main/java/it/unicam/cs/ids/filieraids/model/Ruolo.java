package it.unicam.cs.ids.filieraids.model;

public enum Ruolo {
    CURATORE("Curatore"),
    ANIMATORE("Animatore della Filiera"),
    TRASFORMATORE("Trasformatore"),
    PRODUTTORE("Produttore"),
    DISTRIBUTORE("Distributore"),
    GESTORE("Gestore della Piattaforma"),
    ACQUIRENTE("Acquirente");

    private final String descrizione;

    Ruolo(String descrizione){
        this.descrizione = descrizione;
    }

    public String getDescrizione(){
        return descrizione;
    }

}
