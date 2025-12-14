package it.unicam.cs.ids.filieraids;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
import it.unicam.cs.ids.filieraids.repository.UtenteRepository;
import it.unicam.cs.ids.filieraids.repository.VenditoreRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@SpringBootApplication
public class FilieraAgricolaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilieraAgricolaApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner testSetupDatabase(
            ProdottoService prodottoService,
            CuratoreService curatoreService,
            UtenteRepository utenteRepository,
            VenditoreRepository venditoreRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            System.out.println("\n--- SETUP: CREAZIONE SERVICE ---");
            System.out.println("Service e Repository iniettati da Spring.");

            System.out.println("\n--- SETUP: CREAZIONE ATTORI ---");

            String passNicola = passwordEncoder.encode("pass123");
            Utente u1 = new Utente("nicola.capancioni@studenti.unicam.com", passNicola, "Nicola", "Capancioni");
            u1.addIndirizzo(new Indirizzo("Via Morale da Fermo", "7", "Fermo", "63900", "Marche"));
            u1.setEnabled(true);
            u1.setRuoli(Set.of(Ruolo.ACQUIRENTE));

            Utente u2 = new Utente("martina.frolla@studenti.unicam.com", passwordEncoder.encode("pass123"), "Martina", "Frolla");
            u2.addIndirizzo(new Indirizzo("Corso Garibaldi", "10", "Civitanova", "62012", "Marche"));
            u2.setEnabled(true);
            u2.setRuoli(Set.of(Ruolo.ACQUIRENTE));

            Utente u3 = new Utente("luca.rossi@email.com", passwordEncoder.encode("pass123"), "Luca", "Rossi");
            u3.addIndirizzo(new Indirizzo("Via Roma", "1", "Ancona", "60100", "Marche"));
            u3.setEnabled(true);
            u3.setRuoli(Set.of(Ruolo.ACQUIRENTE));

            Venditore v1 = new Venditore(
                    "paolo.verdi@email.com", passwordEncoder.encode("passV1"), "Paolo", "Verdi",
                    "111222333", "Azienda Agricola Verdi", Set.of(Ruolo.PRODUTTORE)
            );
            v1.addIndirizzo(new Indirizzo(
                    "Via dei Velini", "15", "Macerata", "62100", "Marche",
                    43.2980, 13.4500
            ));
            v1.setEnabled(true);

            Venditore v2 = new Venditore(
                    "maria.bianchi@email.com", passwordEncoder.encode("passV2"), "Maria", "Bianchi",
                    "444555666", "Oleificio Bianchi", Set.of(Ruolo.TRASFORMATORE, Ruolo.DISTRIBUTORE)
            );
            v2.addIndirizzo(new Indirizzo(
                    "Piazza Arringo", "2", "Ascoli Piceno", "63100", "Marche",
                    42.8530, 13.5750
            ));
            v2.setEnabled(true);

            Utente curatore = new Utente("curatore@email.com", passwordEncoder.encode("passCuratore"), "Giulia", "Neri");
            curatore.setRuoli(Set.of(Ruolo.CURATORE));
            curatore.setEnabled(true);

            Utente gestore = new Utente("gestore@filiera.com", passwordEncoder.encode("gestore123"), "Admin", "Gestore");
            gestore.setRuoli(Set.of(Ruolo.GESTORE));
            gestore.setEnabled(true);

            Utente animatore = new Utente("animatore@filiera.com", passwordEncoder.encode("animatore123"),
                    "Andrea", "Animatore");
            animatore.setRuoli(Set.of(Ruolo.ANIMATORE));
            animatore.setEnabled(true);

            utenteRepository.saveAll(List.of(u1, u2, u3, curatore, gestore, animatore));
            venditoreRepository.saveAll(List.of(v1, v2));

            System.out.println("Attori di test creati e salvati nel DB.");

            System.out.println("\n--- SETUP: CREAZIONE PRODOTTI ---");
            Prodotto p1_miele = prodottoService.creaProdotto(new Date(), "Miele Bio", "Miele Bio", "Naturale", 7.50, v1, List.of("BIO"), new Date(), 20);
            Prodotto p2_farina = prodottoService.creaProdotto(new Date(), "Farina 00", "Farina 00", "Tradizionale", 3.20, v1, List.of("KM0"), new Date(), 50);
            Prodotto p3_olio = prodottoService.creaProdotto(new Date(), "Olio EVO", "Olio EVO", "Biologico", 12.00, v2, List.of("BIO"), new Date(), 30);
            Prodotto p4_vino = prodottoService.creaProdotto(new Date(), "Vino Rosso", "Vino Rosso", "DOC", 9.00, v1, List.of("DOC"), new Date(), 40);

            System.out.println("\n--- SETUP: APPROVAZIONE CONTENUTI ---");

            curatoreService.approvaContenuto(p1_miele.getId(), curatore.getEmail(), "OK");
            curatoreService.approvaContenuto(p2_farina.getId(), curatore.getEmail(), "OK");
            curatoreService.approvaContenuto(p3_olio.getId(), curatore.getEmail(), "OK");

            System.out.println("\n--- Catalogo Prodotti Visibili ---");
            prodottoService.getProdottiVisibili().forEach(p -> System.out.println(" - " + p.getNome() + " (Stock: " + p.getQuantita() + ")"));

            System.out.println("\n--- Prodotto da Approvare (per Curatore) ---");
            System.out.println(" - " + p4_vino.getNome() + " (ID: " + p4_vino.getId() + ")");

            System.out.println("\n=== DATABASE DI TEST PRONTO ===\n");
            System.out.println("Avviare i test API con Postman...");
            System.out.println("Gestore: gestore@filiera.com / gestore123");
            System.out.println("Curatore: curatore@email.com / passCuratore");
            System.out.println("Animatore: animatore@filiera.com / animatore123");
        };
    }
}