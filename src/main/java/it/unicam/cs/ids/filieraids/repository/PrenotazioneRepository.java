package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

}
