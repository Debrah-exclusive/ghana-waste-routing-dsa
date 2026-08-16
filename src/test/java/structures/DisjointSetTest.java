import structures.DisjointSet;

import java.util.NoSuchElementException;

/**
 * Unit tests for DisjointSet — normal case, boundary case, invalid input case.
 * Owner: Emmanuel Aseda Kow Bentsil
 *
 * No JUnit jar is checked into the repo, so these are plain Java self-checking
 * tests: compile and run the class, and it prints one line per test plus a
 * summary. Exit code is non-zero if anything fails.
 *
 *   javac -d bin src/main/java/structures/DisjointSet.java src/test/java/structures/DisjointSetTest.java
 *   java -cp bin DisjointSetTest
 */
public class DisjointSetTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== DisjointSet tests ===");

        System.out.println("\n-- normal cases --");
        makeSetCreatesSingleton();
        unionMergesTwoDifferentSets();
        chainedUnionsFormOneComponent();
        pathCompressionKeepsConsistentRoots();
        unionBySizeTracksSizesCorrectly();

        System.out.println("\n-- boundary cases --");
        emptySetHasNoElements();
        unionWithSelfIsNoOp();
        reUnionOfConnectedPairIsRejected();
        duplicateMakeSetIsIdempotent();
        singletonIsOnlyConnectedToItself();

        System.out.println("\n-- invalid input cases --");
        makeSetNullRejected();
        findUnregisteredElementRejected();
        unionUnregisteredElementRejected();
        connectedUnregisteredElementRejected();
        findNullRejected();

        System.out.println("\n=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------ normal cases

    private static void makeSetCreatesSingleton() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        check("find returns itself right after makeSet", "L001", ds.find("L001"));
        check("componentSize of a fresh singleton", 1, ds.componentSize("L001"));
        check("componentCount after one makeSet", 1, ds.componentCount());
    }

    private static void unionMergesTwoDifferentSets() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        ds.makeSet("L002");

        checkFalse("not connected before union", ds.connected("L001", "L002"));
        checkTrue("union of two different sets returns true", ds.union("L001", "L002"));
        checkTrue("connected after union", ds.connected("L001", "L002"));
        check("componentCount drops to 1 after merging 2 singletons", 1, ds.componentCount());
        check("componentSize reflects the merge", 2, ds.componentSize("L001"));
    }

    private static void chainedUnionsFormOneComponent() {
        DisjointSet<String> ds = new DisjointSet<>();
        String[] ids = {"L001", "L002", "L003", "L004", "L005", "L006"};
        for (String id : ids) {
            ds.makeSet(id);
        }

        ds.union("L001", "L002");
        ds.union("L002", "L003");
        ds.union("L004", "L005");
        ds.union("L005", "L006");
        check("two chains -> two components", 2, ds.componentCount());

        ds.union("L003", "L004");
        check("joining the two chains -> one component", 1, ds.componentCount());
        for (String id : ids) {
            checkTrue(id + " connected to L001 after full chain merge", ds.connected("L001", id));
        }
        check("componentSize spans all 6 locations", 6, ds.componentSize("L001"));
    }

    private static void pathCompressionKeepsConsistentRoots() {
        DisjointSet<Integer> ds = new DisjointSet<>();
        for (int i = 0; i < 100; i++) {
            ds.makeSet(i);
        }
        for (int i = 0; i < 99; i++) {
            ds.union(i, i + 1); // build one long 100-element chain
        }
        int root = ds.find(0);
        boolean allSameRoot = true;
        for (int i = 1; i < 100; i++) {
            if (!ds.find(i).equals(root)) {
                allSameRoot = false;
                break;
            }
        }
        checkTrue("all 100 chained elements share one root after path compression", allSameRoot);
        check("componentCount after a 100-element chain", 1, ds.componentCount());
    }

    private static void unionBySizeTracksSizesCorrectly() {
        DisjointSet<String> ds = new DisjointSet<>(true); // union by size
        ds.makeSet("L001");
        ds.makeSet("L002");
        ds.makeSet("L003");
        ds.union("L001", "L002");
        ds.union("L002", "L003");
        check("union-by-size component size after 2 unions", 3, ds.componentSize("L001"));
        check("union-by-size componentCount", 1, ds.componentCount());
    }

    // ------------------------------------------------------------ boundary cases

    private static void emptySetHasNoElements() {
        DisjointSet<String> ds = new DisjointSet<>();
        check("elementCount of an empty structure", 0, ds.elementCount());
        check("componentCount of an empty structure", 0, ds.componentCount());
    }

    private static void unionWithSelfIsNoOp() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        checkFalse("union(x, x) reports no merge happened", ds.union("L001", "L001"));
        check("componentCount unchanged after self-union", 1, ds.componentCount());
    }

    private static void reUnionOfConnectedPairIsRejected() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        ds.makeSet("L002");
        checkTrue("first union succeeds", ds.union("L001", "L002"));
        checkFalse("second union of the same pair reports already connected", ds.union("L001", "L002"));
        checkFalse("re-union with arguments swapped is order-independent", ds.union("L002", "L001"));
        check("componentCount stays 1 across the repeated unions", 1, ds.componentCount());
        check("componentSize unaffected by rejected re-unions", 2, ds.componentSize("L001"));
    }

    private static void duplicateMakeSetIsIdempotent() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        ds.makeSet("L002");
        ds.union("L001", "L002");
        ds.makeSet("L001"); // must NOT reset L001 into a fresh singleton
        checkTrue("still connected after a duplicate makeSet", ds.connected("L001", "L002"));
        check("componentCount unaffected by duplicate makeSet", 1, ds.componentCount());
        check("elementCount unaffected by duplicate makeSet", 2, ds.elementCount());
    }

    private static void singletonIsOnlyConnectedToItself() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("SOLO");
        checkTrue("a singleton is connected to itself", ds.connected("SOLO", "SOLO"));
        check("a singleton's componentSize is 1", 1, ds.componentSize("SOLO"));
    }

    // ------------------------------------------------------------ invalid input cases

    private static void makeSetNullRejected() {
        DisjointSet<String> ds = new DisjointSet<>();
        checkThrows("makeSet(null) throws IllegalArgumentException",
                IllegalArgumentException.class, () -> ds.makeSet(null));
    }

    private static void findUnregisteredElementRejected() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        checkThrows("find() of an unregistered element throws NoSuchElementException",
                NoSuchElementException.class, () -> ds.find("GHOST"));
    }

    private static void unionUnregisteredElementRejected() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        checkThrows("union() where one side is unregistered throws NoSuchElementException",
                NoSuchElementException.class, () -> ds.union("L001", "GHOST"));
    }

    private static void connectedUnregisteredElementRejected() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("L001");
        checkThrows("connected() with an unregistered element throws NoSuchElementException",
                NoSuchElementException.class, () -> ds.connected("L001", "GHOST"));
    }

    private static void findNullRejected() {
        DisjointSet<String> ds = new DisjointSet<>();
        checkThrows("find(null) throws IllegalArgumentException, not NullPointerException",
                IllegalArgumentException.class, () -> ds.find(null));
    }

    // ------------------------------------------------------------ helpers

    private static void check(String name, Object expected, Object actual) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        report(ok, name, ok ? "" : "expected <" + expected + "> but got <" + actual + ">");
    }

    private static void checkTrue(String name, boolean actual) {
        report(actual, name, "expected true");
    }

    private static void checkFalse(String name, boolean actual) {
        report(!actual, name, "expected false");
    }

    private static void checkThrows(String name, Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
            report(false, name, "expected " + expected.getSimpleName() + " but nothing was thrown");
        } catch (Throwable t) {
            boolean ok = expected.isInstance(t);
            report(ok, name, ok ? "" : "expected " + expected.getSimpleName()
                    + " but got " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void report(boolean ok, String name, String detail) {
        if (ok) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name + " -- " + detail);
        }
    }
}
