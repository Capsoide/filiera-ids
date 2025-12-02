package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.Contenuto;

public interface ContenutoObserver {
    void update(Contenuto contenuto);
}