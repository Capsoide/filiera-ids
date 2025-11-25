package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacchettoRepository extends JpaRepository<Pacchetto, Long> {

    List<Pacchetto> findByStatoConferma(Conferma stato);

    List<Pacchetto> findByVenditore(Venditore venditore);
}