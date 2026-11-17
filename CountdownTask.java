package org.openjfx.lab2.auth;

import javafx.application.Platform;

import java.util.function.IntConsumer;

/**
 * Live countdown on the screen while an e-mail is blocked.
 * Every second it reads the time left from AttemptTracker and sends it to the
 * screen with Platform.runLater; when the block ends it calls onDone.
 * The sleep happens here, never on the JavaFX thread.
 * interrupt() stops it immediately (used when the window is closed).
 *
 * Implements Runnable: a unit of work run by a daemon thread the controller creates.
 */
public class CountdownTask implements Runnable {

    private final String email;
    private final AttemptTracker tracker;
    private final IntConsumer onTick;   // seconds left
    private final Runnable onDone;

    public CountdownTask(String email, AttemptTracker tracker, IntConsumer onTick, Runnable onDone) {
        this.email = email;
        this.tracker = tracker;
        this.onTick = onTick;
        this.onDone = onDone;
    }

    @Override
    public void run() {
        try {
            long millis;
            while ((millis = tracker.millisLeft(email)) > 0) {
                int seconds = (int) ((millis + 999) / 1000);
                Platform.runLater(() -> onTick.accept(seconds));
                long untilNextSecond = millis % 1000 == 0 ? 1000 : millis % 1000;
                Thread.sleep(untilNextSecond);
            }
            Platform.runLater(onDone);
        } catch (InterruptedException e) {
            // window closed - stop counting
        }
    }
}
