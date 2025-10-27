package it.unicam.cs.ids.filieraids.model;

import java.util.*;

public class Venditore implements Account {
    private String email;
    private String password;
    private Set<Ruolo> ruoli = new HashSet<>();
    private String nome;    //da rivedere !!!!!!
    private String cognome; //da rivedere !!!!!!
    private String PIVA;
    private String descrizione;
    private List <Indirizzo> indirizzi = new ArrayList<>();
    private List<Prodotto> prodotti = new ArrayList<>(); //lista prodotti in vendita

    public Venditore(String email, String password,
                     String nome, String cognome,
                     String PIVA, String descrizione, Set<Ruolo> ruoli) {

        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.PIVA = PIVA;
        this.descrizione = descrizione;
        this.ruoli = (ruoli != null) ? new HashSet<>(ruoli) : new HashSet<>();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getNomeCompleto() {
        return nome + " " + cognome;
    }



    //gestione prodotti (per il service futuro)
    public List <Prodotto> getProdotti() {
        return prodotti;
    }
    public void addProdotto(Prodotto prodotto) {
        if(prodotto != null &&  !prodotti.contains(prodotto)) {
            prodotti.add(prodotto);
        }
    }

    public void removeProdotto(Prodotto prodotto) {
        prodotti.remove(prodotto);
    }

    @Override public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @Override public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPIVA() { return PIVA; }
    public void setPIVA(String PIVA) { this.PIVA = PIVA; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    @Override public Set<Ruolo> getRuoli() { return ruoli; }
    @Override public void setRuoli(Set<Ruolo> ruoli) { this.ruoli.clear(); if (ruoli != null) this.ruoli.addAll(ruoli); }
    @Override public void addRuolo(Ruolo ruolo) { this.ruoli.add(ruolo); }
    @Override public void removeRuolo(Ruolo ruolo) { this.ruoli.remove(ruolo); }
    @Override public List<Indirizzo> getIndirizzi() { return indirizzi; }
    @Override public void addIndirizzo(Indirizzo indirizzo) { if (indirizzo != null) indirizzi.add(indirizzo); }
    @Override public void removeIndirizzo(Indirizzo indirizzo) { indirizzi.remove(indirizzo); }

    @Override
    public String toString() {
        // Usa il metodo getNomeCompleto() appena aggiunto
        return "Venditore [" + getNomeCompleto() + ", P.IVA=" + PIVA + ", ruoli=" + getRuoli() + "]";
    }
}