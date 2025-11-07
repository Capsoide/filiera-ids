package it.unicam.cs.ids.filieraids.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;

import java.util.*;

@Repository
public interface VenditoreRepository extends JpaRepository<Venditore, Long> {
    Optional<Venditore> findByEmail(String email);
}
