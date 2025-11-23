package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRichiestaDTO(
        @NotBlank(message = "Email obbligatoria")
        @Email(message = "Formato email non valido")
        String email,

        @NotBlank(message = "Password obbligatoria")
        String password
) {}
