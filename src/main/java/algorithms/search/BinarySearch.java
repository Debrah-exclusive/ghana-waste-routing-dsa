package algorithms.search;

import structures.DynamicArray;

/**
 * Owner: Abdul-Aziz Naeem
 * Implemented + trace table for this algorithm.
 * Log runtime results to the algorithm_runs table/CSV when benchmarking.
 */
public class BinarySearch {
    
    /**
     * Performs a binary search on a sorted DynamicArray.
     * @param array the sorted DynamicArray to search in
     * @param target the element to search for
     * @param <T> the type of elements in the array, must be Comparable
     * @return the index of the target if found, or -1 if not found
     */
    public static <T extends Comparable<T>> int search(DynamicArray<T> array, T target) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null for binary search");
        }

        int left = 0;
        int right = array.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            T midValue = array.get(mid);

            if (midValue == null) {
                throw new IllegalStateException("Array contains null elements, which cannot be compared.");
            }

            int comparison = midValue.compareTo(target);

            if (comparison == 0) {
                return mid; // Target found
            } else if (comparison < 0) {
                left = mid + 1; // Target is in the right half
            } else {
                right = mid - 1; // Target is in the left half
            }
        }

        return -1; // Target not found
    }
}
