package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import it.unicam.cs.ids.filieraids.service.*;
import it.unicam.cs.ids.filieraids.service.ProdottoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@RestController
@RequestMapping("/api/curatore")
public class CuratoreController {
    private final CuratoreService curatoreService;
    private final AttoreRepository attoreRepository;
    private final ContenutoRepository contenutoRepository;

    public CuratoreController(CuratoreService curatoreService,
                              AttoreRepository attoreRepository,
                              ContenutoRepository contenutoRepository) {
        this.curatoreService = curatoreService;
        this.attoreRepository = attoreRepository;
        this.contenutoRepository = contenutoRepository;
    }

    //helper per trovare curatore loggato
    private Attore getCuratoreFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return attoreRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + userEmail));
    }

    //endpoint protetto per curatore: approvazione contenuto
    @PostMapping("/approva/{contenutoId}")
    public ResponseEntity<String> approvaContenuto(@PathVariable Long contenutoId, Authentication authentication) {

        Attore curatore = getCuratoreFromAuthentication(authentication);
        Contenuto contenuto = contenutoRepository.findById(contenutoId)
                .orElseThrow(() -> new RuntimeException("Contenuto non trovato"));

        curatoreService.approvaContenuto(curatore, contenuto, "Approvato via API");
        return ResponseEntity.ok("Contenuto " + contenutoId + " approvato.");
    }

    //endpoint protetto per curatore: rifiuta contenuto
    @PostMapping("/rifiuta/{contenutoId}")
    public ResponseEntity<String> rifiutaContenuto(@PathVariable Long contenutoId,
                                                   Authentication authentication,
                                                   @RequestBody String motivo) {
        Attore curatore = getCuratoreFromAuthentication(authentication);
        Contenuto contenuto = contenutoRepository.findById(contenutoId)
                .orElseThrow(() -> new RuntimeException("Contenuto non trovato"));
        //chiamata al service
        curatoreService.rifiutaContenuto(curatore, contenuto, motivo);
        return ResponseEntity.ok("Contenuto " + contenutoId + " rifiutato.");
    }
}
