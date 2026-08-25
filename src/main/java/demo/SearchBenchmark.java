package demo;

import algorithms.search.BinarySearch;
import algorithms.search.LinearSearch;
import structures.DynamicArray;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

/**
 * Owner: Abdul-Aziz Naeem
 *
 * Performance experiment for Linear Search vs Binary Search over DynamicArray<Integer>.
 *
 * Method (as required by the brief):
 *   - four input distributions: random, sorted, reversed, nearly_sorted
 *   - six input sizes per distribution
 *   - every configuration is run REPEATS times (>= 3) and the average is reported
 *   - a warm-up run is discarded first so the JIT has compiled the loop
 *   - binary search always receives a sorted copy of the array (precondition enforced)
 *   - linear search operates on the raw distribution
 *
 * Parameters derived from member index 7 (brief: >= 3 must come from index):
 *   - RNG_SEED = 7  (reproducible random data)
 *   - REPEATS  = 3 + (7 % 3) = 4  (>= 3 guaranteed)
 *   - BASE_SIZE = 100 + (7 * 50) = 450  (smallest n; doubles each step)
 *
 * Outputs (results/csv/):
 *   naeem_search_raw.csv          every individual timed run
 *   naeem_search_summary.csv      per-configuration averages — plot these
 *   naeem_algorithm_runs.csv      same averages in algorithm_runs table shape
 *
 * Run:
 *   java -cp out demo.SearchBenchmark
 */
public class SearchBenchmark {

    // -----------------------------------------------------------------------
    // Parameters derived from member index number 7
    // -----------------------------------------------------------------------
    private static final int MEMBER_INDEX = 7;

    /** Derived parameter 1: LCG seed — makes the random arrays reproducible. */
    private static final long RNG_SEED = MEMBER_INDEX;

    /** Derived parameter 2: repeats per configuration — 3 + (7 % 3) = 4. */
    private static final int REPEATS = 3 + (MEMBER_INDEX % 3);

    /** Derived parameter 3: base (smallest) input size — 100 + (7 * 50) = 450. */
    private static final int BASE_SIZE = 100 + (MEMBER_INDEX * 50);

    private static final int SIZE_STEPS = 6;

    private static final String[] DISTRIBUTIONS = {"random", "sorted", "reversed", "nearly_sorted"};
    private static final String   OUTPUT_DIR     = "results/csv";

    // -----------------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        run();
    }

    public static void run() throws IOException {
        System.out.println("=== Search Benchmark — Linear Search vs Binary Search ===");
        System.out.println("member index : " + MEMBER_INDEX);
        System.out.println("seed         : " + RNG_SEED);
        System.out.println("repeats      : " + REPEATS);
        System.out.println("base size    : " + BASE_SIZE);
        System.out.println("java version : " + System.getProperty("java.version"));
        System.out.println("os           : " + System.getProperty("os.name")
                + " " + System.getProperty("os.arch"));
        System.out.println("cpu cores    : " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("could not create output directory: " + OUTPUT_DIR);
        }

        String today = LocalDate.now().toString();
        int runId    = 1;

        try (PrintWriter raw     = new PrintWriter(new File(dir, "naeem_search_raw.csv"));
             PrintWriter summary = new PrintWriter(new File(dir, "naeem_search_summary.csv"));
             PrintWriter runs    = new PrintWriter(new File(dir, "naeem_algorithm_runs.csv"))) {

            raw    .println("algorithmName,distribution,inputSize,repeat,timeNs,targetIdx,foundIdx");
            summary.println("algorithmName,distribution,inputSize,repeats,avgTimeNs,avgTimeMs,memoryKb");
            runs   .println("runId,algorithmName,inputSize,timeNs,memoryKb,dateRun");

            System.out.printf("%-15s %-14s %8s %16s %8s%n",
                    "algorithm", "distribution", "n", "avg time (ns)", "mem KB");
            System.out.println("-".repeat(67));

            for (String dist : DISTRIBUTIONS) {
                int size = BASE_SIZE;
                for (int step = 0; step < SIZE_STEPS; step++) {

                    // Master array for this (dist, size) pair
                    int[] master = generate(dist, size, RNG_SEED + size);

                    // The target we search for: element at 75% through the array
                    // (worst-ish case for linear, still normal for binary)
                    int target = master[(int)(size * 0.75)];

                    for (String algo : new String[]{"LinearSearch", "BinarySearch"}) {

                        // Warm-up (discarded)
                        timeOnce(algo, master, target);

                        long totalTime = 0;
                        long memKb     = 0;

                        for (int r = 1; r <= REPEATS; r++) {
                            long usedBefore = usedHeap();
                            long t = timeOnce(algo, master, target);
                            long usedAfter  = usedHeap();

                            totalTime += t;
                            memKb      = Math.max(0, (usedAfter - usedBefore) / 1024);

                            // find expected index for logging
                            int expectedIdx = linearIndexOf(master, target);
                            int foundIdx    = (int)(timeOnceWithResult(algo, master, target)[1]);

                            raw.printf("%s,%s,%d,%d,%d,%d,%d%n",
                                    algo, dist, size, r, t, expectedIdx, foundIdx);
                        }

                        long avg = totalTime / REPEATS;
                        summary.printf("%s,%s,%d,%d,%d,%.3f,%d%n",
                                algo, dist, size, REPEATS, avg,
                                avg / 1_000_000.0, memKb);
                        runs.printf("%d,%s,%d,%d,%d,%s%n",
                                runId++, algo + "-" + dist, size, avg, memKb, today);

                        System.out.printf("%-15s %-14s %8d %16d %8d%n",
                                algo, dist, size, avg, memKb);
                    }
                    size *= 2;
                }
            }
        }

        int summaryRows = DISTRIBUTIONS.length * SIZE_STEPS * 2;
        System.out.println();
        System.out.println("Wrote " + summaryRows + " summary rows ("
                + (summaryRows * REPEATS) + " raw runs) to " + OUTPUT_DIR + "/");
        System.out.println("  naeem_search_raw.csv         every timed run");
        System.out.println("  naeem_search_summary.csv     averages — plot avgTimeNs vs inputSize");
        System.out.println("  naeem_algorithm_runs.csv     ready to load into algorithm_runs table");
    }

    // -----------------------------------------------------------------------
    // Timing helpers
    // -----------------------------------------------------------------------

    /** Returns wall-clock nanoseconds for one search. */
    private static long timeOnce(String algo, int[] master, int target) {
        return timeOnceWithResult(algo, master, target)[0];
    }

    /**
     * Returns [elapsedNs, foundIndex].
     * BinarySearch always gets a sorted copy (precondition enforced).
     * LinearSearch gets the raw distribution array.
     */
    private static long[] timeOnceWithResult(String algo, int[] master, int target) {
        DynamicArray<Integer> arr = toArray(master);
        long start = System.nanoTime();
        int  idx;
        if (algo.equals("BinarySearch")) {
            DynamicArray<Integer> sorted = sortedCopy(arr);
            idx = BinarySearch.search(sorted, target);
        } else {
            idx = LinearSearch.search(arr, target);
        }
        long elapsed = System.nanoTime() - start;
        return new long[]{elapsed, idx};
    }

    // -----------------------------------------------------------------------
    // Array generation (same LCG as SortBenchmark for consistency)
    // -----------------------------------------------------------------------

    private static int[] generate(String dist, int n, long seed) {
        int[] a = new int[n];
        switch (dist) {
            case "sorted":
                for (int i = 0; i < n; i++) a[i] = i * 2;
                break;
            case "reversed":
                for (int i = 0; i < n; i++) a[i] = (n - i) * 2;
                break;
            case "nearly_sorted": {
                for (int i = 0; i < n; i++) a[i] = i * 2;
                Lcg rng = new Lcg(seed);
                int swaps = Math.max(1, n * MEMBER_INDEX / 100); // 7% disordered
                for (int s = 0; s < swaps; s++) {
                    int i = rng.nextInt(n), j = rng.nextInt(n);
                    int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
                }
                break;
            }
            default: { // random
                Lcg rng = new Lcg(seed);
                for (int i = 0; i < n; i++) a[i] = rng.nextInt(10 * n);
            }
        }
        return a;
    }

    private static DynamicArray<Integer> toArray(int[] src) {
        DynamicArray<Integer> a = new DynamicArray<>(src.length);
        for (int v : src) a.insert(v);
        return a;
    }

    /** Returns a new DynamicArray containing the elements of src in sorted order. */
    private static DynamicArray<Integer> sortedCopy(DynamicArray<Integer> src) {
        // Copy into primitive array, insertion-sort it, then wrap back
        int n = src.size();
        int[] tmp = new int[n];
        for (int i = 0; i < n; i++) tmp[i] = src.get(i);
        // Insertion sort (small n is fine here; sorting is not the thing being benchmarked)
        for (int i = 1; i < n; i++) {
            int key = tmp[i], j = i - 1;
            while (j >= 0 && tmp[j] > key) { tmp[j + 1] = tmp[j]; j--; }
            tmp[j + 1] = key;
        }
        return toArray(tmp);
    }

    private static int linearIndexOf(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == target) return i;
        return -1;
    }

    private static long usedHeap() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    /** Minimal linear congruential generator (same constants as java.util.Random). */
    private static class Lcg {
        private long state;
        Lcg(long seed) { state = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1); }
        int nextInt(int bound) {
            state = (state * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int v = (int)(state >>> 17) & Integer.MAX_VALUE;
            return v % bound;
        }
    }
}
