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

    /**
     * Endpoint protetto che permette all'acquirente loggato di effettuare una prenotazione
     * per un determinato evento.
     *
     * @param eventoId          l'id dell'evento per cui si vuole effettuare la prenotazione
     * @param numeroPosti       numero di posti richiesti pnella prenotazione
     * @param authentication    rappresenta l'acquirente loggato
     * @return                  la prenotazione effettuata
     */
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
     * Endpoint protetto che permette all'acquirente loggato di ottenere le sue prenotazioni.
     *
     * @param authentication    rappresenta l'acquirente loggato
     * @return                  la lista delle prenotazioni
     */
    @GetMapping("/miei")
    public List<Prenotazione> getMiePrenotazioni(Authentication authentication) {
        String utenteEmail = authentication.getName();
        return prenotazioneService.getMiePrenotazioni(utenteEmail);
    }


    /**
     * Endpoint protetto che permette all'acquirente loggato di annullare una determinata prenotazione.
     *
     * @param prenotazioneId    l'ide della prenotazione da annullare
     * @param authentication    rappresenta l'acquirente loggato
     * @return                  messaggio di conferma dell'annullamento della prenotazione
     */
    @DeleteMapping("/{prenotazioneId}")
    public ResponseEntity<String> annullaPrenotazione(
            @PathVariable Long prenotazioneId,
            Authentication authentication) {

        String utenteEmail = authentication.getName();
        prenotazioneService.annullaPrenotazione(prenotazioneId, utenteEmail);
        return ResponseEntity.ok("Prenotazione " + prenotazioneId + " annullata con successo.");
    }
}
