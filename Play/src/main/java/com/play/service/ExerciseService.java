package com.play.service;

import com.google.gson.Gson;
import com.play.model.Exercise;
import com.play.utils.FileManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service che gestisce il caricamento, filtraggio e analisi degli esercizi.
 * Responsabile del recupero da file JSON, selezione in base a tipo/livello,
 * conteggio dei completamenti e progressi dell'utente.
 */
public class ExerciseService {

    // Numero di esercizi mostrati per sessione
    private static final int EXERCISES_PER_SESSION = 3;

    /**
     * Carica una lista filtrata di esercizi da proporre all'utente.
     *
     * @param type         Tipo di esercizio (output, correggiErrore, ...)
     * @param level        Livello selezionato (facile, medio, difficile)
     * @param completedIds Set degli ID degli esercizi già completati correttamente
     * @return Lista di esercizi ancora da svolgere, limitata a EXERCISES_PER_SESSION
     * @throws IOException Errore di lettura file
     */
    public List<Exercise> loadExercises(String type, String level, Set<String> completedIds) throws IOException {
        return loadAllExercises(type).stream()
                .filter(e -> e.getLevel().equalsIgnoreCase(level))        // Filtra per livello
                .filter(e -> !completedIds.contains(e.getId()))          // Esclude esercizi già completati
                .limit(EXERCISES_PER_SESSION)                            // Limita a 3 per sessione
                .collect(Collectors.toList());
    }

    /**
     * Calcola la percentuale di completamento di un utente per un tipo e livello di esercizio.
     *
     * @param type         Tipo di esercizio
     * @param level        Livello selezionato
     * @param completedIds Set di ID completati correttamente
     * @return Percentuale tra 0 e 100
     */
    public double calculateCompletionPercentage(String type, String level, Set<String> completedIds) {
        try {
            List<Exercise> all = loadAllExercises(type).stream()
                    .filter(e -> e.getLevel().equalsIgnoreCase(level))
                    .collect(Collectors.toList());

            if (all.isEmpty()) return 0;

            long done = all.stream()
                    .filter(e -> completedIds.contains(e.getId()))
                    .count();

            return (done * 100.0) / all.size();

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Carica tutti gli esercizi disponibili per un tipo, da file JSON.
     *
     * @param type Tipo di esercizio
     * @return Lista completa di esercizi per quel tipo
     * @throws IOException In caso di file mancante o errore di parsing
     */
    public List<Exercise> loadAllExercises(String type) throws IOException {
        String fileName;

        // Mappa il tipo al nome del file JSON corrispondente
        switch (type) {
            case "trovaErrore":
                fileName = "/data/es1_trovaCorretto.json";
                break;
            case "output":
                fileName = "/data/es2_output.json";
                break;
            case "completa":
                fileName = "/data/es3_completa.json";
                break;
            case "correggiErrore":
                fileName = "/data/es4_correggiErrore.json";
                break;
            default:
                throw new IllegalArgumentException("Tipo di esercizio sconosciuto: " + type);
        }

        // Caricamento del file come InputStream
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream == null) throw new FileNotFoundException("File non trovato: " + fileName);

        // Parsing JSON -> array di Exercise
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Exercise[] allExercises = gson.fromJson(reader, Exercise[].class);

        return Arrays.asList(allExercises);
    }

    /**
     * Conta il numero totale di tentativi effettuati da un utente su un certo tipo di esercizio.
     *
     * @param type     Tipo di esercizio
     * @param username Nome utente
     * @return Numero di tentativi registrati
     */
    public int countAttempts(String type, String username) {
        return (int) FileManager.loadAttempts().stream()
                .filter(a -> a.getUsername().equals(username))
                .filter(a -> a.getType().equals(type))
                .count();
    }

    /**
     * Ritorna il numero totale di esercizi disponibili per un certo tipo.
     *
     * @param type Tipo di esercizio
     * @return Numero di esercizi disponibili, oppure 0 in caso di errore
     */
    public int getExerciseCountByType(String type) {
        try {
            return loadAllExercises(type).size();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
