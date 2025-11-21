package it.unicam.cs.ids.filieraids.mapper;

import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneUtenteDTO;
import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneVenditoreDTO;
import it.unicam.cs.ids.filieraids.dto.request.ProdottoRichiestaDTO;
import it.unicam.cs.ids.filieraids.dto.response.AttoreRispostaDTO;
import it.unicam.cs.ids.filieraids.dto.response.ProdottoRispostaDTO;
import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Ruolo;
import it.unicam.cs.ids.filieraids.model.Prodotto;
import it.unicam.cs.ids.filieraids.model.Utente;
import it.unicam.cs.ids.filieraids.model.Venditore;
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
}