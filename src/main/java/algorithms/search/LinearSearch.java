package algorithms.search;

import structures.DynamicArray;

/**
 * Owner: Abdul-Aziz Naeem
 * Implemented + trace table for this algorithm.
 * Log runtime results to the algorithm_runs table/CSV when benchmarking.
 */
public class LinearSearch {
    
    /**
     * Performs a linear search on a DynamicArray.
     * @param array the DynamicArray to search in
     * @param target the element to search for
     * @param <T> the type of elements in the array
     * @return the index of the target if found, or -1 if not found
     */
    public static <T> int search(DynamicArray<T> array, T target) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        
        for (int i = 0; i < array.size(); i++) {
            T current = array.get(i);
            if (current == null) {
                if (target == null) {
                    return i;
                }
            } else if (current.equals(target)) {
                return i;
            }
        }
        
        return -1;
    }
}
