package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.Invito;
import it.unicam.cs.ids.filieraids.service.VenditoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venditori")
@PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
public class VenditoreController {

    private final VenditoreService venditoreService;

    public VenditoreController(VenditoreService venditoreService) {
        this.venditoreService = venditoreService;
    }

    @GetMapping("/inviti")
    public List<Invito> getMieiInviti(Authentication authentication) {
        return venditoreService.getMieiInviti(authentication.getName());
    }

    @PostMapping("/inviti/{invitoId}/accetta")
    public ResponseEntity<String> accettaInvito(@PathVariable Long invitoId, Authentication authentication) {
        venditoreService.gestisciInvito(invitoId, true, authentication.getName());
        return ResponseEntity.ok("Invito accettato.");
    }

    @PostMapping("/inviti/{invitoId}/rifiuta")
    public ResponseEntity<String> rifiutaInvito(@PathVariable Long invitoId, Authentication authentication) {
        venditoreService.gestisciInvito(invitoId, false, authentication.getName());
        return ResponseEntity.ok("Invito rifiutato.");
    }
}