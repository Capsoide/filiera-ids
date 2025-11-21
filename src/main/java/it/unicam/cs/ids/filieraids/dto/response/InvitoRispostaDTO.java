package it.unicam.cs.ids.filieraids.dto.response;
import java.util.*;

public record InvitoRispostaDTO(
        Long id,
        Long eventoId,
        String nomeEvento,
        Date dataEvento,
        Long venditoreId,
        String nomeVenditore,
        String stato,
        Date dataInvito
) {}