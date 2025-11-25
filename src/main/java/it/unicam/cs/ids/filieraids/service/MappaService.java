package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.dto.response.PuntoMappaDTO;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.EventoRepository;
import it.unicam.cs.ids.filieraids.repository.VenditoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MappaService {

    private final EventoRepository eventoRepository;
    private final VenditoreRepository venditoreRepository;

    public MappaService(EventoRepository eventoRepository, VenditoreRepository venditoreRepository) {
        this.eventoRepository = eventoRepository;
        this.venditoreRepository = venditoreRepository;
    }

    @Transactional(readOnly = true)
    public List<PuntoMappaDTO> getPuntiMappa() {
        List<PuntoMappaDTO> punti = new ArrayList<>();

        //recupero EVENTI APPROVATI
        List<Evento> eventi = eventoRepository.findByStatoConferma(Conferma.APPROVATO);
        for (Evento e : eventi) {
            if (haCoordinate(e.getIndirizzo())) {
                punti.add(new PuntoMappaDTO(
                        e.getId(),
                        e.getNome(),
                        "EVENTO",
                        e.getIndirizzo().getVia() + ", " + e.getIndirizzo().getComune(),
                        e.getIndirizzo().getLatitudine(),
                        e.getIndirizzo().getLongitudine()
                ));
            }
        }

        //recupera VENDITORI
        List<Venditore> venditori = venditoreRepository.findAll();
        for (Venditore v : venditori) {
            if (v.isEnabled() && !v.getIndirizzi().isEmpty()) {
                // Prendiamo il primo indirizzo (sede principale)
                Indirizzo sede = v.getIndirizzi().get(0);
                if (haCoordinate(sede)) {
                    punti.add(new PuntoMappaDTO(
                            v.getId(),
                            v.getNomeCompleto() + " (" + v.getDescrizione() + ")",
                            "AZIENDA",
                            sede.getVia() + ", " + sede.getComune(),
                            sede.getLatitudine(),
                            sede.getLongitudine()
                    ));
                }
            }
        }

        return punti;
    }

    //metodo per vedere se l'indirizzo ha GPS valido
    private boolean haCoordinate(Indirizzo i) {
        return i != null && i.getLatitudine() != null && i.getLongitudine() != null;
    }
}