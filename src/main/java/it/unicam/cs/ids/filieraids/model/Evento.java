package it.unicam.cs.ids.filieraids.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Invito> inviti = new ArrayList<>();

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

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Attore getAnimatore() { return animatore; }
    public void setAnimatore(Attore animatore) { this.animatore = animatore; }
    public Date getDataEvento() { return dataEvento; }
    public void setDataEvento(Date dataEvento) { this.dataEvento = dataEvento; }
    public Indirizzo getIndirizzo() { return indirizzo; }
    public void setIndirizzo(Indirizzo indirizzo) { this.indirizzo = indirizzo; }
    public int getPostiDisponibili() { return postiDisponibili; }
    public void setPostiDisponibili(int postiDisponibili) { this.postiDisponibili = postiDisponibili; }

    public List<Invito> getInviti() { return inviti; }
    public void setInviti(List<Invito> inviti) { this.inviti = inviti; }
}