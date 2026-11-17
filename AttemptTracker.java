package org.openjfx.lab2.auth;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps a UserState for every e-mail that exists in the users file.
 * The block applies to the e-mail, not to the window, so all logins of the
 * same e-mail (from any thread) share the same UserState object.
 *
 * ConcurrentHashMap.computeIfAbsent guarantees that two threads asking for
 * the same new e-mail at the same moment still get the SAME UserState.
 */
public class AttemptTracker {

    private final ConcurrentHashMap<String, UserState> states = new ConcurrentHashMap<>();
    private final int maxFailures;     // n
    private final int blockSeconds;    // t

    public AttemptTracker(int maxFailures, int blockSeconds) {
        if (maxFailures < 1 || blockSeconds < 1) {
            throw new IllegalArgumentException("n and t must be >= 1");
        }
        this.maxFailures = maxFailures;
        this.blockSeconds = blockSeconds;
    }

    private UserState state(String email) {
        return states.computeIfAbsent(email, e -> new UserState());
    }

    public boolean isBlocked(String email) {
        UserState s = states.get(email);
        return s != null && s.isBlocked(System.currentTimeMillis());
    }

    public int secondsLeft(String email) {
        UserState s = states.get(email);
        return s == null ? 0 : s.secondsLeft(System.currentTimeMillis());
    }

    public long millisLeft(String email) {
        UserState s = states.get(email);
        return s == null ? 0 : s.millisLeft(System.currentTimeMillis());
    }

    public FailureOutcome recordFailure(String email) {
        return state(email).recordFailure(maxFailures, blockSeconds * 1000L, System.currentTimeMillis());
    }

    /** @return true if the counter was reset, false if the e-mail is blocked right now */
    public boolean recordSuccess(String email) {
        UserState s = states.get(email);
        return s == null || s.recordSuccess(System.currentTimeMillis());
    }

    public int getFailures(String email) {
        UserState s = states.get(email);
        return s == null ? 0 : s.getFailures();
    }

    public int getMaxFailures() { return maxFailures; }

    public int getBlockSeconds() { return blockSeconds; }
}
