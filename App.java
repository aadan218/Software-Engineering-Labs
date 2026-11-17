package org.openjfx.lab2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openjfx.lab2.auth.ArgsDialog;
import org.openjfx.lab2.auth.AttemptTracker;
import org.openjfx.lab2.auth.FailedAttempt;
import org.openjfx.lab2.auth.FailureWorker;
import org.openjfx.lab2.auth.PolicyArgs;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Entry point of lab 3.
 * Program arguments: n t [usersFile]
 *   n - max wrong logins, t - block seconds (both whole numbers >= 1),
 *   usersFile - optional input file (default: users.txt from resources).
 */
public class App extends Application {

    private static final int FAILURE_WORKERS = 2;

    private ArrayList<User> users;
    private PolicyArgs policy;
    private final List<FailureWorker> workers = new ArrayList<>();
    private LoginController controller;

    /**
     * Runs on the JavaFX launcher thread, NOT on the JavaFX UI thread,
     * so reading the users file here does not freeze the screen.
     */
    @Override
    public void init() throws IOException {
        List<String> args = getParameters().getRaw();
        policy = PolicyArgs.fromArgs(args);
        users = readUsers(args.size() >= 3 ? args.get(2) : null);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Check n and t before opening the first screen
        if (policy == null) {
            policy = ArgsDialog.ask().orElse(null);
            if (policy == null) {
                Platform.exit();
                return;
            }
        }
        System.out.println("Login policy: " + policy);

        // shared state + the two FailureWorker consumer threads
        AttemptTracker tracker = new AttemptTracker(policy.getMaxFailures(), policy.getBlockSeconds());
        BlockingQueue<FailedAttempt> failureQueue = new LinkedBlockingQueue<>();
        for (int i = 1; i <= FAILURE_WORKERS; i++) {
            FailureWorker w = new FailureWorker("FailureWorker-" + i, failureQueue, tracker);
            workers.add(w);
            w.start();
        }

        FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();
        controller.init(users, tracker, failureQueue);

        stage.setTitle("Users Login  (n=" + policy.getMaxFailures() + ", t=" + policy.getBlockSeconds() + "s)");
        stage.setScene(scene);
        // closing the window (even in the middle of a countdown) ends the program
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.show();
    }

    /** Called when the JavaFX application ends: stop every background thread. */
    @Override
    public void stop() {
        if (controller != null) {
            controller.shutdown();
        }
        for (FailureWorker w : workers) {
            w.interrupt();
        }
        System.out.println("All threads stopped, exiting.");
    }

    /** Reads from the given file, or from the bundled users.txt. */
    private static ArrayList<User> readUsers(String path) throws IOException {
        if (path != null) {
            try (Reader r = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
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
