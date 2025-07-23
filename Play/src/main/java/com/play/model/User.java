package com.play.model;

/**
 * Classe modello che rappresenta un utente dell'applicazione.
 * Utilizzata per autenticazione, registrazione e gestione dei dati personali.
 */
public class User {

    private String username;   // Nome utente (univoco)
    private String password;   // Password associata (⚠️ da proteggere in ambienti reali)
    private String nome;       // Nome proprio dell'utente
    private String cognome;    // Cognome dell'utente

    /**
     * Costruttore vuoto richiesto da GSON o altre librerie di serializzazione.
     */
    public User() {}

    /**
     * Costruttore base, utile per autenticazione.
     *
     * @param username Nome utente
     * @param password Password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Costruttore completo con tutti i dati anagrafici.
     *
     * @param username Nome utente
     * @param password Password
     * @param nome     Nome proprio
     * @param cognome  Cognome
     */
    public User(String username, String password, String nome, String cognome) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
    }

    // ====== Getter & Setter selezionati ======

    /** @return Nome utente */
    public String getUsername() {
        return username;
    }

    /** Imposta un nuovo username */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return Password utente (in chiaro) */
    public String getPassword() {
        return password;
    }

    /** @return Nome proprio dell'utente */
    public String getName() {
        return nome;
    }

    /** @return Cognome dell'utente */
    public String getSurname() {
        return cognome;
    }
}
