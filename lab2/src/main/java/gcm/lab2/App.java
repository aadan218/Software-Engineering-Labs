package gcm.lab2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/** Entry point: loads the users and shows the login screen. */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ArrayList<User> users = UserLoader.load("/users.txt");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setUsers(users);

        stage.setTitle("GCM Login");
        stage.setScene(new Scene(root));
        // Closing the window ends the program (JavaFX exits when the last window closes).
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
