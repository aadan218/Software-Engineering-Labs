package org.openjfx.lab2.auth;

import javafx.application.Platform;
import org.openjfx.lab2.User;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created on every click on Login and runs on its own thread.
 * Manages the whole login flow and reports a LoginResult back to the
 * controller with Platform.runLater (screen updates only on the JavaFX thread).
 *
 * Flow:
 *  1. start a BlockCheckThread and join() it. Blocked -> BLOCKED, password is not checked.
 *  2. validate the details with the lab 1 User class (throws IllegalArgumentException
 *     on invalid input - caught here) and compare with the users list.
 *  3. success -> reset the counter, SUCCESS (unless a parallel login blocked
 *     the e-mail in the meantime - then BLOCKED).
 *  4. unknown e-mail -> ERROR without tracking.
 *  5. existing e-mail, wrong password -> put a FailedAttempt on the queue and
 *     take() the FailureWorker's answer: ERROR with attempts left, or BLOCKED.
 *
 * Implements Runnable: it is a unit of work, not a thread; the controller decides how to run it.
 */
public class LoginTask implements Runnable {

    private final String email;
    private final String password;
    private final List<User> users;
    private final AttemptTracker tracker;
    private final BlockingQueue<FailedAttempt> failureQueue;
    private final Consumer<LoginResult> onResult;

    public LoginTask(String email, String password, List<User> users, AttemptTracker tracker,
                     BlockingQueue<FailedAttempt> failureQueue, Consumer<LoginResult> onResult) {
        this.email = email;
        this.password = password;
        this.users = users;
        this.tracker = tracker;
        this.failureQueue = failureQueue;
        this.onResult = onResult;
    }

    @Override
    public void run() {
        try {
            LoginResult result = login();
            System.out.println(Thread.currentThread().getName() + ": " + result);
            Platform.runLater(() -> onResult.accept(result));
        } catch (InterruptedException e) {
            // program is closing
        }
    }

    /** The login logic itself (no JavaFX). Package-visible so it can be tested without a screen. */
    LoginResult login() throws InterruptedException {
        // 1. block check in a separate thread, wait for it with join
        BlockCheckThread check = new BlockCheckThread(email, tracker);
        check.start();
        check.join();
        if (check.isBlocked()) {
            return LoginResult.blocked(email, check.getSecondsLeft());
        }

        // 2. validate with the lab 1 User class
        boolean validInput;
        try {
            new User(email, password);
            validInput = true;
        } catch (IllegalArgumentException e) {
            validInput = false;     // invalid e-mail/password format -> treated as wrong details
        }
        User account = findUser(email);

        // 4. e-mail does not exist: same error message, no tracking
        if (account == null) {
            return LoginResult.unknownEmail(email);
        }

        // 3. success
        if (validInput && account.getPassword().equals(password)) {
            if (!tracker.recordSuccess(email)) {
                // a parallel login blocked this e-mail after our block check
                return LoginResult.blocked(email, tracker.secondsLeft(email));
            }
            return LoginResult.success(email);
        }

        // 5. existing e-mail, wrong password -> FailureWorker updates the counter
        FailedAttempt attempt = new FailedAttempt(email);
        failureQueue.put(attempt);
        FailureOutcome outcome = attempt.awaitAnswer();
        return outcome.isBlocked()
                ? LoginResult.blocked(email, outcome.getSecondsLeft())
                : LoginResult.error(email, outcome.getAttemptsLeft());
    }

    private User findUser(String email) {
        for (User u : users) {
            if (u.getName().equals(email)) {
                return u;
            }
        }
        return null;
    }
}
