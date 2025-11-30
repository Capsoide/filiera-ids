package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PacchettoService {

    private final PacchettoRepository pacchettoRepository;
    private final PacchettoItemRepository itemRepository;
    private final ProdottoRepository prodottoRepository;
    private final VenditoreRepository venditoreRepository;
    private final AutorizzazioneRepository autorizzazioneRepository;

    public PacchettoService(PacchettoRepository pacchettoRepository,
                            PacchettoItemRepository itemRepository,
                            ProdottoRepository prodottoRepository,
                            VenditoreRepository venditoreRepository,
                            AutorizzazioneRepository autorizzazioneRepository) {
        this.pacchettoRepository = pacchettoRepository;
        this.itemRepository = itemRepository;
        this.prodottoRepository = prodottoRepository;
        this.venditoreRepository = venditoreRepository;
        this.autorizzazioneRepository = autorizzazioneRepository;
    }

    private Venditore getVenditore(String email) {
        return venditoreRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Venditore non trovato"));
    }

    @Transactional
    public Pacchetto creaPacchetto(String venditoreEmail, Pacchetto pacchettoInput) {
        Venditore venditore = getVenditore(venditoreEmail);

        pacchettoInput.setVenditore(venditore);
        pacchettoInput.setStatoConferma(Conferma.ATTESA);

        return pacchettoRepository.save(pacchettoInput);
    }

    @Transactional
    public Pacchetto aggiungiItems(Long pacchettoId, List<PacchettoItem> items) {
        Pacchetto p = pacchettoRepository.findById(pacchettoId)
                .orElseThrow(() -> new RuntimeException("Pacchetto non trovato"));

        for (PacchettoItem item : items) {
            p.addItem(item);
            itemRepository.save(item);
        }

        return pacchettoRepository.save(p);
    }

    public Pacchetto getPacchettoById(Long id) {
        return pacchettoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pacchetto non trovato"));
    }

    public List<Pacchetto> getPacchettiVisibili() {
        return pacchettoRepository.findByStatoConferma(Conferma.APPROVATO);
    }

    @Transactional(readOnly = true)
    public List<Pacchetto> getPacchettiInAttesa() {
        return pacchettoRepository.findByStatoConferma(Conferma.ATTESA);
    }

    public List<Pacchetto> getMieiPacchetti(String email) {
        Venditore v = getVenditore(email);
        return pacchettoRepository.findByVenditore(v);
    }

    @Transactional
    public void approvaPacchetto(Long id, String note) {
        Pacchetto p = getPacchettoById(id);
        if (p.getStatoConferma() != Conferma.ATTESA)
            throw new IllegalStateException("Il pacchetto non è in ATTESA.");
        p.setStatoConferma(Conferma.APPROVATO);
        pacchettoRepository.save(p);
    }

    @Transactional
    public void rifiutaPacchetto(Long id, String motivo) {
        Pacchetto p = getPacchettoById(id);
        if (p.getStatoConferma() != Conferma.ATTESA)
            throw new IllegalStateException("Il pacchetto non è in ATTESA.");
        p.setStatoConferma(Conferma.RIFIUTATO);
        pacchettoRepository.save(p);
    }
}