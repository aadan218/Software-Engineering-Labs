package org.openjfx.lab2.auth;

/**
 * The result that LoginTask reports to the controller:
 *   SUCCESS  - correct details
 *   ERROR    - wrong details; attemptsLeft >= 0 for an existing e-mail,
 *              UNKNOWN_EMAIL (-1) when the e-mail does not exist (not tracked)
 *   BLOCKED  - the e-mail is blocked, secondsLeft until it is released
 */
public final class LoginResult {

    public enum Type { SUCCESS, ERROR, BLOCKED }

    public static final int UNKNOWN_EMAIL = -1;

    private final Type type;
    private final String email;
    private final int attemptsLeft;
    private final int secondsLeft;

    private LoginResult(Type type, String email, int attemptsLeft, int secondsLeft) {
        this.type = type;
        this.email = email;
        this.attemptsLeft = attemptsLeft;
        this.secondsLeft = secondsLeft;
    }

    public static LoginResult success(String email) {
        return new LoginResult(Type.SUCCESS, email, 0, 0);
    }

    public static LoginResult error(String email, int attemptsLeft) {
        return new LoginResult(Type.ERROR, email, attemptsLeft, 0);
    }

    public static LoginResult unknownEmail(String email) {
        return new LoginResult(Type.ERROR, email, UNKNOWN_EMAIL, 0);
    }

    public static LoginResult blocked(String email, int secondsLeft) {
        return new LoginResult(Type.BLOCKED, email, 0, secondsLeft);
    }

    public Type getType() { return type; }

    public String getEmail() { return email; }

    public int getAttemptsLeft() { return attemptsLeft; }

    public int getSecondsLeft() { return secondsLeft; }

    public boolean isUnknownEmail() { return type == Type.ERROR && attemptsLeft == UNKNOWN_EMAIL; }

    @Override
    public String toString() {
        return type + "(" + email + ", attemptsLeft=" + attemptsLeft + ", secondsLeft=" + secondsLeft + ")";
    }
}
