package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.request.*;
import it.unicam.cs.ids.filieraids.dto.response.*;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;

@RestController
@RequestMapping("/api/pacchetti")
public class PacchettoController {

    private final PacchettoService pacchettoService;
    private final ProdottoService prodottoService;
    private final DTOMapper mapper;

    public PacchettoController(PacchettoService pacchettoService,
                               ProdottoService prodottoService,
                               DTOMapper mapper) {
        this.pacchettoService = pacchettoService;
        this.prodottoService = prodottoService;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTORE')")
    public ResponseEntity<PacchettoRispostaDTO> creaPacchetto(
            Authentication auth,
            @Valid @RequestBody PacchettoRichiestaDTO dto) {

        String email = auth.getName();

        Pacchetto p = new Pacchetto(dto.nome(), dto.descrizione(), dto.prezzo(), null);

        Pacchetto created = pacchettoService.creaPacchetto(email, p);

        List<PacchettoItem> items = new ArrayList<>();
        for (PacchettoItemRichiestaDTO r : dto.items()) {
            Prodotto prod = prodottoService.getProdottoById(r.prodottoId());
            items.add(new PacchettoItem(prod, r.quantita()));
        }

        pacchettoService.aggiungiItems(created.getId(), items);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPacchettoDTO(created));
    }

    @GetMapping("/visibili")
    public ResponseEntity<List<PacchettoRispostaDTO>> getVisibili() {
        return ResponseEntity.ok(
                pacchettoService.getPacchettiVisibili()
                        .stream()
                        .map(mapper::toPacchettoDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacchettoRispostaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toPacchettoDTO(pacchettoService.getPacchettoById(id)));
    }

    @GetMapping("/miei")
    @PreAuthorize("hasAnyRole('DISTRIBUTORE')")
    public ResponseEntity<List<PacchettoRispostaDTO>> miei(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(
                pacchettoService.getMieiPacchetti(email)
                        .stream()
                        .map(mapper::toPacchettoDTO)
                        .toList()
        );
    }
}
