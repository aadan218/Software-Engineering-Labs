package org.openjfx.lab2.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Proves that the synchronized methods of UserState do not lose updates.
 * Started by the "sync test" button on the login screen (or from main).
 *
 * With n = 10,000: 20 threads run at the same time against the SAME e-mail,
 * each records 499 failures (9,980 in total). Then 19 more failures must
 * NOT block, and the 20th failure (number 10,000) must block.
 *
 * It uses its own AttemptTracker, so it does not touch the real users.
 */
public class SyncTest {

    public static final int N = 10_000;
    public static final int THREADS = 20;
    public static final int FAILURES_PER_THREAD = 499;
    private static final String EMAIL = "sync.test@example.com";

    /** Runs the test and returns a human-readable report. */
    public static String run() throws InterruptedException {
        AttemptTracker tracker = new AttemptTracker(N, 60);
        CountDownLatch startGate = new CountDownLatch(1);   // all 20 threads start together
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            Thread t = new Thread(() -> {
                try {
                    startGate.await();
                    for (int k = 0; k < FAILURES_PER_THREAD; k++) {
                        tracker.recordFailure(EMAIL);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "SyncTest-" + i);
            threads.add(t);
            t.start();
        }
        long start = System.nanoTime();
        startGate.countDown();
        for (Thread t : threads) {
            t.join();
        }
        long ms = (System.nanoTime() - start) / 1_000_000;

        int expected = THREADS * FAILURES_PER_THREAD;    // 9,980
        int counted = tracker.getFailures(EMAIL);

        StringBuilder sb = new StringBuilder();
        sb.append("Sync test: ").append(THREADS).append(" threads x ").append(FAILURES_PER_THREAD)
          .append(" failures, n=").append(N).append(" (").append(ms).append(" ms)\n");
        sb.append("counted ").append(counted).append(" / expected ").append(expected)
          .append(" -> lost updates: ").append(expected - counted).append('\n');

        // 19 more failures must not block
        boolean blockedEarly = false;
        FailureOutcome last = null;
        for (int i = 1; i <= 19; i++) {
            last = tracker.recordFailure(EMAIL);
            if (last.isBlocked()) {
                blockedEarly = true;
                sb.append("BLOCKED too early at extra failure #").append(i).append('\n');
                break;
            }
        }
        if (!blockedEarly) {
            sb.append("19 more failures: not blocked (").append(last.getAttemptsLeft()).append(" left)\n");
        }
        // the 20th must block
        FailureOutcome twentieth = tracker.recordFailure(EMAIL);
        sb.append("20th extra failure: ").append(twentieth.isBlocked() ? "blocked" : "NOT blocked").append('\n');

        boolean pass = counted == expected && !blockedEarly && twentieth.isBlocked();
        sb.append(pass ? "PASS - no updates were lost" : "FAIL - updates were lost");
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(run());
    }
}
