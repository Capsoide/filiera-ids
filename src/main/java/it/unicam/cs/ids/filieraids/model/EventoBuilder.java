package it.unicam.cs.ids.filieraids.model;

import java.util.Date;

public class EventoBuilder {
    private final Evento evento;

    public EventoBuilder() {
        this.evento = new Evento();
        this.evento.setStatoConferma(Conferma.ATTESA);
        this.evento.setDataCaricamento(new Date());
        this.evento.setCondivisioneSocial(false);
    }

    public EventoBuilder nome(String nome) {
        this.evento.setNome(nome);
        return this;
    }

    public EventoBuilder descrizione(String descrizione) {
        this.evento.setDescrizione(descrizione);
        return this;
    }

    public EventoBuilder animatore(Attore animatore) {
        this.evento.setAnimatore(animatore);
        return this;
    }

    public EventoBuilder data(Date data) {
        this.evento.setDataEvento(data);
        return this;
    }

    public EventoBuilder luogo(Indirizzo indirizzo) {
        this.evento.setIndirizzo(indirizzo);
        return this;
    }

    public EventoBuilder posti(int posti) {
        this.evento.setPostiDisponibili(posti);
        return this;
    }

    public EventoBuilder condividiSuSocial(boolean condividi) {
        this.evento.setCondivisioneSocial(condividi);
        return this;
    }

    public Evento build() {
        return this.evento;
    }
}
