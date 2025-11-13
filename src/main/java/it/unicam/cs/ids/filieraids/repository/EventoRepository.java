package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

}
