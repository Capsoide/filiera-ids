package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.dto.response.PuntoMappaDTO;
import it.unicam.cs.ids.filieraids.service.MappaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mappa")
public class MappaController {
    private final MappaService mappaService;


    public MappaController(MappaService mappaService){
        this.mappaService = mappaService;
    }

    @GetMapping
    public ResponseEntity<List<PuntoMappaDTO>> getPunti() {
        return ResponseEntity.ok(mappaService.getPuntiMappa());
    }
}
