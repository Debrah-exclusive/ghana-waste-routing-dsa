package algorithms.sorting;

import structures.MyLinkedList;

/**
 * Owner: Emmanuel Thisara Otoo
 *
 * Insertion sort, written from scratch (no Arrays.sort, no Collections.sort).
 *
 * Idea: take the next unsorted element as the "key", slide every larger element
 * of the sorted prefix one slot to the right, and drop the key into the gap.
 * This is how a dispatcher slots a newly logged request into a list that is
 * already in deadline order.
 *
 *   pass i:  [ sorted 0..i-1 ][ key ] ...
 *            shift right while a[j] > key, then a[j+1] = key
 *
 * Loop invariant: before pass i, positions 0..i-1 contain the first i elements
 * of the original array, in ascending order. Each pass extends that by one, so
 * after pass n-1 the whole array is sorted and is a permutation of the input.
 *
 * Complexity: best case O(n) — an already-sorted input does one comparison per
 * pass and no shifts. Worst case (reverse order) and average case are O(n^2),
 * with about n^2/4 comparisons on random input. Space O(1), and it IS stable:
 * the shift loop stops at the first element that is <= key, so equal keys keep
 * their original order. It is also adaptive — cost falls with the number of
 * inversions — which is why it beats selection sort on nearly-sorted data.
 *
 * Project use: the service-request list is re-sorted by urgency/deadline after
 * a handful of new rows arrive, so the array is nearly sorted every time. That
 * is exactly insertion sort's best case, and the measured runs in
 * report/linked-list-and-sorts.md show the gap over selection sort.
 */
public class InsertionSort {

    private InsertionSort() {
        // static utility class
    }

    // ------------------------------------------------------------------ int[]

    public static void sort(int[] a) {
        sort(a, new SortMetrics());
    }

    public static void sort(int[] a, SortMetrics metrics) {
        if (a == null) {
            throw new IllegalArgumentException("cannot sort a null array");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("metrics must not be null");
        }
        metrics.reset();
        long start = System.nanoTime();

        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0) {
                metrics.comparisons++;
                if (a[j] > key) {
                    a[j + 1] = a[j];
                    metrics.moves++;
                    j--;
                } else {
                    break;
                }
            }
            if (j + 1 != i) {          // key actually moved, so the write is a real move
                a[j + 1] = key;
                metrics.moves++;
            }
        }

        metrics.elapsedNanos = System.nanoTime() - start;
    }

    // ------------------------------------------------------------ Comparable[]

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

        for (int i = 1; i < a.length; i++) {
            Comparable key = a[i];
            int j = i - 1;
            while (j >= 0) {
                metrics.comparisons++;
                if (a[j].compareTo(key) > 0) {
                    a[j + 1] = a[j];
                    metrics.moves++;
                    j--;
                } else {
                    break;
                }
            }
            a[j + 1] = key;
            metrics.moves++;
        }

        metrics.elapsedNanos = System.nanoTime() - start;
    }

    // ------------------------------------------------------------- linked list

    /**
     * Insertion sort run directly over the linked list. For each element of the
     * input list, walk the already-sorted result with an iterator until a larger
     * value is found, then splice the element in at that position.
     *
     * On a linked list the insert itself is a pointer relink rather than a block
     * shift, so no elements are moved — the cost is the search for the position.
     * The scan stops at the first element strictly greater than the key, which is
     * what keeps the sort stable.
     */
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
        for (T key : list) {
            int position = 0;
            for (T placed : sorted) {
                metrics.comparisons++;
                if (placed.compareTo(key) > 0) {
                    break;
                }
                position++;
            }
            sorted.add(position, key);
            metrics.moves++;
        }

        list.clear();
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
     * Pass-by-pass trace table for the report and the oral defense.
     * Keep the input small (<= 12 elements).
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
        sb.append("Insertion sort trace\n");
        sb.append("Pass | i | key | shifts | array after pass\n");
        sb.append("-----+---+-----+--------+------------------\n");
        sb.append(String.format("%4s | %s | %3s | %6s | %s%n", "-", "-", "-", "-", show(a)));

        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int shifts = 0;
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                shifts++;
                j--;
            }
            a[j + 1] = key;
            sb.append(String.format("%4d | %d | %3d | %6d | %s%n",
                    i, i, key, shifts, show(a)));
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
