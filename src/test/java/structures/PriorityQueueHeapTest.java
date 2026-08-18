package structures;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Unit tests for PriorityQueueHeap — normal case, boundary case, invalid input case.
 * Fill in as part of your module's evidence.
 */
public class PriorityQueueHeapTest {
    public static void main(String[] args) {
        System.out.println("=== RUNNING PRIORITY QUEUE HEAP TESTS ===");
        testNormalCaseMinHeap();
        testNormalCaseMaxHeap();
        testHeapify();
        testBoundaryCases();
        testInvalidInputs();
        System.out.println("ALL PRIORITY QUEUE HEAP TESTS PASSED SUCCESSFULLY!");
    }
    
    // Normal Case Min-Heap
    private static void testNormalCaseMinHeap() {
        PriorityQueueHeap<Integer> minHeap = new PriorityQueueHeap<>();
        minHeap.insert(50);
        minHeap.insert(10);
        minHeap.insert(30);
        minHeap.insert(5);
        assert minHeap.size() == 4;
        assert minHeap.peek() == 5;
        assert minHeap.extractTop() == 5;
        assert minHeap.extractTop() == 10;
        assert minHeap.extractTop() == 30;
        assert minHeap.extractTop() == 50;
        assert minHeap.isEmpty();
        System.out.println("  [PASS] Normal Case Min-Heap");
    }
    private static void testNormalCaseMaxHeap() {
        // Max Heap using Comparator.reverseOrder()
        PriorityQueueHeap<Integer> maxHeap = new PriorityQueueHeap<>(Comparator.reverseOrder());
        maxHeap.insert(10);
        maxHeap.insert(50);
        maxHeap.insert(30);
        assert maxHeap.extractTop() == 50;
        assert maxHeap.extractTop() == 30;
        assert maxHeap.extractTop() == 10;
        System.out.println("  [PASS] Normal Case Max-Heap");
    }
    private static void testHeapify() {
        Integer[] items = {40, 10, 30, 20, 50};
        PriorityQueueHeap<Integer> heapified = PriorityQueueHeap.heapify(items, null);
        assert heapified.size() == 5;
        assert heapified.extractTop() == 10;
        assert heapified.extractTop() == 20;
        assert heapified.extractTop() == 30;
        System.out.println("  [PASS] Static $O(N)$ Heapify");
    }
    // 2. BOUNDARY CASES
    private static void testBoundaryCases() {
        // Single element
        PriorityQueueHeap<Integer> heap = new PriorityQueueHeap<>();
        heap.insert(42);
        assert heap.size() == 1;
        assert heap.peek() == 42;
        assert heap.extractTop() == 42;
        assert heap.isEmpty();
        // Duplicate values
        heap.insert(15);
        heap.insert(15);
        heap.insert(15);
        assert heap.extractTop() == 15;
        assert heap.extractTop() == 15;
        // Capacity expansion test (> 16 elements)
        PriorityQueueHeap<Integer> largeHeap = new PriorityQueueHeap<>(null, 4); // small initial capacity
        for (int i = 100; i >= 1; i--) {
            largeHeap.insert(i);
        }
        assert largeHeap.size() == 100;
        assert largeHeap.peek() == 1;
        System.out.println("  [PASS] Boundary & Capacity Cases");
    }
    // 3. INVALID INPUT CASES
    private static void testInvalidInputs() {
        PriorityQueueHeap<Integer> emptyHeap = new PriorityQueueHeap<>();
        // Test empty extract
        boolean caughtExtract = false;
        try {
            emptyHeap.extractTop();
        } catch (NoSuchElementException e) {
            caughtExtract = true;
        }
        assert caughtExtract : "Expected NoSuchElementException on extractTop() empty heap";
        // Test empty peek
        boolean caughtPeek = false;
        try {
            emptyHeap.peek();
        } catch (NoSuchElementException e) {
            caughtPeek = true;
        }
        assert caughtPeek : "Expected NoSuchElementException on peek() empty heap";
        // Test null insert
        boolean caughtNull = false;
        try {
            emptyHeap.insert(null);
        } catch (IllegalArgumentException e) {
            caughtNull = true;
        }
        assert caughtNull : "Expected IllegalArgumentException on null insert";
        System.out.println("  [PASS] Invalid Input Exception Handling");
    }
}
