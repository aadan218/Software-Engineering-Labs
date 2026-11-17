package org.openjfx.lab2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.openjfx.lab2.App;
import org.openjfx.lab2.User;
import org.openjfx.lab2.auth.AttemptTracker;
import org.openjfx.lab2.auth.CountdownTask;
import org.openjfx.lab2.auth.FailedAttempt;
import org.openjfx.lab2.auth.LoginResult;
import org.openjfx.lab2.auth.LoginTask;
import org.openjfx.lab2.auth.SyncTest;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Controller of login.fxml. Kept thin: reads the fields, starts a LoginTask
 * on a new thread, and shows the LoginResult. No counting or time logic here -
 * the counting is in AttemptTracker/FailureWorker and the timing in CountdownTask.
 * Every method of this class runs on the JavaFX thread.
 */
public class LoginController {

    @FXML private AnchorPane loginScreen;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Label notext;   // inline message (not a pop-up)
    @FXML private Button testButton;
    @FXML private Label testResult;

    private List<User> users = new ArrayList<>();
    private AttemptTracker tracker;
    private BlockingQueue<FailedAttempt> failureQueue;
    private Thread countdownThread;

    /** Called by App after loading the FXML. */
    public void init(List<User> users, AttemptTracker tracker, BlockingQueue<FailedAttempt> failureQueue) {
        this.users = users;
        this.tracker = tracker;
        this.failureQueue = failureQueue;
    }

    /** Handler of the "login" button. */
    @FXML
    void onLoginClick() {
        String name = username.getText().trim();
        String pass = password.getText();

        setInputDisabled(true);          // no double clicks while the login runs
        notext.setText("");

        LoginTask task = new LoginTask(name, pass, users, tracker, failureQueue, this::showResult);
        Thread t = new Thread(task, "LoginTask-" + name);
        t.setDaemon(true);
        t.start();
    }

    /** Called through Platform.runLater by LoginTask - so it runs on the JavaFX thread. */
    private void showResult(LoginResult result) {
        switch (result.getType()) {
            case SUCCESS:
                showWelcome(result.getEmail());
                break;
            case ERROR:
                if (result.isUnknownEmail()) {
                    notext.setText("פרטים שגויים");
                } else {
                    int left = result.getAttemptsLeft();
                    notext.setText(left == 1 ? "פרטים שגויים, נותר ניסיון אחד"
                                             : "פרטים שגויים, נותרו " + left + " ניסיונות");
                }
                setInputDisabled(false);
                password.clear();
                break;
            case BLOCKED:
                showBlocked(result.getSecondsLeft());
                startCountdown(result.getEmail());
                break;
        }
    }

    private void showBlocked(int secondsLeft) {
        notext.setText("החשבון חסום, נותרו " + secondsLeft + " שניות");
    }

    /** Fields stay enabled during the block, the Login button opens again when it ends. */
    private void startCountdown(String email) {
        username.setDisable(false);
        password.setDisable(false);
        loginButton.setDisable(true);
        password.clear();

        CountdownTask countdown = new CountdownTask(email, tracker, this::showBlocked, () -> {
            notext.setText("");
            loginButton.setDisable(false);
        });
        countdownThread = new Thread(countdown, "Countdown-" + email);
        countdownThread.setDaemon(true);
        countdownThread.start();
    }

    private void setInputDisabled(boolean disabled) {
        username.setDisable(disabled);
        password.setDisable(disabled);
        loginButton.setDisable(disabled);
    }

    /** Handler of the "sync test" button: runs SyncTest on a background thread. */
    @FXML
    void onSyncTestClick() {
        testButton.setDisable(true);
        testResult.setText("running 20 threads...");
        Thread t = new Thread(() -> {
            String report;
            try {
                report = SyncTest.run();
            } catch (InterruptedException e) {
                return;
            }
            System.out.println(report);
            Platform.runLater(() -> {
                testResult.setText(report);
                testButton.setDisable(false);
            });
        }, "SyncTestRunner");
        t.setDaemon(true);
        t.start();
    }

    /** Called by App when the window is closed: stop the countdown thread. */
    public void shutdown() {
        if (countdownThread != null) {
            countdownThread.interrupt();
        }
    }

    /** Replaces the login scene with the welcome scene of lab 2. */
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
            setInputDisabled(false);
            e.printStackTrace();
        }
    }
}
