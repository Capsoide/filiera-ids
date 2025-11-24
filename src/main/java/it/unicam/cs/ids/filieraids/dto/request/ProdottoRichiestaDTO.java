package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.*;
import java.util.*;

public record ProdottoRichiestaDTO(
        @NotBlank(message = "Il nome del prodotto è obbligatorio")
        @Size(max = 100, message = "Il nome non puo' superare i 100 caratteri")
        String nome,

        @NotBlank(message = "La descrizione è obbligatoria")
        @Size(max = 500, message = "La descrizione non puo' superare i 500 caratteri")
        String descrizione,

        @NotBlank(message = "Il metodo di coltivazione è obbligatorio")
        String metodoDiColtivazione,

        @NotNull(message = "Il prezzo è obbligatorio")
        @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di zero")
        double prezzo,

        @Min(value = 0, message = "La quantità non puo' essere negativa")
        int quantita,


        @NotNull(message = "La lista certificazioni non può essere nulla (può essere vuota)")
        List<String> certificazioni,

        @NotNull(message = "La data di produzione è obbligatoria")
        @PastOrPresent(message = "La data di produzione non può essere nel futuro")
        Date dataProduzione

        )
{ }
