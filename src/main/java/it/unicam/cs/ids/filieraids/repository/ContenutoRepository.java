package it.unicam.cs.ids.filieraids.repository;
import it.unicam.cs.ids.filieraids.model.Contenuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContenutoRepository extends JpaRepository<Contenuto, Long> { }