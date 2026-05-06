# Filiera Agricola

Piattaforma backend basata su Spring Boot per la gestione e valorizzazione della filiera agricola locale.
Il sistema gestisce l'interazione tra produttori, trasformatori, distributori, acquirenti e animatori territoriali.

---

## Descrizione del Progetto

L'applicazione permette la gestione di:

* **Catalogo Prodotti e Pacchetti:** caricamento, validazione e vendita di prodotti locali
* **Eventi Territoriali:** creazione e gestione di eventi promozionali con sistema di prenotazione
* **Workflow di Approvazione:** ruolo di Curatore per validare contenuti prima della pubblicazione
* **Ordini e Carrello:** gestione completa del processo di acquisto

---

## Architettura e Design Pattern

Il progetto segue un'architettura a livelli:

* Controller
* Service
* Repository
* Model

Design pattern utilizzati:

* **Builder Pattern (GoF):** utilizzato per la costruzione delle entità `Evento`, separando la logica di creazione dalla rappresentazione
* **Observer Pattern (GoF):** utilizzato per il sistema di notifiche; il `CuratoreService` notifica il `SocialService` alla validazione dei contenuti

---

## Requisiti di Sistema

* Java 17 o superiore
* Maven 3.6+
* Database H2 (default) oppure MySQL

---

## Installazione e Avvio

### Clonazione repository

```bash
git clone https://github.com/Capsoide/filiera-ids.git
cd filiera-ids
```

### Build

```bash
mvn clean install
```

### Avvio

```bash
mvn spring-boot:run
```

Applicazione disponibile su:

```
http://localhost:8080
```

---

## API REST
> [!IMPORTANT]
> Regole generali delle API

> [!NOTE]
> - Autenticazione: Basic Auth dove richiesto  
> - Content-Type: `application/json` per POST e PUT  

> [!TIP]
> - Date: formato ISO 8601  
> - Tutti gli ID sono numerici  
> - Uso di query parameters per operazioni dinamiche  

### Base URL

```
http://localhost:8080/api
```

---

# API PUBBLICHE

## GET /prodotti/catalogo

Recupera il catalogo completo dei prodotti\
Auth: NO

---

## GET /prodotti/{id}

Recupera un prodotto specifico
Esempio: `/prodotti/2`
Auth: NO

---

## GET /eventi/visibili

Recupera gli eventi pubblici
Auth: NO

---

## GET /mappa

Recupera i punti geografici delle aziende
Auth: NO

---

# ACQUIRENTE

## POST /auth/registra/acquirente

Registrazione nuovo acquirente

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

## GET /prodotti/catalogo

Catalogo prodotti (autenticato)
Auth: Basic

---

## GET /carrello

Visualizza carrello
Auth: Basic

---

## POST /carrello/aggiungi

Aggiunge un prodotto al carrello

```json
{
  "prodottoId": number,
  "quantita": number
}
```

---

## POST /carrello/aggiungi-pacchetto

Aggiunge un pacchetto al carrello

```json
{
  "prodottoId": number,
  "quantita": number
}
```

---

## POST /carrello/diminuisci

Riduce la quantità di un prodotto

```json
{
  "prodottoId": number,
  "quantita": number
}
```

---

## POST /ordini

Crea un ordine

---

## GET /ordini

Storico ordini

---

## POST /prenotazioni/eventi/{id}?numeroPosti=X

Prenotazione evento
Esempio: `/prenotazioni/eventi/6?numeroPosti=20`

---

# VENDITORE

## POST /auth/registra/venditore

Registrazione venditore

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

## POST /prodotti

Crea un nuovo prodotto

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

## DELETE /prodotti/{id}

Elimina prodotto
Nota: la quantità deve essere 0

---

## PUT /prodotti/{id}

Modifica prodotto

---

## POST /pacchetti

Crea un pacchetto

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

## GET /venditori/inviti

Lista inviti

---

## GET /prodotti/miei

Prodotti del venditore

---

## GET /ordini/venditore

Ordini ricevuti

---

## PUT /venditori/inviti/{id}/rispondi

Risponde a un invito

```json
{
  "azione": "ACCETTA | RIFIUTA"
}
```

---

# TEST CARRELLO

## POST /carrello/aggiungi

Aggiunta parziale

---

## POST /carrello/diminuisci?prodottoId=X&quantita=Y

Rimozione parziale

---

## POST /carrello/diminuisci?prodottoId=X&quantita=99

Rimozione completa

---

## DELETE /carrello/svuota

Svuota carrello

---

# ANIMATORE

## POST /auth/registra/staff

Registrazione animatore

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

## POST /eventi

Crea evento

---

## DELETE /eventi/{id}

Elimina evento

---

## PUT /eventi/{id}

Modifica evento

---

## POST /eventi/{idEvento}/invita/{idVenditore}

Invita venditore

---

## GET /eventi/miei

Eventi creati

---

## GET /eventi/{id}/invitati

Lista invitati

---

## GET /eventi/{id}/prenotazioni

Prenotazioni evento

---

# CURATORE

## POST /auth/registra/staff

Registrazione curatore

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

## POST /curatore/approva/{id}

Approva contenuto o evento

---

## POST /curatore/rifiuta/{id}

Rifiuta contenuto o evento

```json
{
  "azione": "RIFIUTA",
  "motivazione": "string"
}
```

---

## GET /curatore/da-approvare

Contenuti in attesa

---

# GESTORE

## GET /gestore/richieste-in-attesa

Richieste ruolo in attesa

---

## POST /gestore/approva/{id}

Approva richiesta

---

## POST /gestore/rifiuta/{id}

Rifiuta richiesta

---



---
