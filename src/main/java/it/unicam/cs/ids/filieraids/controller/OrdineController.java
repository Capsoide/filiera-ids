package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.response.EventoRispostaDTO;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.dto.response.OrdineRispostaDTO;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordini")
public class OrdineController {
    private final OrdineService ordineService;
    private final DTOMapper mapper;

    public OrdineController(OrdineService ordineService, DTOMapper mapper) {
        this.ordineService = ordineService;
        this.mapper = mapper;
    }

    //endpoint protetto: crea ordine per utente loggato
    @PostMapping
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<OrdineRispostaDTO> creaOrdine(Authentication authentication) {
        String utenteEmail = authentication.getName();
        Ordine nuovoOrdine = ordineService.creaOrdinePerEmail(utenteEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toOrdineDTO(nuovoOrdine));
    }

    //endpoint protetto: ottiene lo sotrico degli ordini dell'utente loggato
    @GetMapping
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<List<OrdineRispostaDTO>> getMieiOrdini(Authentication authentication) {
        String utenteEmail = authentication.getName();
        List<Ordine> ordini = ordineService.getOrdiniPerEmail(utenteEmail);
        List<OrdineRispostaDTO> dtoResponse = ordini.stream()
                .map(mapper::toOrdineDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }
    //endpoint protetto (per gestore): ottiene tutti gli ordini nel sistema
    @GetMapping("/tutti")
    @PreAuthorize("hasRole('GESTORE')")
    public ResponseEntity<List<OrdineRispostaDTO>> getTuttiGliOrdini() {
        List<Ordine> ordini = ordineService.getTuttiGliOrdini();
        List<OrdineRispostaDTO> dtoResponse = ordini.stream()
                .map(mapper::toOrdineDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/venditore")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
    public ResponseEntity<List<OrdineRispostaDTO>> getOrdiniVenditore(Authentication authentication) {
        String venditoreEmail = authentication.getName();
        List<Ordine> ordini = ordineService.getOrdiniPerVenditore(venditoreEmail);
        List<OrdineRispostaDTO> dtoResponse = ordini.stream()
                .map(mapper::toOrdineDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }
}
