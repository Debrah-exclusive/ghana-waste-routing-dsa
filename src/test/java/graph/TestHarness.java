package graph;

import java.util.*;

public final class TestHarness {
    private TestHarness() { }

    public interface Executable { void run() throws Exception; }

    private static int passed = 0;
    private static int failed = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void test(String name, Executable body) {
        try {
            body.run();
            passed++;
            System.out.println("  [PASS] " + name);
        } catch (Throwable t) {
            failed++;
            failures.add(name + " -> " + t);
            System.out.println("  [FAIL] " + name + " -> " + t);
        }
    }

    public static void assertEquals(double expected, double actual, double tolerance, String msg) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(msg + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void assertEquals(Object expected, Object actual, String msg) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(msg + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError(msg);
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Executable body, String msg) {
        try {
            body.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) return;
            throw new AssertionError(msg + " - wrong exception type: " + t.getClass() + " (expected " + expectedType + ")");
        }
        throw new AssertionError(msg + " - expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }

    public static void printSummary(String suiteName) {
        System.out.println();
        System.out.println(suiteName + ": " + passed + " passed, " + failed + " failed"
            + (failed > 0 ? " out of " + (passed + failed) : ""));
        if (failed > 0) {
            System.out.println("Failures:");
            failures.forEach(f -> System.out.println("  - " + f));
        }
    }

    public static boolean hasFailures() {
        return failed > 0;
    }

    public static void reset() {
        passed = 0;
        failed = 0;
        failures.clear();
    }
}
