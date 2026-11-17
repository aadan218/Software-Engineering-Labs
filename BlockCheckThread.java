package org.openjfx.lab2.auth;

/**
 * Checks whether an e-mail is blocked right now.
 * LoginTask starts it and waits for it with join(), then reads the result.
 *
 * Extends Thread: a one-shot thread that carries its own result fields,
 * so after join() LoginTask simply reads them from the same object.
 */
public class BlockCheckThread extends Thread {

    private final String email;
    private final AttemptTracker tracker;

    // written by this thread before it ends; join() makes them visible to the caller
    private boolean blocked;
    private int secondsLeft;

    public BlockCheckThread(String email, AttemptTracker tracker) {
        super("BlockCheck-" + email);
        this.email = email;
        this.tracker = tracker;
        setDaemon(true);
    }

    @Override
    public void run() {
        secondsLeft = tracker.secondsLeft(email);
        blocked = secondsLeft > 0;
    }

    public boolean isBlocked() { return blocked; }

    public int getSecondsLeft() { return secondsLeft; }
}
