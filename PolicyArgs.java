package org.openjfx.lab2.auth;

import java.util.List;

/**
 * The two policy parameters given on the command line:
 *   n - maximum number of wrong logins before the e-mail is blocked
 *   t - block duration in seconds
 * Both must be whole numbers >= 1. Example: java -jar app.jar 3 30
 */
public final class PolicyArgs {

    private final int maxFailures;   // n
    private final int blockSeconds;  // t

    public PolicyArgs(int maxFailures, int blockSeconds) {
        if (maxFailures < 1 || blockSeconds < 1) {
            throw new IllegalArgumentException("n and t must be whole numbers >= 1");
        }
        this.maxFailures = maxFailures;
        this.blockSeconds = blockSeconds;
    }

    /**
     * Parses n and t from the program arguments.
     * @return the parameters, or null if they are missing, not whole numbers, or smaller than 1
     */
    public static PolicyArgs fromArgs(List<String> args) {
        if (args == null || args.size() < 2) {
            return null;
        }
        Integer n = parsePositive(args.get(0));
        Integer t = parsePositive(args.get(1));
        return (n == null || t == null) ? null : new PolicyArgs(n, t);
    }

    /** @return the value if text is a whole number >= 1, otherwise null */
    public static Integer parsePositive(String text) {
        if (text == null) {
            return null;
        }
        try {
            int v = Integer.parseInt(text.trim());
            return v >= 1 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public int getMaxFailures() { return maxFailures; }

    public int getBlockSeconds() { return blockSeconds; }

    @Override
    public String toString() { return "n=" + maxFailures + ", t=" + blockSeconds; }
}
