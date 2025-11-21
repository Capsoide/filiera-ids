package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import it.unicam.cs.ids.filieraids.model.Indirizzo;
import java.util.Date;

public record EventoRichiestaDTO(

        @NotBlank(message = "Il nome dell'evento è obbligatorio")
        @Size(max = 100, message = "Il nome non può superare i 100 caratteri")
        String nome,

        @NotBlank(message = "La descrizione dell'evento è obbligatoria")
        @Size(max = 500, message = "La descrizione non puo' superare i 500 caratteri")
        String descrizione,

        @NotNull(message = "La data dell'evento è obbligatoria")
        @FutureOrPresent(message = "La data dell'evento non può essere nel passato")
        Date dataEvento,

        @NotNull(message = "L'indirizzo dell'evento è obbligatorio")
        @Valid
        Indirizzo indirizzo,

        @Min(value = 1, message = "I posti disponibili devono essere almeno 1")
        int postiDisponibili
) {}
