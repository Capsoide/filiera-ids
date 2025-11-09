package it.unicam.cs.ids.filieraids.model;

import jakarta.persistence.*;
import org.springframework.lang.Nullable;

import java.util.*;

@Entity
@Table(name="richiesta_ruolo")
public class RichiestaRuolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="attore_id", nullable=false, unique=true)
    private Attore attoreRichiedente;

    @Enumerated(EnumType.STRING)
    private Conferma stato;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "richiesta_ruoli_richiesti", joinColumns = @JoinColumn(name = "richiesta_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo")
    private Set<Ruolo> ruoliRichiesti = new HashSet<>();

    public RichiestaRuolo() {}

    public RichiestaRuolo(Attore attore, Set<Ruolo> ruoli, String motivazione, Conferma stato) {
        this.attoreRichiedente = attore;
        this.ruoliRichiesti = ruoli;
        this.stato = Conferma.ATTESA;
    }

    public Long getId() {
        return id;
    }

    public Attore getAttoreRichiedente() {
        return attoreRichiedente;
    }

    public void setAttoreRichiedente(Attore attoreRichiedente) {
        this.attoreRichiedente = attoreRichiedente;
    }

    public Conferma getStato() {
        return stato;
    }

    public void setStato(Conferma stato) {
        this.stato = stato;
    }

    public Set<Ruolo> getRuoliRichiesti() {
        return ruoliRichiesti;
    }

    public void setRuoliRichiesti(Set<Ruolo> ruoliRichiesti) {
        this.ruoliRichiesti = ruoliRichiesti;
    }

}
