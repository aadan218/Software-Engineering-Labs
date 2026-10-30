package org.openjfx.lab2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openjfx.lab2.controller.LoginController;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point of lab 2. Loads the valid users, then shows the login screen.
 * Optional argument: path of an input file (default: users.txt from resources).
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // 2.1 - read the input file into an ArrayList of valid users
        ArrayList<User> users = readUsers();

        // 2.2 - open the login screen
        FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());
        LoginController controller = loader.getController();
        controller.setUsers(users);

        stage.setTitle("Users Login");
        stage.setScene(scene);

        // 2.4 - closing the window with X ends the program
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.show();
    }

    /** Reads from the file given as first argument, or from the bundled users.txt. */
    private ArrayList<User> readUsers() throws IOException {
        List<String> args = getParameters().getRaw();
        if (!args.isEmpty()) {
            try (Reader r = Files.newBufferedReader(Paths.get(args.get(0)), StandardCharsets.UTF_8)) {
                return UserLoader.load(r);
            }
        }
        InputStream in = App.class.getResourceAsStream(UserLoader.DEFAULT_RESOURCE);
        if (in == null) {
            System.err.println("Input file not found: " + UserLoader.DEFAULT_RESOURCE);
            return new ArrayList<>();
        }
        try (Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return UserLoader.load(r);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
