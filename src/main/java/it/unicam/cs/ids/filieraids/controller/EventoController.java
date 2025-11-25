package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.request.EventoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.request.PrenotazioneRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.EventoRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.InvitoRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.PrenotazioneRispostaDTO;
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

    /**
     * Endpoint pubblico che permette di ottenere tutti gli eventi approvati e visibili.
     *
     * @return      la lista degli eventi approvati e visibili, in formato DTO
     */
    @GetMapping("/visibili")
    public ResponseEntity<List<EventoRispostaDTO>> getEventiVisibili() {
        List<Evento> eventi = eventoService.getEventiVisibili();
        // Converti lista Entità -> lista DTO usando uno stream
        List<EventoRispostaDTO> dtoResponse = eventi.stream()
                .map(mapper::toEventoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoResponse);
    }

    /**
     * Endpoint pubblico che permette di ottenere un singolo evento tramite il suo id.
     *
     * @param id    l'id dell'evento da ottenere
     * @return      l'evento corrispondente all'id, in formato DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventoRispostaDTO> getEventoById(@PathVariable Long id){
        Evento e = eventoService.getEventoVisibileById(id); // Nota: getEventoById include già il controllo visibilità/esistenza
        if(e == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toEventoDTO(e));
    }

    /**
     * Endpoint protetto che permette all'animatore loggato di creare un nuovo evento.
     *
     * @param dto               i dati necessari per la creazione dell'evento (inviati nel body della richiesta)
     * @param authentication    rappresenta l'animatore attualmente loggato
     * @return                  l'evento creato, in formato DTO
     */
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

    /**
     * Endpoint protetto che permette all'animatore loggato di ottenere la lista dei propri eventi.
     *
     * @param authentication    rappresenta l'animatore attualmente loggato
     * @return                  la lista degli eventi dell'animatore loggato, in formato DTO
     */
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

    /**
     * Endpoint protetto che permette all'animatore loggato di eliminare un evento da lui creato.
     *
     * @param id                l'id dell'evento da eliminare
     * @param authentication    rappresenta l'animatore attualmente loggato
     * @return                  messaggio di conferma dell'avvenuta eliminazione
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<String> eliminaEvento(@PathVariable Long id, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        eventoService.eliminaEvento(id, animatoreEmail);
        return ResponseEntity.ok("Evento eliminato con successo.");
    }

    /**
     * Endpoint protetto che permette all'animatore loggato di ottenere tutte le prenotazioni
     * relative ad uno dei suoi eventi.
     *
     * @param id                l'id dell'evento da cui ottenere le prenotazioni
     * @param authentication    rappresenta lanimatore attualmente loggatto
     * @return                  la lista delle prenotazioni dell'evento specificato
     */
    @GetMapping("/{id}/prenotazioni")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<List<Prenotazione>> getPrenotazioniPerEvento(@PathVariable Long id, Authentication authentication) {
        String animatoreEmail = authentication.getName();
        return ResponseEntity.ok(eventoService.getPrenotazioniPerEvento(id, animatoreEmail));
    }

    /*AGGIUNTA PER IL DTO
    @GetMapping("/{id}/prenotazioni")
    public ResponseEntity<List<PrenotazioneRispostaDTO>> getPrenotazioniPerEvento(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String animatoreEmail = authentication.getName();
        List<Prenotazione> lista = eventoService.getPrenotazioniPerEvento(id, animatoreEmail);

        List<PrenotazioneRispostaDTO> dto = lista.stream()
                .map(mapper::toPrenotazioneDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dto);
    }
    */

    /*AGGIUNTA PER IL DTO
    @PostMapping("/{eventoId}/prenota")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<PrenotazioneRispostaDTO> prenota(
            @PathVariable Long eventoId,
            @Valid @RequestBody PrenotazioneRichiestaDTO dto,
            Authentication authentication
    ) {
        String email = authentication.getName();

        Prenotazione p = eventoService.creaPrenotazione(eventoId, dto.numeroPosti(), email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toPrenotazioneDTO(p));
    }*/

    /**
     * Endpoint protetto che permette all'animatore loggato di invitare un venditore ad uno dei propri eventi.
     *
     * @param eventoId          l'id dell'evento a cui invitare il venditore
     * @param venditoreId       l'id del venditore da invitare
     * @param authentication    rappresenta l'animatore attualmente loggato
     * @return                  messaggio di conferma dell'avvenuto invito
     */
    @PostMapping("/{eventoId}/invita/{venditoreId}")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<String> invitaVenditore(@PathVariable Long eventoId,
                                                  @PathVariable Long venditoreId,
                                                  Authentication authentication) {
        String animatoreEmail = authentication.getName();
        eventoService.invitaVenditore(eventoId, venditoreId, animatoreEmail);
        return ResponseEntity.ok("Venditore " + venditoreId + " invitato all'evento " + eventoId);
    }

    /**
     * Endpoint protetto che permette all'animatore loggato di ottenere la lista dei venditori
     * invitati ad uno dei propri eventi.
     *
     * @param eventoId          l'id dell'evento di cui ottenere gli invitati
     * @param authentication    rappresenta l'animatore attualmente loggato
     * @return                  la lista degli inviti relativi all'evento specificato, in formato dto
     */
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANIMATORE')")
    public ResponseEntity<EventoRispostaDTO> modificaEvento(@PathVariable Long id,
                                                            @Valid @RequestBody EventoRichiestaDTO dto,
                                                            Authentication authentication) {
        //da DTO a entità
        Evento datiAggiornati = mapper.fromEventoDTO(dto);
        String animatoreEmail = authentication.getName();

        //chiamo il servizio
        Evento eventoModificato = eventoService.modificaEvento(id, datiAggiornati, animatoreEmail);

        return ResponseEntity.ok(mapper.toEventoDTO(eventoModificato));
    }
}