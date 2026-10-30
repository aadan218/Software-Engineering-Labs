package org.openjfx.lab2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.openjfx.lab2.App;
import org.openjfx.lab2.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller of login.fxml.
 * Checks the entered credentials against the list of valid users.
 */
public class LoginController {

    @FXML private AnchorPane loginScreen;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Label notext;   // inline error message (not a pop-up)

    private List<User> users = new ArrayList<>();

    /** Called by App after loading the FXML, to supply the valid users. */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /** Handler of the "login" button. */
    @FXML
    void onLoginClick() {
        String name = username.getText().trim();
        String pass = password.getText();

        for (User u : users) {
            if (u.getName().equals(name) && u.getPassword().equals(pass)) {
                showWelcome(name);
                return;
            }
        }
        // 2.3 - wrong details: show an error label on the same screen
        notext.setText("user or password do not match");
    }

    /** Replaces the login scene with the welcome scene (loads a new fxml). */
    private void showWelcome(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("welcome.fxml"));
            Scene welcomeScene = new Scene(loader.load());
            WelcomeController wc = loader.getController();
            wc.setUsername(name);

            Stage stage = (Stage) loginScreen.getScene().getWindow();
            stage.setScene(welcomeScene);
        } catch (IOException e) {
            notext.setText("internal error: cannot open welcome screen");
            e.printStackTrace();
        }
    }
}
