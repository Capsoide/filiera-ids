package it.unicam.cs.ids.filieraids.mapper;

import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneUtenteDTO;
import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneVenditoreDTO;
import it.unicam.cs.ids.filieraids.dto.response.AttoreRispostaDTO;
import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Ruolo;
import it.unicam.cs.ids.filieraids.model.Utente;
import it.unicam.cs.ids.filieraids.model.Venditore;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component //component fa in modo che la classe possa essere iniettata nei controller
public class DTOMapper {

    //daa dto a entità (eequest)
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
}