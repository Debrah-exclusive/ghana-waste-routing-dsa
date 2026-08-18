import algorithms.sorting.SelectionSort;
import algorithms.sorting.SortMetrics;
import structures.MyLinkedList;

/**
 * Unit tests for SelectionSort — normal case, boundary case, invalid input case.
 * Owner: Emmanuel Thisara Otoo
 *
 *   javac -d bin src/main/java/structures/MyLinkedList.java src/main/java/algorithms/sorting/*.java src/test/java/algorithms/sorting/SelectionSortTest.java
 *   java -cp bin SelectionSortTest
 */
public class SelectionSortTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== SelectionSort tests ===");

        System.out.println("\n-- normal cases --");
        sortsRandomOrder();
        sortsAlreadySorted();
        sortsReverseOrder();
        keepsDuplicates();
        sortsNegativeAndMixed();
        sortsStrings();
        sortsLinkedListInPlace();
        isAPermutationOfTheInput();

        System.out.println("\n-- boundary cases --");
        emptyArray();
        singleElement();
        twoElements();
        allEqualElements();
        extremeIntValues();
        emptyAndSingleLinkedList();
        comparisonCountMatchesTheFormula();

        System.out.println("\n-- invalid input cases --");
        nullArrayRejected();
        nullMetricsRejected();
        nullElementRejected();
        nullListRejected();
        nullTraceRejected();
        nonComparableElementRejected();

        System.out.println("\n=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------ normal cases

    private static void sortsRandomOrder() {
        int[] a = {42, 7, 19, 3, 25, 11};
        SelectionSort.sort(a);
        check("random order sorted ascending", "[3, 7, 11, 19, 25, 42]", show(a));
    }

    private static void sortsAlreadySorted() {
        int[] a = {1, 2, 3, 4, 5};
        SortMetrics m = new SortMetrics();
        SelectionSort.sort(a, m);
        check("already sorted stays sorted", "[1, 2, 3, 4, 5]", show(a));
        check("no swaps needed on sorted input", 0L, m.moves);
        check("comparisons are still n(n-1)/2", 10L, m.comparisons);
    }

    private static void sortsReverseOrder() {
        int[] a = {5, 4, 3, 2, 1};
        SelectionSort.sort(a);
        check("reverse order sorted", "[1, 2, 3, 4, 5]", show(a));
    }

    private static void keepsDuplicates() {
        int[] a = {3, 1, 3, 1, 2};
        SelectionSort.sort(a);
        check("duplicates are all kept", "[1, 1, 2, 3, 3]", show(a));
    }

    private static void sortsNegativeAndMixed() {
        int[] a = {0, -5, 12, -1, 7};
        SelectionSort.sort(a);
        check("negatives sort below zero", "[-5, -1, 0, 7, 12]", show(a));
    }

    private static void sortsStrings() {
        Comparable[] zones = {"Madina", "Ablekuma", "Nima", "Kaneshie"};
        SelectionSort.sort(zones);
        check("strings sort alphabetically",
                "[Ablekuma, Kaneshie, Madina, Nima]", showObjects(zones));
    }

    private static void sortsLinkedListInPlace() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        int[] input = {9, 2, 7, 4, 4, 1};
        for (int value : input) {
            list.addLast(value);
        }
        SortMetrics m = new SortMetrics();
        SelectionSort.sort(list, m);
        check("linked list sorted in place", "[1, 2, 4, 4, 7, 9]", list.toString());
        check("size preserved by the list sort", 6, list.size());
        checkTrue("list invariants hold after sorting", list.invariantsHold());
        checkTrue("list sort counted comparisons", m.comparisons > 0);
    }

    private static void isAPermutationOfTheInput() {
        int[] a = {8, 3, 8, 1, 9, 3, 0};
        long sumBefore = 0;
        for (int value : a) {
            sumBefore += value;
        }
        SelectionSort.sort(a);
        long sumAfter = 0;
        for (int value : a) {
            sumAfter += value;
        }
        check("no element is lost or invented", sumBefore, sumAfter);
        checkTrue("output is sorted", SelectionSort.isSorted(a));
    }

    // ---------------------------------------------------------- boundary cases

    private static void emptyArray() {
        int[] a = {};
        SelectionSort.sort(a);
        check("empty array is left empty", "[]", show(a));
        checkTrue("empty array counts as sorted", SelectionSort.isSorted(a));
    }

    private static void singleElement() {
        int[] a = {42};
        SortMetrics m = new SortMetrics();
        SelectionSort.sort(a, m);
        check("single element unchanged", "[42]", show(a));
        check("no comparisons for one element", 0L, m.comparisons);
    }

    private static void twoElements() {
        int[] ordered = {1, 2};
        int[] swapped = {2, 1};
        SelectionSort.sort(ordered);
        SelectionSort.sort(swapped);
        check("two ordered elements", "[1, 2]", show(ordered));
        check("two swapped elements", "[1, 2]", show(swapped));
    }

    private static void allEqualElements() {
        int[] a = {5, 5, 5, 5};
        SortMetrics m = new SortMetrics();
        SelectionSort.sort(a, m);
        check("all-equal array unchanged", "[5, 5, 5, 5]", show(a));
        check("all-equal input needs no swaps", 0L, m.moves);
    }

    private static void extremeIntValues() {
        int[] a = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0};
        SelectionSort.sort(a);
        check("int extremes sort correctly",
                "[" + Integer.MIN_VALUE + ", 0, " + Integer.MAX_VALUE + "]", show(a));
    }

    private static void emptyAndSingleLinkedList() {
        MyLinkedList<Integer> empty = new MyLinkedList<>();
        SelectionSort.sort(empty);
        check("empty list stays empty", "[]", empty.toString());

        MyLinkedList<Integer> single = new MyLinkedList<>();
        single.addLast(7);
        SelectionSort.sort(single);
        check("single-element list unchanged", "[7]", single.toString());
        checkTrue("invariants hold after sorting one node", single.invariantsHold());
    }

    private static void comparisonCountMatchesTheFormula() {
        // selection sort always does exactly n(n-1)/2 comparisons, whatever the order
        int n = 10;
        int[] sorted = new int[n];
        int[] reversed = new int[n];
        for (int i = 0; i < n; i++) {
            sorted[i] = i;
            reversed[i] = n - i;
        }
        SortMetrics m1 = new SortMetrics();
        SortMetrics m2 = new SortMetrics();
        SelectionSort.sort(sorted, m1);
        SelectionSort.sort(reversed, m2);
        long expected = (long) n * (n - 1) / 2;
        check("sorted input: n(n-1)/2 comparisons", expected, m1.comparisons);
        check("reverse input: same comparison count", expected, m2.comparisons);
    }

    // ----------------------------------------------------- invalid input cases

    private static void nullArrayRejected() {
        checkThrows("sort((int[]) null)", IllegalArgumentException.class,
                () -> SelectionSort.sort((int[]) null));
        checkThrows("sort((Comparable[]) null)", IllegalArgumentException.class,
                () -> SelectionSort.sort((Comparable[]) null));
        checkFalse("isSorted(null) is false", SelectionSort.isSorted(null));
    }

    private static void nullMetricsRejected() {
        int[] a = {3, 1};
        checkThrows("null metrics", IllegalArgumentException.class,
                () -> SelectionSort.sort(a, (SortMetrics) null));
    }

    private static void nullElementRejected() {
        Comparable[] a = {"B", null, "A"};
        checkThrows("null element inside the array", IllegalArgumentException.class,
                () -> SelectionSort.sort(a));
    }

    private static void nullListRejected() {
        checkThrows("sort((MyLinkedList) null)", IllegalArgumentException.class,
                () -> SelectionSort.sort((MyLinkedList<Integer>) null));
    }

    private static void nullTraceRejected() {
        checkThrows("trace(null)", IllegalArgumentException.class,
                () -> SelectionSort.trace(null));
    }

    private static void nonComparableElementRejected() {
        MyLinkedList<Object> list = new MyLinkedList<>();
        list.addLast(new Object());
        checkThrows("toComparableArray on non-Comparable elements",
                IllegalStateException.class, list::toComparableArray);
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
