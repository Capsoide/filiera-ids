package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ValutazioneRichiestaDTO(
        //accetta solo APPROVA o RIFIUTA, come in InvitoAzioneDTO
        @NotBlank(message = "L'azione e' obbligatoria")
        @Pattern(regexp = "(?i)APPROVA|RIFIUTA", message = "L'azione deve essere APPROVATA o RIFIUTATA")
        String azione,

        //facoltativo per APPROVAZIONE, utile per RIFIUTO
        String motivazione
) {}
