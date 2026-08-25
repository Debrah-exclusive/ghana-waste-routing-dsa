package demo;

import algorithms.sorting.InsertionSort;
import algorithms.sorting.SelectionSort;
import algorithms.sorting.SortMetrics;
import structures.MyLinkedList;

import java.util.Iterator;

/**
 * Owner: Emmanuel Thisara Otoo
 *
 * Everything the examiner needs to see for the Linked List + Selection/Insertion
 * Sort module, in one runnable class: the pointer diagrams, the iterator
 * demonstrations, and the two trace tables.
 *
 * Run it standalone:
 *   java -cp bin demo.LinkedListSortDemo
 *
 * Or call {@link #run()} from menu.ConsoleMenu (integration lead: this is the
 * single entry point for my module — no other wiring needed).
 */
public class LinkedListSortDemo {

    /** Sample service requests, in the order they were logged. */
    private static final String[] SAMPLE_ZONES = {
            "Accra Central", "Madina", "Kaneshie", "Nima", "Ablekuma"
    };

    /** Small input used for the trace tables; kept short so the tables stay readable. */
    private static final int[] TRACE_INPUT = {42, 7, 19, 3, 25, 11};

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        section("1. Building the doubly linked list");
        MyLinkedList<String> zones = new MyLinkedList<>();
        System.out.println("empty list:  " + zones.diagram());
        for (String zone : SAMPLE_ZONES) {
            zones.addLast(zone);
            System.out.println("addLast(" + pad(zone) + "): " + zones.diagram());
        }
        System.out.println("size = " + zones.size());

        section("2. Inserting and removing in the middle (the reason for a linked list)");
        zones.add(2, "Tema Newtown");
        System.out.println("add(2, Tema Newtown):");
        System.out.println("   " + zones.diagram());
        System.out.println("   only two pointers changed: predecessor.next and successor.prev");
        zones.removeValue("Nima");
        System.out.println("removeValue(Nima):");
        System.out.println("   " + zones.diagram());
        System.out.println("invariants hold: " + zones.invariantsHold());

        section("3. Iterator demonstrations");
        System.out.print("forward  (for-each, follows next): ");
        for (String zone : zones) {
            System.out.print(zone + " -> ");
        }
        System.out.println("null");

        System.out.print("backward (descendingIterator, follows prev): ");
        Iterator<String> back = zones.descendingIterator();
        while (back.hasNext()) {
            System.out.print(back.next() + " -> ");
        }
        System.out.println("null");

        MyLinkedList<Integer> numbers = new MyLinkedList<>();
        for (int i = 1; i <= 8; i++) {
            numbers.addLast(i);
        }
        System.out.println("before iterator.remove(): " + numbers);
        Iterator<Integer> it = numbers.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) {
                it.remove();     // safe removal during a walk
            }
        }
        System.out.println("after removing evens:     " + numbers);

        System.out.print("modifying the list mid-walk: ");
        try {
            Iterator<Integer> stale = numbers.iterator();
            stale.next();
            numbers.addLast(99);
            stale.next();
            System.out.println("no error (BUG - this should not happen)");
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException, as designed");
        }

        section("4. Reversing in place");
        MyLinkedList<String> route = new MyLinkedList<>();
        route.addLast("Depot");
        route.addLast("Madina");
        route.addLast("Kaneshie");
        route.addLast("Landfill");
        System.out.println("outbound: " + route.diagram());
        route.reverse();
        System.out.println("return:   " + route.diagram());
        System.out.println("(prev and next swapped on every node; no new nodes allocated)");

        section("5. Selection sort trace");
        System.out.println(SelectionSort.trace(TRACE_INPUT));

        section("6. Insertion sort trace");
        System.out.println(InsertionSort.trace(TRACE_INPUT));

        section("7. Sorting the linked list itself");
        MyLinkedList<Integer> unsorted = new MyLinkedList<>();
        for (int value : TRACE_INPUT) {
            unsorted.addLast(value);
        }
        MyLinkedList<Integer> copy = new MyLinkedList<>();
        for (int value : TRACE_INPUT) {
            copy.addLast(value);
        }

        SortMetrics selectionMetrics = new SortMetrics();
        SortMetrics insertionMetrics = new SortMetrics();
        SelectionSort.sort(unsorted, selectionMetrics);
        InsertionSort.sort(copy, insertionMetrics);

        System.out.println("input:          " + show(TRACE_INPUT));
        System.out.println("selection sort: " + unsorted + "   " + selectionMetrics);
        System.out.println("insertion sort: " + copy + "   " + insertionMetrics);
        System.out.println("list invariants hold after sorting: "
                + (unsorted.invariantsHold() && copy.invariantsHold()));

        section("8. Why the choice matters");
        System.out.println("Selection sort always does n(n-1)/2 comparisons - sorted input does not help it.");
        System.out.println("Insertion sort does n-1 comparisons on sorted input, so it wins whenever the");
        System.out.println("list is already close to deadline order, which is the normal case for the");
        System.out.println("dispatch queue. Full measurements: demo.SortBenchmark -> results/csv/.");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("================================================================");
        System.out.println(title);
        System.out.println("================================================================");
    }

    private static String pad(String s) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < 13) {
            sb.append(' ');
        }
        return sb.toString();
    }

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
}
