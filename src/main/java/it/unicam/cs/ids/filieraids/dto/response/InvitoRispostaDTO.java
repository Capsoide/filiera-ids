package it.unicam.cs.ids.filieraids.dto.response;

public record InvitoRispostaDTO(
        Long id,
        Long eventoId,
        String eventoNome,
        Long venditoreId,
        String venditoreNomeCompleto,
        String statoInvito
) {}