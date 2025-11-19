package it.unicam.cs.ids.filieraids.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {
    List<Ordine> findByUtente(Utente utente);
    List<Ordine> findByStatoOrdine(StatoOrdine stato);


    //seleziona gli ordini collegando le tabelle
    //Ordine -> Carrello -> Righe -> Prodotto -> Venditore per recuperare il prodotto di un venditore
    @Query("SELECT DISTINCT o FROM Ordine o " +
            "JOIN o.carrello c " +
            "JOIN c.contenuti r " +
            "JOIN r.prodotto p " +
            "WHERE p.venditore = :venditore")
    List<Ordine> findByProdottoVenditore(@Param("venditore") Venditore venditore);
}
