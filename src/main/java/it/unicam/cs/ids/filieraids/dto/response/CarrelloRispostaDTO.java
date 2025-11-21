package it.unicam.cs.ids.filieraids.dto.response;

import java.util.List;

public record CarrelloRispostaDTO(
        Long id,
        double prezzoTotale,
        List<RigaCarrelloRispostaDTO> righe
) {}