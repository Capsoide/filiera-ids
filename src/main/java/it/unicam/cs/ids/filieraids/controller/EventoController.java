package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.Evento;
import it.unicam.cs.ids.filieraids.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public List<Evento> getAllEventi() {
        return eventoService.getAllEventi();
    }

    @GetMapping("/{id}")
    public Optional<Evento> getEventoById(@PathVariable Long id) {
        return eventoService.getEventoById(id);
    }

    @PostMapping
    public Evento creaEvento(@RequestBody Evento evento) {
        return eventoService.saveEvento(evento);
    }

    @DeleteMapping("/{id}")
    public void eliminaEvento(@PathVariable Long id) {
        eventoService.deleteEvento(id);
    }
}
