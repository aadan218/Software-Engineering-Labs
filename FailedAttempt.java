package org.openjfx.lab2.auth;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Event "a wrong login of an existing e-mail", put by LoginTask on the shared
 * failure queue. It carries its own small reply queue (capacity 1): the
 * FailureWorker that handles it puts the outcome there, and LoginTask
 * take()s it - so LoginTask knows right away whether THIS failure caused a block.
 */
public final class FailedAttempt {

    private final String email;
    private final BlockingQueue<FailureOutcome> reply = new ArrayBlockingQueue<>(1);

    public FailedAttempt(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }

    /** Called by FailureWorker. */
    public void answer(FailureOutcome outcome) throws InterruptedException {
        reply.put(outcome);
    }

    /** Called by LoginTask: waits (without busy wait) until a FailureWorker answers. */
    public FailureOutcome awaitAnswer() throws InterruptedException {
        return reply.take();
    }
}
