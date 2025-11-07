## Scenari di Test in PostMan
> ⚠️**Nota:**  
Gli scenari di test descritti di seguito si basano sui dati di setup (popolamento iniziale) definiti nel file `FilieraAgricolaApplication.java`.
Se questi dati iniziali sono stati modificati (ad esempio, lo **stock di un prodotto** o i prezzi), i riferimenti numerici specifici in questa guida (es. "Stock Olio = 8" o "totale 16.0") potrebbero non essere più corretti. I risultati attesi potrebbero essere differenti.
 
### Test sul `CarrelloController`

Questi test verificano la logica di `diminuisci` e `svuota` del carrello.

#### 1.1: Aggiungere e Rimuovere Parzialmente

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Azione 1:** `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=2&quantita=5`
    * *Descrizione:* Aggiunge 5 Farine. Lo stock (45) è sufficiente.
* **Risultato 1:** `200 OK`. Il carrello ha 5 Farine, totale 16.0.
* **Azione 2:** `POST http://localhost:8080/api/carrello/diminuisci?prodottoId=2&quantita=2`
    * *Descrizione:* Rimuove 2 Farine.
* **Risultato 2:** `200 OK`. Il carrello ora ha 3 Farine, totale 9.6.

#### 1.2: Rimuovere Completamente un Prodotto (tramite `diminuisci`)

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Stato Iniziale:** Il carrello ha 3 Farine (dal test 1.1).
* **Azione:** `POST http://localhost:8080/api/carrello/diminuisci?prodottoId=2&quantita=99`
    * *Logica:* Chiede di rimuoverne 99. Poiché `3 - 99` è `< 0`, la riga del prodotto viene eliminata.
* **Risultato:** `200 OK`. Il carrello ora ha `contenuti: []` e `prezzoTotale: 0.0`.

#### 1.3: Svuotare il Carrello

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Azione 1 (Riempi):**
    * `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=2&quantita=2`
    * `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=3&quantita=1`
* **Azione 2 (Svuota):** `DELETE http://localhost:8080/api/carrello/svuota`
* **Risultato 2:** `200 OK`. Il carrello ora ha `contenuti: []` e `prezzoTotale: 0.0`.

---

### Test sull'`OrdineController`

Questi test verificano il fallimento del checkout e la gestione della concorrenza.

#### 2.1: Checkout con Carrello Vuoto

* **Auth:** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola)
* **Setup:** Assicurarsi che il carrello di Nicola sia vuoto (es. chiamando `DELETE /api/carrello/svuota`).
* **Azione:** `POST http://localhost:8080/api/ordini`
* **Risultato Atteso:** `409 Conflict`. Il server blocca correttamente la creazione di un ordine vuoto (gestione `IllegalStateException`).

#### 2.2: Test di Concorrenza (Gestione Stock)

* **Attore 1 (Nicola):** Basic Auth -> `nicola.capancioni@...` / `pass123`
* **Attore 2 (Martina):** Basic Auth -> `martina.frolla@...` / `pass123`
* **Stock Iniziale:** Prodotto "Olio" (ID 3) = 8 unità.

---

* **Azione 1 (Nicola):** `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=3&quantita=5`
    * *Stato:* Nicola aggiunge 5 Oli. (Stock DB: 8)
* **Azione 2 (Martina):** `POST http://localhost:8080/api/carrello/aggiungi?prodottoId=3&quantita=6`
    * *Stato:* Martina aggiunge 6 Oli. (Stock DB: 8). Entrambi i carrelli sono validi *prima* del checkout.
* **Azione 3 (Nicola - Checkout):** `POST http://localhost:8080/api/ordini`
    * **Risultato 3:** `200 OK`. L'ordine di Nicola passa. Lo stock dell'Olio nel DB scende da 8 a **3**.
* **Azione 4 (Martina - Checkout):** `POST http://localhost:8080/api/ordini`
    * **Risultato 4:** `409 Conflict` (Errore). Il pre-check di `OrdineService` fallisce: Martina chiede 6 Oli, ma lo stock disponibile è solo 3.
* **Conclusione:** Il sistema previene correttamente lo stock negativo.

---

### Test di Sicurezza (Fallimenti Autorizzazione)

Questi test verificano che le restrizioni basate sui ruoli (SecurityConfig) funzionino correttamente.

#### 3.1: Ruolo Errato (Venditore -> Curatore)

* **Auth:** Basic Auth -> `paolo.verdi@email.com` / `passV1` (Paolo, Ruolo: `PRODUTTORE`/`DISTRIBUTORE`)
* **Azione:** `POST http://localhost:8080/api/curatore/approva/4`
* **Risultato Atteso:** `403 Forbidden`. L'utente è autenticato ma non ha il ruolo `CURATORE`.

#### 3.2: Ruolo Errato (Curatore -> Produttore)

* **Auth:** Basic Auth -> `curatore@email.com` / `passCuratore` (Giulia, Ruolo: `CURATORE`)
* **Azione:** `POST http://localhost:8080/api/prodotti` (con Body JSON: `{ "nome": "Test", ... }`)
* **Risultato Atteso:** `403 Forbidden`. L'utente è autenticato ma non ha il ruolo `PRODUTTORE` o `DISTRIBUTORE`.

#### 3.3: Ruolo Errato (Curatore -> Acquirente)

* **Auth:** Basic Auth -> `curatore@email.com` / `passCuratore` (Giulia, Ruolo: `CURATORE`)
* **Azione:** `GET http://localhost:8080/api/carrello`
* **Risultato Atteso:** `403 Forbidden`. L'utente è autenticato ma non ha il ruolo `ACQUIRENTE`.

#### 3.4: Test Endpoint Protetto (Gestore)

* **Auth (Test 1):** Basic Auth -> `nicola.capancioni@...` / `pass123` (Nicola, Ruolo: `ACQUIRENTE`)
* **Azione:** `GET http://localhost:8080/api/ordini/tutti`
* **Risultato Atteso:** `403 Forbidden`.
* **Conclusione:** L'endpoint protetto con `hasRole("GESTORE")` funziona. (Il test fallisce come atteso per `ACQUIRENTE`, `PRODUTTORE`, `CURATORE`, ecc.)
