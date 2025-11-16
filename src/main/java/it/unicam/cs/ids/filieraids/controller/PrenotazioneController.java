package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.Prenotazione;
import it.unicam.cs.ids.filieraids.service.PrenotazioneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/prenotazioni")
@PreAuthorize("hasRole('ACQUIRENTE')")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @PostMapping("/eventi/{eventoId}")
    public ResponseEntity<Prenotazione> creaPrenotazione(
            @PathVariable Long eventoId,
            @RequestParam int numeroPosti,
            Authentication authentication) {

        String utenteEmail = authentication.getName();
        Prenotazione prenotazione = prenotazioneService.creaPrenotazione(eventoId, numeroPosti, utenteEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(prenotazione);
    }

    /**
     * Endpoint per l'Acquirente per vedere le sue prenotazioni.
     */
    @GetMapping("/miei")
    public List<Prenotazione> getMiePrenotazioni(Authentication authentication) {
        String utenteEmail = authentication.getName();
        return prenotazioneService.getMiePrenotazioni(utenteEmail);
    }


    //elimina prenotazione
    @DeleteMapping("/{prenotazioneId}")
    public ResponseEntity<String> annullaPrenotazione(
            @PathVariable Long prenotazioneId,
            Authentication authentication) {

        String utenteEmail = authentication.getName();
        prenotazioneService.annullaPrenotazione(prenotazioneId, utenteEmail);
        return ResponseEntity.ok("Prenotazione " + prenotazioneId + " annullata con successo.");
    }
}
