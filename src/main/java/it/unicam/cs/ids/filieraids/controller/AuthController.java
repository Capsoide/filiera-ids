package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Utente;
import it.unicam.cs.ids.filieraids.model.Venditore;
import it.unicam.cs.ids.filieraids.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registra/acquirente")
    public ResponseEntity<Utente> registraAcquirente(@RequestBody Utente utente) {
        Utente utenteSalvato = authService.registraAcquirente(utente);

        //risposta 201 Created (standard REST per la creazione)
        return ResponseEntity.status(HttpStatus.CREATED).body(utenteSalvato);
    }

    @PostMapping("/registra/venditore")
    public ResponseEntity<Attore> registraVenditore(@RequestBody Venditore venditore) {
        Attore venditoreSalvato = authService.registraVenditore(venditore);

        //risposta 202 Accepted (standard REST per "richiesta accettata, ma non ancora completata")
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(venditoreSalvato);
    }
}