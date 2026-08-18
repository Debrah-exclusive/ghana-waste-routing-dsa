package algorithms.sorting;

/**
 * Owner: Emmanuel Thisara Otoo
 *
 * Counters carried through a sort so the report can quote operation counts, not
 * just wall-clock time. Timing alone is noisy on a shared laptop; comparison and
 * move counts are deterministic and are what the O(n^2) analysis actually predicts.
 *
 * Shared with the other sorting modules (Merge/Quicksort) if they want it.
 */
public class SortMetrics {

    /** Number of element-to-element comparisons performed. */
    public long comparisons;

    /** Number of element writes: a swap counts as 3 writes, a shift as 1. */
    public long moves;

    /** Nanoseconds spent inside the sort, excluding setup. */
    public long elapsedNanos;

    public void reset() {
        comparisons = 0;
        moves = 0;
        elapsedNanos = 0;
    }

    @Override
    public String toString() {
        return "comparisons=" + comparisons
                + ", moves=" + moves
                + ", elapsedNanos=" + elapsedNanos;
    }
}
