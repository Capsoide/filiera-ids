package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.response.RichiestaRuoloRispostaDTO;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.model.RichiestaRuolo;
import it.unicam.cs.ids.filieraids.service.GestoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST per la gestione delle operazioni riservate al ruolo di 'GESTORE'.
 *
 * Questa classe gestisce principalmente il flusso di approvazione o rifiuto delle richieste di ruolo
 * pendenti. Fornisce un punto di accesso unificato per visualizzare tutte le richieste in attesa
 * (sia per nuovi venditori che per promozioni dello staff) e endpoint specifici per approvarle o rifiutarle tramite ID.
 * L'accesso a tutti gli endpoint di questa classe è protetto e richiede che l'utente autenticato abbia il ruolo 'GESTORE'.
 * </p>
 */
@RestController
@RequestMapping("/api/gestore")
@PreAuthorize("hasRole('GESTORE')")
public class GestoreController {

    private final GestoreService gestoreService;
    private final DTOMapper mapper;

    /**
     * Costruttore con iniezione delle dipendenze necessarie.
     *
     * @param gestoreService Il service che contiene la logica di business per le operazioni del gestore.
     * @param mapper         Il mapper utilizzato per convertire le entità interne in DTO di risposta.
     */
    public GestoreController(GestoreService gestoreService, DTOMapper mapper) {
        this.gestoreService = gestoreService;
        this.mapper = mapper;
    }

    /**
     * Endpoint (GET) per ottenere la lista unificata di tutte le richieste di ruolo che sono attualmente in stato di attesa.
     * <p>
     * Questo metodo recupera le entità {@link RichiestaRuolo} dal service e le converte in una lista di
     * {@link it.unicam.cs.ids.filieraids.dto.response.RichiestaRuoloRispostaDTO} prima di restituirle al client.
     *
     * @return Una {@link ResponseEntity} contenente la lista dei DTO delle richieste in attesa e lo stato HTTP 200 (OK).
     */
    @GetMapping("/richieste-in-attesa")
    public ResponseEntity<List<RichiestaRuoloRispostaDTO>> getTutteRichiesteInAttesa() {
        // 1. Ottieni la lista unificata dal service
        List<RichiestaRuolo> richieste = gestoreService.getRichiesteInAttesa();

        // 2. Converti in DTO
        List<RichiestaRuoloRispostaDTO> dtos = richieste.stream()
                .map(mapper::toRichiestaRuoloDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint (POST) per approvare una specifica richiesta di ruolo.
     * L'approvazione comporta l'assegnazione dei ruoli richiesti all'utente associato,
     * la sua abilitazione al login e l'aggiornamento dello stato della richiesta ad APPROVATO.
     *
     * @param idRichiesta L'ID univoco della richiesta di ruolo da approvare, passato come variabile nel percorso.
     * @return Una {@link ResponseEntity} contenente un messaggio testuale di conferma e lo stato HTTP 200 (OK).
     */
    @PostMapping("/approva/{idRichiesta}")
    public ResponseEntity<String> approvaRichiesta(@PathVariable Long idRichiesta) {
        gestoreService.approvaRichiesta(idRichiesta);
        return ResponseEntity.ok("Richiesta " + idRichiesta + " approvata.");
    }

    /**
     * Endpoint (POST) per rifiutare una specifica richiesta di ruolo.
     * Il rifiuto cambia lo stato della richiesta in RIFIUTATO. L'utente associato non riceve i nuovi ruoli
     * e il suo stato di abilitazione non viene modificato.
     *
     * @param idRichiesta L'ID univoco della richiesta di ruolo da rifiutare, passato come variabile nel percorso.
     * @return Una {@link ResponseEntity} contenente un messaggio testuale di conferma del rifiuto e lo stato HTTP 200 (OK).
     */
    @PostMapping("/rifiuta/{idRichiesta}")
    public ResponseEntity<String> rifiutaRichiesta(@PathVariable Long idRichiesta) {
        gestoreService.rifiutaRichiesta(idRichiesta);
        return ResponseEntity.ok("Richiesta " + idRichiesta + " rifiutata.");
    }
}