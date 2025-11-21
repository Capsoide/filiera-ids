package it.unicam.cs.ids.filieraids.dto;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Indirizzo;

import java.util.Date;

public class EventoDTO {

    private String nome;

    private Attore animatore;

    private Date dataEvento;

    private Indirizzo indirizzo;

    private int postiDisponibili;

    public EventoDTO(String nome, Date dataEvento, Indirizzo indirizzo, int postiDisponibili, Attore animatore) {
        this.postiDisponibili = postiDisponibili;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.dataEvento = dataEvento;
        this.animatore = animatore;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public Attore getAnimatore() {
        return animatore;
    }

    public void setAnimatore(Attore animatore) {
        this.animatore = animatore;
    }
}
