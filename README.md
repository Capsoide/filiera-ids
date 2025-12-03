Attori di Test (pre-caricati)
•	Gestore: gestore@filiera.com / gestore123
•	Curatore: curatore@email.com / passCuratore
•	Venditore (Paolo): paolo.verdi@email.com / passV1 (Ruolo: PRODUTTORE. Ha "Vino Rosso" in attesa ID: 4)
•	Venditore (Maria): maria.bianchi@email.com / passV2 (Ruoli: TRASFORMATORE, DISTRIBUTORE)
•	Acquirente (Nicola): nicola.capancioni@studenti.unicam.com / pass123
 
1. Flusso Pubblico (Nessuna Autenticazione)
Questi test verificano cosa può vedere un utente non registrato.
•	GET http://localhost:8080/api/prodotti/visibili
o	Risultato Atteso: 200 OK. Mostra "Miele Bio", "Farina 00", "Olio EVO". Non deve mostrare "Vino Rosso" (ID 4).
•	GET http://localhost:8080/api/prodotti/visibili/4
o	Risultato Atteso: 404 NOT FOUND (o 409 CONFLICT a seconda del GlobalExceptionHandler). L'utente non può vedere un prodotto non approvato.
•	GET http://localhost:8080/api/eventi/visibili
o	Risultato Atteso: 200 OK. Lista vuota [] (non abbiamo ancora creato eventi).
•	GET http://localhost:8080/api/carrello
o	Risultato Atteso: 401 UNAUTHORIZED. L'endpoint è protetto.
 
2. Flusso di Registrazione (Auth)
Testiamo la creazione di nuovi account.
•	POST http://localhost:8080/api/auth/registra/acquirente
o	(Nessuna Autenticazione)
o	Body: { "email": "test.acquirente@mail.com", "password": "pass", "nome": "Test", "cognome": "Acquirente" }
o	Risultato Atteso: 201 CREATED. L'utente è creato e enabled: true.
•	POST http://localhost:8080/api/auth/registra/acquirente
o	(Nessuna Autenticazione)
o	Body: { "email": "test.acquirente@mail.com", ... } (stesso utente)
o	Risultato Atteso: 409 CONFLICT (Email già in uso).
•	POST http://localhost:8080/api/auth/registra/venditore
o	(Nessuna Autenticazione)
o	Body: { "email": "test.venditore@mail.com", "password": "pass", "nome": "Test", "cognome": "Venditore", "PIVA": "123", "ruoli": ["PRODUTTORE"] }
o	Risultato Atteso: 202 ACCEPTED. L'utente è creato (enabled: false) e una RichiestaRuolo è stata generata.
•	POST http://localhost:8080/api/auth/registra/venditore
o	(Nessuna Autenticazione)
o	Body: { "email": "test.animatore@mail.com", "password": "pass", "nome": "Test", "cognome": "Animatore", "PIVA": "456", "ruoli": ["ANIMATORE"] }
o	Risultato Atteso: 202 ACCEPTED.
 
3. Flusso Gestore (Approvazione Ruoli)
Ora il Gestore approva i nuovi utenti.
•	Autenticazione: gestore@filiera.com / gestore123
•	GET http://localhost:8080/api/gestore/richieste-ruolo
o	Risultato Atteso: 200 OK. Mostra le richieste di test.venditore@mail.com e test.animatore@mail.com. Prendi nota dei loro id (es. 1 e 2).
•	POST http://localhost:8080/api/gestore/richieste-ruolo/1/approva (Approva il venditore)
o	Risultato Atteso: 200 OK. Il venditore è ora enabled: true.
•	POST http://localhost:8080/api/gestore/richieste-ruolo/2/rifiuta (Rifiuta l'animatore)
o	Risultato Atteso: 200 OK.
•	GET http://localhost:8080/api/gestore/richieste-ruolo
o	Risultato Atteso: 200 OK. La lista ora dovrebbe essere vuota.
•	Verifica (Test Login):
o	Prova a loggarti come test.venditore@mail.com / pass. Funziona.
o	Prova a loggarti come test.animatore@mail.com / pass. Fallisce (401 Unauthorized, l'account non è enabled).
 
4. Flusso Venditore (Gestione Prodotti)
•	Autenticazione: paolo.verdi@email.com / passV1 (il PRODUTTORE pre-caricato)
•	POST http://localhost:8080/api/prodotti
o	Body: { "nome": "Marmellata Bio", "descrizione": "Fatta in casa", "prezzo": 6.50, "quantita": 50 }
o	Risultato Atteso: 200 OK. Prodotto creato con statoConferma: ATTESA. Prendi nota del suo id (es. 5).
•	GET http://localhost:8080/api/prodotti/miei
o	Risultato Atteso: 200 OK. Mostra "Vino Rosso" (ID 4, ATTESA) e "Marmellata Bio" (ID 5, ATTESA), oltre ai suoi prodotti già approvati.
•	GET http://localhost:8080/api/carrello
o	Risultato Atteso: 403 FORBIDDEN. Un venditore non può avere un carrello.
 
5. Flusso Curatore (Approvazione Contenuti)
Ora il Curatore approva i contenuti in sospeso.
•	Autenticazione: curatore@email.com / passCuratore
•	GET http://localhost:8080/api/curatore/da-approvare
o	Risultato Atteso: 200 OK. Mostra "Vino Rosso" (ID 4) e "Marmellata Bio" (ID 5).
•	POST http://localhost:8080/api/curatore/approva/4 (Approva il Vino)
o	Body (raw, text): "OK"
o	Risultato Atteso: 200 OK.
•	POST http://localhost:8080/api/curatore/rifiuta/5 (Rifiuta la Marmellata)
o	Body (raw, text): "Etichetta non conforme"
o	Risultato Atteso: 200 OK.
•	GET http://localhost:8080/api/curatore/da-approvare
o	Risultato Atteso: 200 OK. La lista ora è vuota.
•	Verifica (Pubblico):
o	GET http://localhost:8080/api/prodotti/visibili (Nessuna Autenticazione).
o	Risultato Atteso: La lista ora include "Vino Rosso" (ID 4).
 
6. Flusso Animatore (Gestione Eventi)
Per questo flusso, devi prima creare e approvare un Animatore (vedi Flusso 2 e 3). Assumiamo di aver approvato test.animatore@mail.com.
•	Autenticazione: test.animatore@mail.com / pass
•	POST http://localhost:8080/api/eventi
o	Body: { "nome": "Sagra del Miele", "descrizione": "Evento con Paolo Verdi", "dataEvento": "2026-05-10T19:00:00.000+00:00", "indirizzo": { "via": "Piazza", "numCivico": "1", "comune": "Camerino", "CAP": "62032", "regione": "Marche" }, "postiDisponibili": 100 }
o	Risultato Atteso: 200 OK. Evento creato (ID es. 1) con statoConferma: ATTESA.
•	GET http://localhost:8080/api/eventi/miei
o	Risultato Atteso: 200 OK. Mostra "Sagra del Miele" (ID 1, ATTESA).
•	Verifica (Curatore):
o	GET http://localhost:8080/api/curatore/da-approvare (Autenticazione: Curatore).
o	Risultato Atteso: Mostra la "Sagra del Miele" (ID 1).
o	POST http://localhost:8080/api/curatore/approva/1 (Autenticazione: Curatore).
o	Risultato Atteso: 200 OK.
•	Verifica (Pubblico):
o	GET http://localhost:8080/api/eventi/visibili (Nessuna Autenticazione).
o	Risultato Atteso: Mostra "Sagra del Miele" (ID 1).
•	Flusso Inviti (Animatore):
o	Autenticazione: test.animatore@mail.com / pass
o	POST http://localhost:8080/api/eventi/1/invita/1 (Invita Paolo Verdi - ID 1, assunto dal DB)
o	Risultato Atteso: 200 OK.
o	GET http://localhost:8080/api/eventi/1/invitati
o	Risultato Atteso: 200 OK. Mostra i dettagli di Paolo Verdi.
 
7. Flusso Acquirente (Carrello, Ordini, Prenotazioni)
•	Autenticazione: nicola.capancioni@studenti.unicam.com / pass123
•	GET http://localhost:8080/api/carrello
o	Risultato Atteso: 200 OK. Carrello vuoto.
•	POST http://localhost:8080/api/carrello/aggiungi?prodottoId=4&quantita=2 (Aggiunge 2 Vini Rossi)
o	Risultato Atteso: 200 OK. Carrello aggiornato.
•	POST http://localhost:8080/api/carrello/aggiungi?prodottoId=5&quantita=1 (Prova ad aggiungere Marmellata RIFIUTATA)
o	Risultato Atteso: 409 CONFLICT (Prodotto non approvato).
•	POST http://localhost:8080/api/carrello/aggiungi?prodottoId=4&quantita=999 (Stock insufficiente, ne ha 40)
o	Risultato Atteso: 409 CONFLICT (Stock non sufficiente).
•	POST http://localhost:8080/api/carrello/diminuisci?prodottoId=4&quantita=1
o	Risultato Atteso: 200 OK. Quantità nel carrello è 1.
•	POST http://localhost:8080/api/ordini
o	Risultato Atteso: 200 OK. Ordine creato.
•	GET http://localhost:8080/api/carrello
o	Risultato Atteso: 200 OK. Carrello di nuovo vuoto.
•	GET http://localhost:8080/api/ordini
o	Risultato Atteso: 200 OK. Mostra l'ordine appena creato.
•	Flusso Prenotazioni:
o	GET http://localhost:8080/api/prenotazioni/miei
o	Risultato Atteso: 200 OK. Lista vuota.
o	POST http://localhost:8080/api/prenotazioni/eventi/1?numeroPosti=3 (Prenota 3 posti per la Sagra)
o	Risultato Atteso: 201 CREATED. Prenotazione creata (ID es. 1).
o	GET http://localhost:8080/api/prenotazioni/miei
o	Risultato Atteso: 200 OK. Mostra la prenotazione (ID 1).
o	DELETE http://localhost:8080/api/prenotazioni/1 (Annulla la prenotazione)
o	Risultato Atteso: 200 OK.
o	GET http://localhost:8080/api/prenotazioni/miei
o	Risultato Atteso: 200 OK. Lista vuota.
 
8. Flusso di Eliminazione (Cascata)
•	Verifica (Animatore):
o	Autenticazione: test.animatore@mail.com / pass
o	GET http://localhost:8080/api/eventi/1/prenotazioni
o	Risultato Atteso: 200 OK. Lista vuota (Nicola ha annullato).
o	DELETE http://localhost:8080/api/eventi/1 (Elimina la Sagra)
o	Risultato Atteso: 200 OK.
•	Verifica (Pubblico):
o	GET http://localhost:8080/api/eventi/visibili (Nessuna Autenticazione).
o	Risultato Atteso: 200 OK. Lista vuota.
 

9. Altre chiamate
Svuota Carrello (Acquirente)
•	Endpoint: DELETE /api/carrello/svuota
•	Come testarlo: Fai il Flusso 7 (Acquirente), aggiungi 2-3 prodotti al carrello, poi esegui questa chiamata.
•	Autenticazione: nicola.capancioni@studenti.unicam.com / pass123
•	Risultato Atteso: 200 OK. Il carrello torna vuoto.
 Vedi Tutti gli Ordini (Gestore/Admin)
•	Endpoint: GET /api/ordini/tutti
•	Come testarlo: Dopo che Nicola ha creato un ordine (Flusso 7), esegui questa chiamata.
•	Autenticazione: gestore@filiera.com / gestore123 (Nota: la tua SecurityConfig attuale non protegge questo endpoint in modo specifico, quindi è coperto da .anyRequest().authenticated(). Probabilmente solo il Gestore dovrebbe vederlo).
•	Risultato Atteso: 200 OK. Mostra l'ordine di Nicola.
Test di Sicurezza Esplicito (403 Forbidden)
•	Endpoint: GET /api/carrello
•	Come testarlo: Prova a chiamare un endpoint da acquirente usando un account venditore.
•	Autenticazione: paolo.verdi@email.com / passV1
•	Risultato Atteso: 403 FORBIDDEN (Accesso Negato). Questo testa il GlobalExceptionHandler e la SecurityConfig.
