package it.unicam.cs.ids.filieraids.dto.response;
import java.util.Date;
import java.util.Set;

public record RichiestaRuoloRispostaDTO(
        Long idRichiesta,
        Long idUtenteRichiedente,
        String nomeUtenteRichiedente,
        String emailUtenteRichiedente,
        Set<String> ruoliRichiesti,
        String statoRichiesta
) {}




