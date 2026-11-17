package org.openjfx.lab2.auth;

import java.util.concurrent.BlockingQueue;

/**
 * Consumer thread (two of them run). Takes FailedAttempt events from the
 * shared BlockingQueue, updates the failure counter in AttemptTracker, and
 * answers whether attempts are left or the user is now blocked.
 *
 * Extends Thread: it is a long-lived thread with a fixed identity (name, daemon flag)
 * that the program starts once and stops on exit.
 * Daemon: it must never keep the JVM alive after the window is closed.
 */
public class FailureWorker extends Thread {

    private final BlockingQueue<FailedAttempt> queue;
    private final AttemptTracker tracker;

    public FailureWorker(String name, BlockingQueue<FailedAttempt> queue, AttemptTracker tracker) {
        super(name);
        this.queue = queue;
        this.tracker = tracker;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                FailedAttempt attempt = queue.take();          // sleeps until an event arrives
                FailureOutcome outcome = tracker.recordFailure(attempt.getEmail());
                System.out.println(getName() + ": failure of " + attempt.getEmail() + " -> " + outcome);
                attempt.answer(outcome);
            }
        } catch (InterruptedException e) {
            // asked to stop
        }
    }
}
