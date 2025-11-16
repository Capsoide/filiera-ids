package it.unicam.cs.ids.filieraids.model.DTO;

import it.unicam.cs.ids.filieraids.model.Indirizzo;
import it.unicam.cs.ids.filieraids.model.Pagamento;

import java.util.Date;

public class OrdineDTO {

    private Pagamento pagamento;

    private Date dataOrdine;

    private Indirizzo indirizzoDiFatturazione;

    public OrdineDTO(Pagamento pagamento, Date dataOrdine, Indirizzo indirizzoFatturazione) {
        this.indirizzoDiFatturazione = indirizzoFatturazione;
        this.dataOrdine = dataOrdine;
        this.pagamento = pagamento;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public Indirizzo getIndirizzoDiFatturazione() {
        return indirizzoDiFatturazione;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }
}
