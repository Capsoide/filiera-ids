package it.unicam.cs.ids.filieraids.controller;

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

    public CarrelloController(CarrelloService carrelloService) {
        this.carrelloService = carrelloService;
    }

    //endpoint protetto: ottiene il carrello dell'utente loggato
    @GetMapping
    public Carrello getCarrello(Authentication authentication) {
        String userEmail = authentication.getName();
        return carrelloService.getCarrelloByEmail(userEmail);
    }

    //endpoint protetto: aggiunge prodotto al carrello dell''utente loggato
    @PostMapping("/aggiungi")
    public Carrello aggiungiAlCarrello(Authentication authentication,
                                       @RequestParam Long prodottoId,
                                       @RequestParam int quantita) {
        String userEmail = authentication.getName();
        return carrelloService.aggiungiAlCarrelloByEmail(userEmail, prodottoId, quantita);
    }


    //endpoint protetto: diminuisce la quantità di un prodotto
    @PostMapping("/diminuisci")
    public Carrello diminuisciDalCarrello(Authentication  authentication,
                                          @RequestParam Long prodottoId,
                                          @RequestParam int quantita) {
        String userEmail = authentication.getName();
        return carrelloService.diminuisciDalCarrelloByEmail(userEmail, prodottoId, quantita);
    }

    //endpoint protetto: svuota carrello del loggato
    @DeleteMapping("/svuota")
    public Carrello svuotaCarrello(Authentication authentication) {
        String userEmail = authentication.getName();
        return carrelloService.svuotaCarrelloByEmail(userEmail);
    }
}
