package it.unicam.cs.ids.filieraids.controller;

import it.unicam.cs.ids.filieraids.model.Attore;
import it.unicam.cs.ids.filieraids.model.Utente;
import it.unicam.cs.ids.filieraids.model.Venditore;
import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneUtenteDTO;
import it.unicam.cs.ids.filieraids.dto.request.RegistrazioneVenditoreDTO;
import it.unicam.cs.ids.filieraids.dto.response.AttoreRispostaDTO;
import it.unicam.cs.ids.filieraids.service.AuthService;
import it.unicam.cs.ids.filieraids.mapper.DTOMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final DTOMapper mapper;

    public AuthController(AuthService authService, DTOMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping("/registra/acquirente")

    //@Valid attiva la validazione automatica di Spring sul dto
    public ResponseEntity<AttoreRispostaDTO> registraAcquirente(@Valid @RequestBody RegistrazioneUtenteDTO dto) {

        //converte dto in entità
        Utente utenteDaRegistrare = mapper.fromRegistrazioneUtenteDTO(dto);

        //chiama il service per logica di business, salvataggio in db e hash password
        Utente utenteRegistrato = authService.registraAcquirente(utenteDaRegistrare);

        //converte entità in dto riisposta senza password in chiaro
        AttoreRispostaDTO risposta = mapper.toAttoreDTO(utenteRegistrato);

        return ResponseEntity.status(HttpStatus.CREATED).body(risposta);
    }

    @PostMapping("/registra/venditore")
    public ResponseEntity<AttoreRispostaDTO> registraVenditore(@Valid @RequestBody RegistrazioneVenditoreDTO dto) {
        //converte dto in entita
        Venditore venditoreDaRegistrare = mapper.fromRegistrazioneVenditoreDTO(dto);

        //chiamo il service
        Attore venditoreRegistrato = authService.registraVenditore(venditoreDaRegistrare);

        //converte entità indto di risposta
        AttoreRispostaDTO risposta = mapper.toAttoreDTO(venditoreRegistrato);

        //ritorna:accepted 202 perché il venditore deve essere ancora approvato dal gestore
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(risposta);
    }
}