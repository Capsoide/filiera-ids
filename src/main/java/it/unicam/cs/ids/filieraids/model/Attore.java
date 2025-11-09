package it.unicam.cs.ids.filieraids.model;
import java.util.*;
import jakarta.persistence.*;

@Entity
@Table(name = "attori")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Attore implements Account {
    //non so se metere l'id
    //ho messo l'id almeno viene ereditata LOL lo lascio esteregg
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Ruolo> ruoli = new HashSet<>();

    private String nome;
    private String cognome;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Indirizzo> indirizzi = new ArrayList<>();
    
    private boolean enabled; //flag utente disabilitato

    protected Attore (String email, String password, String nome, String cognome){
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.enabled = false;
    }

    //costruttore vuoto per JPA
    public Attore(){}

    public Long getId(){return id;}
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    @Override
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public Set<Ruolo> getRuoli() { return ruoli; }

    @Override
    public void setRuoli(Set<Ruolo> ruoli) {
        this.ruoli.clear();
        if (ruoli != null) {
            this.ruoli.addAll(ruoli);
        }
    }

    @Override
    public void addRuolo(Ruolo ruolo) { this.ruoli.add(ruolo); }

    @Override
    public void removeRuolo(Ruolo ruolo) { this.ruoli.remove(ruolo); }

    @Override
    public List<Indirizzo> getIndirizzi() { return indirizzi; }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void addIndirizzo(Indirizzo indirizzo) {
        if (indirizzo != null && !indirizzi.contains(indirizzo)) {
            indirizzi.add(indirizzo);
        }
    }

    @Override
    public void removeIndirizzo(Indirizzo indirizzo) { indirizzi.remove(indirizzo); }

    @Override
    public String toString() {
        return "Attore [" + getNomeCompleto() + ", email=" + getEmail() + ", ruoli=" + getRuoli() + "]";
    }
}