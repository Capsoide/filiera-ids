package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.request.ValutazioneRichiestaDTO;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.service.EventoService;
import it.unicam.cs.ids.filieraids.service.PacchettoService;
import it.unicam.cs.ids.filieraids.service.ProdottoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final PacchettoService pacchettoService;
    private final DTOMapper mapper;

    public CuratoreController(ProdottoService prodottoService,
                              EventoService eventoService,
                              PacchettoService pacchettoService,
                              DTOMapper mapper) {
        this.prodottoService = prodottoService;
        this.eventoService = eventoService;
        this.pacchettoService = pacchettoService;
        this.mapper = mapper;
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

        //PRODOTTI
        contenuti.addAll(prodottoService.getProdottiInAttesa().stream()
                .map(mapper::toProdottoDTO)
                .collect(Collectors.toList()));

        //EVENTI
        contenuti.addAll(eventoService.getEventiInAttesa().stream()
                .map(mapper::toEventoDTO)
                .collect(Collectors.toList()));

        //PACCHETTI
        contenuti.addAll(pacchettoService.getPacchettiInAttesa().stream()
                .map(mapper::toPacchettoDTO)
                .collect(Collectors.toList()));

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

        String note = (dto != null && dto.motivazione() != null) ? dto.motivazione() : "Approvato via API";

        try {
            //PRODOTTO
            prodottoService.approvaProdotto(contenutoId, note);
            return ResponseEntity.ok("Prodotto " + contenutoId + " approvato.");

        } catch (IllegalStateException eStato) {
            throw eStato;
        } catch (RuntimeException eProdottoNotFound) {
            try {
                //EVENTO
                eventoService.approvaEvento(contenutoId, note);
                return ResponseEntity.ok("Evento " + contenutoId + " approvato.");

            } catch (RuntimeException eEventoNotFound) {
                //PACCHETTO
                pacchettoService.approvaPacchetto(contenutoId, note);
                return ResponseEntity.ok("Pacchetto " + contenutoId + " approvato.");
            }
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

        if (!"RIFIUTA".equalsIgnoreCase(dto.azione())) {
            return ResponseEntity.badRequest().body("L'azione deve essere RIFIUTA per questo endpoint");
        }

        String motivo = dto.motivazione();

        try {
            //PRODOTTO
            prodottoService.rifiutaProdotto(contenutoId, motivo);
            return ResponseEntity.ok("Prodotto " + contenutoId + " rifiutato.");

        } catch (IllegalStateException eStato) {
            throw eStato;
        } catch (RuntimeException eProdottoNotFound) {
            try {
                //EVENTO
                eventoService.rifiutaEvento(contenutoId, motivo);
                return ResponseEntity.ok("Evento " + contenutoId + " rifiutato.");

            } catch (RuntimeException eEventoNotFound) {
                //PACCHETTO
                pacchettoService.rifiutaPacchetto(contenutoId, motivo);
                return ResponseEntity.ok("Pacchetto " + contenutoId + " rifiutato.");
            }
        }
    }
}