package com.play.controller;

import com.play.model.Attempt;
import com.play.model.Exercise;
import com.play.utils.FileManager;
import com.play.utils.SyntaxHighlighter;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Controller della vista esercizi.
 * Gestisce la visualizzazione delle domande, del codice, delle opzioni di risposta
 * e delle statistiche temporali e di correttezza dell'utente.
 */
public class ExerciseController {

    @FXML private Label questionLabel;
    @FXML private VBox optionsBox;
    @FXML private Label timerLabel;
    @FXML private VBox codeContainer;
    @FXML private Button exitButton;

    private CodeArea codeArea;                // Editor per il codice evidenziato
    private List<Exercise> exercises;         // Lista degli esercizi da svolgere
    private int currentIndex = 0;             // Indice dell'esercizio corrente
    private int correctCount = 0;             // Conteggio delle risposte corrette
    private long totalTimeMillis = 0;         // Tempo totale impiegato
    private long startTime;                   // Inizio del timer per la domanda corrente
    private Timeline timer;                   // Timer per visualizzare il tempo in UI

    /**
     * Metodo chiamato automaticamente all'inizializzazione del controller.
     * Carica gli esercizi selezionati e mostra il primo.
     */
    @FXML
    public void initialize() {
        exercises = ExerciseSelectionController.getSelectedExercises();

        // Verifica che esistano esercizi da svolgere
        if (exercises == null || exercises.isEmpty()) {
            questionLabel.setText("Nessun esercizio disponibile.");
            return;
        }

        setupCodeArea();
        fadeIn(questionLabel);
        showExercise();
    }

    /**
     * Inizializza l'area di codice con numeri di linea e stile di evidenziazione.
     */
    private void setupCodeArea() {
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setEditable(false);
        codeArea.setWrapText(true);
        codeArea.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 15px;");
        codeArea.setPrefHeight(250);
        codeArea.getStylesheets().add(getClass().getResource("/styles/syntax.css").toExternalForm());
        codeContainer.getChildren().add(codeArea);
    }

    /**
     * Mostra l'esercizio corrente. Se non ci sono più esercizi, passa alla schermata di riepilogo.
     */
    private void showExercise() {
        if (currentIndex >= exercises.size()) {
            goToSummary();
            return;
        }

        Exercise ex = exercises.get(currentIndex);
        questionLabel.setText("Domanda " + (currentIndex + 1) + ": " + ex.getQuestion());

        // Se l'esercizio include codice, lo mostra con evidenziazione
        Object rawCode = ex.getCode();
        if (rawCode instanceof String && !((String) rawCode).trim().isEmpty()) {
            String code = (String) rawCode;
            codeArea.setVisible(true);
            codeArea.replaceText(code);
            StyleSpans<Collection<String>> spans = SyntaxHighlighter.computeHighlighting(code);
            codeArea.setStyleSpans(0, spans);
        } else {
            codeArea.clear();
            codeArea.setVisible(false);
        }

        renderOptions(ex);
        fadeIn(optionsBox);
        startTimer();
    }

    /**
     * Mostra le opzioni di risposta come pulsanti.
     *
     * @param ex L'esercizio corrente
     */
    private void renderOptions(Exercise ex) {
        optionsBox.getChildren().clear();
        List<String> options = ex.getOptions();

        if (options == null || options.isEmpty()) {
            Label warning = new Label("⚠️ Nessuna opzione disponibile per questo esercizio.");
            warning.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            optionsBox.getChildren().add(warning);
            return;
        }

        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            Button btn = new Button(option);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setFont(Font.font("Arial", 16));
            btn.setStyle("-fx-background-color: #eeeeee; -fx-border-color: #cccccc;");

            // Aggiunge evento click alla risposta
            int finalI = i;
            btn.setOnAction(e -> checkAnswer(finalI, btn));
            addHoverEffect(btn);

            optionsBox.getChildren().add(btn);
        }
    }

    /**
     * Controlla se la risposta selezionata è corretta e aggiorna l'interfaccia.
     *
     * @param selectedIndex indice della risposta selezionata
     * @param selectedButton pulsante selezionato
     */
    private void checkAnswer(int selectedIndex, Button selectedButton) {
        long timeSpent = System.currentTimeMillis() - startTime;
        totalTimeMillis += timeSpent;

        if (timer != null) timer.stop();

        Exercise ex = exercises.get(currentIndex);
        boolean correct = (selectedIndex == ex.getCorrectOptionIndex());

        // Disabilita tutti i pulsanti dopo la risposta
        optionsBox.getChildren().forEach(node -> node.setDisable(true));

        // Mostra feedback visivo sulla risposta selezionata
        animateAnswer(selectedButton, correct);

        // Evidenzia la risposta corretta se l'utente ha sbagliato
        if (!correct && ex.getCorrectOptionIndex() < optionsBox.getChildren().size()) {
            Button correctBtn = (Button) optionsBox.getChildren().get(ex.getCorrectOptionIndex());
            animateAnswer(correctBtn, true);
        }

        if (correct) correctCount++;

        // Salva il tentativo dell'utente
        FileManager.saveAttempt(new Attempt(
                AuthController.loggedUser,
                ex.getType(),
                ex.getLevel(),
                ex.getQuestion(),
                correct,
                ex.getId()
        ));

        // Passa al prossimo esercizio con breve pausa
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            currentIndex++;
            showExercise();
        });
        pause.play();
    }

    /**
     * Applica un'animazione di feedback visivo su una risposta.
     *
     * @param btn Pulsante da animare
     * @param isCorrect Se la risposta è corretta
     */
    private void animateAnswer(Button btn, boolean isCorrect) {
        btn.setStyle(isCorrect
                ? "-fx-background-color: #a5d6a7; -fx-font-weight: bold;"  // Verde
                : "-fx-background-color: #ef9a9a; -fx-font-weight: bold;"); // Rosso

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), btn);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    /**
     * Avvia il timer per tracciare il tempo della domanda corrente.
     */
    private void startTimer() {
        startTime = System.currentTimeMillis();
        if (timer != null) timer.stop();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /**
     * Aggiorna la visualizzazione del tempo trascorso sulla domanda corrente.
     */
    private void updateTimer() {
        long elapsed = System.currentTimeMillis() - startTime;
        timerLabel.setText("⏱ " + (elapsed / 1000) + "s");
    }

    /**
     * Naviga alla schermata di riepilogo finale con i risultati dell’utente.
     */
    private void goToSummary() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SummaryView.fxml"));
            Parent root = loader.load();

            // Passa i dati al controller del riepilogo
            SummaryController controller = loader.getController();
            controller.setData(correctCount, exercises.size(), totalTimeMillis);

            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applica un effetto fade-in a un nodo.
     *
     * @param node Il nodo su cui applicare l'effetto
     */
    private void fadeIn(javafx.scene.Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Aggiunge effetto hover visivo a un pulsante.
     *
     * @param btn Pulsante su cui applicare l’effetto
     */
    private void addHoverEffect(Button btn) {
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #cfd8dc;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #eeeeee; -fx-border-color: #cccccc;"));
    }

    /**
     * Metodo associato al pulsante "Esci". Ferma il timer e torna alla dashboard.
     */
    @FXML
    private void onExit() {
        if (timer != null) timer.stop();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"));
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
