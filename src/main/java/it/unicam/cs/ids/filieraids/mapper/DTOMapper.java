package it.unicam.cs.ids.filieraids.mapper;

import it.unicam.cs.ids.filieraids.dto.request.*;
import it.unicam.cs.ids.filieraids.dto.response.*;
import it.unicam.cs.ids.filieraids.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOMapper {

    public Utente fromRegistrazioneUtenteDTO(RegistrazioneUtenteDTO dto) {
        Utente utente = new Utente(dto.email(), dto.password(), dto.nome(), dto.cognome());
        if (dto.indirizzo() != null) {
            utente.addIndirizzo(dto.indirizzo());
        }
        return utente;
    }

    public Venditore fromRegistrazioneVenditoreDTO(RegistrazioneVenditoreDTO dto) {
        Venditore v = new Venditore(
                dto.email(),
                dto.password(),
                dto.nome(),
                dto.cognome(),
                dto.piva(),
                dto.descrizione(),
                dto.ruoli().stream().map(Ruolo::valueOf).collect(Collectors.toSet())
        );
        if (dto.indirizzo() != null) {
            v.addIndirizzo(dto.indirizzo());
        }
        return v;
    }

    public Utente fromRegistrazioneStaffDTO(RegistrazioneStaffDTO dto) {
        return new Utente(
                dto.email(),
                dto.password(),
                dto.nome(),
                dto.cognome()
        );
    }

    public AttoreRispostaDTO toAttoreDTO(Attore attore) {
        return new AttoreRispostaDTO(
                attore.getId(),
                attore.getEmail(),
                attore.getNomeCompleto(),
                attore.getRuoli().stream().map(Enum::name).collect(Collectors.toSet()),
                attore.isEnabled()
        );
    }

    public Prodotto fromProdottoDTO(ProdottoRichiestaDTO dto) {
        Prodotto p = new Prodotto();
        p.setNome(dto.nome());
        p.setDescrizione(dto.descrizione());
        p.setMetodoDiColtivazione(dto.metodoDiColtivazione());
        p.setPrezzo(dto.prezzo());
        p.setQuantita(dto.quantita());
        p.setCertificazioni(dto.certificazioni());
        p.setDataProduzione(dto.dataProduzione());
        return p;
    }

    public ProdottoRispostaDTO toProdottoDTO(Prodotto p) {
        return new ProdottoRispostaDTO(
                p.getId(),
                p.getNome(),
                p.getDescrizione(),
                p.getPrezzo(),
                p.getQuantita(),
                p.getMetodoDiColtivazione(),
                p.getCertificazioni(),
                p.getStatoConferma().name(),
                p.getVenditore().getId(),
                p.getVenditore().getNomeCompleto()
        );
    }


    public Evento fromEventoDTO(EventoRichiestaDTO dto) {
        Evento e = new Evento();
        e.setNome(dto.nome());
        e.setDescrizione(dto.descrizione());
        e.setDataEvento(dto.dataEvento());
        e.setIndirizzo(dto.indirizzo());
        e.setPostiDisponibili(dto.postiDisponibili());
        return e;
    }

    public EventoRispostaDTO toEventoDTO(Evento e) {
        return new EventoRispostaDTO(
                e.getId(),
                e.getNome(),
                e.getDescrizione(),
                e.getDataEvento(),
                e.getIndirizzo(),
                e.getPostiDisponibili(),
                e.getStatoConferma().name(),
                e.getAnimatore().getId(),
                e.getAnimatore().getNomeCompleto()
        );
    }

    public InvitoRispostaDTO toInvitoDTO(Invito i) {
        return new InvitoRispostaDTO(
                i.getId(),
                i.getEvento().getId(),
                i.getEvento().getNome(),
                i.getEvento().getDataEvento(),
                i.getVenditore().getId(),
                i.getVenditore().getNomeCompleto(),
                i.getStato().name(),
                i.getDataInvito()
        );
    }

    public PrenotazioneRispostaDTO toPrenotazioneDTO(Prenotazione p) {
        return new PrenotazioneRispostaDTO(
                p.getId(),
                p.getEvento().getId(),
                p.getEvento().getNome(),
                p.getEvento().getDataEvento(),
                p.getUtente().getId(),
                p.getUtente().getNomeCompleto(),
                p.getNumeroPostiPrenotati(),
                p.getDataPrenotazione()
        );
    }

    public PacchettoItemRispostaDTO toPacchettoItemDTO(PacchettoItem item) {
        return new PacchettoItemRispostaDTO(
                item.getProdotto().getId(),
                item.getProdotto().getNome(),
                item.getQuantita()
        );
    }

    public PacchettoRispostaDTO toPacchettoDTO(Pacchetto pacchetto) {
        return new PacchettoRispostaDTO(
                pacchetto.getId(),
                pacchetto.getNome(),
                pacchetto.getDescrizione(),
                pacchetto.getPrezzo(),
                pacchetto.getVenditore().getEmail(),
                pacchetto.getStatoConferma().name(),
                pacchetto.getItems().stream()
                        .map(this::toPacchettoItemDTO)
                        .toList()
        );
    }

    public RigaCarrelloRispostaDTO toRigaCarrelloRispostaDTO(RigaCarrello r) {
        Long idOggetto;
        String nomeOggetto;

        if (r.getProdotto() != null) {
            idOggetto = r.getProdotto().getId();
            nomeOggetto = r.getProdotto().getNome();
        } else if (r.getPacchetto() != null) {
            idOggetto = r.getPacchetto().getId();
            nomeOggetto = "[PACCHETTO] " + r.getPacchetto().getNome();
        } else {
            idOggetto = 0L;
            nomeOggetto = "Oggetto non identificato";
        }

        return new RigaCarrelloRispostaDTO(
                r.getId(),
                idOggetto,
                nomeOggetto,
                r.getQuantita(),
                r.getPrezzoUnitarioSnapshot(),
                r.getPrezzoTotaleRiga()
        );
    }

    public CarrelloRispostaDTO toCarrelloDTO(Carrello c) {
        List<RigaCarrelloRispostaDTO> righe = c.getContenuti()
                .stream()
                .map(this::toRigaCarrelloRispostaDTO)
                .toList();

        return new CarrelloRispostaDTO(
                c.getId(),
                c.getPrezzoTotale(),
                righe
        );
    }


    private RigaOrdineRispostaDTO toRigaOrdineDTO(RigaCarrello riga) {
        Long idOggetto;
        String nomeOggetto;

        if (riga.getProdotto() != null) {
            idOggetto = riga.getProdotto().getId();
            nomeOggetto = riga.getProdotto().getNome();
        } else if (riga.getPacchetto() != null) {
            idOggetto = riga.getPacchetto().getId();
            nomeOggetto = "[PACCHETTO] " + riga.getPacchetto().getNome();
        } else {
            idOggetto = 0L;
            nomeOggetto = "Oggetto non identificato";
        }

        return new RigaOrdineRispostaDTO(
                idOggetto,
                nomeOggetto,
                riga.getPrezzoUnitarioSnapshot(),
                riga.getQuantita(),
                riga.getPrezzoTotaleRiga()
        );
    }

    public OrdineRispostaDTO toOrdineDTO(Ordine ordine) {
        List<RigaOrdineRispostaDTO> elementiDTO = ordine.getCarrello().getContenuti().stream()
                .map(this::toRigaOrdineDTO)
                .collect(Collectors.toList());

        return new OrdineRispostaDTO(
                ordine.getId(),
                ordine.getDataOrdine(),
                ordine.getStatoOrdine().name(),
                ordine.getTotale(),
                ordine.getIndirizzoDiFatturazione(),
                elementiDTO
        );
    }

    public RichiestaRuoloRispostaDTO toRichiestaRuoloDTO(RichiestaRuolo r) {
        return new RichiestaRuoloRispostaDTO(
                r.getId(),
                r.getAttoreRichiedente().getId(),
                r.getAttoreRichiedente().getNomeCompleto(),
                r.getAttoreRichiedente().getEmail(),
                r.getRuoliRichiesti().stream().map(Enum::name).collect(Collectors.toSet()),
                r.getStato().name()
        );
    }
}