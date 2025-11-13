package it.unicam.cs.ids.filieraids.controller;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;

@RestController
@RequestMapping("/api/prodotti")
public class ProdottoController {
    private final ProdottoService prodottoService;

    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    //endpoint pubblico: ottiene tutti i prodotti approvati e visibili (quindi tutto il catalogo)
    @GetMapping("/visibili")
    public List<Prodotto> getProdottiVisibili() {
        return prodottoService.getProdottiVisibili();
    }

    //endpoint pubblico: ottiene tutti i prodotti approvati e visibili
    @GetMapping("/{id}")
    public ResponseEntity<Prodotto> getProdottoById(@PathVariable Long id){
        Prodotto p = prodottoService.getProdottoById(id);
        if(p == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(p);
    }


     //Endpoint protetto per un Venditore per vedere tutti i suoi prodotti,
     //inclusi quelli in stato ATTESA.
    @GetMapping("/miei")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
    public List<Prodotto> getMieiProdotti(Authentication authentication) {
        String venditoreEmail = authentication.getName();
        return prodottoService.getProdottiPerVenditoreEmail(venditoreEmail);
    }

    //endpoint protetto per venditore: crea un nuovo prodotto per l'utente loggato
    @PostMapping
    public Prodotto creaProdotto(@RequestBody Prodotto prodotto, Authentication authentication){
        //prendo la mail dell'utente loggato
        String venditoreEmail = authentication.getName();

        return prodottoService.creaProdottoPerVenditore(prodotto, venditoreEmail);
    }

}
