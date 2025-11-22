package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.RichiestaRuolo;
import org.springframework.web.bind.annotation.RestController;
import it.unicam.cs.ids.filieraids.service.GestoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/gestore")
@PreAuthorize("hasRole('GESTORE')")
public class GestoreController {

    private final GestoreService gestoreService;

    public GestoreController(GestoreService gestoreService) {
        this.gestoreService = gestoreService;
    }

    /**
     * Endpoint protetto che permette al gestore di visualizzare tutte le richieste del ruolo in attesa.
     *
     * @return  la lista delle richieste in attesa
     */
    @GetMapping("/richieste-ruolo")
    public List<RichiestaRuolo> getRichieste() {
        return gestoreService.getRichiesteInAttesa();
    }

    /**
     * Endpoint protetto che permette al gestore di approvare una richiesta in attesa.
     *
     * @param id    l'id della richiesta da approvare
     * @return      messaggio di conferma dell'approvazione della richiesta
     */
    @PostMapping("/richieste-ruolo/{id}/approva")
    public ResponseEntity<String> approvaRichiesta(@PathVariable Long id){
        gestoreService.approvaRichiesta(id);
        return ResponseEntity.ok("Richiesta con " + id + " approvata con successo");
    }

    /**
     * Endpoint protetto che permette al gestore loggato di rifiutare una richiesta in attesa.
     *
     * @param id    l'id della richiesta da rifiutare
     * @return      messaggio di conferma di rifuto della richiesta
     */
    @PostMapping("/richieste-ruolo/{id}/rifiuta")
    public ResponseEntity<String> rifutaRichiesta(@PathVariable Long id){
        gestoreService.rifiutaRichiesta(id);
        return ResponseEntity.ok("Richiesta con " + id + " rifutata");
    }
}
