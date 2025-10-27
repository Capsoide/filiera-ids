package it.unicam.cs.ids.filieraids.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utente implements Account{

    private String email;
    private String password;
    private Set<Ruolo> ruoli = new HashSet<>();
    private String nome;
    private String cognome;
    private List<Indirizzo> indirizzi = new ArrayList<>();
    private Carrello carrello;
    private List<Ordine> ordini = new ArrayList<>();


    public Utente(String email, String password, String nome, String cognome) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.carrello = new Carrello();
        ruoli.add(Ruolo.ACQUIRENTE);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Carrello getCarrello() {
        return carrello;
    }

    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //gestione ordini
    public List<Ordine> getOrdini() {
        return ordini;
    }

    public void addOrdine(Ordine ordine) {
        if (ordine != null && !ordini.contains(ordine)) {
            ordini.add(ordine);
        }
    }

    public void removeOrdine(Ordine ordine) {
        ordini.remove(ordine);
    }

    @Override
    public Set<Ruolo> getRuoli() {
        return ruoli;
    }

    @Override
    public void setRuoli(Set<Ruolo> ruoli) {
        this.ruoli.clear();
        this.ruoli.addAll(ruoli);
    }

    @Override
    public void addRuolo(Ruolo ruolo) {
        this.ruoli.add(ruolo);
    }

    @Override
    public void removeRuolo(Ruolo ruolo) {
        this.ruoli.remove(ruolo);
    }

    @Override
    public List<Indirizzo> getIndirizzi() {
        return indirizzi;
    }

    @Override
    public void addIndirizzo(Indirizzo indirizzo) {
        indirizzi.add(indirizzo);
    }

    @Override
    public void removeIndirizzo(Indirizzo indirizzo) {
        indirizzi.remove(indirizzo);
    }

    @Override
    public String toString() {
        return "Utente [" + nome + " " + cognome + ", email=" + email + ", ruoli=" + ruoli + "]";
    }
}
