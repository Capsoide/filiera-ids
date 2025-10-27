package it.unicam.cs.ids.filieraids.model;

/**
 * Classe POJO "stupida" che rappresenta una riga del carrello.
 * Non contiene logica di business, solo dati.
 */
public class RigaCarrello {

    private static int counter = 1; // Sarà sostituito da @Id @GeneratedValue
    private int id;

    private Prodotto prodotto; // Usa Prodotto direttamente
    private int quantita;

    // "Snapshot" del prezzo unitario al momento dell'aggiunta
    private double prezzoUnitarioSnapshot;

    public RigaCarrello() {
        this.id = counter++;
    }

    /**
     * Costruttore "stupido". Riceve tutti i dati dal Service.
     * Non fa calcoli o validazioni.
     */
    public RigaCarrello(Prodotto prodotto, int quantita, double prezzoUnitarioSnapshot) {
        this.id = counter++;
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.prezzoUnitarioSnapshot = prezzoUnitarioSnapshot;

        // Logica di calcolo e validazione rimossa
    }

    // Getter per il prezzo totale CALCOLATO AL MOMENTO
    public double getPrezzoTotaleRiga() {
        return this.prezzoUnitarioSnapshot * this.quantita;
    }

    // --- GETTER E SETTER STANDARD ---

    public int getId() { return id; }
    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    public double getPrezzoUnitarioSnapshot() { return prezzoUnitarioSnapshot; }
    public void setPrezzoUnitarioSnapshot(double prezzoUnitarioSnapshot) { this.prezzoUnitarioSnapshot = prezzoUnitarioSnapshot; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RigaCarrello other)) return false;
        return prodotto != null && prodotto.equals(other.getProdotto());
    }

    @Override
    public int hashCode() {
        return (prodotto != null ? prodotto.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "RigaCarrello [prodotto=" + (prodotto != null ? prodotto.getNome() : "N/D") +
                ", quantita=" + quantita +
                ", prezzoTotale=" + getPrezzoTotaleRiga() + "]";
    }
}