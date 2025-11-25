package it.unicam.cs.ids.filieraids.dto.response;

public record PuntoMappaDTO(
        Long id,
        String nome,
        String tipo,
        String via,
        Double latitudine,
        Double longitudine
) {
}
