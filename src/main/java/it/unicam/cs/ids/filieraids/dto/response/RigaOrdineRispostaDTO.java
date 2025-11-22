package it.unicam.cs.ids.filieraids.dto.response;

public record RigaOrdineRispostaDTO(
        Long prodottoId,
        String nomeProdotto,
        double prezzoProdotto,
        int quantita,
        double totaleRiga
) {}
