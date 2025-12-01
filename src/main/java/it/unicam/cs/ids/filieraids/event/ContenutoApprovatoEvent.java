package it.unicam.cs.ids.filieraids.event;

import it.unicam.cs.ids.filieraids.model.Contenuto;

public class ContenutoApprovatoEvent {
    private final Contenuto contenuto;

    public ContenutoApprovatoEvent(Contenuto contenuto) {
        this.contenuto = contenuto;
    }

    public Contenuto getContenuto() {
        return contenuto;
    }
}