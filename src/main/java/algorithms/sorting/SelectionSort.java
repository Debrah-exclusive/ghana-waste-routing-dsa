package algorithms.sorting;

import structures.MyLinkedList;

/**
 * Owner: Emmanuel Thisara Otoo
 *
 * Selection sort, written from scratch (no Arrays.sort, no Collections.sort).
 *
 * Idea: the array is split into a sorted prefix and an unsorted suffix. Each
 * pass scans the whole suffix for its minimum and swaps that minimum into the
 * boundary position, growing the prefix by one.
 *
 *   pass i:  [ sorted 0..i-1 | ? ? ? min ? ? ]
 *                              ^i        ^minIndex   -> swap(i, minIndex)
 *
 * Loop invariant: before pass i, positions 0..i-1 hold the i smallest elements
 * of the original array in ascending order, and every one of them is <= every
 * element in positions i..n-1. After pass n-1 the whole array is sorted.
 *
 * Complexity: comparisons are always (n-1)+(n-2)+...+1 = n(n-1)/2, so best,
 * average and worst case are all O(n^2) — the input order cannot help it.
 * Moves are at most 3(n-1), which is the one thing it is good at: when a write
 * is expensive relative to a comparison, selection sort moves the least data of
 * the elementary sorts. Space is O(1) (in place). It is NOT stable: swapping a
 * distant minimum over equal keys can reorder them.
 *
 * Project use: ordering a small batch of collection points by distance, or
 * resources by capacity, where n is in the tens and the data is already in memory.
 * For the 300-row service-request file it is deliberately the wrong tool — see
 * report/linked-list-and-sorts.md for the measured comparison against insertion
 * sort and the O(n log n) sorts.
 */
public class SelectionSort {

    private SelectionSort() {
        // static utility class
    }

    // ------------------------------------------------------------------ int[]

    /** Sorts ascending in place. Rejects a null array; an empty or 1-element array is already sorted. */
    public static void sort(int[] a) {
        sort(a, new SortMetrics());
    }

    /** Same, but fills {@code metrics} with comparison/move/time counts for the report. */
    public static void sort(int[] a, SortMetrics metrics) {
        if (a == null) {
            throw new IllegalArgumentException("cannot sort a null array");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("metrics must not be null");
        }
        metrics.reset();
        long start = System.nanoTime();

        for (int i = 0; i < a.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < a.length; j++) {
                metrics.comparisons++;
                if (a[j] < a[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int tmp = a[i];
                a[i] = a[minIndex];
                a[minIndex] = tmp;
                metrics.moves += 3;
            }
        }

        metrics.elapsedNanos = System.nanoTime() - start;
    }

    // ------------------------------------------------------------ Comparable[]

    /** Sorts any Comparable elements ascending, in place. */
    public static void sort(Comparable[] a) {
        sort(a, new SortMetrics());
    }

    @SuppressWarnings("unchecked")
    public static void sort(Comparable[] a, SortMetrics metrics) {
        if (a == null) {
            throw new IllegalArgumentException("cannot sort a null array");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("metrics must not be null");
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null) {
                throw new IllegalArgumentException("null element at index " + i);
            }
        }
        metrics.reset();
        long start = System.nanoTime();

        for (int i = 0; i < a.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < a.length; j++) {
                metrics.comparisons++;
                if (a[j].compareTo(a[minIndex]) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                Comparable tmp = a[i];
                a[i] = a[minIndex];
                a[minIndex] = tmp;
                metrics.moves += 3;
            }
        }

        metrics.elapsedNanos = System.nanoTime() - start;
    }

    // ------------------------------------------------------------- linked list

    /**
     * Selection sort run directly over the linked list, without copying to an array:
     * each pass walks the remaining nodes with an iterator to find the smallest
     * value, removes it, and appends it to the result. This is the version
     * demonstrated live, because it shows the structure and the algorithm together.
     *
     * The list is sorted in place (its contents are replaced by the sorted order).
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> void sort(MyLinkedList<T> list, SortMetrics metrics) {
        if (list == null) {
            throw new IllegalArgumentException("cannot sort a null list");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("metrics must not be null");
        }
        metrics.reset();
        long start = System.nanoTime();

        MyLinkedList<T> sorted = new MyLinkedList<>();
        while (!list.isEmpty()) {
            // one iterator pass over the remaining nodes finds the minimum
            java.util.Iterator<T> it = list.iterator();
            T min = it.next();
            int minIndex = 0;
            int j = 0;
            while (it.hasNext()) {
                j++;
                T candidate = it.next();
                metrics.comparisons++;
                if (candidate.compareTo(min) < 0) {
                    min = candidate;
                    minIndex = j;
                }
            }
            sorted.addLast(list.remove(minIndex));
            metrics.moves++;
        }
        for (T value : sorted) {
            list.addLast(value);
        }

        metrics.elapsedNanos = System.nanoTime() - start;
    }

    public static <T extends Comparable<T>> void sort(MyLinkedList<T> list) {
        sort(list, new SortMetrics());
    }

    // ------------------------------------------------------------------ trace

    /**
     * Runs the sort and returns a pass-by-pass trace table for the report and the
     * oral defense. Keep the input small (<= 12 elements) or the table is unreadable.
     */
    public static String trace(int[] input) {
        if (input == null) {
            throw new IllegalArgumentException("cannot trace a null array");
        }
        int[] a = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            a[i] = input[i];
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Selection sort trace\n");
        sb.append("Pass | i | minIndex | swap      | array after pass\n");
        sb.append("-----+---+----------+-----------+------------------\n");
        sb.append(String.format("%4s | %s | %8s | %-9s | %s%n", "-", "-", "-", "start", show(a)));

        for (int i = 0; i < a.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j] < a[minIndex]) {
                    minIndex = j;
                }
            }
            String swap;
            if (minIndex != i) {
                swap = a[i] + "<->" + a[minIndex];
                int tmp = a[i];
                a[i] = a[minIndex];
                a[minIndex] = tmp;
            } else {
                swap = "none";
            }
            sb.append(String.format("%4d | %d | %8d | %-9s | %s%n",
                    i + 1, i, minIndex, swap, show(a)));
        }
        return sb.toString();
    }

    private static String show(int[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]);
            if (i < a.length - 1) {
                sb.append(' ');
            }
        }
        return sb.append(']').toString();
    }

    /** Convenience check used by the tests and the demo. */
    public static boolean isSorted(int[] a) {
        if (a == null) {
            return false;
        }
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }
        return true;
    }
}
