package it.unicam.cs.ids.filieraids.builder;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Evento;

public class EventoDirector {

    /**
     * Costruisce un evento completo partendo dai dati di input.
     * @param builder Il costruttore concreto da utilizzare
     * @param datiInput I dati grezzi arrivati dal Controller/DTO
     * @param animatore L'utente che sta creando l'evento
     */
    public void costruisciEventoCompleto(IEventoBuilder builder, Evento datiInput, Attore animatore) {
        builder.reset();

        builder.buildNome(datiInput.getNome());
        builder.buildDescrizione(datiInput.getDescrizione());
        builder.buildAnimatore(animatore);
        builder.buildData(datiInput.getDataEvento());
        builder.buildLuogo(datiInput.getIndirizzo());
        builder.buildPosti(datiInput.getPostiDisponibili());

        // Logica del Director: Gli eventi creati in questo modo vanno condivisi sui social
        builder.buildCondivisioneSocial(true);
    }
}