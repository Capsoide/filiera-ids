package it.unicam.cs.ids.filieraids.repository;
import it.unicam.cs.ids.filieraids.model.Contenuto;
import it.unicam.cs.ids.filieraids.model.Conferma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ContenutoRepository extends JpaRepository<Contenuto, Long> {

    //permette al curatoreservice di trovare tutti i contenuti (sia prodotti che eventi) che sono in attesa di approvazione
    List<Contenuto> findByStatoConferma(Conferma stato);

}