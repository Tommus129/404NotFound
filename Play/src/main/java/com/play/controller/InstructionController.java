package com.play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Controller per la schermata introduttiva degli esercizi.
 * Mostra un messaggio personalizzato a seconda del tipo di esercizio selezionato.
 */
public class InstructionController {

    @FXML private Label instructionLabel;

    /** Tipo di esercizio selezionato (es. "output", "completa", ...) */
    private static String exerciseType;

    /**
     * Imposta il tipo di esercizio per cui verrà mostrata l'istruzione.
     * Metodo statico chiamato prima del caricamento della scena.
     *
     * @param type Tipo dell'esercizio (output, correggiErrore, ecc.)
     */
    public static void setExerciseType(String type) {
        exerciseType = type;
    }

    /**
     * Metodo chiamato all'inizializzazione della schermata.
     * Imposta il messaggio di istruzione in base al tipo di esercizio selezionato.
     */
    @FXML
    public void initialize() {
        String text;

        // Messaggi personalizzati per ciascun tipo di esercizio
        switch (exerciseType) {
            case "trovaErrore":
                text = "👀 Tra i quattro frammenti di codice proposti, solo uno è corretto. Riesci a trovare quello giusto?";
                break;
            case "output":
                text = "🖥 In questo esercizio vedrai un frammento di codice. Il tuo compito è prevedere quale output verrà stampato.";
                break;
            case "completa":
                text = "🧩 Completa correttamente il codice scegliendo l'opzione mancante!";
                break;
            case "correggiErrore":
                text = "🐞 Trova e correggi l'errore nel codice. Attento ai dettagli!";
                break;
            default:
                text = "📘 Leggi attentamente le istruzioni e seleziona la risposta corretta.";
        }

        // Imposta il testo sull’etichetta della GUI
        instructionLabel.setText(text);
    }

    /**
     * Evento legato al click del pulsante "OK".
     * Passa alla schermata principale dell'esercizio.
     */
    @FXML
    private void onOkClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExerciseView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) instructionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
