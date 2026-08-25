import structures.MyLinkedList;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Unit tests for MyLinkedList — normal case, boundary case, invalid input case.
 * Owner: Emmanuel Thisara Otoo
 *
 * No JUnit jar is checked into the repo, so these are plain Java self-checking
 * tests: compile and run the class, and it prints one line per test plus a
 * summary. Exit code is non-zero if anything fails, so it can go in CI later.
 *
 *   javac -d bin src/main/java/structures/MyLinkedList.java src/test/java/structures/MyLinkedListTest.java
 *   java -cp bin MyLinkedListTest
 */
public class MyLinkedListTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== MyLinkedList tests ===");

        System.out.println("\n-- normal cases --");
        addAndReadBackInOrder();
        addFirstReversesTheOrder();
        insertInTheMiddle();
        removeFromBothEnds();
        removeByValueAndByIndex();
        indexOfAndContains();
        setReplacesInPlace();
        reverseFlipsTheList();
        forwardAndBackwardIteration();
        iteratorRemoveDeletesTheCurrentNode();
        toArraySnapshot();

        System.out.println("\n-- boundary cases --");
        emptyListShape();
        singleElementList();
        addAtIndexZeroAndAtSize();
        removeDownToEmptyThenReuse();
        clearResetsEverything();
        reverseOfEmptyAndSingle();
        nodeAtWalksFromNearerEnd();

        System.out.println("\n-- invalid input cases --");
        nullElementsRejected();
        indexOutOfRangeRejected();
        removeFromEmptyRejected();
        getOnEmptyRejected();
        iteratorPastEndRejected();
        modifyingDuringIterationDetected();
        iteratorRemoveBeforeNextRejected();
        removeValueOfAbsentOrNullIsFalse();

        System.out.println("\n=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------ normal cases

    private static void addAndReadBackInOrder() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("Accra Central");
        list.addLast("Madina");
        list.addLast("Kaneshie");

        check("addLast keeps insertion order", "[Accra Central, Madina, Kaneshie]", list.toString());
        check("size after 3 adds", 3, list.size());
        check("get(0)", "Accra Central", list.get(0));
        check("get(2)", "Kaneshie", list.get(2));
        check("getFirst", "Accra Central", list.getFirst());
        check("getLast", "Kaneshie", list.getLast());
        checkTrue("invariants hold after addLast", list.invariantsHold());
    }

    private static void addFirstReversesTheOrder() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addFirst(3);
        list.addFirst(2);
        list.addFirst(1);
        check("addFirst pushes to the front", "[1, 2, 3]", list.toString());
        checkTrue("invariants hold after addFirst", list.invariantsHold());
    }

    private static void insertInTheMiddle() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A");
        list.addLast("C");
        list.add(1, "B");
        check("add(1, B) splices between A and C", "[A, B, C]", list.toString());
        check("size after middle insert", 3, list.size());
        checkTrue("invariants hold after middle insert", list.invariantsHold());
    }

    private static void removeFromBothEnds() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 1; i <= 5; i++) {
            list.addLast(i);
        }
        check("removeFirst returns the head value", 1, list.removeFirst());
        check("removeLast returns the tail value", 5, list.removeLast());
        check("list after removing both ends", "[2, 3, 4]", list.toString());
        check("size after two removals", 3, list.size());
        checkTrue("invariants hold after end removals", list.invariantsHold());
    }

    private static void removeByValueAndByIndex() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("REQ-1");
        list.addLast("REQ-2");
        list.addLast("REQ-3");

        checkTrue("removeValue finds an existing element", list.removeValue("REQ-2"));
        check("list after removeValue", "[REQ-1, REQ-3]", list.toString());
        check("remove(0) returns the removed value", "REQ-1", list.remove(0));
        check("list after remove(0)", "[REQ-3]", list.toString());
        checkTrue("invariants hold after mixed removals", list.invariantsHold());
    }

    private static void indexOfAndContains() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("Ablekuma");
        list.addLast("Nima");
        check("indexOf finds the first match", 1, list.indexOf("Nima"));
        check("indexOf of a missing value", -1, list.indexOf("Tema"));
        checkTrue("contains present", list.contains("Ablekuma"));
        checkFalse("contains absent", list.contains("Tema"));
    }

    private static void setReplacesInPlace() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(10);
        list.addLast(20);
        check("set returns the old value", 20, list.set(1, 99));
        check("list after set", "[10, 99]", list.toString());
        check("size unchanged by set", 2, list.size());
    }

    private static void reverseFlipsTheList() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 1; i <= 4; i++) {
            list.addLast(i);
        }
        list.reverse();
        check("reverse flips the order", "[4, 3, 2, 1]", list.toString());
        check("head after reverse", 4, list.getFirst());
        check("tail after reverse", 1, list.getLast());
        checkTrue("invariants hold after reverse", list.invariantsHold());
    }

    private static void forwardAndBackwardIteration() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");

        StringBuilder forward = new StringBuilder();
        for (String s : list) {
            forward.append(s);
        }
        check("for-each walks head to tail", "ABC", forward.toString());

        StringBuilder backward = new StringBuilder();
        Iterator<String> it = list.descendingIterator();
        while (it.hasNext()) {
            backward.append(it.next());
        }
        check("descendingIterator walks tail to head", "CBA", backward.toString());
    }

    private static void iteratorRemoveDeletesTheCurrentNode() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 1; i <= 5; i++) {
            list.addLast(i);
        }
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) {
                it.remove();
            }
        }
        check("iterator.remove drops the even values", "[1, 3, 5]", list.toString());
        check("size after iterator removals", 3, list.size());
        checkTrue("invariants hold after iterator removals", list.invariantsHold());
    }

    private static void toArraySnapshot() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(7);
        list.addLast(4);
        Object[] snapshot = list.toArray();
        check("toArray length", 2, snapshot.length);
        check("toArray[0]", 7, snapshot[0]);
        list.addLast(9);
        check("snapshot is not a live view", 2, snapshot.length);

        Comparable[] comparables = list.toComparableArray();
        check("toComparableArray length", 3, comparables.length);
    }

    // ---------------------------------------------------------- boundary cases

    private static void emptyListShape() {
        MyLinkedList<String> list = new MyLinkedList<>();
        check("new list size", 0, list.size());
        checkTrue("new list isEmpty", list.isEmpty());
        check("empty toString", "[]", list.toString());
        check("empty indexOf", -1, list.indexOf("anything"));
        checkFalse("empty contains", list.contains("anything"));
        check("empty toArray length", 0, list.toArray().length);
        checkFalse("empty iterator hasNext", list.iterator().hasNext());
        checkTrue("invariants hold on an empty list", list.invariantsHold());
    }

    private static void singleElementList() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("only");
        check("single element size", 1, list.size());
        check("head equals tail value", list.getFirst(), list.getLast());
        checkTrue("invariants hold with one node", list.invariantsHold());
        check("removing the only element", "only", list.removeFirst());
        checkTrue("empty again after removing the only element", list.isEmpty());
        checkTrue("invariants hold after emptying", list.invariantsHold());
    }

    private static void addAtIndexZeroAndAtSize() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.add(0, 2);                 // into an empty list
        list.add(0, 1);                 // at the very front
        list.add(list.size(), 3);       // at the very end (index == size is legal)
        check("add at index 0 and at size", "[1, 2, 3]", list.toString());
        checkTrue("invariants hold after boundary inserts", list.invariantsHold());
    }

    private static void removeDownToEmptyThenReuse() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        list.removeLast();
        list.removeLast();
        checkTrue("empty after removing every node", list.isEmpty());
        checkTrue("invariants hold when drained", list.invariantsHold());

        list.addLast(42);
        check("list is reusable after being drained", "[42]", list.toString());
        checkTrue("invariants hold after reuse", list.invariantsHold());
    }

    private static void clearResetsEverything() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 0; i < 10; i++) {
            list.addLast(i);
        }
        list.clear();
        check("size after clear", 0, list.size());
        check("toString after clear", "[]", list.toString());
        checkTrue("invariants hold after clear", list.invariantsHold());
    }

    private static void reverseOfEmptyAndSingle() {
        MyLinkedList<Integer> empty = new MyLinkedList<>();
        empty.reverse();
        check("reverse of empty is empty", "[]", empty.toString());
        checkTrue("invariants hold after reversing empty", empty.invariantsHold());

        MyLinkedList<Integer> single = new MyLinkedList<>();
        single.addLast(5);
        single.reverse();
        check("reverse of one element", "[5]", single.toString());
        checkTrue("invariants hold after reversing one node", single.invariantsHold());
    }

    private static void nodeAtWalksFromNearerEnd() {
        // exercises both branches of the internal walk (front half and back half)
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 0; i < 11; i++) {
            list.addLast(i);
        }
        check("get from the front half", 2, list.get(2));
        check("get from the back half", 9, list.get(9));
        check("get exactly at the midpoint", 5, list.get(5));
        check("get at the last index", 10, list.get(list.size() - 1));
    }

    // ----------------------------------------------------- invalid input cases

    private static void nullElementsRejected() {
        MyLinkedList<String> list = new MyLinkedList<>();
        checkThrows("addLast(null)", IllegalArgumentException.class, () -> list.addLast(null));
        checkThrows("addFirst(null)", IllegalArgumentException.class, () -> list.addFirst(null));
        checkThrows("add(0, null)", IllegalArgumentException.class, () -> list.add(0, null));
        list.addLast("A");
        checkThrows("set(0, null)", IllegalArgumentException.class, () -> list.set(0, null));
        check("list unchanged by rejected nulls", "[A]", list.toString());
        checkTrue("invariants hold after rejected nulls", list.invariantsHold());
    }

    private static void indexOutOfRangeRejected() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        checkThrows("get(-1)", IndexOutOfBoundsException.class, () -> list.get(-1));
        checkThrows("get(size)", IndexOutOfBoundsException.class, () -> list.get(2));
        checkThrows("set(5, x)", IndexOutOfBoundsException.class, () -> list.set(5, 9));
        checkThrows("remove(9)", IndexOutOfBoundsException.class, () -> list.remove(9));
        checkThrows("add(-1, x)", IndexOutOfBoundsException.class, () -> list.add(-1, 9));
        checkThrows("add(size+1, x)", IndexOutOfBoundsException.class, () -> list.add(3, 9));
        check("list unchanged by rejected indexes", "[1, 2]", list.toString());
    }

    private static void removeFromEmptyRejected() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        checkThrows("removeFirst on empty", NoSuchElementException.class, list::removeFirst);
        checkThrows("removeLast on empty", NoSuchElementException.class, list::removeLast);
        checkThrows("remove(0) on empty", IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    private static void getOnEmptyRejected() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        checkThrows("getFirst on empty", NoSuchElementException.class, list::getFirst);
        checkThrows("getLast on empty", NoSuchElementException.class, list::getLast);
        checkThrows("get(0) on empty", IndexOutOfBoundsException.class, () -> list.get(0));
    }

    private static void iteratorPastEndRejected() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        Iterator<Integer> it = list.iterator();
        it.next();
        checkThrows("next() past the end", NoSuchElementException.class, it::next);

        Iterator<Integer> back = list.descendingIterator();
        back.next();
        checkThrows("descending next() past the front", NoSuchElementException.class, back::next);
    }

    private static void modifyingDuringIterationDetected() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        Iterator<Integer> it = list.iterator();
        it.next();
        list.addLast(3);   // structural change behind the iterator's back
        checkThrows("next() after a concurrent add",
                ConcurrentModificationException.class, it::next);
    }

    private static void iteratorRemoveBeforeNextRejected() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(1);
        Iterator<Integer> it = list.iterator();
        checkThrows("remove() before next()", IllegalStateException.class, it::remove);

        it.next();
        it.remove();
        checkThrows("remove() twice in a row", IllegalStateException.class, it::remove);
    }

    private static void removeValueOfAbsentOrNullIsFalse() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("A");
        checkFalse("removeValue of an absent value", list.removeValue("Z"));
        checkFalse("removeValue(null)", list.removeValue(null));
        check("list unchanged", "[A]", list.toString());
    }

    // ------------------------------------------------------------ tiny harness

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
