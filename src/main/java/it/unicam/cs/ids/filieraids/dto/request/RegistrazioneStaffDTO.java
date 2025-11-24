package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO utilizzato per l'AUTO-REGISTRAZIONE di aspiranti Curatori e Animatori.
 * Il ruolo GESTORE è escluso da questa procedura.
 */
public record RegistrazioneStaffDTO(
        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Formato email non valido")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        String password,

        @NotBlank(message = "Il nome è obbligatorio")
        String nome,

        @NotBlank(message = "Il cognome è obbligatorio")
        String cognome,

        @NotBlank(message = "Il ruolo richiesto è obbligatorio")

        @Pattern(regexp = "CURATORE|ANIMATORE", message = "Il ruolo richiesto deve essere CURATORE o ANIMATORE")
        String ruoloRichiesto
) {}