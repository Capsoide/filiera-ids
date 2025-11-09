package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.Conferma;
import it.unicam.cs.ids.filieraids.model.RichiestaRuolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RichiestaRuoloRepository extends JpaRepository<RichiestaRuolo, Long>{

    List<RichiestaRuolo> findByStato(Conferma stato);

}
