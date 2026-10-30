package gcm.lab2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/** Controller of the login screen. */
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private ArrayList<User> users = new ArrayList<>();

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    /** Handler of the login button: on success replaces the scene with the welcome screen. */
    @FXML
    private void onLogin() throws IOException {
        String name = usernameField.getText();
        String pass = passwordField.getText();

        for (User user : users) {
            if (user.matches(name, pass)) {
                showWelcome(user);
                return;
            }
        }
        // Inline error message (not a pop-up)
        errorLabel.setText("Invalid username or password");
    }

    private void showWelcome(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        Parent root = loader.load();
        WelcomeController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Welcome");
        stage.setScene(new Scene(root));
    }
}
