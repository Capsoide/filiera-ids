package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.request.EventoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.EventoRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.InvitoRispostaDTO;
import it.unicam.cs.ids.filieraids.model.Evento;
import it.unicam.cs.ids.filieraids.model.Invito;
import it.unicam.cs.ids.filieraids.model.Prenotazione;
import it.unicam.cs.ids.filieraids.service.EventoService;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api/eventi")
public class EventoController {

    private final EventoService eventoService;
    private final DTOMapper mapper;

    public EventoController(EventoService eventoService, DTOMapper mapper) {
        this.eventoService = eventoService;
        this.mapper = mapper;
    }

    //endpoint pubblici

    //ottiene tutti gli eventi approvati e visibili
    @GetMapping("/visibili")
    public ResponseEntity<List<EventoRispostaDTO>> getEventiVisibili() {
        List<Evento> eventi = eventoService.getEventiVisibili();
        // Converti lista Entità -> lista DTO usando uno stream
        List<EventoRispostaDTO> dtoResponse = eventi.stream()
                .map(mapper::toEventoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }

    //ottiene un singolo evento per ID
    @GetMapping("/{id}")
    public ResponseEntity<EventoRispostaDTO> getEventoById(@PathVariable Long id){
        Evento e = eventoService.getEventoVisibileById(id); // Nota: getEventoById include già il controllo visibilità/esistenza
        if(e == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toEventoDTO(e));
    }

    //endpoint protetti Animatore

    //crea un nuovo evento
    @PostMapping
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<EventoRispostaDTO> creaEvento(@Valid @RequestBody EventoRichiestaDTO dto, Authentication authentication){
        //Da Map DTO a Entità
        Evento eventoDaCreare = mapper.fromEventoDTO(dto);
        String animatoreEmail = authentication.getName();

        //chiamo il servizio
        Evento eventoCreato = eventoService.creaEvento(eventoDaCreare, animatoreEmail);

        //da Map Entity a Response DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toEventoDTO(eventoCreato));
    }

    //ottiene gli eventi dell'animatore loggato
    @GetMapping("/miei")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<List<EventoRispostaDTO>> getMieiEventi(Authentication authentication) {
        String animatoreEmail = authentication.getName();
        List<Evento> mieiEventi = eventoService.getMieiEventi(animatoreEmail);
        //converte lista Entità a lista DTO
        List<EventoRispostaDTO> dtoResponse = mieiEventi.stream()
                .map(mapper::toEventoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }

    //elimina un evento
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<String> eliminaEvento(@PathVariable Long id, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        eventoService.eliminaEvento(id, animatoreEmail);
        return ResponseEntity.ok("Evento eliminato con successo.");
    }

    @GetMapping("/{id}/prenotazioni")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<List<Prenotazione>> getPrenotazioniPerEvento(@PathVariable Long id, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        return ResponseEntity.ok(eventoService.getPrenotazioniPerEvento(id, animatoreEmail));
    }

    @PostMapping("/{eventoId}/invita/{venditoreId}")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<String> invitaVenditore(@PathVariable Long eventoId,
                                                  @PathVariable Long venditoreId,
                                                  Authentication authentication) {
        String animatoreEmail = authentication.getName();
        eventoService.invitaVenditore(eventoId, venditoreId, animatoreEmail);
        return ResponseEntity.ok("Venditore " + venditoreId + " invitato all'evento " + eventoId);
    }

    @GetMapping("/{eventoId}/invitati")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<List<InvitoRispostaDTO>> getInvitatiPerEvento(@PathVariable Long eventoId, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        List<Invito> inviti = eventoService.getInvitatiPerEvento(eventoId, animatoreEmail);

        //da entità a dto
        List<InvitoRispostaDTO> dtoResponse = inviti.stream()
                .map(mapper::toInvitoDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoResponse);
    }
}