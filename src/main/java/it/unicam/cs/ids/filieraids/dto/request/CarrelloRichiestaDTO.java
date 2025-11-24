package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CarrelloRichiestaDTO(
        @NotNull(message = "L'id del prodotto è obbligatorio")
        Long prodottoId,
        @Min(value = 1, message = "La quantità deve essere almeno 1")
        int quantita
) {
}
