package gcm.lab2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Controller of the welcome screen shown after a successful login. */
public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    public void setUser(User user) {
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
    }
}
