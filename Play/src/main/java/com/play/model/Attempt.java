package com.play.model;

import java.time.LocalDateTime;

/**
 * Classe modello che rappresenta un tentativo effettuato da un utente su un esercizio.
 * Contiene informazioni relative al tipo, livello, esito e tempo del tentativo.
 */
public class Attempt {

    private String username;       // Username dell'utente che ha effettuato il tentativo
    private String type;           // Tipo di esercizio (es. output, correggiErrore, ...)
    private String level;          // Livello di difficoltà (facile, medio, difficile)
    private String question;       // Testo della domanda (per referenza)
    private boolean correct;       // Indica se la risposta è stata corretta
    private String exerciseId;     // ID univoco dell'esercizio svolto
    private LocalDateTime timestamp; // Timestamp del momento in cui è stato registrato il tentativo

    /**
     * Costruttore vuoto richiesto da librerie di serializzazione come Gson.
     */
    public Attempt() {}

    /**
     * Costruttore completo. Registra il tentativo con data e ora corrente.
     *
     * @param username   Nome utente
     * @param type       Tipo dell'esercizio
     * @param level      Livello di difficoltà
     * @param question   Domanda visualizzata
     * @param correct    Esito del tentativo (true se corretto)
     * @param exerciseId Identificatore univoco dell'esercizio
     */
    public Attempt(String username, String type, String level, String question, boolean correct, String exerciseId) {
        this.username = username;
        this.type = type;
        this.level = level;
        this.question = question;
        this.correct = correct;
        this.exerciseId = exerciseId;
        this.timestamp = LocalDateTime.now();
    }

    // ===== Getter Methods =====

    /** @return Nome utente che ha svolto l’esercizio */
    public String getUsername() { return username; }

    /** @return Tipo dell'esercizio (output, completa, ecc.) */
    public String getType() { return type; }

    /** @return Livello dell'esercizio (facile, medio, difficile) */
    public String getLevel() { return level; }

    /** @return Testo della domanda associata al tentativo */
    public String getQuestion() { return question; }

    /** @return true se la risposta è stata corretta */
    public boolean isCorrect() { return correct; }

    /** @return ID univoco dell'esercizio */
    public String getExerciseId() { return exerciseId; }

    /** @return Data e ora del tentativo */
    public LocalDateTime getTimestamp() { return timestamp; }
}
