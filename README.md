# Scenari di Test in Postman

> ⚠️ **Nota:**
> Gli scenari di test descritti di seguito si basano sui dati di setup (popolamento iniziale) definiti nel file `FilieraAgricolaApplication.java`.
> Se questi dati iniziali sono stati modificati (ad esempio, lo **stock di un prodotto** o i prezzi), i riferimenti numerici specifici in questa guida (es. "Stock Olio = 8" o "totale 16.0") potrebbero non essere più corretti. I risultati attesi potrebbero essere differenti.

---

## Test Flusso Registrazione e Approvazione (Fase 1)

Questi test verificano l'intero ciclo di vita della registrazione e dell'approvazione dei ruoli.

### 1.1: Registrazione e Login ACQUIRENTE (Approvazione Automatica)

* **Obiettivo:** Verificare che un acquirente possa registrarsi ed effettuare il login immediatamente.
* **Test 1 (Registrazione):**
    * **Metodo:** `POST`
    * **URL:** `http://localhost:8080/api/auth/registra/acquirente`
    * **Body (JSON):**
        ```json
        {
          "email": "mario.rossi@email.com",
          "password": "pass",
          "nome": "Mario",
          "cognome": "Rossi"
        }
        ```
    * **Risultato Atteso:** `201 Created`.
* **Test 2 (Login):**
    * **Metodo:** `GET`
    * **URL:** `http://localhost:8080/api/carrello`
    * **Authorization:** Basic Auth -> `mario.rossi@email.com` / `pass`
    * **Risultato Atteso:** `200 OK`. Prova che `setEnabled(true)` ha funzionato.

### 1.2: Registrazione e Login NEGATO (Venditore)

* **Obiettivo:** Verificare che un nuovo venditore si registri ma NON possa effettuare il login perché `enabled` è `false`.
* **Test 3 (Registrazione):**
    * **Metodo:** `POST`
    * **URL:** `http://localhost:8080/api/auth/registra/venditore`
    * **Body (JSON):**
        ```json
        {
          "email": "azienda.agricola@email.com",
          "password": "passV",
          "nome": "Lucia",
          "cognome": "Bianchi",
          "piva": "123456789",
          "descrizione": "La mia azienda",
          "ruoli": ["PRODUTTORE"]
        }
        ```
    * **Risultato Atteso:** `202 Accepted`. L'account è creato ma disabilitato.
* **Test 4 (Login Negato):**
    * **Metodo:** `GET`
    * **URL:** `http://localhost:8080/api/prodotti/visibili`
    * **Authorization:** Basic Auth -> `azienda.agricola@email.com` / `passV`
    * **Risultato Atteso:** `401 Unauthorized`. Spring Security lo blocca (via `CustomUserDetailsService`) perché `enabled` è `false`.

### 1.3: Flusso di Approvazione e Rifiuto (Gestore)

* **Obiettivo:** Verificare che il `GESTORE` possa gestire le richieste.
* **Test 5 (Vedi Richieste):**
    * **Auth:** Basic Auth -> `gestore@filiera.com` / `gestore123`
    * **Azione:** `GET http://localhost:8080/api/gestore/richieste-ruolo`
    * **Risultato Atteso:** `200 OK`. (Prendere nota dell' `id` della richiesta per `azienda.agricola@email.com`, es. `1`).
* **Test 6 (Approva Richiesta):**
    * **Auth:** Basic Auth -> `gestore@filiera.com` / `gestore123`
    * **Azione:** `POST http://localhost:8080/api/gestore/richieste-ruolo/1/approva` (sostituire `1` con l'ID reale).
    * **Risultato Atteso:** `200 OK` ("Richiesta approvata. L'utente è ora abilitato.").
* **Test 7 (Rifiuta Richiesta):**
    * *(Setup: Registrare un secondo venditore, es. `venditore.rifiutato@email.com` e ottenere il suo ID, es. `2`)*.
    * **Auth:** Basic Auth -> `gestore@filiera.com` / `gestore123`
    * **Azione:** `POST http://localhost:8080/api/gestore/richieste-ruolo/2/rifiuta`
    * **Body (raw, text):**
        ```
        "Documentazione non valida."
        ```
    * **Risultato Atteso:** `200 OK` ("Richiesta... rifiutata").

### 1.4: Login RIUSCITO (Venditore Approvato)

* **Obiettivo:** Verificare che l'account sbloccato dal Gestore ora funzioni.
* **Test 8 (Login Riuscito):**
    * **Auth:** Basic Auth -> `azienda.agricola@email.com` / `passV`
    * **Azione:** `POST http://localhost:8080/api/prodotti` (Endpoint protetto per `PRODUTTORE`)
    * **Body (JSON):**
        ```json
        {
          "nome": "Nuove Mele",
          "descrizione": "Appena raccolte",
          "prezzo": 2.50,
          "quantita": 100
        }
        ```
    * **Risultato Atteso:** `201 Created` (o `200 OK`). Il login funziona.

### 1.5: Test di Robustezza (Email Duplicata)

* **Test 9 (Email Duplicata):**
    * **Auth:** `No Auth`
    * **Azione:** Eseguire il Test 1 (`POST /api/auth/registra/acquirente`) una *seconda volta* con lo stesso Body.
    * **Risultato Atteso:** `500 Internal Server Error` (e `IllegalStateException: Errore: Email già in uso.` nel log).

---

## Test sul `CarrelloController`

Questi test verificano la logica di `diminuisci` e `svuota` del carrello.

### 2.1: Aggiungere e Rimuovere Parzialmente

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Azione 1:** `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=2&quantita=5`
    * *Descrizione:* Aggiunge 5 Farine. Lo stock (45) è sufficiente.
* **Risultato 1:** `200 OK`. Il carrello ha 5 Farine, totale 16.0.
* **Azione 2:** `POST http://localhost:8080/api/carrello/diminuisci?prodottoId=2&quantita=2`
    * *Descrizione:* Rimuove 2 Farine.
* **Risultato 2:** `200 OK`. Il carrello ora ha 3 Farine, totale 9.6.

### 2.2: Rimuovere Completamente un Prodotto (tramite `diminuisci`)

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Stato Iniziale:** Il carrello ha 3 Farine (dal test 2.1).
* **Azione:** `POST http://localhost:8080/api/carrello/diminuisci?prodottoId=2&quantita=99`
    * *Logica:* Chiede di rimuoverne 99. Poiché `3 - 99` è `< 0`, la riga del prodotto viene eliminata.
* **Risultato:** `200 OK`. Il carrello ora ha `contenuti: []` e `prezzoTotale: 0.0`.

### 2.3: Svuotare il Carrello

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Azione 1 (Riempi):**
    * `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=2&quantita=2`
    * `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=3&quantita=1`
* **Azione 2 (Svuota):** `DELETE http://localhost:8080/api/carrello/svuota`
* **Risultato 2:** `200 OK`. Il carrello ora ha `contenuti: []` e `prezzoTotale: 0.0`.

---

## Test sull'`OrdineController`

Questi test verificano il fallimento del checkout e la gestione della concorrenza.

### 3.1: Checkout con Carrello Vuoto

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Setup:** Assicurarsi che il carrello di Nicola sia vuoto (es. chiamando `DELETE /api/carrello/svuota`).
* **Azione:** `POST http://localhost:8080/api/ordini`
* **Risultato Atteso:** `409 Conflict` (o `500 Internal Server Error`). Il server blocca la creazione di un ordine vuoto.

### 3.2: Test di Concorrenza (Gestione Stock)

* **Attore 1 (Nicola):** Basic Auth -> `nicola.capancioni@...` / `pass123`
* **Attore 2 (Martina):** Basic Auth -> `martina.frolla@...` / `pass123`
* **Stock Iniziale:** Prodotto "Olio" (ID 3) = 8 unità.

---

* **Azione 1 (Nicola):** `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=3&quantita=5`
    * *Stato:* Nicola aggiunge 5 Oli. (Stock DB: 8)
* **Azione 2 (Martina):** `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=3&quantita=6`
    * *Stato:* Martina aggiunge 6 Oli. (Stock DB: 8).
* **Azione 3 (Nicola - Checkout):** `POST http://localhost:8080/api/ordini`
    * **Risultato 3:** `200 OK`. L'ordine di Nicola passa. Lo stock dell'Olio nel DB scende da 8 a **3**.
* **Azione 4 (Martina - Checkout):** `POST http://localhost:8080/api/ordini`
    * **Risultato 4:** `409 Conflict` (o `500 Internal Server Error`). Il pre-check di `OrdineService` fallisce.
* **Conclusione:** Il sistema previene correttamente lo stock negativo.

---

## Test di Sicurezza (Fallimenti Autorizzazione)

Questi test verificano che le restrizioni basate sui ruoli funzionino correttamente.

### 4.1: Ruolo Errato (Venditore -> Curatore)

* **Auth:** Basic Auth -> `paolo.verdi@email.com` / `passV1` (Paolo, Ruolo: `PRODUTTORE`/`DISTRIBUTORE`)
* **Azione:** `POST http://localhost:8080/api/curatore/approva/4`
* **Risultato Atteso:** `403 Forbidden`.

### 4.2: Ruolo Errato (Curatore -> Produttore)

* **Auth:** Basic Auth -> `curatore@email.com` / `passCuratore` (Giulia, Ruolo: `CURATORE`)
* **Azione:** `POST http://localhost:8080/api/prodotti` (con Body JSON: `{ "nome": "Test", ... }`)
* **Risultato Atteso:** `403 Forbidden`.

### 4.3: Ruolo Errato (Curatore -> Acquirente)

* **Auth:** Basic Auth -> `curatore@email.com` / `passCuratore` (Giulia, Ruolo: `CURATORE`)
* **Azione:** `GET http://localhost:8080/api/carrello`
* **Risultato Atteso:** `403 Forbidden`.

### 4.4: Test Endpoint Protetto (Gestore)

* **Auth (Test 1):** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola, Ruolo: `ACQUIRENTE`)
* **Azione:** `GET http://localhost:8080/api/ordini/tutti`
* **Risultato Atteso:** `403 Forbidden`.
* **Conclusione:** L'endpoint protetto con `hasRole("GESTORE")` funziona.
