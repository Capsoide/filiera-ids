package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.Contenuto;

public interface ContenutoSubject {
    void addObserver(ContenutoObserver observer);
    void removeObserver(ContenutoObserver observer);
    void notifyObservers(Contenuto contenuto);
}