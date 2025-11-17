package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VenditoreService {

    private final InvitoRepository invitoRepository;
    private final VenditoreRepository venditoreRepository;

    public VenditoreService(InvitoRepository invitoRepository, VenditoreRepository venditoreRepository) {
        this.invitoRepository = invitoRepository;
        this.venditoreRepository = venditoreRepository;
    }

    private Venditore getVenditoreByEmail(String email) {
        return venditoreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Venditore non trovato: " + email));
    }

    @Transactional(readOnly = true)
    public List<Invito> getMieiInviti(String venditoreEmail) {
        Venditore venditore = getVenditoreByEmail(venditoreEmail);
        return invitoRepository.findByVenditore(venditore);
    }

    @Transactional
    public void gestisciInvito(Long invitoId, boolean accetta, String venditoreEmail) {
        Venditore venditore = getVenditoreByEmail(venditoreEmail);
        Invito invito = invitoRepository.findById(invitoId)
                .orElseThrow(() -> new RuntimeException("Invito non trovato"));

        if (!invito.getVenditore().equals(venditore)) {
            throw new SecurityException("Non puoi gestire un invito non tuo.");
        }

        if (invito.getStato() != StatoInvito.ATTESA) {
            throw new IllegalStateException("L'invito è già stato processato.");
        }

        if (accetta) {
            invito.setStato(StatoInvito.APPROVATO);
            System.out.println("Invito " + invitoId + " ACCETTATO da " + venditoreEmail);
        } else {
            invito.setStato(StatoInvito.RIFIUTATO);
            System.out.println("Invito " + invitoId + " RIFIUTATO da " + venditoreEmail);
        }
        invitoRepository.save(invito);
    }
}
