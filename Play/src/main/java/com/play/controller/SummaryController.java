package com.play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;

/**
 * Controller per la schermata di riepilogo finale.
 * Mostra all'utente il numero di risposte corrette e il tempo impiegato per completare la sessione.
 */
public class SummaryController {

    @FXML private Label resultLabel;
    @FXML private Label timeLabel;

    // Dati aggregati sulla performance dell'utente
    private int correctCount;
    private int totalCount;
    private long totalTimeMillis;

    /**
     * Metodo pubblico chiamato per impostare i dati della sessione completata.
     * Calcola anche la percentuale di risposte corrette.
     *
     * @param correct Numero di risposte corrette
     * @param total Numero totale di domande
     * @param millis Tempo totale impiegato in millisecondi
     */
    public void setData(int correct, int total, long millis) {
        this.correctCount = correct;
        this.totalCount = total;
        this.totalTimeMillis = millis;

        // Calcolo della percentuale di risposte corrette
        double percent = ((double) correct / total) * 100;

        // Aggiorna la UI con i risultati
        resultLabel.setText("Hai risposto correttamente a " + correct + " su " + total + " (" + (int) percent + "%)");
        timeLabel.setText("Tempo totale: " + (millis / 1000.0) + " secondi");
    }

    /**
     * Metodo chiamato al click del pulsante "Torna alla Dashboard".
     * Naviga indietro alla schermata principale.
     */
    @FXML
    private void onBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"));
            Stage stage = (Stage) resultLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
