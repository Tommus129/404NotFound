package com.play.controller;

import com.play.model.Attempt;
import com.play.utils.FileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller per la classifica degli utenti.
 * Mostra il punteggio totale degli utenti in base agli esercizi completati correttamente,
 * e assegna badge in base alla posizione (oro, argento, bronzo).
 */
public class LeaderboardController {

    // Colonne della tabella
    @FXML private TableView<UserStats> leaderboardTable;
    @FXML private TableColumn<UserStats, String> usernameCol;
    @FXML private TableColumn<UserStats, String> scoreCol;
    @FXML private TableColumn<UserStats, String> badgeCol;

    /**
     * Metodo di inizializzazione chiamato da JavaFX.
     * Popola la tabella con gli utenti ordinati per punteggio e assegna le medaglie.
     */
    @FXML
    public void initialize() {
        // Mappa le proprietà della classe UserStats alle colonne della tabella
        usernameCol.setCellValueFactory(data -> data.getValue().usernameProperty());
        scoreCol.setCellValueFactory(data -> data.getValue().scoreProperty());
        badgeCol.setCellValueFactory(data -> data.getValue().badgeProperty());

        // Carica e ordina i dati
        List<UserStats> statsList = loadLeaderboardData();
        statsList.sort(Comparator.comparingInt(UserStats::getScore).reversed());

        // Assegna medaglie in base alla posizione
        for (int i = 0; i < statsList.size(); i++) {
            UserStats user = statsList.get(i);
            switch (i) {
                case 0:
                    user.setBadge("🥇 Gold");
                    break;
                case 1:
                    user.setBadge("🥈 Silver");
                    break;
                case 2:
                    user.setBadge("🥉 Bronze");
                    break;
                default:
                    user.setBadge("🎓 Newbie");
                    break;
            }
        }

        // Popola la tabella con i dati finali
        leaderboardTable.setItems(FXCollections.observableArrayList(statsList));
    }

    /**
     * Carica e aggrega i tentativi degli utenti per calcolare il punteggio.
     * Il punteggio varia a seconda del livello di difficoltà:
     * - facile = 10 pt
     * - medio = 20 pt
     * - difficile = 30 pt
     *
     * @return Lista degli utenti con i relativi punteggi
     */
    private List<UserStats> loadLeaderboardData() {
        List<Attempt> allAttempts = FileManager.loadAttempts();

        // Raggruppa i tentativi per utente
        Map<String, List<Attempt>> attemptsByUser = allAttempts.stream()
                .collect(Collectors.groupingBy(Attempt::getUsername));

        List<UserStats> stats = new ArrayList<>();

        for (Map.Entry<String, List<Attempt>> entry : attemptsByUser.entrySet()) {
            String user = entry.getKey();
            List<Attempt> userAttempts = entry.getValue();

            // Calcola il punteggio totale per utente, solo per tentativi corretti
            int score = userAttempts.stream()
                    .filter(Attempt::isCorrect)
                    .mapToInt(attempt -> {
                        String level = attempt.getLevel().toLowerCase();
                        if (level.equals("facile")) return 10;
                        if (level.equals("medio")) return 20;
                        if (level.equals("difficile")) return 30;
                        return 0;
                    }).sum();

            stats.add(new UserStats(user, score));
        }

        return stats;
    }

    /**
     * Torna alla schermata della dashboard principale.
     */
    @FXML
    private void onBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"));
            Stage stage = (Stage) leaderboardTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Classe interna per rappresentare le statistiche utente.
     * Ogni istanza rappresenta un utente con username, punteggio e badge assegnato.
     */
    public static class UserStats {
        private final String username;
        private final int score;
        private String badge;

        public UserStats(String username, int score) {
            this.username = username;
            this.score = score;
        }

        public javafx.beans.property.SimpleStringProperty usernameProperty() {
            return new javafx.beans.property.SimpleStringProperty(username);
        }

        public javafx.beans.property.SimpleStringProperty scoreProperty() {
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(score));
        }

        public javafx.beans.property.SimpleStringProperty badgeProperty() {
            return new javafx.beans.property.SimpleStringProperty(badge);
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public int getScore() {
            return score;
        }
    }
}
