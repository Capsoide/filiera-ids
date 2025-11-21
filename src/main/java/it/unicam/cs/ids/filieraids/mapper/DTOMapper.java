package it.unicam.cs.ids.filieraids.mapper;

import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneUtenteDTO;
import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneVenditoreDTO;
import it.unicam.cs.ids.filieraids.dto.request.ProdottoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.request.EventoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.*;
import it.unicam.cs.ids.filieraids.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component //fa in modo che la classe possa essere iniettata nei controller
public class DTOMapper {

    //daa dto a entità (request)
    public Utente fromRegistrazioneUtenteDTO(RegistrazioneUtenteDTO dto) {
        Utente utente = new Utente(dto.email(), dto.password(), dto.nome(), dto.cognome());
        if (dto.indirizzo() != null) {
            utente.addIndirizzo(dto.indirizzo());
        }
        return utente;
    }

    public Venditore fromRegistrazioneVenditoreDTO(RegistrazioneVenditoreDTO dto) {
        return new Venditore(
                dto.email(),
                dto.password(),
                dto.nome(),
                dto.cognome(),
                dto.piva(),
                dto.descrizione(),
                dto.ruoli().stream().map(Ruolo::valueOf).collect(Collectors.toSet())
        );
    }

    //da entità a dto (response)
    public AttoreRispostaDTO toAttoreDTO(Attore attore) {
        return new AttoreRispostaDTO(
                attore.getId(),
                attore.getEmail(),
                attore.getNomeCompleto(),

                //converte il Set<Ruolo> (enum) in Set<String> per il dto
                attore.getRuoli().stream().map(Enum::name).collect(Collectors.toSet()),
                attore.isEnabled()
        );
    }

    //da request dto a entità per creazione/modifica del prodotto
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

    //da entità a response DTO
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

    // Dentro DTOMapper

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

    //mapper per prenotazione
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


    //mapper per carrello
    public RigaCarrelloRispostaDTO toRigaCarrelloRispostaDTO(RigaCarrello r) {
        return new RigaCarrelloRispostaDTO(
                r.getId(),
                r.getProdotto().getId(),
                r.getProdotto().getNome(),
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

}