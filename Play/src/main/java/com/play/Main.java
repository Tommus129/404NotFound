package com.play;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

/**
 * Classe principale dell'applicazione JavaFX.
 * Esegue l'avvio del programma e carica inizialmente la schermata di login.
 * Estende {@link javafx.application.Application}, come richiesto da JavaFX.
 */
public class Main extends Application {

    /**
     * Metodo chiamato automaticamente da JavaFX all'avvio dell'app.
     * Inizializza lo stage principale e carica la prima scena (LoginView.fxml).
     *
     * @param primaryStage Stage principale fornito dal framework
     * @throws Exception In caso di errori nel caricamento FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica il layout della schermata iniziale (login)
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));

        // Crea una scena con il layout caricato e dimensioni iniziali generose (non determinanti)
        Scene scene = new Scene(root, 1500, 1500); // ← imposta dimensioni iniziali ampie (poi sovrascritte sotto)

        primaryStage.setTitle("Playground!");     // Titolo della finestra

        // Imposta la scena principale
        primaryStage.setScene(scene);

        // Imposta dimensioni fisse e minime (es. 1000x700)
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(1000);

        // Centra la finestra al centro dello schermo
        primaryStage.centerOnScreen();

        // Mostra lo stage
        primaryStage.show();
    }

    /**
     * Metodo statico di avvio dell'applicazione.
     *
     * @param args Argomenti passati da linea di comando (non utilizzati)
     */
    public static void main(String[] args) {
        launch(args); // Avvia JavaFX
    }
}
