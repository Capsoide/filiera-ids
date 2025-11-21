package it.unicam.cs.ids.filieraids.dto.response;
import java.util.*;

public record AttoreRispostaDTO(
        Long id,
        String email,
        String nomeCompleto,
        Set<String> ruoli,
        boolean abilitato
) {}