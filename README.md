# Filiera Agricola Project

Piattaforma backend basata su Spring Boot per la gestione e valorizzazione della filiera agricola locale. 
Il sistema gestisce l'interazione tra produttori, trasformatori, distributori, acquirenti e animatori territoriali.


## Descrizione del Progetto

L'applicazione permette la gestione di:
* **Catalogo Prodotti e Pacchetti:** Caricamento, validazione e vendita di prodotti locali.
* **Eventi Territoriali:** Creazione e gestione di eventi promozionali con sistema di prenotazione.
* **Workflow di Approvazione:** Ruolo di Curatore per validare contenuti prima della pubblicazione.
* **Ordini e Carrello:** Gestione completa del processo di acquisto.


## Architettura e Design Pattern

Il progetto segue un'architettura a livelli (Controller, Service, Repository, Model). Sono stati implementati specifici Design Pattern per risolvere problematiche architetturali:

* **Builder Pattern (GoF):** Utilizzato per la costruzione complessa e validata delle entità `Evento`, separando la logica di costruzione dalla rappresentazione dell'oggetto.
* **Observer Pattern (GoF):** Implementato per il sistema di notifiche social. Il `CuratoreService` (Subject) notifica il `SocialService` (Observer) quando un contenuto viene approvato, disaccoppiando la logica di business da quella di notifica.


## Requisiti di Sistema

* Java 17 o superiore
* Maven 3.6+
* Database H2 (In-memory, default) o MySQL


## Installazione e Avvio

1.  **Clonare il repository:**
    ```bash
    git clone https://github.com/Capsoide/filiera-ids.git
    cd filiera-ids
    ```

2.  **Compilare il progetto:**
    ```bash
    mvn clean install
    ```

3.  **Avviare l'applicazione:**
    ```bash
    mvn spring-boot:run
    ```

L'applicazione sarà disponibile all'indirizzo: `http://localhost:8080`


## API Endpoints

L'interazione avviene tramite API REST.

## Base URL

```
http://localhost:8080/api
```

---

# API PUBBLICHE (NO AUTH)

## Catalogo Prodotti

### GET `/prodotti/catalogo`

* Descrizione: Recupero catalogo completo prodotti
* Auth: NO

---

## Prodotto specifico

### GET `/prodotti/{id}`

* Esempio: `/prodotti/2`
* Auth: NO

---

## Eventi visibili

### GET `/eventi/visibili`

* Auth: NO

---

## Mappa aziende agricole

### GET `/mappa`

* Auth: NO

---

# ACQUIRENTE

## Registrazione

### POST `/auth/registra/acquirente`

```json
{
  "email": "string",
  "password": "string",
  "nome": "string",
  "cognome": "string",
  "indirizzo": {
    "via": "string",
    "numCivico": "string",
    "comune": "string",
    "cap": "string",
    "regione": "string"
  }
}
```

---

## Catalogo (autenticato)

### GET `/prodotti/catalogo`

* Auth: Basic

---

## Visualizza carrello

### GET `/carrello`

* Auth: Basic

---

## Aggiungi prodotto al carrello

### POST `/carrello/aggiungi`

```json
{
  "prodottoId": number,
  "quantita": number
}
```

---

## Aggiungi pacchetto

### POST `/carrello/aggiungi-pacchetto`

```json
{
  "prodottoId": number,
  "quantita": number
}
```

---

## Diminuisci prodotto

### POST `/carrello/diminuisci`

```json
{
  "prodottoId": number,
  "quantita": number
}
```

---

## Crea ordine

### POST `/ordini`

---

## Storico ordini

### GET `/ordini`

---

## Prenotazione evento

### POST `/prenotazioni/eventi/{id}?numeroPosti=X`

* Esempio: `/prenotazioni/eventi/6?numeroPosti=20`

---

# VENDITORE

## Registrazione

### POST `/auth/registra/venditore`

```json
{
  "email": "string",
  "password": "string",
  "nome": "string",
  "cognome": "string",
  "piva": "string",
  "descrizione": "string",
  "ruoli": ["PRODUTTORE"],
  "indirizzo": {
    "via": "string",
    "numCivico": "string",
    "cap": "string",
    "comune": "string",
    "regione": "string",
    "latitudine": number,
    "longitudine": number
  }
}
```

---

## Crea prodotto

### POST `/prodotti`

```json
{
  "nome": "string",
  "descrizione": "string",
  "metodoDiColtivazione": "string",
  "prezzo": number,
  "quantita": number,
  "certificazioni": ["string"],
  "dataProduzione": "ISO_DATE"
}
```

---

## Elimina prodotto

### DELETE `/prodotti/{id}`

* Nota: quantità deve essere 0

---

## Modifica prodotto

### PUT `/prodotti/{id}`

---

## Crea pacchetto

### POST `/pacchetti`

```json
{
  "nome": "string",
  "descrizione": "string",
  "prezzo": number,
  "items": [
    {
      "prodottoId": number,
      "quantita": number
    }
  ]
}
```

---

## Vedi inviti

### GET `/venditori/inviti`

---

## Prodotti del venditore

### GET `/prodotti/miei`

---

## Ordini ricevuti

### GET `/ordini/venditore`

---

## Rispondi invito

### PUT `/venditori/inviti/{id}/rispondi`

```json
{
  "azione": "ACCETTA | RIFIUTA"
}
```

---

# TEST CARRELLO

## Aggiunta parziale

### POST `/carrello/aggiungi`

---

## Rimozione parziale

### POST `/carrello/diminuisci?prodottoId=X&quantita=Y`

---

## Rimozione completa

### POST `/carrello/diminuisci?prodottoId=X&quantita=99`

---

## Svuota carrello

### DELETE `/carrello/svuota`

---

# ANIMATORE

## Registrazione

### POST `/auth/registra/staff`

```json
{
  "email": "string",
  "password": "string",
  "nome": "string",
  "cognome": "string",
  "ruoloRichiesto": "ANIMATORE"
}
```

---

## Crea evento

### POST `/eventi`

---

## Elimina evento

### DELETE `/eventi/{id}`

---

## Modifica evento

### PUT `/eventi/{id}`

---

## Invita venditore

### POST `/eventi/{idEvento}/invita/{idVenditore}`

---

## Eventi miei

### GET `/eventi/miei`

---

## Invitati evento

### GET `/eventi/{id}/invitati`

---

## Prenotazioni evento

### GET `/eventi/{id}/prenotazioni`

---

# CURATORE

## Registrazione

### POST `/auth/registra/staff`

```json
{
  "email": "string",
  "password": "string",
  "nome": "string",
  "cognome": "string",
  "ruoloRichiesto": "CURATORE"
}
```

---

## Approva evento/contenuto

### POST `/curatore/approva/{id}`

---

## Rifiuta evento/contenuto

### POST `/curatore/rifiuta/{id}`

```json
{
  "azione": "RIFIUTA",
  "motivazione": "string"
}
```

---

## Contenuti da approvare

### GET `/curatore/da-approvare`

---

# GESTORE

## Richieste ruolo

### GET `/gestore/richieste-in-attesa`

---

## Approva richiesta

### POST `/gestore/approva/{id}`

---

## Rifiuta richiesta

### POST `/gestore/rifiuta/{id}`

---

# NOTE IMPORTANTI

* Autenticazione: **Basic Auth** dove richiesto
* Content-Type: `application/json` per tutte le POST/PUT con body
* Date: formato ISO 8601
* Tutti gli ID sono numerici
* Le query parameters sono usate per operazioni dinamiche (es. carrello, prenotazioni)

---
