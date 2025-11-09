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

    /**
     * Questo Bean ora serve solo a POPOLARE IL DATABASE
     * con dati di test (utenti, prodotti) per poterli
     * usare con Postman.
     * La vecchia logica di test (ordini, carrello) è stata rimossa
     * perché ora va testata tramite le API REST.
     */
    @Bean
    @Transactional
    CommandLineRunner testSetupDatabase(
            ProdottoService prodottoService,
            CuratoreService curatoreService,
            UtenteRepository utenteRepository,
            VenditoreRepository venditoreRepository,
            PasswordEncoder passwordEncoder
            // Nota: OrdineService e CarrelloService non servono più qui
    ) {
        return args -> {

            System.out.println("\n--- SETUP: CREAZIONE SERVICE ---");
            System.out.println("Service e Repository iniettati da Spring.");

            // === 1. CREAZIONE ATTORI ===
            System.out.println("\n--- SETUP: CREAZIONE ATTORI ---");

            // Crittografa le password
            String passNicola = passwordEncoder.encode("pass123");
            String passMartina = passwordEncoder.encode("pass123");
            String passLuca = passwordEncoder.encode("pass123");
            String passPaolo = passwordEncoder.encode("passV1");
            String passMaria = passwordEncoder.encode("passV2");
            String passGiulia = passwordEncoder.encode("passCuratore");

            // Acquirente 1 (Abilitato subito)
            Utente u1 = new Utente("nicola.capancioni@studenti.unicam.com", passNicola, "Nicola", "Capancioni");
            u1.addIndirizzo(new Indirizzo("Via Morale da Fermo", "7", "Fermo", "63900", "Marche"));
            u1.setEnabled(true);
            u1.setRuoli(Set.of(Ruolo.ACQUIRENTE));

            // Acquirente 2 (Abilitato subito)
            Utente u2 = new Utente("martina.frolla@studenti.unicam.com", passMartina, "Martina", "Frolla");
            u2.addIndirizzo(new Indirizzo("Corso Garibaldi", "10", "Civitanova", "62012", "Marche"));
            u2.setEnabled(true);
            u2.setRuoli(Set.of(Ruolo.ACQUIRENTE));

            // Acquirente 3 (Abilitato subito)
            Utente u3 = new Utente("luca.rossi@email.com", passLuca, "Luca", "Rossi");
            u3.addIndirizzo(new Indirizzo("Via Roma", "1", "Ancona", "60100", "Marche"));
            u3.setEnabled(true);
            u3.setRuoli(Set.of(Ruolo.ACQUIRENTE));

            // Venditore 1 (Abilitato subito per test)
            Venditore v1 = new Venditore(
                    "paolo.verdi@email.com", passPaolo, "Paolo", "Verdi",
                    "111222333", "Azienda Agricola Verdi",
                    Set.of(Ruolo.PRODUTTORE)
            );
            v1.setEnabled(true);

            // Venditore 2 (Abilitato subito per test)
            Venditore v2 = new Venditore(
                    "maria.bianchi@email.com", passMaria, "Maria", "Bianchi",
                    "444555666", "Oleificio Bianchi",
                    Set.of(Ruolo.TRASFORMATORE, Ruolo.DISTRIBUTORE)
            );
            v2.setEnabled(true);

            // Curatore (Abilitato subito per test)
            Utente curatore = new Utente("curatore@email.com", passGiulia, "Giulia", "Neri");
            curatore.setRuoli(Set.of(Ruolo.CURATORE));
            curatore.setEnabled(true);

            // --- INIZIO AGGIUNTA GESTORE ---
            // Gestore (Abilitato subito per testare l'approvazione)
            String passGestore = passwordEncoder.encode("gestore123");
            Utente gestore = new Utente("gestore@filiera.com", passGestore, "Admin", "Gestore");
            gestore.setRuoli(Set.of(Ruolo.GESTORE)); // Assegna il ruolo
            gestore.setEnabled(true); // Abilitalo subito
            // --- FINE AGGIUNTA GESTORE ---


            // Salvataggio nel DB
            utenteRepository.saveAll(List.of(u1, u2, u3, curatore, gestore));
            venditoreRepository.saveAll(List.of(v1, v2));

            System.out.println("Attori di test creati e salvati nel DB.");

            // === 2. CREAZIONE PRODOTTI ===
            System.out.println("\n--- SETUP: CREAZIONE PRODOTTI ---");
            Prodotto p1_miele = prodottoService.creaProdotto(new Date(), "Miele Bio", "Miele Bio", "Naturale", 7.50, v1, List.of("BIO"), new Date(), 20); // Stock 20
            Prodotto p2_farina = prodottoService.creaProdotto(new Date(), "Farina 00", "Farina 00", "Tradizionale", 3.20, v1, List.of("KM0"), new Date(), 50); // Stock 50
            Prodotto p3_olio = prodottoService.creaProdotto(new Date(), "Olio EVO", "Olio EVO", "Biologico", 12.00, v2, List.of("BIO"), new Date(), 30); // Stock 30
            Prodotto p4_vino = prodottoService.creaProdotto(new Date(), "Vino Rosso", "Vino Rosso", "DOC", 9.00, v1, List.of("DOC"), new Date(), 40); // In Attesa

            // === 3. APPROVAZIONE CONTENUTI ===
            System.out.println("\n--- SETUP: APPROVAZIONE CONTENUTI ---");
            curatoreService.approvaContenuto(curatore, p1_miele, "OK");
            curatoreService.approvaContenuto(curatore, p2_farina, "OK");
            curatoreService.approvaContenuto(curatore, p3_olio, "OK");
            // p4_vino rimane in ATTESA per testare l'approvazione del curatore via API

            System.out.println("\n--- Catalogo Prodotti Visibili ---");
            prodottoService.getProdottiVisibili().forEach(p -> System.out.println(" - " + p.getNome() + " (Stock: " + p.getQuantita() + ")"));

            System.out.println("\n--- Prodotto da Approvare (per Curatore) ---");
            System.out.println(" - " + p4_vino.getNome() + " (ID: " + p4_vino.getId() + ")");

            System.out.println("\n=== DATABASE DI TEST PRONTO ===\n");
            System.out.println("Avviare i test API con Postman...");
            System.out.println("Gestore: gestore@filiera.com / gestore123");
        };
    }
}