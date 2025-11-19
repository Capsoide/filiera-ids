package it.unicam.cs.ids.filieraids.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/**
 * Gestore Globale delle Eccezioni.
 * Questa classe "cattura" le eccezioni lanciate da qualsiasi Controller
 * e le trasforma in una risposta JSON pulita, invece di un 500 Internal Server Error.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Struttura di una risposta di errore standard.
     * (Usiamo un 'record' Java per una classe dati semplice e immutabile).
     */
    record ErrorResponse(int statusCode, String error, String message) {}

    /**
     * Cattura le eccezioni 'Not Found' (ID non trovato).
     * Mappa: RuntimeException
     * Codice: 404 Not Found
     * Esempio: "Richiesta non trovata con ID: 2"
     * Esempio: "Contenuto non trovato"
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        System.err.println("ERRORE RUNTIME: " + ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Risorsa Non Trovata",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Cattura le eccezioni 'User Not Found'.
     * Mappa: UsernameNotFoundException
     * Codice: 404 Not Found
     * Esempio: "Utente non trovato con email: ..."
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {

        System.err.println("ERRORE UTENTE: " + ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Utente Non Trovato",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    /**
     * Cattura le eccezioni di Logica di Business (Stato Illegale).
     * Mappa: IllegalStateException
     * Codice: 409 Conflict (per dati duplicati) o 422 Unprocessable Entity
     * Esempio: "Errore: Email già in uso."
     * Esempio: "Stock non sufficiente per: ..."
     * Esempio: "Impossibile creare un ordine con un carrello vuoto."
     * Esempio: "Questa richiesta è già stata processata."
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {

        System.err.println("ERRORE DI STATO: " + ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflitto di Logica di Business",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Cattura le eccezioni di Sicurezza (Autorizzazione).
     * Mappa: SecurityException
     * Codice: 403 Forbidden
     * Esempio: "Solo i CURATORI possono approvare contenuti."
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex, WebRequest request) {

        System.err.println("ERRORE DI SICUREZZA: " + ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Accesso Negato",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Cattura tutte le altre eccezioni non gestite.
     * Mappa: Exception.class
     * Codice: 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {

        System.err.println("ERRORE GENERICO NON CATTURATO: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Errore Interno del Server",
                "Si è verificato un errore imprevisto."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), //400 Bad Request
                "Dati non validi",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}