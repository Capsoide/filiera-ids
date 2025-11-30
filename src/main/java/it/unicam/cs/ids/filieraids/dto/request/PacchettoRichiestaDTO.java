package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record PacchettoRichiestaDTO(

        @NotBlank(message="Il nome del pacchetto è obbligatorio")
        @Size(max = 100, message = "Il nome non può superare i 100 caratteri")
        String nome,

        @NotBlank(message = "La descrizione è obbligatoria")
        @Size(max = 500, message = "La descrizione non può superare i 500 caratteri")
        String descrizione,

        @NotNull(message = "Il prezzo è obbligatorio")
        @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di zero")
        Double prezzo,

        @NotEmpty(message = "La lista dei prodotti non può essere vuota")
        List<PacchettoItemRichiestaDTO> items
) {}