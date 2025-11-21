package it.unicam.cs.ids.filieraids.dto.response;

import java.util.*;

public record ProdottoRispostaDTO(
        Long id,
        String nome,
        String descrizione,
        double prezzo,
        int quantitaDisponibile,
        String metodoDiColtivazione,
        List<String> certificazioni,
        String statoConferma,
        Long venditoreId,
        String venditoreNomeCompleto
)
{ }
