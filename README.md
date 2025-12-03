# Filiera Agricola - IDs Project

Piattaforma backend basata su Spring Boot per la gestione e valorizzazione della filiera agricola locale. Il sistema gestisce l'interazione tra produttori, trasformatori, distributori, acquirenti e animatori territoriali.

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
    git clone [https://github.com/Capsoide/filiera-agricola.git](https://github.com/Capsoide/filiera-agricola.git)
    cd filiera-agricola
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
