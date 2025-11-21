package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record RegistrazioneVenditoreDTO(
        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Formato email non valido")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
        String password,

        @NotBlank(message = "Il nome è obbligatorio")
        String nome,

        @NotBlank(message = "Il cognome è obbligatorio")
        String cognome,

        @NotBlank(message = "La P.IVA è obbligatoria")
        @Pattern(regexp = "\\d{11}", message = "La P.IVA deve essere composta da 11 cifre")
        String piva,

        @Size(max = 500, message = "La descrizione non può superare i 500 caratteri")
        String descrizione,

        @NotEmpty(message = "È necessario specificare almeno un ruolo (es. PRODUTTORE)")
        Set<String> ruoli
) {}