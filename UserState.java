package org.openjfx.lab2.auth;

/**
 * Login state of ONE e-mail: how many wrong logins in a row, and until when it is blocked.
 *
 * The same object is shared by every thread that handles this e-mail
 * (two FailureWorkers, BlockCheckThreads, CountdownTask, the sync test),
 * so every method is synchronized: a read-modify-write such as
 * "failures++ and maybe block" runs as one atomic step, and no update is lost.
 *
 * Kept in memory only - it disappears when the program closes.
 */
public class UserState {

    private int failures;          // wrong logins since the last reset
    private long blockedUntil;     // System.currentTimeMillis() when the block ends, 0 = not blocked

    /** If the block time has passed: the counter resets and n new attempts are allowed. */
    private synchronized void expireBlock(long now) {
        if (blockedUntil != 0 && now >= blockedUntil) {
            blockedUntil = 0;
            failures = 0;
        }
    }

    public synchronized boolean isBlocked(long now) {
        expireBlock(now);
        return blockedUntil != 0;
    }

    /** @return milliseconds of block left, 0 if not blocked */
    public synchronized long millisLeft(long now) {
        expireBlock(now);
        return blockedUntil == 0 ? 0 : blockedUntil - now;
    }

    /** @return whole seconds of block left (rounded up), 0 if not blocked */
    public synchronized int secondsLeft(long now) {
        return (int) ((millisLeft(now) + 999) / 1000);
    }

    /**
     * Records one wrong login. The n-th failure starts a block of blockMillis.
     * A failure that arrives while already blocked is not counted.
     */
    public synchronized FailureOutcome recordFailure(int maxFailures, long blockMillis, long now) {
        expireBlock(now);
        if (blockedUntil != 0) {
            return FailureOutcome.blocked(secondsLeft(now));
        }
        int updated = failures + 1;
        failures = updated;
        if (updated >= maxFailures) {
            blockedUntil = now + blockMillis;
            return FailureOutcome.blocked(secondsLeft(now));
        }
        return FailureOutcome.attemptsLeft(maxFailures - updated);
    }

    /**
     * A successful login resets the counter - but only if the e-mail is not blocked.
     * The check and the reset are one atomic step: if a parallel login of the same
     * e-mail started a block after our BlockCheckThread ran, a correct password
     * must NOT cancel that block (policy rule 7).
     * @return true if the counter was reset, false if the e-mail is blocked
     */
    public synchronized boolean recordSuccess(long now) {
        expireBlock(now);
        if (blockedUntil != 0) {
            return false;
        }
        failures = 0;
        return true;
    }

    public synchronized int getFailures() {
        return failures;
    }
}
