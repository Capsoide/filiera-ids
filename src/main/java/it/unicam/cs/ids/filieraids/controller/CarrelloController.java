package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.request.CarrelloRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.CarrelloRispostaDTO;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;

@RestController
@RequestMapping("/api/carrello")
@PreAuthorize("hasRole('ACQUIRENTE')")
public class CarrelloController {

    private final CarrelloService carrelloService;
    private final DTOMapper mapper;

    public CarrelloController(CarrelloService carrelloService, DTOMapper mapper) {
        this.carrelloService = carrelloService;
        this.mapper = mapper;
    }

    /**
     * Endpoint protetto che permette di ottenere il carrello per l'utente loggato.
     *
     * @param authentication    rappresenta l'utente attualmente loggato
     * @return                  carrello dell'utente loggato, in formato DTO
     */
    @GetMapping
    public ResponseEntity<CarrelloRispostaDTO> getCarrello(Authentication authentication) {
        String email = authentication.getName();
        Carrello c = carrelloService.getCarrelloByEmail(email);
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }

    /**
     * Endpoint protetto che permette all'utente loggato di aggiungere un prodotto al carrello.
     *
     * @param authentication    rappresenta l'utente attualmente loggato
     * @param dto               dati del prodotto da aggiungere al carrello, in formato DTO
     * @return                  carrello aggiornato dell'utente loggato, in formato DTO
     */
    @PostMapping("/aggiungi")
    public ResponseEntity<CarrelloRispostaDTO> aggiungi(Authentication authentication,
                                                        @Valid @RequestBody CarrelloRichiestaDTO dto) {
        String email = authentication.getName();
        Carrello c = carrelloService.aggiungiAlCarrelloByEmail(email, dto.prodottoId(), dto.quantita());
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }

    /**
     * Endpoint protetto che permette all'utente loggatto di diminuire la quantità
     * di un prodotto nel proprio carrello.
     *
     * @param authentication    rappresneta lì'utente attualmente loggato
     * @param dto               pdati del prodotto da modificare nel carrello, in formato DTO
     * @return                  carrello aggiornato dell'utente loggato, in formato DTO
     */
    @PostMapping("/diminuisci")
    public ResponseEntity<CarrelloRispostaDTO> diminuisci(Authentication authentication,
                                                          @Valid @RequestBody CarrelloRichiestaDTO dto) {
        String email = authentication.getName();
        Carrello c = carrelloService.diminuisciDalCarrelloByEmail(email, dto.prodottoId(), dto.quantita());
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }

    /**
     * Endpoint protetto che permette all'utente loggato di svuotare il carrello.
     *
     * @param authentication    rappresenta l'utente attualmente loggato
     * @return                  carrello svuotato dell'utente loggato, in formato DTO
     */
    @DeleteMapping("/svuota")
    public ResponseEntity<CarrelloRispostaDTO> svuota(Authentication authentication) {
        String email = authentication.getName();
        Carrello c = carrelloService.svuotaCarrelloByEmail(email);
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }
}
