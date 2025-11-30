package it.unicam.cs.ids.filieraids.controller;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import it.unicam.cs.ids.filieraids.dto.request.ProdottoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.ProdottoRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.PacchettoRispostaDTO;
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
    private final PacchettoService pacchettoService;
    private final ProdottoService prodottoService;
    private final DTOMapper mapper;

    public ProdottoController(ProdottoService prodottoService, DTOMapper mapper, PacchettoService pacchettoService) {
        this.prodottoService = prodottoService;
        this.pacchettoService = pacchettoService;
        this.mapper = mapper;
    }
    @GetMapping("/catalogo")
    public ResponseEntity<Map<String, Object>> getCatalogoCompleto() {

        // Recupera prodotti
        List<ProdottoRispostaDTO> prodotti = prodottoService.getProdottiVisibili().stream()
                .map(mapper::toProdottoDTO)
                .collect(Collectors.toList());

        // Recupera pacchetti
        List<PacchettoRispostaDTO> pacchetti = pacchettoService.getPacchettiVisibili().stream()
                .map(mapper::toPacchettoDTO)
                .toList();

        // Metti tutto in una mappa
        Map<String, Object> risposta = new HashMap<>();
        risposta.put("prodotti", prodotti);
        risposta.put("pacchetti", pacchetti);

        return ResponseEntity.ok(risposta);
    }

    /**
     * Endpoint pubblico che permette di ottenere tutti i prodotti approvati e visibili.
     *
     * @return  la lista dei prodotti approvati e visibili.
     */
    @GetMapping("/visibili")
    public ResponseEntity<List<ProdottoRispostaDTO>> getProdottiVisibili() {
        List<Prodotto> prodotti = prodottoService.getProdottiVisibili();

        //converte lista di entità in lista di dto usando uno stream
        List<ProdottoRispostaDTO> dtoResponse = prodotti.stream()
                .map(mapper::toProdottoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }

    /**
     * Endpoint pubblico che permette di ottenere un prodotto approvato tramite l'id.
     *
     * @param id    l'id del prodotto da ottenere
     * @return      il prodotto corrispondente all'id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdottoRispostaDTO> getProdottoById(@PathVariable Long id){
        Prodotto p = prodottoService.getProdottoById(id);
        if(p == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toProdottoDTO(p));  //ritorna le entità convertite in DTO
    }

    /**
     * Endpoint protetto che permette al venditore loggato di ottenere tutti i propri prodotti,
     * sia approvati sia in attesa.
     *
     * @param authentication    rappresenta il venditore loggato
     * @return                  la lista dei prodotti del venditore loggato
     */
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

    /**
     * Endpoint protetto che permette al venditore loggato di creare un nuovo prodotto.
     *
     * @param dto               i dati per il prodotto da creare, in formato dto
     * @param authentication    rappresenta il venditore loggato
     * @return                  il prodotto creato
     */
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

    /**
     * Endpoint protetto che permette al venditore loggato di modificare un prodotto pubblicato.
     *
     * @param id                l'id del prodotto da modificare
     * @param dto               i dati da modificare
     * @param authentication    rappresenta il venditore loggato
     * @return                  il prodotto modificato
     */
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

    /**
     * Endpoint protetto che permette al venditore loggato di eliminare un prodotto pubblicato.
     *
     * @param id                l'id del prodotto da modificare
     * @param authentication    rappresenta il venditore loggato
     * @return                  messaggio di conferma dell'eliminazione
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
    public ResponseEntity<String> eliminaProdotto(@PathVariable Long id,
                                                  Authentication authentication) {
        String venditoreEmail = authentication.getName();
        prodottoService.eliminaProdotto(id, venditoreEmail);
        return ResponseEntity.ok("Prodotto eliminato con successo.");
    }

}
