package it.unicam.cs.ids.filieraids.dto.response;

import it.unicam.cs.ids.filieraids.model.Indirizzo;

import java.util.Date;

public record EventoRispostaDTO(
        Long id,
        String nome,
        String descrizione,
        Date dataEvento,
        Indirizzo indirizzo,
        int postiDisponibili,
        String statoConferma,
        Long animatoreId,
        String nomeAnimatore
) {}