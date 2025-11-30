package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PacchettoItemRichiestaDTO(
        @NotBlank(message = "L'id del prodotto è obbligatorio")
        Long prodottoId,

        @NotBlank(message = "La quantità del prodotto è obbligatoria")
        @Min(value = 1, message = "La quantita non può essere zero")
        int quantita
) {}
