package it.unicam.cs.ids.filieraids.model;

public class Autorizzazione {
    private static int count = 1;
    private int id;
    private Utente curatore;
    private Contenuto contenutoDaApprovare;
    private String motivo;
    private boolean autorizzato;

    public Autorizzazione(Utente curatore, Contenuto contenutoDaApprovare, String motivo, boolean autorizzato) {
        this.id = count++;
        this.curatore = curatore;
        this.contenutoDaApprovare = contenutoDaApprovare;
        this.motivo = motivo;
        this.autorizzato = autorizzato;
    }

    public int getId() { return id; }
    public Utente getCuratore() { return curatore; }
    public Contenuto getContenutoDaApprovare() { return contenutoDaApprovare; }
    public String getMotivo() { return motivo; }
    public boolean isAutorizzato() { return autorizzato; }

    @Override
    public String toString() {
        return "Autorizzazione {" +
                "id=" + id +
                ", contenuto=" + (contenutoDaApprovare != null ? contenutoDaApprovare.getDescrizione() : "n/d") +
                ", autorizzato=" + (autorizzato ? "APPROVATO" : "RIFIUTATO") +
                '}';
    }
}
