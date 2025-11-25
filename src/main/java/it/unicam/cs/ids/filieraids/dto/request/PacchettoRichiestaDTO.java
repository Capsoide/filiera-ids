package it.unicam.cs.ids.filieraids.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PacchettoRichiestaDTO(

        @NotBlank
        String nome,

        @NotBlank
        String descrizione,

        @NotNull
        Double prezzo,

        @NotNull
        List<PacchettoItemRichiestaDTO> items
) {}