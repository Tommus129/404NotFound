package com.play.controller;

import com.play.model.Attempt;
import com.play.service.ExerciseService;
import com.play.utils.FileManager;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller della dashboard principale dell'applicazione.
 * Visualizza statistiche, barre di avanzamento e gestisce la navigazione tra le diverse sezioni.
 */
public class DashboardController {

    // Etichette e barre di avanzamento collegate al layout FXML
    @FXML private Label welcomeLabel;
    @FXML private Label attemptsCountLabel;
    @FXML private Label successRateLabel;

    @FXML private ProgressBar readCodeBar;
    @FXML private ProgressBar orderStepsBar;
    @FXML private ProgressBar debugBar;
    @FXML private ProgressBar quizBar;
    @FXML private ProgressBar globalProgressBar;

    @FXML private Label readCodeLabel;
    @FXML private Label orderStepsLabel;
    @FXML private Label debugLabel;
    @FXML private Label quizLabel;

    // Service per l'accesso ai dati sugli esercizi
    private final ExerciseService service = new ExerciseService();

    /**
     * Metodo di inizializzazione del controller chiamato automaticamente da JavaFX.
     * Verifica se l'utente è loggato, mostra messaggi di benvenuto e avvia il calcolo delle statistiche.
     */
    @FXML
    public void initialize() {
        // Verifica se l'utente è loggato
        if (AuthController.loggedUser == null) {
            System.err.println("⚠️ Utente non loggato!");
            return;
        }

        String user = AuthController.loggedUser != null ? AuthController.loggedUser : "utente";
        welcomeLabel.setText("Bentornato, " + user + "!");

        // Animazioni fade-in per migliorare l'esperienza utente
        fadeInNode(welcomeLabel);
        fadeInNode(attemptsCountLabel);
        fadeInNode(successRateLabel);

        // Esegui update asincroni dopo il rendering UI
        Platform.runLater(() -> {
            updateProgress();
            updateGlobalProgress();
        });
    }

    /**
     * Aggiorna le statistiche di avanzamento per ogni tipo di esercizio e mostra il tasso di successo complessivo.
     */
    private void updateProgress() {
        String username = AuthController.loggedUser;

        int totalAttempts = 0;

        // Aggiorna le statistiche per ogni categoria di esercizio
        totalAttempts += updateSingleProgress("output", readCodeBar, readCodeLabel, username);
        totalAttempts += updateSingleProgress("completa", orderStepsBar, orderStepsLabel, username);
        totalAttempts += updateSingleProgress("correggiErrore", debugBar, debugLabel, username);
        totalAttempts += updateSingleProgress("trovaErrore", quizBar, quizLabel, username);

        // Calcola la percentuale media di successo
        double globalPercent = (readCodeBar.getProgress() + orderStepsBar.getProgress()
                + debugBar.getProgress() + quizBar.getProgress()) / 4.0 * 100.0;

        // Aggiorna le etichette UI
        attemptsCountLabel.setText(String.valueOf(totalAttempts));
        successRateLabel.setText(String.format("%.0f%%", globalPercent));
    }

    /**
     * Aggiorna la progress bar e l'etichetta per un tipo specifico di esercizio.
     *
     * @param type Tipo di esercizio (es. "output", "completa", ...)
     * @param bar ProgressBar associata al tipo
     * @param label Label associata al tipo
     * @param username Nome dell'utente loggato
     * @return Numero di tentativi effettuati dall'utente per quel tipo
     */
    private int updateSingleProgress(String type, ProgressBar bar, Label label, String username) {
        // Filtra i tentativi per tipo e utente
        List<Attempt> attempts = FileManager.loadAttempts().stream()
                .filter(a -> a.getUsername().equals(username))
                .filter(a -> a.getType().equals(type))
                .collect(Collectors.toList());

        // Identifica gli esercizi risolti correttamente
        Set<String> correctIds = attempts.stream()
                .filter(Attempt::isCorrect)
                .map(Attempt::getExerciseId)
                .collect(Collectors.toSet());

        int totalExercises = service.getExerciseCountByType(type);
        double percent = totalExercises == 0 ? 0 : (correctIds.size() * 100.0) / totalExercises;
        int attemptCount = attempts.size();

        // Aggiorna UI per quella categoria
        updateBar(bar, label, percent, attemptCount);
        return attemptCount;
    }

    /**
     * Imposta i valori di una barra di progresso e della relativa etichetta.
     *
     * @param bar       Barra da aggiornare
     * @param label     Etichetta descrittiva
     * @param percent   Percentuale completata
     * @param attempts  Numero di tentativi totali
     */
    private void updateBar(ProgressBar bar, Label label, double percent, int attempts) {
        if (bar != null) {
            bar.setVisible(true);
            bar.setProgress(percent / 100.0);
            animateProgressBar(bar);
        }

        if (label != null) {
            label.setVisible(true);
            label.setText(String.format("%.0f%% (%d tentativi)", percent, attempts));
        }
    }

    /**
     * Anima visivamente una ProgressBar con effetto di scala.
     *
     * @param bar La barra da animare
     */
    private void animateProgressBar(ProgressBar bar) {
        if (bar == null) return;
        ScaleTransition st = new ScaleTransition(Duration.millis(300), bar);
        st.setFromX(0.9);
        st.setToX(1.0);
        st.setCycleCount(1);
        st.play();
    }

    /**
     * Applica una transizione fade-in a un elemento della UI.
     *
     * @param node Nodo su cui applicare l'effetto
     */
    private void fadeInNode(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(600), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Calcola il progresso globale dell'utente basandosi su tutti gli esercizi completati.
     */
    private void updateGlobalProgress() {
        if (globalProgressBar == null) return;

        String user = AuthController.loggedUser;

        double correct = FileManager.loadAttempts().stream()
                .filter(a -> a.getUsername().equals(user))
                .filter(Attempt::isCorrect)
                .map(Attempt::getExerciseId)
                .distinct()
                .count();

        double total = FileManager.loadExercises().size();
        double percent = (total == 0) ? 0 : correct / total;

        globalProgressBar.setProgress(percent);
        animateProgressBar(globalProgressBar);
    }

    // ====== Event Handlers per la Navigazione ======

    @FXML private void onReadCode() { navigate("output"); }

    @FXML private void onOrderSteps() { navigate("completa"); }

    @FXML private void onDebug() { navigate("correggiErrore"); }

    @FXML private void onQuiz() { navigate("trovaErrore"); }

    /**
     * Metodo per navigare alla schermata di selezione esercizi con tipo preimpostato.
     *
     * @param type Tipo di esercizio da pre-selezionare
     */
    private void navigate(String type) {
        try {
            ExerciseSelectionController.setPreselectedType(type);
            Parent root = FXMLLoader.load(getClass().getResource("/view/ExerciseSelectionView.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo chiamato al logout dell'utente. Ritorna alla schermata di login.
     */
    @FXML
    private void onLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo per mostrare la classifica degli utenti.
     */
    @FXML
    private void onLeaderboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LeaderboardView.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
