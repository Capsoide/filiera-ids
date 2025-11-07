package it.unicam.cs.ids.filieraids.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.*;
import jakarta.persistence.*;

@Entity
@Table(name = "prodotti")
public class Prodotto extends Contenuto {
    private String nome;
    private String metodoDiColtivazione;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "prodotto_certificazioni", joinColumns = @JoinColumn(name = "prodotto_id"))
    @Column(name = "certificazione")
    private List<String> certificazioni;

    @Temporal(TemporalType.DATE)
    private Date dataProduzione;

    private double prezzo;
    private int quantita;

    @ManyToOne(fetch = FetchType.LAZY) //relazione con venditore :molti prodotti appartengono a un venditore
    @JoinColumn(name = "venditore_id", nullable = false)
    @JsonBackReference
    private Venditore venditore;

    public Prodotto() {
        super();
        this.certificazioni = new ArrayList<>();
    }

    public Prodotto(Date dataCaricamento,
                    String descrizione,
                    String nome,
                    String metodoDiColtivazione,
                    double prezzo,
                    Venditore produttore, //NOTA per mat e marty, quando fate il costruttore, come in sto caso siate precisi, il prodotto è di un venditore non di utente
                    List<String> certificazioni,
                    Date dataProduzione,
                    int quantita) {

        super(Conferma.ATTESA, dataCaricamento, descrizione);
        this.nome = nome;
        this.metodoDiColtivazione = metodoDiColtivazione;
        this.certificazioni = (certificazioni != null) ? certificazioni : new ArrayList<>();
        this.dataProduzione = dataProduzione;
        this.prezzo = prezzo;
        this.quantita = quantita;
        this.venditore = produttore;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getMetodoDiColtivazione() { return metodoDiColtivazione; }
    public void setMetodoDiColtivazione(String metodoDiColtivazione) { this.metodoDiColtivazione = metodoDiColtivazione; }
    public List<String> getCertificazioni() { return certificazioni; }
    public void setCertificazioni(List<String> certificazioni) { this.certificazioni = certificazioni; }
    public Date getDataProduzione() { return dataProduzione; }
    public void setDataProduzione(Date dataProduzione) { this.dataProduzione = dataProduzione; }
    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    public Venditore getVenditore() { return venditore; }
    public void setVenditore(Venditore venditore) { this.venditore = venditore; }

    @Override
    public String toString() {
        return "Prodotto {" +
                "nome='" + nome + '\'' +
                ", prezzo=" + prezzo +
                ", quantita=" + quantita +
                ", stato=" + getStatoConferma() +
                '}';
    }
}
