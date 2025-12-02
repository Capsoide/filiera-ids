package it.unicam.cs.ids.filieraids.builder;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Conferma;
import it.unicam.cs.ids.filieraids.model.Evento;
import it.unicam.cs.ids.filieraids.model.Indirizzo;
import java.util.Date;

public class EventoConcreteBuilder implements IEventoBuilder {

    private Evento evento;

    public EventoConcreteBuilder() {
        this.reset();
    }

    @Override
    public void reset() {
        this.evento = new Evento();
        this.evento.setStatoConferma(Conferma.ATTESA);
        this.evento.setDataCaricamento(new Date());
        this.evento.setCondivisioneSocial(false);
    }

    @Override
    public void buildNome(String nome) {
        this.evento.setNome(nome);
    }

    @Override
    public void buildDescrizione(String descrizione) {
        this.evento.setDescrizione(descrizione);
    }

    @Override
    public void buildAnimatore(Attore animatore) {
        this.evento.setAnimatore(animatore);
    }

    @Override
    public void buildData(Date data) {
        this.evento.setDataEvento(data);
    }

    @Override
    public void buildLuogo(Indirizzo indirizzo) {
        this.evento.setIndirizzo(indirizzo);
    }

    @Override
    public void buildPosti(int posti) {
        this.evento.setPostiDisponibili(posti);
    }

    @Override
    public void buildCondivisioneSocial(boolean condividi) {
        this.evento.setCondivisioneSocial(condividi);
    }

    @Override
    public Evento getResult() {
        return this.evento;
    }
}