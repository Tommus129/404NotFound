package com.play.controller;

import com.play.model.User;
import com.play.utils.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.util.List;

/**
 * Controller della schermata di registrazione.
 * Consente all'utente di creare un nuovo account, verificando che l'username non sia già esistente.
 */
public class RegisterController {

    // Campi FXML per l'inserimento dati utente
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;

    /**
     * Metodo chiamato al click sul pulsante "Registrati".
     * Valida i campi, controlla l'univocità dell'username, salva l'utente e ritorna alla login.
     */
    @FXML
    private void onRegister() {
        // Recupera e normalizza i dati inseriti
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String nome = nameField.getText().trim();
        String cognome = surnameField.getText().trim();

        // Verifica che nessun campo sia vuoto
        if (username.isEmpty() || password.isEmpty() || nome.isEmpty() || cognome.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Tutti i campi sono obbligatori.");
            return;
        }

        // Controlla se l'username è già registrato
        List<User> users = FileManager.loadUsers();
        boolean exists = users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));

        if (exists) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Username già esistente. Scegli un altro.");
            return;
        }

        // Crea il nuovo utente e lo salva nel file
        User newUser = new User(username, password, nome, cognome);
        FileManager.saveUser(newUser);

        // Conferma registrazione completata
        showAlert(Alert.AlertType.INFORMATION, "Successo", "Registrazione completata!");

        // Torna alla schermata di login
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Torna alla schermata di login senza registrarsi.
     */
    @FXML
    private void onBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra un messaggio a schermo tramite finestra modale (JavaFX Alert).
     *
     * @param type    Tipo di alert (INFORMATION, ERROR, WARNING)
     * @param title   Titolo della finestra
     * @param message Testo contenuto nel messaggio
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
