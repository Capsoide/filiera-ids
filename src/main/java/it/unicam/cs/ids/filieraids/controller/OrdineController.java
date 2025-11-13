package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.*;

@RestController
@RequestMapping("/api/ordini")
public class OrdineController {
    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    //endpoint protetto: crea ordine per utente loggato
    @PostMapping
    public ResponseEntity<Ordine> creaOrdine(Authentication authentication) {
        String utenteEmail = authentication.getName();
        Ordine nuovoOrdine = ordineService.creaOrdinePerEmail(utenteEmail);
        return ResponseEntity.ok(nuovoOrdine);
    }

    //endpoint protetto: ottiene lo sotrico degli ordini dell'utente loggato
    @GetMapping
    public List<Ordine> getOrdiniUtente(Authentication authentication) {
        String utenteEmail = authentication.getName();
        return ordineService.getOrdiniPerEmail(utenteEmail);
    }
    //endpoint protetto (per gestore): ottiene tutti gli ordini nel sistema
    @GetMapping("/tutti")
    public List<Ordine> getTuttiGliOrdini() {
        return ordineService.getTuttiGliOrdini();
    }
}
