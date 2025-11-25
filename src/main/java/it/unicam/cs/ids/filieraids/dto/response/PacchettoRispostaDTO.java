package it.unicam.cs.ids.filieraids.dto.response;

import java.util.List;

public record PacchettoRispostaDTO(
        Long id,
        String nome,
        String descrizione,
        double prezzo,
        String venditoreEmail,
        String statoConferma,
        List<PacchettoItemRispostaDTO> items
) {}
