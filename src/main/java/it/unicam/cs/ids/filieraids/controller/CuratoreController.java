package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@RestController
@RequestMapping("/api/curatore")
@PreAuthorize("hasRole('CURATORE')")
public class CuratoreController {
    private final CuratoreService curatoreService;

    public CuratoreController(CuratoreService curatoreService) {
        this.curatoreService = curatoreService;
    }

    /**
     * Endpoint protetto che permette al curatore di ottenere tutti i contenuti in stato di attesa.
     *
     * @return  la lista dei contenuti in stato di attesa
     */
    @GetMapping("/da-approvare")
    public List<Contenuto> getContenutiInAttesa() {
        return curatoreService.getContenutiInAttesa();
    }

    /**
     * Endpoint protetto che permette al curatore loggato di approvare un contenuto.
     *
     * @param contenutoId       l'id del contenuto da approvare
     * @param authentication    rappresenta il curatore loggato
     * @return                  messaggio di conferma dell'approvazione del contenuto
     */
    @PostMapping("/approva/{contenutoId}")
    public ResponseEntity<String> approvaContenuto(@PathVariable Long contenutoId, Authentication authentication) {

        String curatoreEmail = authentication.getName();
        curatoreService.approvaContenuto(contenutoId, curatoreEmail, "Approvato via API");

        return ResponseEntity.ok("Contenuto " + contenutoId + " approvato.");
    }

    /**
     * Endpoint protetto che permette al curatore loggato di rifiutare un contenuto in stato di attesa.
     *
     * @param contenutoId       l'id del contenuto da rifiutare
     * @param authentication    rappresenta il curatore loggato
     * @param motivo            motivo del rifiuto del contenuto
     * @return                  messaggio di conferma del rifuto del contenuto
     */
    @PostMapping("/rifiuta/{contenutoId}")
    public ResponseEntity<String> rifiutaContenuto(@PathVariable Long contenutoId,
                                                   Authentication authentication,
                                                   @RequestBody String motivo) {

        String curatoreEmail = authentication.getName();
        curatoreService.rifiutaContenuto(contenutoId, curatoreEmail, motivo);

        return ResponseEntity.ok("Contenuto " + contenutoId + " rifiutato.");
    }
}
