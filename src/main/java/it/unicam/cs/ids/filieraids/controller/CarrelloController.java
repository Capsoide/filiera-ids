package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {

    private final CarrelloService carrelloService;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;

    public CarrelloController(CarrelloService carrelloService,
                              UtenteRepository utenteRepository,
                              ProdottoRepository prodottoRepository) {
        this.carrelloService = carrelloService;
        this.utenteRepository = utenteRepository;
        this.prodottoRepository = prodottoRepository;
    }

    //trovare l'utente loggato tramite username dall'oggetto Authentication
    private Utente getUtenteFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return utenteRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + userEmail));
    }

    //endpoint protetto: ottiene il carrello dell'utente loggato
    @GetMapping
    public Carrello getCarrello(Authentication authentication) {
        Utente utente = getUtenteFromAuthentication(authentication);
        return utente.getCarrello();
    }

    //endpoint protetto: aggiunge prodotto al carrello dell''utente loggato
    @PostMapping("/aggiungi")
    public Carrello aggiungiAlCarrello(Authentication authentication,
                                       @RequestParam Long prodottoId,
                                       @RequestParam int quantita) {
        Utente utente = getUtenteFromAuthentication(authentication);
        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

        carrelloService.aggiungiAlCarrello(utente.getCarrello(), prodotto, quantita);
        return utente.getCarrello();
    }


    //endpoint protetto: diminuisce la quantità di un prodotto
    @PostMapping("/diminuisci")
    public Carrello diminuisciDalCarrello(Authentication  authentication,
                                          @RequestParam Long prodottoId,
                                          @RequestParam int quantita) {
        Utente utente = getUtenteFromAuthentication(authentication);
        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

        carrelloService.diminuisciQuantita(utente.getCarrello(), prodotto, quantita);
        return utente.getCarrello();
    }

    //endpoint protetto: svuota carrello del loggato
    @DeleteMapping("/svuota")
    public Carrello svuotaCarrello(Authentication authentication) {
        Utente utente = getUtenteFromAuthentication(authentication);
        carrelloService.svuotaCarrello(utente.getCarrello());
        return utente.getCarrello();
    }
}
