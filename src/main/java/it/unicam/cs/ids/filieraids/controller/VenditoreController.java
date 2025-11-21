package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.Invito;
import it.unicam.cs.ids.filieraids.service.VenditoreService;
import it.unicam.cs.ids.filieraids.dto.response.InvitoRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.request.InvitoAzioneDTO;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/venditori")
@PreAuthorize("hasAnyRole('PRODUTTORE', 'DISTRIBUTORE', 'TRASFORMATORE')")
public class VenditoreController {

    private final VenditoreService venditoreService;
    private final DTOMapper mapper;

    public VenditoreController(VenditoreService venditoreService, DTOMapper mapper) {
        this.venditoreService = venditoreService;
        this.mapper = mapper;
    }

    @GetMapping("/inviti")
    public ResponseEntity<List<InvitoRispostaDTO>> getMieiInviti(Authentication authentication) {
        List<Invito> inviti = venditoreService.getMieiInviti(authentication.getName());

        List<InvitoRispostaDTO> dtoResponse = inviti.stream()
                .map(mapper::toInvitoDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoResponse);
    }

    @PutMapping("/inviti/{invitoId}/rispondi")
    public ResponseEntity<String> rispondiAInvito(
            @PathVariable Long invitoId,
            @Valid @RequestBody InvitoAzioneDTO dto,
            Authentication authentication) {

        //il dto contiene una stringa "ACCETTA" o "RIFIUTA".
        boolean accetta = dto.azione().equalsIgnoreCase("ACCETTA");

        venditoreService.gestisciInvito(invitoId, accetta, authentication.getName());

        return ResponseEntity.ok("Invito " + dto.azione().toLowerCase() + "to con successo.");
    }
}
