package it.unicam.cs.ids.filieraids.repository;
import it.unicam.cs.ids.filieraids.model.Carrello;
import it.unicam.cs.ids.filieraids.model.Prodotto;
import it.unicam.cs.ids.filieraids.model.RigaCarrello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface RigaCarrelloRepository extends JpaRepository<RigaCarrello, Long> {
    Optional<RigaCarrello> findByCarrelloAndProdotto(Carrello carrello, Prodotto prodotto);
}