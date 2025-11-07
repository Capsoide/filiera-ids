package it.unicam.cs.ids.filieraids;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
// Importa i package corretti
import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;
// Importa i REPOSITORY che ci servono per il setup
import it.unicam.cs.ids.filieraids.repository.UtenteRepository;
import it.unicam.cs.ids.filieraids.repository.VenditoreRepository;
// Importa il PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@SpringBootApplication
public class FilieraAgricolaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilieraAgricolaApplication.class, args);
    }

    // COMMENTA QUESTO BEAN PER DISATTIVARE IL TEST
    @Bean
    @Transactional
    CommandLineRunner testInMemoria(
            ProdottoService prodottoService,
            CuratoreService curatoreService,
            OrdineService ordineService,
            CarrelloService carrelloService,
            UtenteRepository utenteRepository,
            VenditoreRepository venditoreRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            System.out.println("\n--- SETUP: CREAZIONE SERVICE ---");
            System.out.println("Service e Repository iniettati da Spring.");

            // === CREAZIONE ATTORI ===
            System.out.println("\n--- SETUP: CREAZIONE ATTORI ---");

            // Crittografa le password
            String passNicola = passwordEncoder.encode("pass123");
            String passMartina = passwordEncoder.encode("pass123");
            String passLuca = passwordEncoder.encode("pass123");
            String passPaolo = passwordEncoder.encode("passV1");
            String passMaria = passwordEncoder.encode("passV2");
            String passGiulia = passwordEncoder.encode("passCuratore");

            // Acquirente 1
            Utente u1 = new Utente("nicola.capancioni@studenti.unicam.com", passNicola, "Nicola", "Capancioni");
            Indirizzo ind1 = new Indirizzo("Via Morale da Fermo", "7", "Fermo", "63900", "Marche");
            u1.addIndirizzo(ind1);
            Pagamento pag1 = new Pagamento("Visa", "1111-...", "Nicola Capancioni");

            // Acquirente 2
            Utente u2 = new Utente("martina.frolla@studenti.unicam.com", passMartina, "Martina", "Frolla");
            Indirizzo ind2 = new Indirizzo("Corso Garibaldi", "10", "Civitanova", "62012", "Marche");
            u2.addIndirizzo(ind2);
            Pagamento pag2 = new Pagamento("Mastercard", "2222-...", "Martina Frolla");

            // Acquirente 3
            Utente u3 = new Utente("luca.rossi@email.com", passLuca, "Luca", "Rossi");
            Indirizzo ind3 = new Indirizzo("Via Roma", "1", "Ancona", "60100", "Marche");
            u3.addIndirizzo(ind3);
            Pagamento pag3 = new Pagamento("Amex", "3333-...", "Luca Rossi");

            // Venditore 1
            Venditore v1 = new Venditore(
                    "paolo.verdi@email.com", passPaolo, "Paolo", "Verdi",
                    "111222333", "Azienda Agricola Verdi",
                    Set.of(Ruolo.PRODUTTORE)
            );

            // Venditore 2
            Venditore v2 = new Venditore(
                    "maria.bianchi@email.com", passMaria, "Maria", "Bianchi",
                    "444555666", "Oleificio Bianchi",
                    Set.of(Ruolo.TRASFORMATORE, Ruolo.DISTRIBUTORE)
            );

            // Curatore
            Utente curatore = new Utente("curatore@email.com", passGiulia, "Giulia", "Neri");
            curatore.setRuoli(Set.of(Ruolo.CURATORE));

            // Salvataggio nel DB
            utenteRepository.saveAll(List.of(u1, u2, u3, curatore));
            venditoreRepository.saveAll(List.of(v1, v2));

            System.out.println("Attori creati e salvati nel DB.");

            // === CREAZIONE PRODOTTI ===
            System.out.println("\n--- SETUP: CREAZIONE PRODOTTI ---");
            Prodotto p1_miele = prodottoService.creaProdotto(new Date(), "Miele Bio", "Miele Bio", "Naturale", 7.50, v1, List.of("BIO"), new Date(), 20); // Stock 20
            Prodotto p2_farina = prodottoService.creaProdotto(new Date(), "Farina 00", "Farina 00", "Tradizionale", 3.20, v1, List.of("KM0"), new Date(), 50); // Stock 50
            Prodotto p3_olio = prodottoService.creaProdotto(new Date(), "Olio EVO", "Olio EVO", "Biologico", 12.00, v2, List.of("BIO"), new Date(), 30); // Stock 30
            Prodotto p4_vino = prodottoService.creaProdotto(new Date(), "Vino Rosso", "Vino Rosso", "DOC", 9.00, v1, List.of("DOC"), new Date(), 40);

            // === APPROVAZIONE CONTENUTI ===
            System.out.println("\n--- SETUP: APPROVAZIONE CONTENUTI ---");
            curatoreService.approvaContenuto(curatore, p1_miele, "OK");
            curatoreService.approvaContenuto(curatore, p2_farina, "OK");
            curatoreService.approvaContenuto(curatore, p3_olio, "OK");
            curatoreService.rifiutaContenuto(curatore, p4_vino, "Manca DOC");

            System.out.println("\n--- Catalogo Prodotti Visibili ---");
            prodottoService.getProdottiVisibili().forEach(p -> System.out.println(" - " + p.getNome() + " (Stock: " + p.getQuantita() + ")"));


            // === SCENARIO 1: ACQUISTO NICOLA (u1) ===
            System.out.println("\n--- SCENARIO 1: ACQUISTO (Nicola) ---");
            Carrello c_nicola = u1.getCarrello();
            carrelloService.aggiungiAlCarrello(c_nicola, p2_farina, 20);
            carrelloService.aggiungiAlCarrello(c_nicola, p3_olio, 5);
            carrelloService.aggiungiAlCarrello(c_nicola, p4_vino, 10); // Fallisce (Rifiutato)
            System.out.println("...Checkout Nicola...");
            Ordine o1_nicola = ordineService.creaOrdine(u1, c_nicola, pag1, ind1);
            System.out.println("Ordine 1 (Nicola) creato. ID: " + o1_nicola.getId());


            // === SCENARIO 2: ACQUISTO MARTINA (u2) ===
            System.out.println("\n--- SCENARIO 2: ACQUISTO (Martina) ---");
            Carrello c_martina = u2.getCarrello();
            System.out.println("...Martina controlla stock...");
            Prodotto p2_live_1 = prodottoService.getProdottoById(p2_farina.getId());
            Prodotto p3_live_1 = prodottoService.getProdottoById(p3_olio.getId());
            System.out.println("Stock Farina attuale: " + p2_live_1.getQuantita());
            System.out.println("Stock Olio attuale: " + p3_live_1.getQuantita());

            System.out.println("...Martina aggiunge 40 Farina (fallirà)...");
            carrelloService.aggiungiAlCarrello(c_martina, p2_live_1, 40); // Fallisce (Stock 30)
            carrelloService.aggiungiAlCarrello(c_martina, p2_live_1, 25);
            System.out.println("...Checkout Martina...");
            Ordine o2_martina = ordineService.creaOrdine(u2, c_martina, pag2, ind2);
            System.out.println("Ordine 2 (Martina) creato. ID: " + o2_martina.getId());


            // === SCENARIO 3: ACQUISTO LUCA (u3) ===
            System.out.println("\n--- SCENARIO 3: ACQUISTO (Luca) ---");
            Carrello c_luca = u3.getCarrello();
            Prodotto p2_live_2 = prodottoService.getProdottoById(p2_farina.getId());
            Prodotto p3_live_2 = prodottoService.getProdottoById(p3_olio.getId());
            System.out.println("Stock Farina attuale: " + p2_live_2.getQuantita());
            System.out.println("Stock Olio attuale: " + p3_live_2.getQuantita());

            System.out.println("...Luca aggiunge 10 Farina (fallirà)...");
            carrelloService.aggiungiAlCarrello(c_luca, p2_live_2, 10); // Fallisce (Stock 5)
            carrelloService.aggiungiAlCarrello(c_luca, p2_live_2, 5);
            carrelloService.aggiungiAlCarrello(c_luca, p3_live_2, 10);
            System.out.println("...Checkout Luca...");
            Ordine o3_luca = ordineService.creaOrdine(u3, c_luca, pag3, ind3);
            System.out.println("Ordine 3 (Luca) creato. ID: " + o3_luca.getId());


            // === SCENARIO 4: GESTIONE ORDINI (Stati) ===
            System.out.println("\n--- SCENARIO 4: GESTIONE ORDINI ---");
            Prodotto p2_post_ordini = prodottoService.getProdottoById(p2_farina.getId());
            Prodotto p3_post_ordini = prodottoService.getProdottoById(p3_olio.getId());
            System.out.println("Stock Finale Farina: " + p2_post_ordini.getQuantita());
            System.out.println("Stock Finale Olio: " + p3_post_ordini.getQuantita());

            System.out.println("...Aggiornamento stati...");
            ordineService.aggiornaStatoOrdine(o1_nicola, StatoOrdine.SPEDITO);
            ordineService.aggiornaStatoOrdine(o1_nicola, StatoOrdine.CONSEGNATO);
            ordineService.aggiornaStatoOrdine(o2_martina, StatoOrdine.SPEDITO);
            // o3_luca rimane in ATTESA

            // --- CORREZIONE BUG INCONGRUENZA (riga 149-152) ---
            // Ricarichiamo l'ordine 1 dal DB per vederne lo stato aggiornato
            Ordine o1_nicola_aggiornato = ordineService.getOrdineById(o1_nicola.getId());
            System.out.println("Stato finale Ordine 1: " + o1_nicola_aggiornato.getStatoOrdine()); // Ora stamperà CONSEGNATO
            // --- FINE CORREZIONE ---


            // === SCENARIO 5: ANNULLAMENTI (Casi Limite) ===
            System.out.println("\n--- SCENARIO 5: ANNULLAMENTI ---");

            System.out.println("...Tentativo annullamento Ordine 1 (CONSEGNATO)...");
            ordineService.annullaOrdine(o1_nicola_aggiornato); // Fallirà (usiamo l'oggetto aggiornato)

            System.out.println("...Tentativo annullamento Ordine 2 (SPEDITO)...");
            ordineService.annullaOrdine(o2_martina); // Fallirà

            System.out.println("...Tentativo annullamento Ordine 3 (ATTESA)...");
            ordineService.annullaOrdine(o3_luca); // Successo


            // === SCENARIO 6: VERIFICA RIPRISTINO STOCK ===
            System.out.println("\n--- SCENARIO 6: VERIFICA RIPRISTINO STOCK ---");
            Prodotto p2_post_annullamento = prodottoService.getProdottoById(p2_farina.getId());
            Prodotto p3_post_annullamento = prodottoService.getProdottoById(p3_olio.getId());
            System.out.println("Stock Farina (dopo annullamento o3): " + p2_post_annullamento.getQuantita());
            System.out.println("Stock Olio (dopo annullamento o3): " + p3_post_annullamento.getQuantita());


            // === 10. REPORT FINALE ===
            System.out.println("\n--- REPORT FINALE ---");

            // (Il bug LazyInitializationException è stato risolto
            //  modificando Ordine.toString() per usare utente.getId())

            System.out.println("\n--- Ordini totali di Nicola (u1) ---");
            ordineService.getOrdiniByUtente(u1).forEach(System.out::println);

            System.out.println("\n--- Ordini totali di Martina (u2) ---");
            ordineService.getOrdiniByUtente(u2).forEach(System.out::println);

            System.out.println("\n--- Ordini totali di Luca (u3) ---");
            ordineService.getOrdiniByUtente(u3).forEach(System.out::println);

            System.out.println("\n--- Ordini totali in gestione (Gestore) ---");
            ordineService.getTuttiGliOrdini().forEach(System.out::println);

            System.out.println("\n=== TEST STRESS COMPLETATO ===\n");
        };
    }
}