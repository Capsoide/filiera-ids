package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@RestController
@RequestMapping("/api/prodotti")
public class ProdottoController {
    private final ProdottoService prodottoService;
    private final VenditoreRepository venditoreRepository;

    public ProdottoController(ProdottoService prodottoService, VenditoreRepository venditoreRepository) {
        this.prodottoService = prodottoService;
        this.venditoreRepository = venditoreRepository;
    }

    //helper per trovare il vednitore loggato
    private Venditore getVenditoreFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return venditoreRepository.findByEmail(userEmail) //richiede findByEmail in VenditoreRepository
                .orElseThrow(() -> new UsernameNotFoundException("Venditore non trovato: " + userEmail));
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

    //endpoint protetto per venditore: crea un nuovo prodotto per l'utente loggato
    @PostMapping
    public Prodotto creaProdotto(@RequestBody Prodotto prodotto, Authentication authentication){
        //cerco il venditore dal db
        Venditore venditore = getVenditoreFromAuthentication(authentication);

        //chiamo il service
        return prodottoService.creaProdotto(
                new Date(),
                prodotto.getDescrizione(),
                prodotto.getNome(),
                prodotto.getMetodoDiColtivazione(),
                prodotto.getPrezzo(),
                venditore,
                prodotto.getCertificazioni(),
                new Date(),
                prodotto.getQuantita()
        );
    }

}
