package it.unicam.cs.ids.filieraids.dto.request;

import it.unicam.cs.ids.filieraids.model.Indirizzo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrazioneUtenteDTO (
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

    @NotNull(message = "L'indirizzo è obbligatorio")
    @Valid
    Indirizzo indirizzo
) {}



