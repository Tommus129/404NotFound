package com.play.model;

import java.util.List;

/**
 * Classe modello che rappresenta un esercizio all'interno dell'applicazione.
 * Contiene tutte le informazioni necessarie per visualizzare e valutare un esercizio:
 * testo della domanda, codice, opzioni di risposta e soluzione corretta.
 */
public class Exercise {

    private String id;                   // ID univoco dell'esercizio
    private String type;                // Tipo (es. output, completa, correggiErrore, ...)
    private String level;               // Livello di difficoltà (facile, medio, difficile)
    private String question;            // Testo della domanda
    private Object code;                // Codice associato all'esercizio (può essere String o altro)
    private List<String> options;       // Lista delle possibili risposte
    private int correctOptionIndex;     // Indice della risposta corretta nella lista delle opzioni


    /**
     * @return Identificatore univoco dell'esercizio
     */
    public String getId() { return id; }

    /**
     * @return Tipo dell'esercizio (es. output, completa, correggiErrore, ...)
     */
    public String getType() { return type; }

    /**
     * @return Livello dell'esercizio (facile, medio, difficile)
     */
    public String getLevel() { return level; }

    /**
     * @return Testo della domanda
     */
    public String getQuestion() { return question; }

    /**
     * @return Codice associato all'esercizio (può essere null)
     */
    public Object getCode() { return code; }

    /**
     * @return Elenco delle opzioni di risposta
     */
    public List<String> getOptions() { return options; }

    /**
     * @return Indice (0-based) dell'opzione corretta nella lista delle risposte
     */
    public int getCorrectOptionIndex() { return correctOptionIndex; }

    // =======================
    //       SETTERS (opzionali)
    // =======================
    // Se serve modificare gli oggetti in fase runtime, puoi aggiungere qui i setter.
}
