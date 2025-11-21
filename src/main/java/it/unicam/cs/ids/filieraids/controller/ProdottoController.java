package it.unicam.cs.ids.filieraids.controller;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import it.unicam.cs.ids.filieraids.dto.request.ProdottoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.ProdottoRispostaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api/prodotti")
public class ProdottoController {
    private final ProdottoService prodottoService;
    private final DTOMapper mapper;

    public ProdottoController(ProdottoService prodottoService, DTOMapper mapper) {
        this.prodottoService = prodottoService;
        this.mapper = mapper;
    }

    //endpoint pubblico: ottiene tutti i prodotti approvati e visibili (quindi tutto il catalogo)
    @GetMapping("/visibili")
    public ResponseEntity<List<ProdottoRispostaDTO>> getProdottiVisibili() {
        List<Prodotto> prodotti = prodottoService.getProdottiVisibili();

        //converte lista di entità in lista di dto usando uno stream
        List<ProdottoRispostaDTO> dtoResponse = prodotti.stream()
                .map(mapper::toProdottoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }

    //endpoint pubblico: ottiene tutti i prodotti approvati e visibili
    @GetMapping("/{id}")
    public ResponseEntity<ProdottoRispostaDTO> getProdottoById(@PathVariable Long id){
        Prodotto p = prodottoService.getProdottoById(id);
        if(p == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toProdottoDTO(p));  //ritorna le entità convertite in DTO
    }


     //endpoint protetto per un Venditore per vedere tutti i suoi prodotti,
     //inclusi quelli in stato ATTESA.
     @GetMapping("/miei")
     @PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
     public ResponseEntity<List<ProdottoRispostaDTO>> getMieiProdotti(Authentication authentication) {
         String venditoreEmail = authentication.getName();
         List<Prodotto> mieiProdotti = prodottoService.getProdottiPerVenditoreEmail(venditoreEmail);
         //converte lista Entità in lista di DTO
         List<ProdottoRispostaDTO> dtoResponse = mieiProdotti.stream()
                 .map(mapper::toProdottoDTO)
                 .collect(Collectors.toList());
         return ResponseEntity.ok(dtoResponse);
     }

    //endpoint protetto per venditore: crea un nuovo prodotto per l'utente loggato
    @PostMapping
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
    public ResponseEntity<ProdottoRispostaDTO> creaProdotto(@Valid @RequestBody ProdottoRichiestaDTO dto, Authentication authentication){
        //map DTO a Entity
        Prodotto prodottoDaCreare = mapper.fromProdottoDTO(dto);
        String venditoreEmail = authentication.getName();
        //chiama il servizio
        Prodotto prodottoCreato = prodottoService.creaProdottoPerVenditore(prodottoDaCreare, venditoreEmail);
        //da map Entità a Response DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toProdottoDTO(prodottoCreato));
    }

    //endpoint protetto per modificare prodotto
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
    public ResponseEntity<ProdottoRispostaDTO> modificaProdotto(@PathVariable Long id,
                                                                @Valid @RequestBody ProdottoRichiestaDTO dto,
                                                                Authentication authentication) {
        //da dto a entita (usiamo i dati del DTO per aggiornare l'entità)
        Prodotto datiAggiornati = mapper.fromProdottoDTO(dto);
        String venditoreEmail = authentication.getName();

        //chiamo il servizio
        Prodotto prodottoAggiornato = prodottoService.modificaProdotto(id, datiAggiornati, venditoreEmail);

        //da map entity a Response dto
        return ResponseEntity.ok(mapper.toProdottoDTO(prodottoAggiornato));
    }

    //endpoint protetto per eliminare prodotto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
    public ResponseEntity<String> eliminaProdotto(@PathVariable Long id,
                                                  Authentication authentication) {
        String venditoreEmail = authentication.getName();
        prodottoService.eliminaProdotto(id, venditoreEmail);
        return ResponseEntity.ok("Prodotto eliminato con successo.");
    }

}
