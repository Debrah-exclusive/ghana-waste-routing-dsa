import algorithms.sorting.InsertionSort;
import algorithms.sorting.SortMetrics;
import structures.MyLinkedList;

/**
 * Unit tests for InsertionSort — normal case, boundary case, invalid input case.
 * Owner: Emmanuel Thisara Otoo
 *
 *   javac -d bin src/main/java/structures/MyLinkedList.java src/main/java/algorithms/sorting/*.java src/test/java/algorithms/sorting/InsertionSortTest.java
 *   java -cp bin InsertionSortTest
 */
public class InsertionSortTest {

    private static int passed = 0;
    private static int failed = 0;

    /** A request whose ordering key is the deadline only, so ties expose (in)stability. */
    private static class Request implements Comparable<Request> {
        final String id;
        final int deadline;

        Request(String id, int deadline) {
            this.id = id;
            this.deadline = deadline;
        }

        @Override
        public int compareTo(Request other) {
            return Integer.compare(this.deadline, other.deadline);
        }

        @Override
        public String toString() {
            return id + "@" + deadline;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== InsertionSort tests ===");

        System.out.println("\n-- normal cases --");
        sortsRandomOrder();
        sortsAlreadySorted();
        sortsReverseOrder();
        keepsDuplicates();
        sortsNegativeAndMixed();
        sortsStrings();
        sortIsStable();
        sortsLinkedListInPlace();
        linkedListSortIsStable();
        isAPermutationOfTheInput();
        adaptiveOnNearlySortedInput();

        System.out.println("\n-- boundary cases --");
        emptyArray();
        singleElement();
        twoElements();
        allEqualElements();
        extremeIntValues();
        emptyAndSingleLinkedList();
        bestCaseComparisonCount();
        worstCaseComparisonCount();

        System.out.println("\n-- invalid input cases --");
        nullArrayRejected();
        nullMetricsRejected();
        nullElementRejected();
        nullListRejected();
        nullTraceRejected();

        System.out.println("\n=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------ normal cases

    private static void sortsRandomOrder() {
        int[] a = {42, 7, 19, 3, 25, 11};
        InsertionSort.sort(a);
        check("random order sorted ascending", "[3, 7, 11, 19, 25, 42]", show(a));
    }

    private static void sortsAlreadySorted() {
        int[] a = {1, 2, 3, 4, 5};
        SortMetrics m = new SortMetrics();
        InsertionSort.sort(a, m);
        check("already sorted stays sorted", "[1, 2, 3, 4, 5]", show(a));
        check("no moves on sorted input", 0L, m.moves);
    }

    private static void sortsReverseOrder() {
        int[] a = {5, 4, 3, 2, 1};
        InsertionSort.sort(a);
        check("reverse order sorted", "[1, 2, 3, 4, 5]", show(a));
    }

    private static void keepsDuplicates() {
        int[] a = {3, 1, 3, 1, 2};
        InsertionSort.sort(a);
        check("duplicates are all kept", "[1, 1, 2, 3, 3]", show(a));
    }

    private static void sortsNegativeAndMixed() {
        int[] a = {0, -5, 12, -1, 7};
        InsertionSort.sort(a);
        check("negatives sort below zero", "[-5, -1, 0, 7, 12]", show(a));
    }

    private static void sortsStrings() {
        Comparable[] zones = {"Madina", "Ablekuma", "Nima", "Kaneshie"};
        InsertionSort.sort(zones);
        check("strings sort alphabetically",
                "[Ablekuma, Kaneshie, Madina, Nima]", showObjects(zones));
    }

    private static void sortIsStable() {
        // three requests share deadline 5; their original relative order must survive
        Comparable[] requests = {
                new Request("REQ-1", 5),
                new Request("REQ-2", 2),
                new Request("REQ-3", 5),
                new Request("REQ-4", 1),
                new Request("REQ-5", 5)
        };
        InsertionSort.sort(requests);
        check("insertion sort is stable on equal keys",
                "[REQ-4@1, REQ-2@2, REQ-1@5, REQ-3@5, REQ-5@5]", showObjects(requests));
    }

    private static void sortsLinkedListInPlace() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        int[] input = {9, 2, 7, 4, 4, 1};
        for (int value : input) {
            list.addLast(value);
        }
        SortMetrics m = new SortMetrics();
        InsertionSort.sort(list, m);
        check("linked list sorted in place", "[1, 2, 4, 4, 7, 9]", list.toString());
        check("size preserved by the list sort", 6, list.size());
        checkTrue("list invariants hold after sorting", list.invariantsHold());
        checkTrue("list sort counted comparisons", m.comparisons > 0);
    }

    private static void linkedListSortIsStable() {
        MyLinkedList<Request> list = new MyLinkedList<>();
        list.addLast(new Request("REQ-1", 5));
        list.addLast(new Request("REQ-2", 2));
        list.addLast(new Request("REQ-3", 5));
        InsertionSort.sort(list);
        check("linked list insertion sort is stable",
                "[REQ-2@2, REQ-1@5, REQ-3@5]", list.toString());
    }

    private static void isAPermutationOfTheInput() {
        int[] a = {8, 3, 8, 1, 9, 3, 0};
        long sumBefore = 0;
        for (int value : a) {
            sumBefore += value;
        }
        InsertionSort.sort(a);
        long sumAfter = 0;
        for (int value : a) {
            sumAfter += value;
        }
        check("no element is lost or invented", sumBefore, sumAfter);
        checkTrue("output is sorted", InsertionSort.isSorted(a));
    }

    private static void adaptiveOnNearlySortedInput() {
        // the practical claim being defended: cost tracks the number of inversions
        int n = 200;
        int[] nearlySorted = new int[n];
        int[] reversed = new int[n];
        for (int i = 0; i < n; i++) {
            nearlySorted[i] = i;
            reversed[i] = n - i;
        }
        int tmp = nearlySorted[10];       // introduce a single inversion
        nearlySorted[10] = nearlySorted[11];
        nearlySorted[11] = tmp;

        SortMetrics nearly = new SortMetrics();
        SortMetrics worst = new SortMetrics();
        InsertionSort.sort(nearlySorted, nearly);
        InsertionSort.sort(reversed, worst);

        checkTrue("nearly-sorted input costs far less than reverse input",
                nearly.comparisons * 10 < worst.comparisons);
        checkTrue("nearly-sorted result is sorted", InsertionSort.isSorted(nearlySorted));
    }

    // ---------------------------------------------------------- boundary cases

    private static void emptyArray() {
        int[] a = {};
        InsertionSort.sort(a);
        check("empty array is left empty", "[]", show(a));
        checkTrue("empty array counts as sorted", InsertionSort.isSorted(a));
    }

    private static void singleElement() {
        int[] a = {42};
        SortMetrics m = new SortMetrics();
        InsertionSort.sort(a, m);
        check("single element unchanged", "[42]", show(a));
        check("no comparisons for one element", 0L, m.comparisons);
    }

    private static void twoElements() {
        int[] ordered = {1, 2};
        int[] swapped = {2, 1};
        InsertionSort.sort(ordered);
        InsertionSort.sort(swapped);
        check("two ordered elements", "[1, 2]", show(ordered));
        check("two swapped elements", "[1, 2]", show(swapped));
    }

    private static void allEqualElements() {
        int[] a = {5, 5, 5, 5};
        SortMetrics m = new SortMetrics();
        InsertionSort.sort(a, m);
        check("all-equal array unchanged", "[5, 5, 5, 5]", show(a));
        check("all-equal input needs no moves", 0L, m.moves);
        check("all-equal input is the best case", 3L, m.comparisons);
    }

    private static void extremeIntValues() {
        int[] a = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0};
        InsertionSort.sort(a);
        check("int extremes sort correctly",
                "[" + Integer.MIN_VALUE + ", 0, " + Integer.MAX_VALUE + "]", show(a));
    }

    private static void emptyAndSingleLinkedList() {
        MyLinkedList<Integer> empty = new MyLinkedList<>();
        InsertionSort.sort(empty);
        check("empty list stays empty", "[]", empty.toString());

        MyLinkedList<Integer> single = new MyLinkedList<>();
        single.addLast(7);
        InsertionSort.sort(single);
        check("single-element list unchanged", "[7]", single.toString());
        checkTrue("invariants hold after sorting one node", single.invariantsHold());
    }

    private static void bestCaseComparisonCount() {
        // sorted input: one comparison per pass, so exactly n-1
        int n = 10;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        SortMetrics m = new SortMetrics();
        InsertionSort.sort(a, m);
        check("best case is n-1 comparisons", (long) (n - 1), m.comparisons);
    }

    private static void worstCaseComparisonCount() {
        // reverse input: every pass scans the whole prefix, so n(n-1)/2
        int n = 10;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = n - i;
        }
        SortMetrics m = new SortMetrics();
        InsertionSort.sort(a, m);
        check("worst case is n(n-1)/2 comparisons", (long) n * (n - 1) / 2, m.comparisons);
    }

    // ----------------------------------------------------- invalid input cases

    private static void nullArrayRejected() {
        checkThrows("sort((int[]) null)", IllegalArgumentException.class,
                () -> InsertionSort.sort((int[]) null));
        checkThrows("sort((Comparable[]) null)", IllegalArgumentException.class,
                () -> InsertionSort.sort((Comparable[]) null));
        checkFalse("isSorted(null) is false", InsertionSort.isSorted(null));
    }

    private static void nullMetricsRejected() {
        int[] a = {3, 1};
        checkThrows("null metrics", IllegalArgumentException.class,
                () -> InsertionSort.sort(a, (SortMetrics) null));
    }

    private static void nullElementRejected() {
        Comparable[] a = {"B", null, "A"};
        checkThrows("null element inside the array", IllegalArgumentException.class,
                () -> InsertionSort.sort(a));
    }

    private static void nullListRejected() {
        checkThrows("sort((MyLinkedList) null)", IllegalArgumentException.class,
                () -> InsertionSort.sort((MyLinkedList<Integer>) null));
    }

    private static void nullTraceRejected() {
        checkThrows("trace(null)", IllegalArgumentException.class,
                () -> InsertionSort.trace(null));
    }

    // ------------------------------------------------------------ tiny harness

    private static String show(int[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]);
            if (i < a.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append(']').toString();
    }

    private static String showObjects(Object[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]);
            if (i < a.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append(']').toString();
    }

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
