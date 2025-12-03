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
    git clone [https://github.com/Capsoide/filiera-ids.git]
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


## API Endpoints Principali

L'interazione avviene tramite API REST. Di seguito alcuni degli endpoint principali:

* **Auth:** `POST /api/auth/login` (Autenticazione)
* **Eventi:** `POST /api/eventi` (Creazione evento - Richiede ruolo Animatore)
* **Prodotti:** `GET /api/prodotti/catalogo` (Visualizzazione catalogo)
* **Curatore:** `POST /api/curatore/approva/{id}` (Approvazione contenuti)
* **Ordini:** `POST /api/ordini` (Checkout carrello)


## Testing

Per testare le funzionalità è possibile utilizzare **Postman**.
Assicurarsi di includere l'header di autenticazione (Basic Auth o Bearer Token) per gli endpoint protetti.

Filiera Agricola - Piattaforma di Digitalizzazione e Valorizzazione
Descrizione
La piattaforma permette la gestione, valorizzazione e tracciabilità dei prodotti agricoli locali. Consente di caricare, visualizzare e condividere informazioni legate alla filiera agricola, includendo dati relativi a produzione, trasformazione e vendita dei prodotti tipici. Supporta l'organizzazione di eventi locali, fiere, visite guidate alle aziende e la creazione di pacchetti esperienziali.

Tecnologie
Java 17
Spring Boot
Spring Security
H2 Database (default)
Maven
API REST
Attori principali
Produttore: carica informazioni sui prodotti, certificazioni, metodi di coltivazione e vende sul marketplace.
Trasformatore: carica dati sui processi di trasformazione e certificazioni di qualità, collega le fasi ai produttori.
Distributore di Tipicità: gestisce prodotti e pacchetti di prodotti per esperienze gastronomiche.
Curatore: approva e verifica la validità dei contenuti caricati dagli altri attori.
Animatore della Filiera: organizza eventi, fiere e visite guidate alle aziende.
Acquirente: accede alle informazioni e acquista prodotti o prenota eventi/fiere.
Utente Generico: consulta contenuti per informarsi sulla provenienza e qualità dei prodotti.
Gestore della Piattaforma: gestisce aspetti amministrativi, autorizzazioni e accrediti.
Sistemi Social: destinatari dei contenuti condivisi dagli utenti.
Sistema OSM: fornisce mappe e visualizza punti della filiera agricola.
Funzionalità
Registrazione, modifica, eliminazione di utenti e venditori
Assegnazione di ruoli singoli o multipli
Gestione contenuti: prodotti, trasformazioni, pacchetti
Gestione ordini e carrello con controllo dei ruoli
Organizzazione e prenotazione eventi, fiere e visite guidate
Tracciabilità completa dei prodotti lungo la filiera
Visualizzazione dei punti della filiera su mappa interattiva
Condivisione dei contenuti sui social
Sicurezza e validazione dati tramite handler personalizzati
Endpoint principali
Account
Registrazione/modifica/eliminazione utenti e venditori
Assegnazione di ruoli
Ricerca utenti e venditori
Contenuto
Aggiunta prodotti, trasformazioni e pacchetti
Esperienza/Eventi
Organizzazione, accettazione e prenotazione di eventi e visite guidate
Carrello
Gestione acquisti e prenotazioni
Ordine
Gestione ordini dei prodotti
Autorizzazione
Gestione delle approvazioni dei contenuti
OSM
Visualizzazione punti della filiera sulla mappa
H2 Console
Accessibile senza autenticazione, utilizzato per test
Sicurezza
Controllo accesso basato sui ruoli (ROLE_ + nome ruolo)
Basic Auth con sessione stateless
Password di default non criptate (NoOpPasswordEncoder)
Validazioni su email e campi non nulli tramite handler personalizzati
Note
Endpoints protetti secondo ruolo dell’utente
Supporto a design pattern per gestione validazioni e flussi di autorizzazione
La piattaforma promuove la tracciabilità e la valorizzazione del territorioFiliera Agricola - Piattaforma di Digitalizzazione e Valorizzazione
Descrizione
La piattaforma permette la gestione, valorizzazione e tracciabilità dei prodotti agricoli locali. Consente di caricare, visualizzare e condividere informazioni legate alla filiera agricola, includendo dati relativi a produzione, trasformazione e vendita dei prodotti tipici. Supporta l'organizzazione di eventi locali, fiere, visite guidate alle aziende e la creazione di pacchetti esperienziali.

Tecnologie
Java 17
Spring Boot
Spring Security
H2 Database (default)
Maven
API REST
Attori principali
Produttore: carica informazioni sui prodotti, certificazioni, metodi di coltivazione e vende sul marketplace.
Trasformatore: carica dati sui processi di trasformazione e certificazioni di qualità, collega le fasi ai produttori.
Distributore di Tipicità: gestisce prodotti e pacchetti di prodotti per esperienze gastronomiche.
Curatore: approva e verifica la validità dei contenuti caricati dagli altri attori.
Animatore della Filiera: organizza eventi, fiere e visite guidate alle aziende.
Acquirente: accede alle informazioni e acquista prodotti o prenota eventi/fiere.
Utente Generico: consulta contenuti per informarsi sulla provenienza e qualità dei prodotti.
Gestore della Piattaforma: gestisce aspetti amministrativi, autorizzazioni e accrediti.
Sistemi Social: destinatari dei contenuti condivisi dagli utenti.
Sistema OSM: fornisce mappe e visualizza punti della filiera agricola.
Funzionalità
Registrazione, modifica, eliminazione di utenti e venditori
Assegnazione di ruoli singoli o multipli
Gestione contenuti: prodotti, trasformazioni, pacchetti
Gestione ordini e carrello con controllo dei ruoli
Organizzazione e prenotazione eventi, fiere e visite guidate
Tracciabilità completa dei prodotti lungo la filiera
Visualizzazione dei punti della filiera su mappa interattiva
Condivisione dei contenuti sui social
Sicurezza e validazione dati tramite handler personalizzati
Endpoint principali
Account
Registrazione/modifica/eliminazione utenti e venditori
Assegnazione di ruoli
Ricerca utenti e venditori
Contenuto
Aggiunta prodotti, trasformazioni e pacchetti
Esperienza/Eventi
Organizzazione, accettazione e prenotazione di eventi e visite guidate
Carrello
Gestione acquisti e prenotazioni
Ordine
Gestione ordini dei prodotti
Autorizzazione
Gestione delle approvazioni dei contenuti
OSM
Visualizzazione punti della filiera sulla mappa
H2 Console
Accessibile senza autenticazione, utilizzato per test
Sicurezza
Controllo accesso basato sui ruoli (ROLE_ + nome ruolo)
Basic Auth con sessione stateless
Password di default non criptate (NoOpPasswordEncoder)
Validazioni su email e campi non nulli tramite handler personalizzati
Note
Endpoints protetti secondo ruolo dell’utente
Supporto a design pattern per gestione validazioni e flussi di autorizzazione
La piattaforma promuove la tracciabilità e la valorizzazione del territorioFiliera Agricola - Piattaforma di Digitalizzazione e Valorizzazione
Descrizione
La piattaforma permette la gestione, valorizzazione e tracciabilità dei prodotti agricoli locali. Consente di caricare, visualizzare e condividere informazioni legate alla filiera agricola, includendo dati relativi a produzione, trasformazione e vendita dei prodotti tipici. Supporta l'organizzazione di eventi locali, fiere, visite guidate alle aziende e la creazione di pacchetti esperienziali.

Tecnologie
Java 17
Spring Boot
Spring Security
H2 Database (default)
Maven
API REST
Attori principali
Produttore: carica informazioni sui prodotti, certificazioni, metodi di coltivazione e vende sul marketplace.
Trasformatore: carica dati sui processi di trasformazione e certificazioni di qualità, collega le fasi ai produttori.
Distributore di Tipicità: gestisce prodotti e pacchetti di prodotti per esperienze gastronomiche.
Curatore: approva e verifica la validità dei contenuti caricati dagli altri attori.
Animatore della Filiera: organizza eventi, fiere e visite guidate alle aziende.
Acquirente: accede alle informazioni e acquista prodotti o prenota eventi/fiere.
Utente Generico: consulta contenuti per informarsi sulla provenienza e qualità dei prodotti.
Gestore della Piattaforma: gestisce aspetti amministrativi, autorizzazioni e accrediti.
Sistemi Social: destinatari dei contenuti condivisi dagli utenti.
Sistema OSM: fornisce mappe e visualizza punti della filiera agricola.
Funzionalità
Registrazione, modifica, eliminazione di utenti e venditori
Assegnazione di ruoli singoli o multipli
Gestione contenuti: prodotti, trasformazioni, pacchetti
Gestione ordini e carrello con controllo dei ruoli
Organizzazione e prenotazione eventi, fiere e visite guidate
Tracciabilità completa dei prodotti lungo la filiera
Visualizzazione dei punti della filiera su mappa interattiva
Condivisione dei contenuti sui social
Sicurezza e validazione dati tramite handler personalizzati
Endpoint principali
Account
Registrazione/modifica/eliminazione utenti e venditori
Assegnazione di ruoli
Ricerca utenti e venditori
Contenuto
Aggiunta prodotti, trasformazioni e pacchetti
Esperienza/Eventi
Organizzazione, accettazione e prenotazione di eventi e visite guidate
Carrello
Gestione acquisti e prenotazioni
Ordine
Gestione ordini dei prodotti
Autorizzazione
Gestione delle approvazioni dei contenuti
OSM
Visualizzazione punti della filiera sulla mappa
H2 Console
Accessibile senza autenticazione, utilizzato per test
Sicurezza
Controllo accesso basato sui ruoli (ROLE_ + nome ruolo)
Basic Auth con sessione stateless
Password di default non criptate (NoOpPasswordEncoder)
Validazioni su email e campi non nulli tramite handler personalizzati
Note
Endpoints protetti secondo ruolo dell’utente
Supporto a design pattern per gestione validazioni e flussi di autorizzazione
La piattaforma promuove la tracciabilità e la valorizzazione del territorio
