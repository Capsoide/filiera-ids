package it.unicam.cs.ids.filieraids.builder;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Evento;
import it.unicam.cs.ids.filieraids.model.Indirizzo;
import java.util.Date;

public interface IEventoBuilder {
    void reset();
    void buildNome(String nome);
    void buildDescrizione(String descrizione);
    void buildAnimatore(Attore animatore);
    void buildData(Date data);
    void buildLuogo(Indirizzo indirizzo);
    void buildPosti(int posti);

    // Metodo fondamentale per far funzionare l'Observer (Social)
    void buildCondivisioneSocial(boolean condividi);

    Evento getResult();
}