package it.unicam.cs.ids.filieraids.dto.response;

import java.util.Date;

public record PrenotazioneRispostaDTO(
        Long id,
        Long eventoId,
        String nomeEvento,
        Date dataEvento,
        Long utenteId,
        String nomeUtente,
        int numeroPostiPrenotati,
        Date dataPrenotazione
) {}