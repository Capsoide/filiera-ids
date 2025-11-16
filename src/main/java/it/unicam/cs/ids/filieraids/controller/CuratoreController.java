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

    @GetMapping("/da-approvare")
    public List<Contenuto> getContenutiInAttesa() {
        return curatoreService.getContenutiInAttesa();
    }

    //endpoint protetto per curatore: approvazione contenuto
    @PostMapping("/approva/{contenutoId}")
    public ResponseEntity<String> approvaContenuto(@PathVariable Long contenutoId, Authentication authentication) {

        String curatoreEmail = authentication.getName();
        curatoreService.approvaContenuto(contenutoId, curatoreEmail, "Approvato via API");

        return ResponseEntity.ok("Contenuto " + contenutoId + " approvato.");
    }

    //endpoint protetto per curatore: rifiuta contenuto
    @PostMapping("/rifiuta/{contenutoId}")
    public ResponseEntity<String> rifiutaContenuto(@PathVariable Long contenutoId,
                                                   Authentication authentication,
                                                   @RequestBody String motivo) {

        String curatoreEmail = authentication.getName();
        curatoreService.rifiutaContenuto(contenutoId, curatoreEmail, motivo);

        return ResponseEntity.ok("Contenuto " + contenutoId + " rifiutato.");
    }
}
