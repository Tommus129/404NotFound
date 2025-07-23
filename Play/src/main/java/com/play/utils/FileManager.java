package com.play.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.play.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Utility class per la gestione dei file dell'applicazione.
 * Si occupa di caricare e salvare esercizi, tentativi e utenti tramite file JSON.
 * Tutte le operazioni sono centralizzate per mantenere coerenza e riutilizzabilità.
 */
public class FileManager {

    private static final String ATTEMPTS_PATH = "src/main/resources/data/attempts.json";
    private static final String USERS_PATH = "src/main/resources/data/users.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Gestione date custom
            .setPrettyPrinting()
            .create();

    // ==============================================================
    // ==================  GESTIONE ESERCIZI ========================
    // ==============================================================

    /**
     * Carica esercizi da file JSON in base al tipo.
     *
     * @param type Tipo di esercizio (es: output, completa, correggiErrore, trovaErrore)
     * @return Lista di esercizi del tipo specificato
     */
    public static List<Exercise> loadExercisesFromType(String type) {
        String filePath;
        switch (type) {
            case "trovaErrore":
                filePath = "src/main/resources/data/es1_trovaCorretto.json";
                break;
            case "output":
                filePath = "src/main/resources/data/es2_output.json";
                break;
            case "completa":
                filePath = "src/main/resources/data/es3_completa.json";
                break;
            case "correggiErrore":
                filePath = "src/main/resources/data/es4_correggiErrore.json";
                break;
            default:
                throw new IllegalArgumentException("Tipo esercizio non valido: " + type);
        }
        return loadJsonExercises(filePath);
    }

    /**
     * Metodo privato di supporto per caricare un file JSON contenente esercizi.
     *
     * @param path Percorso del file JSON
     * @return Lista di esercizi letti dal file
     */
    private static List<Exercise> loadJsonExercises(String path) {
        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            Type listType = new TypeToken<List<Exercise>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento esercizi da: " + path);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Carica tutti gli esercizi disponibili da tutti i file conosciuti.
     *
     * @return Lista completa di tutti gli esercizi
     */
    public static List<Exercise> loadExercises() {
        List<Exercise> allExercises = new ArrayList<>();

        String[] filenames = {
                "es1_trovaCorretto.json",
                "es2_output.json",
                "es3_completa.json",
                "es4_correggiErrore.json"
        };

        for (String filename : filenames) {
            try (InputStream is = FileManager.class.getResourceAsStream("/data/" + filename)) {
                if (is != null) {
                    Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                    Type listType = new TypeToken<List<Exercise>>() {}.getType();
                    List<Exercise> exercises = gson.fromJson(reader, listType);
                    if (exercises != null) {
                        allExercises.addAll(exercises);
                    }
                }
            } catch (IOException e) {
                System.err.println("Errore nel file: " + filename);
                e.printStackTrace();
            }
        }

        return allExercises;
    }

    // ==============================================================
    // ==================  GESTIONE TENTATIVI ========================
    // ==============================================================

    /**
     * Salva un tentativo effettuato da un utente aggiungendolo al file JSON.
     *
     * @param attempt Oggetto Attempt da salvare
     */
    public static void saveAttempt(Attempt attempt) {
        List<Attempt> attempts = loadAttempts();
        attempts.add(attempt);
        try (Writer writer = Files.newBufferedWriter(Paths.get(ATTEMPTS_PATH))) {
            gson.toJson(attempts, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica tutti i tentativi registrati nel file.
     *
     * @return Lista dei tentativi effettuati dagli utenti
     */
    public static List<Attempt> loadAttempts() {
        try (Reader reader = Files.newBufferedReader(Paths.get(ATTEMPTS_PATH))) {
            Type listType = new TypeToken<List<Attempt>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // ==============================================================
    // ===================  GESTIONE UTENTI =========================
    // ==============================================================

    /**
     * Carica tutti gli utenti registrati nel sistema.
     *
     * @return Lista di utenti
     */
    public static List<User> loadUsers() {
        try (Reader reader = Files.newBufferedReader(Paths.get(USERS_PATH))) {
            Type listType = new TypeToken<List<User>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Salva un nuovo utente nel file JSON, evitando duplicati.
     *
     * @param user Nuovo utente da registrare
     */
    public static void saveUser(User user) {
        List<User> users = loadUsers();

        boolean alreadyExists = users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));

        if (alreadyExists) {
            System.out.println("⚠️ Username già esistente: " + user.getUsername());
            return;
        }

        users.add(user);
        Path path = Paths.get(USERS_PATH);

        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            try (Writer writer = Files.newBufferedWriter(path)) {
                gson.toJson(users, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Autentica un utente verificando username e password.
     *
     * @param username Nome utente
     * @param password Password in chiaro
     * @return Optional contenente l'utente autenticato, se valido
     */
    public static Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) return Optional.empty();

        return loadUsers().stream()
                .filter(u -> u.getUsername().trim().equals(username.trim()))
                .filter(u -> u.getPassword().trim().equals(password.trim()))
                .findFirst();
    }

    /**
     * Recupera il nome completo (nome + cognome) di un utente a partire dall'username.
     *
     * @param username Nome utente
     * @return Nome completo o stringa "Utente Sconosciuto" se non trovato
     */
    public static String getUserFullName(String username) {
        return loadUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .map(u -> u.getName() + " " + u.getSurname())
                .orElse("Utente Sconosciuto");
    }
}
