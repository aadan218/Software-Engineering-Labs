package org.openjfx.lab2.auth;

/**
 * What happened after one wrong login was recorded:
 * either the user still has attempts left, or she is now blocked.
 */
public final class FailureOutcome {

    private final boolean blocked;
    private final int attemptsLeft;
    private final int secondsLeft;

    private FailureOutcome(boolean blocked, int attemptsLeft, int secondsLeft) {
        this.blocked = blocked;
        this.attemptsLeft = attemptsLeft;
        this.secondsLeft = secondsLeft;
    }

    public static FailureOutcome attemptsLeft(int left) { return new FailureOutcome(false, left, 0); }

    public static FailureOutcome blocked(int secondsLeft) { return new FailureOutcome(true, 0, secondsLeft); }

    public boolean isBlocked() { return blocked; }

    public int getAttemptsLeft() { return attemptsLeft; }

    public int getSecondsLeft() { return secondsLeft; }

    @Override
    public String toString() {
        return blocked ? "BLOCKED(" + secondsLeft + "s)" : "LEFT(" + attemptsLeft + ")";
    }
}
