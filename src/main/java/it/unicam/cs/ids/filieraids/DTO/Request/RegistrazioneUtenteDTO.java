package it.unicam.cs.ids.filieraids.DTO.Request;

import it.unicam.cs.ids.filieraids.model.Indirizzo;

public class RegistrazioneUtenteDTO {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private Indirizzo indirizzo;

    public RegistrazioneUtenteDTO(String nome, String cognome, String email, String password, Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }
}
