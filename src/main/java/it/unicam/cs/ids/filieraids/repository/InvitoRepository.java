package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitoRepository extends JpaRepository<Invito, Long> {
    List<Invito> findByVenditore(Venditore venditore);
    List<Invito> findByEvento(Evento evento);
    Optional<Invito> findByEventoAndVenditore(Evento evento, Venditore venditore);
}
