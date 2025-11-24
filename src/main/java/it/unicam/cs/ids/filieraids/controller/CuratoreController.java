package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.request.ValutazioneRichiestaDTO;
import it.unicam.cs.ids.filieraids.service.EventoService;
import it.unicam.cs.ids.filieraids.service.ProdottoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Il CuratoreController gestisce le richieste relative alle operazioni del curatore,
 * come l'approvazione e il rifiuto dei contenuti (prodotti ed eventi).
 * L'accesso a questo controller è limitato agli utenti con ruolo 'CURATORE'.
 */
@RestController
@RequestMapping("/api/curatore")
@PreAuthorize("hasRole('CURATORE')")
public class CuratoreController {

    private final ProdottoService prodottoService;
    private final EventoService eventoService;

    /**
     * Costruttore del CuratoreController.
     * Inietta i service specifici per prodotti ed eventi.
     */
    public CuratoreController(ProdottoService prodottoService, EventoService eventoService) {
        this.prodottoService = prodottoService;
        this.eventoService = eventoService;
    }

    /**
     * Endpoint protetto che permette al curatore di ottenere tutti i contenuti (prodotti ed eventi)
     * che si trovano attualmente in stato di attesa.
     *
     * @return  Una lista mista contenente prodotti ed eventi in attesa di approvazione.
     */
    @GetMapping("/da-approvare")
    public List<Object> getContenutiInAttesa() {
        List<Object> contenuti = new ArrayList<>();
        contenuti.addAll(prodottoService.getProdottiInAttesa());
        contenuti.addAll(eventoService.getEventiInAttesa());
        return contenuti;
    }

    /**
     * Endpoint protetto che permette al curatore loggato di approvare un contenuto.
     * Il metodo tenta prima di approvare come prodotto, poi come evento.
     *
     * @param contenutoId       L'ID del contenuto da approvare.
     * @param authentication    Le informazioni di autenticazione del curatore loggato.
     * @param dto               Il DTO contenente la motivazione (opzionale) nel body.
     * @return                  Un messaggio di conferma o un errore se l'operazione non è permessa.
     */
    @PostMapping("/approva/{contenutoId}")
    public ResponseEntity<String> approvaContenuto(@PathVariable Long contenutoId,
                                                   Authentication authentication,
                                                   @RequestBody(required = false) ValutazioneRichiestaDTO dto) {

        String curatoreEmail = authentication.getName();
        String note = (dto != null && dto.motivazione() != null) ? dto.motivazione() : "Approvato via API";

        try {
            //questo metodo nel service ha il controllo che blocca se non è in ATTESA
            prodottoService.approvaProdotto(contenutoId, note);
            return ResponseEntity.ok("Prodotto " + contenutoId + " approvato.");

        } catch (IllegalStateException eStato) {
            throw eStato;
        } catch (RuntimeException eProdottoNotFound) {
            eventoService.approvaEvento(contenutoId, note);
            return ResponseEntity.ok("Evento " + contenutoId + " approvato.");
        }
    }

    /**
     * Endpoint protetto che permette al curatore loggato di rifiutare un contenuto in stato di attesa.
     * Il metodo tenta prima di rifiutare come prodotto, poi come evento.
     *
     * @param contenutoId       L'ID del contenuto da rifiutare.
     * @param authentication    Le informazioni di autenticazione del curatore loggato.
     * @param dto               Il DTO nel body, deve contenere azione="RIFIUTA" e una motivazione.
     * @return                  Un messaggio di conferma o un errore se l'operazione non è permessa.
     */
    @PostMapping("/rifiuta/{contenutoId}")
    public ResponseEntity<String> rifiutaContenuto(@PathVariable Long contenutoId,
                                                   Authentication authentication,
                                                   @Valid @RequestBody ValutazioneRichiestaDTO dto) {

        String curatoreEmail = authentication.getName();
        if (!"RIFIUTA".equalsIgnoreCase(dto.azione())) {
            return ResponseEntity.badRequest().body("L'azione deve essere RIFIUTA per questo endpoint");
        }

        String motivo = dto.motivazione();

        try {
            prodottoService.rifiutaProdotto(contenutoId, motivo);
            return ResponseEntity.ok("Prodotto " + contenutoId + " rifiutato.");

        } catch (IllegalStateException eStato) {
            throw eStato;
        } catch (RuntimeException eProdottoNotFound) {
            eventoService.rifiutaEvento(contenutoId, motivo);
            return ResponseEntity.ok("Evento " + contenutoId + " rifiutato.");
        }
    }
}