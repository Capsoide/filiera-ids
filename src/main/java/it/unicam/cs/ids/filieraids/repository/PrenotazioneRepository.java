package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.Prenotazione;
import it.unicam.cs.ids.filieraids.model.Utente;
import it.unicam.cs.ids.filieraids.model.Evento;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    //trova le prenotazioni di un utente
    List<Prenotazione> findByUtente(Utente utente);

    //trova tutte le prenotazioni per un evento
    List<Prenotazione> findByEvento(Evento evento);

    //elimina tutte le prenotazioni associate a un determinato evento, si usa quando un animatore cancella il suo evento
    @Transactional
    void deleteByEvento(Evento evento);

}
