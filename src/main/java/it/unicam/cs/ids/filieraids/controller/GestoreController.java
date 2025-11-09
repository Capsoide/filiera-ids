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

    //Visualizza tutte le richieste in ATTESA
    @GetMapping("/richieste-ruolo")
    public List<RichiestaRuolo> getRichieste() {
        return gestoreService.getRichiesteInAttesa();
    }

    //Approva richiesta
    @PostMapping("/richieste-ruolo/{id}/approva")
    public ResponseEntity<String> approvaRichiesta(@PathVariable Long id){
        gestoreService.approvaRichiesta(id);
        return ResponseEntity.ok("Richiesta con " + id + " approvata con successo");
    }

    //Rifuta richiesta
    @PostMapping("/richieste-ruolo/{id}/rifiuta")
    public ResponseEntity<String> rifutaRichiesta(@PathVariable Long id){
        gestoreService.rifiutaRichiesta(id);
        return ResponseEntity.ok("Richiesta con " + id + " rifutata");
    }


}
