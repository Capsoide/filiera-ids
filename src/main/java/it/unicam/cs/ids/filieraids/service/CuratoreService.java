package it.unicam.cs.ids.filieraids.service;
import java.util.*;
import it.unicam.cs.ids.filieraids.model.*;


public class CuratoreService {
    private final List<Autorizzazione> autorizzazioni = new ArrayList<>();



    //Il contenuto di default, prima che il curatore approva/rifiuta è settato in ATTESA
    //Approva un contenuto
    public Autorizzazione approvaContenuto(Utente curatore, Contenuto contenuto, String motivo){
        if(!curatore.getRuoli().contains(Ruolo.CURATORE)){
            throw new SecurityException("Solo il CURATORE puo' approvare contenuti!!");
        }
        contenuto.setStatoConferma(Conferma.APPROVATO);

        Autorizzazione log = new Autorizzazione(curatore, contenuto, motivo, true);
        autorizzazioni.add(log);

        System.out.println("Contenuto ID " + contenuto.getId() + "APPROVATO dal Curatore" + curatore.getEmail());
        return log;
    }

    //rifiuta un contenuto
    public Autorizzazione rifiutaContenuto(Utente curatore, Contenuto contenuto, String motivo){
        if(!curatore.getRuoli().contains(Ruolo.CURATORE)){
            throw new SecurityException("Solo il CURATORE puo' approvare contenuti!!");
        }
        contenuto.setStatoConferma(Conferma.RIFIUTATO);

        Autorizzazione log = new Autorizzazione(curatore, contenuto, motivo, false);
        autorizzazioni.add(log);

        System.out.println("Contenuto ID " + contenuto.getId() + "RIFIUTATO dal Curatore" + curatore.getEmail());
        return log;
    }

    public List<Autorizzazione> getStoricoAutorizzazioni() {
        return autorizzazioni;
    }
}
