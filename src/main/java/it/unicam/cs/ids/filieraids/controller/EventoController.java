package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.Evento;
import it.unicam.cs.ids.filieraids.model.Prenotazione;
import it.unicam.cs.ids.filieraids.model.Venditore;
import it.unicam.cs.ids.filieraids.model.Invito; // Importa Invito
import it.unicam.cs.ids.filieraids.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/eventi")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/visibili")
    public List<Evento> getEventiVisibili() {
        return eventoService.getEventiVisibili();
    }

    @GetMapping("/visibili/{id}")
    public ResponseEntity<Evento> getEventoVisibileById(@PathVariable Long id) {
        Evento evento = eventoService.getEventoVisibileById(id);
        return ResponseEntity.ok(evento);
    }

    @PostMapping
    @PreAuthorize("hasRole('ANIMATORE')")
    public Evento creaEvento(@RequestBody Evento evento, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        return eventoService.creaEvento(evento, animatoreEmail);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<String> eliminaEvento(@PathVariable Long id, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        eventoService.eliminaEvento(id, animatoreEmail);
        return ResponseEntity.ok("Evento " + id + " eliminato correttamente.");
    }

    @GetMapping("/miei")
    @PreAuthorize("hasRole('ANIMATORE')")
    public List<Evento> getMieiEventi(Authentication authentication) {
        String animatoreEmail = authentication.getName();
        return eventoService.getMieiEventi(animatoreEmail);
    }

    @GetMapping("/{id}/prenotazioni")
    @PreAuthorize("hasRole('ANIMATORE')")
    public List<Prenotazione> getPrenotazioniPerEvento(@PathVariable Long id, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        return eventoService.getPrenotazioniPerEvento(id, animatoreEmail);
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
    public List<Invito> getInvitatiPerEvento(@PathVariable Long eventoId, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        return eventoService.getInvitatiPerEvento(eventoId, animatoreEmail);
    }
}