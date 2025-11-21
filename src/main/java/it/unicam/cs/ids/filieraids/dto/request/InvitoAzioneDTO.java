package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


//venditore risponde all'invito
//contiene solo l'azione (di accetta o rfiuta)
public record InvitoAzioneDTO(
        @NotBlank(message = "L'azione e' obbligatoria")
        @Pattern(regexp = "(?i)ACCETTA|RIFIUTA", message = "L'azione deve essere ACCETTATA o RIFIUTATA")
        String azione
) {}
