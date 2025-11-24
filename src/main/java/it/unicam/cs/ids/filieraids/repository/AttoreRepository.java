package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.Attore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttoreRepository extends JpaRepository<Attore, Long> {

    Optional<Attore> findByEmail(String email);
    List<Attore> findByEnabledFalse();
}