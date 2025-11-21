package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.response.CarrelloRispostaDTO;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;

@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {

    private final CarrelloService carrelloService;
    private final DTOMapper mapper;

    public CarrelloController(CarrelloService carrelloService, DTOMapper mapper) {
        this.carrelloService = carrelloService;
        this.mapper = mapper;
    }

    //endpoint protetto: ottiene il carrello dell'utente loggato
    @GetMapping
    public ResponseEntity<CarrelloRispostaDTO> getCarrello(Authentication authentication) {
        String email = authentication.getName();
        Carrello c = carrelloService.getCarrelloByEmail(email);
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }

    //endpoint protetto: aggiunge prodotto al carrello dell''utente loggato
    @PostMapping("/aggiungi")
    public ResponseEntity<CarrelloRispostaDTO> aggiungi(Authentication authentication,
                                                        @RequestParam Long prodottoId,
                                                        @RequestParam int quantita) {
        String email = authentication.getName();
        Carrello c = carrelloService.aggiungiAlCarrelloByEmail(email, prodottoId, quantita);
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }


    //endpoint protetto: diminuisce la quantità di un prodotto
    @PostMapping("/diminuisci")
    public ResponseEntity<CarrelloRispostaDTO> diminuisci(Authentication authentication,
                                                          @RequestParam Long prodottoId,
                                                          @RequestParam int quantita) {
        String email = authentication.getName();
        Carrello c = carrelloService.diminuisciDalCarrelloByEmail(email, prodottoId, quantita);
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }

    //endpoint protetto: svuota carrello del loggato
    @DeleteMapping("/svuota")
    public ResponseEntity<CarrelloRispostaDTO> svuota(Authentication authentication) {
        String email = authentication.getName();
        Carrello c = carrelloService.svuotaCarrelloByEmail(email);
        return ResponseEntity.ok(mapper.toCarrelloDTO(c));
    }
}
