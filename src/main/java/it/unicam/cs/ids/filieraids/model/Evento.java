package it.unicam.cs.ids.filieraids.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "eventi")

public class Evento extends Contenuto {
    private String nome;

    @ManyToOne
    @JoinColumn(name = "animatore_id")
    private Attore animatore;

    private Date dataEvento;

    @Embedded
    private Indirizzo indirizzo;

    private int postiDisponibili;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "evento_inviti",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "venditore_id")
    )
    private Set<Venditore> venditoriInvitati = new HashSet<>();

    public Evento() {
        super();
    }

    public Evento(String nome, String descrizione, Attore animatore, Date dataEvento, Indirizzo indirizzo, int postiDisponibili){
        super(Conferma.ATTESA, new Date(), descrizione);
        this.nome = nome;
        this.animatore = animatore;
        this.dataEvento = dataEvento;
        this.indirizzo = indirizzo;
        this.postiDisponibili = postiDisponibili;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Attore getAnimatore() {
        return animatore;
    }

    public void setAnimatore(Attore animatore) {
        this.animatore = animatore;
    }

    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public Set<Venditore> getVenditoriInvitati() {
        return venditoriInvitati;
    }

    public void setVenditoriInvitati(Set<Venditore> venditoriInvitati) {
        this.venditoriInvitati = venditoriInvitati;
    }

    public void addInvitato(Venditore venditore) {
        this.venditoriInvitati.add(venditore);
        venditore.getEventiInvitato().add(this);
    }

    public void removeInvitato(Venditore venditore) {
        this.venditoriInvitati.remove(venditore);
        venditore.getEventiInvitato().remove(this);
    }
}
