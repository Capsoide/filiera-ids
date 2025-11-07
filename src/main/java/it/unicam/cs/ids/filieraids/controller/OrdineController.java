package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/api/ordini")
public class OrdineController {
    private final OrdineService ordineService;
    private final UtenteRepository utenteRepository;

    public OrdineController(OrdineService ordineService, UtenteRepository utenteRepository) {
        this.ordineService = ordineService;
        this.utenteRepository = utenteRepository;
    }

    //helper per trovare utente loggato da mail
    private Utente getUtenteFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return utenteRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
    }

    //endpoint protetto: crea ordine per utente loggato
    @PostMapping
    public ResponseEntity<Ordine> creaOrdine(Authentication authentication) {
        Utente utente = getUtenteFromAuthentication(authentication);

        if (utente.getIndirizzi().isEmpty()) {
            throw new RuntimeException("Impossibile creare ordine: l'utente non ha indirizzi salvati.");
        }
        Indirizzo indirizzo = utente.getIndirizzi().get(0); // Usa il primo indirizzo
        Pagamento pagamento = new Pagamento("Visa", "1234-Simulato", utente.getNomeCompleto());

        try {
            Ordine nuovoOrdine = ordineService.creaOrdine(utente, utente.getCarrello(), pagamento, indirizzo);
            return ResponseEntity.ok(nuovoOrdine);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(null);
        }
    }

    //endpoint protetto: ottiene lo sotrico degli ordini dell'utente loggato
    @GetMapping
    public List<Ordine> getOrdiniUtente(Authentication authentication) {
        Utente utente = getUtenteFromAuthentication(authentication);
        return ordineService.getOrdiniByUtente(utente);
    }
    //endpoint protetto (per gestore): ottiene tutti gli ordini nel sistema
    @GetMapping("/tutti")
    public List<Ordine> getTuttiGliOrdini() {
        return ordineService.getTuttiGliOrdini();
    }
}
