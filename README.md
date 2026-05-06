# Filiera Agricola Project

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

## Ruoli del Sistema

* **Acquirente:** acquista prodotti e prenota eventi
* **Venditore:** gestisce prodotti e pacchetti
* **Animatore:** crea e gestisce eventi
* **Curatore:** approva o rifiuta contenuti
* **Gestore:** approva richieste di ruolo

---

## Architettura e Design Pattern

Architettura a livelli:

* Controller
* Service
* Repository
* Model

Design pattern:

* **Builder Pattern (GoF):** costruzione controllata dell'entità `Evento`
* **Observer Pattern (GoF):** notifiche tra `CuratoreService` e `SocialService`

---

## Struttura del Progetto

```
src/main/java/
├── controller
├── service
├── repository
├── model
├── config
```

---

## Requisiti di Sistema

* Java 17+
* Maven 3.6+
* Database H2 (default) oppure MySQL

---

## Installazione e Avvio

```bash
git clone https://github.com/Capsoide/filiera-ids.git
cd filiera-ids
mvn clean install
mvn spring-boot:run
```

Applicazione disponibile su:

```
http://localhost:8080
```

---

## API REST

### Base URL

```
http://localhost:8080/api
```

---

## Panoramica API

* Formato: JSON
* Autenticazione: Basic Auth
* Codici HTTP utilizzati:

  * 200 OK
  * 201 Created
  * 400 Bad Request
  * 401 Unauthorized
  * 404 Not Found
  * 500 Internal Server Error

---

## Autenticazione

Le API protette utilizzano Basic Auth:

```
Authorization: Basic base64(username:password)
```

---

# API PUBBLICHE

## GET /prodotti/catalogo

Recupera il catalogo completo dei prodotti
Auth: NO

### Response Example

```json
[
  {
    "id": 1,
    "nome": "Miele Bio",
    "prezzo": 10.0,
    "quantita": 50
  }
]
```

---

## GET /prodotti/{id}

Recupera un prodotto specifico

---

## GET /eventi/visibili

Recupera gli eventi pubblici

---

## GET /mappa

Recupera i punti geografici delle aziende

---

# ACQUIRENTE

## POST /auth/registra/acquirente

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

## GET /carrello

### Response Example

```json
{
  "items": [],
  "totale": 0
}
```

---

## POST /carrello/aggiungi

```json
{
  "prodottoId": 1,
  "quantita": 2
}
```

### Response

* 200 OK
* 400 Bad Request

---

## POST /ordini

### Response Example

```json
{
  "id": 10,
  "stato": "CREATO",
  "totale": 50.0
}
```

---

# VENDITORE

## POST /prodotti

```json
{
  "nome": "string",
  "prezzo": 10.0,
  "quantita": 100
}
```

### Response

* 201 Created

---

## DELETE /prodotti/{id}

* 200 OK
* 400 se quantità > 0

---

## GET /prodotti/miei

### Response Example

```json
[
  {
    "id": 1,
    "nome": "Formaggio",
    "quantita": 50
  }
]
```

---

# ANIMATORE

## POST /eventi

```json
{
  "nome": "Evento",
  "dataEvento": "2025-12-20T09:00:00"
}
```

---

## GET /eventi/miei

---

# CURATORE

## POST /curatore/approva/{id}

---

## POST /curatore/rifiuta/{id}

```json
{
  "azione": "RIFIUTA",
  "motivazione": "string"
}
```

---

# GESTORE

## GET /gestore/richieste-in-attesa

---

## POST /gestore/approva/{id}

---

## POST /gestore/rifiuta/{id}

---

## Note

> [!IMPORTANT]
> Regole generali delle API

> [!NOTE]
>
> * Autenticazione: Basic Auth dove richiesto
> * Content-Type: `application/json` per POST e PUT

> [!TIP]
>
> * Date: formato ISO 8601
> * Tutti gli ID sono numerici
> * Uso di query parameters per operazioni dinamiche


