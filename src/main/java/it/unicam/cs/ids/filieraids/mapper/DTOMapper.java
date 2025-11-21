package it.unicam.cs.ids.filieraids.mapper;

import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneUtenteDTO;
import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneVenditoreDTO;
import it.unicam.cs.ids.filieraids.dto.request.ProdottoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.request.EventoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.AttoreRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.InvitoRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.ProdottoRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.EventoRispostaDTO;
import it.unicam.cs.ids.filieraids.model.*;
import org.springframework.stereotype.Component;

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

    public InvitoRispostaDTO toInvitoDTO(Invito invito) {
        return new InvitoRispostaDTO(
                invito.getId(),
                invito.getEvento().getId(),
                invito.getEvento().getNome(),
                invito.getVenditore().getId(),
                invito.getVenditore().getNomeCompleto(),
                invito.getStato().name()
        );
    }
}