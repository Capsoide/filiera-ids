package it.unicam.cs.ids.filieraids.repository;

import it.unicam.cs.ids.filieraids.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacchettoItemRepository extends JpaRepository<PacchettoItem, Long> {

}