package com.play.controller;

import com.play.model.User;
import com.play.utils.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.List;

/**
 * Controller responsabile della gestione dell'autenticazione dell'utente.
 * Gestisce le azioni di login e di registrazione, interagendo con il FileManager per validare le credenziali.
 * Inoltre, consente la navigazione tra le diverse scene dell'applicazione.
 */
public class AuthController {

    /** Variabile statica per salvare l'username dell'utente loggato */
    public static String loggedUser;

    /** Campo di input per l'username, collegato da FXML */
    @FXML private TextField usernameField;

    /** Campo di input per la password, collegato da FXML */
    @FXML private PasswordField passwordField;

    /** Etichetta per mostrare messaggi di login all'utente */
    @FXML private Label loginMessageLabel;

    /**
     * Metodo chiamato al click del pulsante "Login".
     * Verifica le credenziali inserite e carica la scena successiva in caso di successo.
     * In caso di fallimento, mostra un messaggio di errore.
     */
    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Verifica delle credenziali tramite il FileManager
        if (FileManager.authenticate(username, password).isPresent()) {
            // Se l'autenticazione ha successo, salva l'utente loggato
            loggedUser = username;
            // Carica la dashboard dopo il login
            loadSelectionScene();
        } else {
            // Mostra un messaggio di errore se le credenziali sono errate
            loginMessageLabel.setText("❌ Credenziali errate");
        }
    }

    /**
     * Metodo chiamato al click del pulsante "Registrati".
     * Cambia la scena corrente e carica il form di registrazione.
     */
    @FXML
    private void onRegister() {
        try {
            // Carica il file FXML della vista di registrazione
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RegisterView.fxml"));
            Parent root = loader.load();

            // Ottiene lo stage corrente dalla scena attuale e imposta la nuova scena
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            // In caso di errore nel caricamento del file FXML, stampa lo stack trace
            e.printStackTrace();
        }
    }

    /**
     * Carica la scena della dashboard principale dopo un login riuscito.
     * Gestisce l'eventuale errore di I/O durante il caricamento della vista.
     */
    private void loadSelectionScene() {
        try {
            // Carica la dashboard dopo il login
            Parent root = FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
