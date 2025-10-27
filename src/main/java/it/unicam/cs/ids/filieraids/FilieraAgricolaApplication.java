package it.unicam.cs.ids.filieraids;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.service.*;

import java.util.*;

@SpringBootApplication
public class FilieraAgricolaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilieraAgricolaApplication.class, args);
    }

    @Bean
    CommandLineRunner testInMemoria() {
        return args -> {

            System.out.println("\n--- SETUP: CREAZIONE SERVICE ---");
            ProdottoService prodottoService = new ProdottoService();
            CuratoreService curatoreService = new CuratoreService();
            OrdineService ordineService = new OrdineService(prodottoService);
            CarrelloService carrelloService = new CarrelloService(prodottoService);
            System.out.println("Service creati e iniettati.");

            // === CREAZIONE ATTORI ===
            System.out.println("\n--- SETUP: CREAZIONE ATTORI ---");

            // Acquirente 1
            Utente u1 = new Utente("nicola.capancioni@studenti.unicam.com", "capsone", "Nicola", "Capancioni");
            Indirizzo ind1 = new Indirizzo("Via Morale da Fermo", "7", "Fermo", "63900", "Marche");
            u1.addIndirizzo(ind1);
            Pagamento pag1 = new Pagamento("Visa", "1234-...", "Nicola Capancioni");

            // Acquirente 2
            Utente u2 = new Utente("martina.frolla@studenti.unicam.com", "rossabis", "Martina", "Frolla");
            Indirizzo ind2 = new Indirizzo("Corso Garibaldi", "10", "Civitanova", "62012", "Marche");
            u2.addIndirizzo(ind2);
            Pagamento pag2 = new Pagamento("Mastercard", "5678-...", "Martina Frolla");

            // Venditore (con Nome e Cognome)
            Venditore v1 = new Venditore(
                    "paolo.verdi@email.com", "pass", "Paolo", "Verdi",
                    "11122233344", "Distributore locale",
                    Set.of(Ruolo.DISTRIBUTORE, Ruolo.PRODUTTORE)
            );

            // Curatore
            Utente curatore = new Utente("curatore@email.com", "securepass", "Giulia", "Bianchi");
            curatore.addRuolo(Ruolo.CURATORE);
            curatore.removeRuolo(Ruolo.ACQUIRENTE);

            System.out.println("Attori creati:");
            System.out.println(" - Acquirente 1: " + u1.getNome());
            System.out.println(" - Acquirente 2: " + u2.getNome());
            System.out.println(" - Venditore: " + v1.getNome() + " " + v1.getCognome());
            System.out.println(" - Curatore: " + curatore.getNome());


            // === CREAZIONE PRODOTTI ===
            System.out.println("\n--- SETUP: CREAZIONE PRODOTTI ---");
            Prodotto p1 = prodottoService.creaProdotto(new Date(), "Miele millefiori biologico", "Miele Bio", "Naturale", 7.50, v1, List.of("BIO", "KM0"), new Date(), 20);
            Prodotto p2 = prodottoService.creaProdotto(new Date(), "Farina di grano tenero", "Farina tipo 0", "Tradizionale", 3.20, v1, List.of("BIO", "KM0"), new Date(), 50);
            Prodotto p3 = prodottoService.creaProdotto(new Date(), "Olio extravergine d'oliva", "Olio EVO", "Biologico", 12.00, v1, List.of("BIO"), new Date(), 10);
            Prodotto p4 = prodottoService.creaProdotto(new Date(), "Vino Rosso Piceno DOC", "Vino Rosso", "DOC", 9.00, v1, List.of("DOC"), new Date(), 30);

            // === APPROVAZIONE CONTENUTI ===
            System.out.println("\n--- SETUP: APPROVAZIONE CONTENUTI ---");
            curatoreService.rifiutaContenuto(curatore, p1, "Certificazione incompleta"); // RIFIUTATO
            curatoreService.approvaContenuto(curatore, p2, "Prodotto conforme"); // APPROVATO
            curatoreService.approvaContenuto(curatore, p3, "Prodotto conforme"); // APPROVATO
            // p4 rimane in ATTESA

            System.out.println("\n--- Prodotti Visibili (Solo APPROVATI) ---");
            prodottoService.getProdottiVisibili()
                    .forEach(p -> System.out.println(" - " + p.getNome() + " (" + p.getStatoConferma() + ")"));


            // === SCENARIO 1: ACQUISTO NICOLA ===
            System.out.println("\n--- SCENARIO 1: ACQUISTO (Utente: Nicola) ---");
            Carrello carrelloNicola = u1.getCarrello();
            System.out.println("...Nicola riempie il carrello...");
            carrelloService.aggiungiAlCarrello(carrelloNicola, p2, 5);    // OK (Farina x5)
            carrelloService.aggiungiAlCarrello(carrelloNicola, p3, 2);    // OK (Olio x2)
            carrelloService.aggiungiAlCarrello(carrelloNicola, p1, 1);    // Fallirà (Rifiutato)
            carrelloService.aggiungiAlCarrello(carrelloNicola, p4, 1);    // Fallirà (Attesa)

            System.out.println("...Nicola modifica il carrello (rimuove 1 Farina)...");
            carrelloService.diminuisciQuantita(carrelloNicola, p2, 1); // Rimane Farina x4

            System.out.println("Totale Carrello Nicola: " + carrelloNicola.getPrezzoTotale()); // Atteso: (4*3.2) + (2*12) = 12.8 + 24 = 36.8

            System.out.println("...Nicola conclude l'ordine...");
            Ordine ordine1 = ordineService.creaOrdine(u1, carrelloNicola, pag1, ind1);
            System.out.println("Ordine 1 (Nicola) creato. ID: " + ordine1.getId());


            // === SCENARIO 2: ACQUISTO MARTINA ===
            System.out.println("\n--- SCENARIO 2: ACQUISTO (Utente: Martina) ---");
            // Martina vede lo stock aggiornato dopo l'acquisto di Nicola
            System.out.println("Stock Farina (p2) attuale: " + p2.getQuantita()); // Atteso: 50 - 4 = 46
            System.out.println("Stock Olio (p3) attuale: " + p3.getQuantita());   // Atteso: 10 - 2 = 8

            Carrello carrelloMartina = u2.getCarrello();
            System.out.println("...Martina riempie il carrello...");
            carrelloService.aggiungiAlCarrello(carrelloMartina, p2, 10);  // OK (Farina x10)
            carrelloService.aggiungiAlCarrello(carrelloMartina, p3, 10);  // Fallirà (Stock è 8)
            carrelloService.aggiungiAlCarrello(carrelloMartina, p3, 8);   // OK (Olio x8)

            System.out.println("Totale Carrello Martina: " + carrelloMartina.getPrezzoTotale()); // Atteso: (10*3.2) + (8*12) = 32 + 96 = 128

            System.out.println("...Martina conclude l'ordine...");
            Ordine ordine2 = ordineService.creaOrdine(u2, carrelloMartina, pag2, ind2);
            System.out.println("Ordine 2 (Martina) creato. ID: " + ordine2.getId());


            // === SCENARIO 3: GESTIONE ORDINI ===
            System.out.println("\n--- SCENARIO 3: GESTIONE ORDINI (Spedizione) ---");
            System.out.println("\n--- Stock Finale Post-Ordini ---");
            System.out.println("Stock Farina (p2) finale: " + p2.getQuantita()); // Atteso: 46 - 10 = 36
            System.out.println("Stock Olio (p3) finale: " + p3.getQuantita());   // Atteso: 8 - 8 = 0

            System.out.println("\n...Spedizione e Consegna Ordine 1 (Nicola)...");
            ordineService.aggiornaStatoOrdine(ordine1, StatoOrdine.SPEDITO);
            ordineService.aggiornaStatoOrdine(ordine1, StatoOrdine.CONSEGNATO);
            System.out.println("Stato finale Ordine 1: " + ordine1.getStatoOrdine());


            // === SCENARIO 4: ANNULLAMENTO ORDINE ===
            System.out.println("\n--- SCENARIO 4: GESTIONE ORDINI (Annullamento) ---");

            System.out.println("\n--- Tentativo di annullare Ordine 1 (CONSEGNATO) ---");
            ordineService.annullaOrdine(ordine1); // Fallirà (Già consegnato)

            System.out.println("\n--- Tentativo di annullare Ordine 2 (ATTESA) ---");
            System.out.println("Stock Farina (p2) prima annullamento: " + p2.getQuantita()); // Atteso: 36
            System.out.println("Stock Olio (p3) prima annullamento: " + p3.getQuantita());   // Atteso: 0

            ordineService.annullaOrdine(ordine2); // Successo

            System.out.println("\n--- Stock Post-Annullamento ---");
            System.out.println("Stock Farina (p2) dopo annullamento: " + p2.getQuantita()); // Atteso: 36 + 10 = 46
            System.out.println("Stock Olio (p3) dopo annullamento: " + p3.getQuantita());   // Atteso: 0 + 8 = 8


            // === REPORT FINALE ===
            System.out.println("\n--- REPORT FINALE ---");

            System.out.println("\n--- Ordini totali di Nicola (u1) ---");
            ordineService.getOrdiniByUtente(u1).forEach(System.out::println); // Mostra Ordine 1 (CONSEGNATO)

            System.out.println("\n--- Ordini totali di Martina (u2) ---");
            ordineService.getOrdiniByUtente(u2).forEach(System.out::println); // Lista vuota (Ordine 2 annullato e rimosso)

            System.out.println("\n--- Ordini totali in gestione (Gestore) ---");
            ordineService.getTuttiGliOrdini().forEach(System.out::println); // Mostra solo Ordine 1

            System.out.println("\n=== TEST SEQUENZIALE COMPLETATO ===\n");
        };
    }
}