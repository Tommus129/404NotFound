package com.play.controller;

import com.play.model.Attempt;
import com.play.model.Exercise;
import com.play.service.ExerciseService;
import com.play.utils.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller per la selezione degli esercizi.
 * Permette all'utente di scegliere la tipologia e il livello degli esercizi,
 * visualizzare il progresso e iniziare una nuova sessione.
 */
public class ExerciseSelectionController {

    // ToggleButton per la selezione del tipo di esercizio
    @FXML private ToggleButton btnOutput;
    @FXML private ToggleButton btnCorreggi;
    @FXML private ToggleButton btnCompleta;
    @FXML private ToggleButton btnQuiz;

    @FXML private HBox exerciseButtonsBox; // Contenitore dei bottoni dei tipi

    @FXML private ComboBox<String> levelComboBox; // Dropdown per il livello
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    private ToggleGroup typeGroup; // Gestione mutua esclusione bottoni tipo

    private static List<Exercise> selectedExercises; // Esercizi caricati per la sessione
    private final ExerciseService service = new ExerciseService();

    // Selezioni preimpostate per accesso diretto da altri controller
    private static String preselectedType = null;
    private static String preselectedLevel = null;

    /**
     * Imposta il tipo pre-selezionato (opzionale).
     */
    public static void setPreselectedType(String type) {
        preselectedType = type;
    }

    /**
     * Imposta il livello pre-selezionato (opzionale).
     */
    public static void setPreselectedLevel(String level) {
        preselectedLevel = level;
    }

    /**
     * Ritorna la lista di esercizi selezionati per la sessione attuale.
     */
    public static List<Exercise> getSelectedExercises() {
        return selectedExercises;
    }

    /**
     * Metodo chiamato all'inizializzazione della scena.
     * Imposta i bottoni dei tipi, i listener, e aggiorna il progresso.
     */
    @FXML
    public void initialize() {
        // Gruppo per gestire un solo tipo selezionato alla volta
        typeGroup = new ToggleGroup();
        btnOutput.setToggleGroup(typeGroup); btnOutput.setUserData("output");
        btnCorreggi.setToggleGroup(typeGroup); btnCorreggi.setUserData("correggiErrore");
        btnCompleta.setToggleGroup(typeGroup); btnCompleta.setUserData("completa");
        btnQuiz.setToggleGroup(typeGroup); btnQuiz.setUserData("trovaErrore");

        // Aggiunge le difficoltà disponibili
        levelComboBox.getItems().addAll("facile", "medio", "difficile");

        // Nasconde tutti i bottoni tranne quello pre-selezionato (se presente)
        if (preselectedType != null) {
            for (javafx.scene.Node node : exerciseButtonsBox.getChildren()) {
                if (node instanceof ToggleButton) {
                    ToggleButton btn = (ToggleButton) node;
                    boolean match = btn.getUserData().equals(preselectedType);
                    btn.setVisible(match);
                    btn.setManaged(match);
                    if (match) btn.setSelected(true);
                }
            }
        }

        // Seleziona livello predefinito se fornito
        if (preselectedLevel != null) {
            levelComboBox.setValue(preselectedLevel);
        }

        // Listener per aggiornare il progresso dinamicamente
        levelComboBox.setOnAction(e -> updateProgress());
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateProgress());

        updateProgress();
    }

    /**
     * Calcola e aggiorna il progresso dell'utente in base a tipo e livello selezionati.
     */
    private void updateProgress() {
        Toggle selected = typeGroup.getSelectedToggle();
        String level = levelComboBox.getValue();

        // Controlla se i filtri sono validi
        if (selected == null || level == null) {
            progressBar.setProgress(0);
            progressLabel.setText("Completamento: 0%");
            return;
        }

        String selectedType = (String) selected.getUserData();

        // Recupera tentativi completati correttamente dall'utente loggato
        List<Attempt> done = FileManager.loadAttempts();
        Set<String> completedCorrect = done.stream()
                .filter(a -> a.getUsername().equals(AuthController.loggedUser))
                .filter(Attempt::isCorrect)
                .map(Attempt::getExerciseId)
                .collect(Collectors.toSet());

        // Calcola la percentuale di completamento
        double completion = service.calculateCompletionPercentage(selectedType, level, completedCorrect);
        progressBar.setProgress(completion / 100.0);
        progressLabel.setText("Completamento: " + Math.round(completion) + "%");
    }

    /**
     * Avvia la sessione di esercizi con i parametri selezionati.
     * Controlla la presenza di esercizi disponibili, altrimenti mostra un messaggio.
     */
    @FXML
    private void onStart() {
        Toggle selected = typeGroup.getSelectedToggle();
        String level = levelComboBox.getValue();

        if (selected == null || level == null) {
            new Alert(Alert.AlertType.WARNING, "Per iniziare un esercizio, seleziona tipo e difficoltà.").show();
            return;
        }

        String selectedType = (String) selected.getUserData();

        try {
            // Ottiene esercizi ancora da completare per l'utente
            List<Attempt> done = FileManager.loadAttempts();
            Set<String> completedCorrect = done.stream()
                    .filter(a -> a.getUsername().equals(AuthController.loggedUser))
                    .filter(Attempt::isCorrect)
                    .map(Attempt::getExerciseId)
                    .collect(Collectors.toSet());

            selectedExercises = service.loadExercises(selectedType, level, completedCorrect);

            // Se l’utente ha completato tutti gli esercizi, mostra avviso
            if (selectedExercises.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Hai completato tutti gli esercizi per questa categoria.").show();
                return;
            }

            // Passa il tipo selezionato all’InstructionController
            InstructionController.setExerciseType(selectedType);

            // Carica la schermata di istruzioni
            Parent root = FXMLLoader.load(getClass().getResource("/view/InstructionView.fxml"));
            Stage stage = (Stage) levelComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));

            // Reset dei valori preimpostati
            preselectedType = null;
            preselectedLevel = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Naviga alla schermata della classifica.
     */
    @FXML
    private void onLeaderboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LeaderboardView.fxml"));
            Stage stage = (Stage) levelComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Torna alla dashboard principale.
     */
    @FXML
    private void onBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"));
            Stage stage = (Stage) levelComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
