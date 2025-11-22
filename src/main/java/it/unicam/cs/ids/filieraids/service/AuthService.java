package it.unicam.cs.ids.filieraids.service;        //porcodddiomerda

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.AttoreRepository;
import it.unicam.cs.ids.filieraids.repository.RichiestaRuoloRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final AttoreRepository attoreRepository;
    private final RichiestaRuoloRepository richiestaRuoloRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AttoreRepository attoreRepository,
                       RichiestaRuoloRepository richiestaRuoloRepository,
                       PasswordEncoder passwordEncoder) {
        this.attoreRepository = attoreRepository;
        this.richiestaRuoloRepository = richiestaRuoloRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Utente registraAcquirente(Utente utente) {
        if (attoreRepository.findByEmail(utente.getEmail()).isPresent()) {
            throw new IllegalStateException("Errore: Email già in uso.");
        }
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        utente.setRuoli(Set.of(Ruolo.ACQUIRENTE));
        utente.setEnabled(true);
        return attoreRepository.save(utente);
    }


    @Transactional
    public Attore registraVenditore(Venditore venditore) {
        if (attoreRepository.findByEmail(venditore.getEmail()).isPresent()) {
            throw new IllegalStateException("Errore: Email già in uso.");
        }
        Set<Ruolo> ruoliRichiesti = new HashSet<>(venditore.getRuoli());
        venditore.setPassword(passwordEncoder.encode(venditore.getPassword()));
        venditore.setRuoli(Set.of());

        Attore attoreSalvato = attoreRepository.save(venditore);

        RichiestaRuolo richiesta = new RichiestaRuolo(
                attoreSalvato,
                ruoliRichiesti,
                Conferma.ATTESA
        );

        richiestaRuoloRepository.save(richiesta);

        return attoreSalvato;
    }
}