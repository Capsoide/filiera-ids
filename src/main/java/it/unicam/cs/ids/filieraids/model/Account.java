package it.unicam.cs.ids.filieraids.model;

import java.util.List;
import java.util.Set;

public interface Account {

    String getEmail();

    Set<Ruolo> getRuoli();

    void addRuolo(Ruolo ruolo);

    void removeRuolo(Ruolo ruolo);

    void setRuoli(Set<Ruolo> ruoli);

    String getPassword();

    List<Indirizzo> getIndirizzi();

    void addIndirizzo(Indirizzo indirizzo);

    void removeIndirizzo(Indirizzo indirizzo);

}
