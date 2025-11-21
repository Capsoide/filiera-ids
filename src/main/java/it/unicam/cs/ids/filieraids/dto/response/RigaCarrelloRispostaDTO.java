package it.unicam.cs.ids.filieraids.dto.response;

public record RigaCarrelloRispostaDTO(
        Long id,
        Long prodottoId,
        String nomeProdotto,
        int quantita,
        double prezzoUnitario,
        double totaleRiga
) {}