package it.unicam.cs.ids.filieraids.dto.response;

import it.unicam.cs.ids.filieraids.model.Indirizzo;

import java.util.*;

public record OrdineRispostaDTO(
        Long id,
        Date dataOrdine,
        String statoOrdine,
        double totaleOrdine,
        Indirizzo indirizzoFatturazione,
        List<RigaOrdineRispostaDTO> prodottiAcquistati
) {}
