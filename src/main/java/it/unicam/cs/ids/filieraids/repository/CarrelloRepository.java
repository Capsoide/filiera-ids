package it.unicam.cs.ids.filieraids.repository;
import it.unicam.cs.ids.filieraids.model.Carrello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, Long> { }
