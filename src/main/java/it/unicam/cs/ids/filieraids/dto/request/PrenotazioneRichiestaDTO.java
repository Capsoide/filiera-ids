package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PrenotazioneRichiestaDTO(
        @NotNull(message = "Il numero di posti è obbligatorio")
        @Min(value = 1, message = "Devi prenotare almeno 1 posto")
        Integer numeroPosti
) {}