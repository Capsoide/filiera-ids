package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.Evento;
import it.unicam.cs.ids.filieraids.model.Conferma;
import it.unicam.cs.ids.filieraids.model.Attore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    //trova tutti gli eventi approvati
    List<Evento> findByStatoConferma(Conferma stato);

    //trova tutti gli eventi creati da uno specifico animatore
    List<Evento> findByAnimatore(Attore animatore);

}
