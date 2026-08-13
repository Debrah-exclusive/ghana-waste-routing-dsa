package demo;

import algorithms.sorting.InsertionSort;
import algorithms.sorting.SelectionSort;
import algorithms.sorting.SortMetrics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

/**
 * Owner: Emmanuel Thisara Otoo
 *
 * Performance experiment for Selection Sort vs Insertion Sort.
 *
 * Method (as required by the brief):
 *   - four input distributions: random, already sorted, reverse sorted, nearly sorted
 *   - six input sizes per distribution
 *   - every configuration is run REPEATS times (>= 3) and the average is reported
 *   - a warm-up run is discarded first so the JIT has compiled the sort loop
 *   - both algorithms see an identical copy of the same generated array
 *
 * Outputs (results/csv/):
 *   otoo_sorts_raw.csv        every individual run
 *   otoo_sorts_summary.csv    per-configuration averages -> plot these
 *   otoo_algorithm_runs.csv   the same averages shaped like the algorithm_runs
 *                             table in db/schema.sql, for the DB owner to load
 *
 * Run:
 *   java -cp bin demo.SortBenchmark
 */
public class SortBenchmark {

    // ---------------------------------------------------------------------
    // Parameters derived from the member index number (brief: >= 3 parameters
    // must be derived from index numbers, so no two members run the same
    // experiment configuration).
    //
    // Emmanuel Thisara Otoo, index number 22146178.
    // ---------------------------------------------------------------------
    private static final long INDEX_NUMBER = 22146178L;

    /** Derived parameter 1: the pseudo-random seed, so the run is reproducible. */
    private static final long RNG_SEED = INDEX_NUMBER;

    /** Derived parameter 2: repeats per configuration — always at least 3. */
    private static final int REPEATS = 3 + (int) (INDEX_NUMBER % 3);

    /** Derived parameter 3: the smallest input size; sizes double from here. */
    private static final int BASE_SIZE = 100 + (int) (INDEX_NUMBER % 7) * 50;

    /** Derived parameter 4: how disordered the "nearly sorted" input is, in percent. */
    private static final int NEARLY_SORTED_PERCENT = 1 + (int) (INDEX_NUMBER % 5);

    private static final int SIZE_STEPS = 6;

    private static final String[] DISTRIBUTIONS = {"random", "sorted", "reversed", "nearly_sorted"};

    private static final String OUTPUT_DIR = "results/csv";

    public static void main(String[] args) throws IOException {
        run();
    }

    public static void run() throws IOException {
        System.out.println("Sort benchmark - Selection vs Insertion");
        System.out.println("index number      : " + INDEX_NUMBER);
        System.out.println("seed              : " + RNG_SEED);
        System.out.println("repeats per config: " + REPEATS);
        System.out.println("base input size   : " + BASE_SIZE);
        System.out.println("nearly-sorted     : " + NEARLY_SORTED_PERCENT + "% of positions swapped");
        System.out.println("java version      : " + System.getProperty("java.version"));
        System.out.println("os                : " + System.getProperty("os.name")
                + " " + System.getProperty("os.arch"));
        System.out.println("cpu cores         : " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("could not create " + OUTPUT_DIR);
        }

        String today = LocalDate.now().toString();
        int runId = 1;

        try (PrintWriter raw = new PrintWriter(new File(dir, "otoo_sorts_raw.csv"));
             PrintWriter summary = new PrintWriter(new File(dir, "otoo_sorts_summary.csv"));
             PrintWriter runs = new PrintWriter(new File(dir, "otoo_algorithm_runs.csv"))) {

            raw.println("algorithmName,distribution,inputSize,repeat,timeNs,comparisons,moves");
            summary.println("algorithmName,distribution,inputSize,repeats,avgTimeNs,avgTimeMs,"
                    + "comparisons,moves,memoryKb");
            runs.println("runId,algorithmName,inputSize,timeNs,memoryKb,dateRun");

            System.out.printf("%-15s %-14s %8s %14s %14s %14s%n",
                    "algorithm", "distribution", "n", "avg time (ns)", "comparisons", "moves");
            System.out.println("--------------------------------------------------------------"
                    + "-------------------------");

            for (String distribution : DISTRIBUTIONS) {
                int size = BASE_SIZE;
                for (int step = 0; step < SIZE_STEPS; step++) {

                    int[] master = generate(distribution, size, RNG_SEED + size);

                    for (String algorithm : new String[]{"SelectionSort", "InsertionSort"}) {

                        // warm-up run, discarded
                        runOnce(algorithm, copyOf(master), new SortMetrics());

                        long totalTime = 0;
                        long comparisons = 0;
                        long moves = 0;
                        long memoryKb = 0;

                        for (int repeat = 1; repeat <= REPEATS; repeat++) {
                            int[] input = copyOf(master);
                            SortMetrics metrics = new SortMetrics();

                            long usedBefore = usedHeapBytes();
                            runOnce(algorithm, input, metrics);
                            long usedAfter = usedHeapBytes();

                            if (!isSorted(input)) {
                                throw new IllegalStateException(
                                        algorithm + " produced an unsorted array for "
                                                + distribution + " n=" + size);
                            }

                            totalTime += metrics.elapsedNanos;
                            comparisons = metrics.comparisons;   // deterministic across repeats
                            moves = metrics.moves;
                            memoryKb = Math.max(0, (usedAfter - usedBefore) / 1024);

                            raw.printf("%s,%s,%d,%d,%d,%d,%d%n",
                                    algorithm, distribution, size, repeat,
                                    metrics.elapsedNanos, metrics.comparisons, metrics.moves);
                        }

                        long avgTime = totalTime / REPEATS;
                        summary.printf("%s,%s,%d,%d,%d,%.3f,%d,%d,%d%n",
                                algorithm, distribution, size, REPEATS,
                                avgTime, avgTime / 1_000_000.0, comparisons, moves, memoryKb);
                        runs.printf("%d,%s,%d,%d,%d,%s%n",
                                runId++, algorithm + "-" + distribution, size, avgTime, memoryKb, today);

                        System.out.printf("%-15s %-14s %8d %14d %14d %14d%n",
                                algorithm, distribution, size, avgTime, comparisons, moves);
                    }
                    size *= 2;
                }
            }
        }

        int summaryRows = DISTRIBUTIONS.length * SIZE_STEPS * 2;
        System.out.println();
        System.out.println("wrote " + summaryRows + " summary rows ("
                + (summaryRows * REPEATS) + " raw runs) to " + OUTPUT_DIR + "/");
        System.out.println("  otoo_sorts_raw.csv       every run");
        System.out.println("  otoo_sorts_summary.csv   averages - plot avgTimeNs against inputSize");
        System.out.println("  otoo_algorithm_runs.csv  ready to load into the algorithm_runs table");
    }

    // ------------------------------------------------------------------ helpers

    private static void runOnce(String algorithm, int[] input, SortMetrics metrics) {
        if (algorithm.equals("SelectionSort")) {
            SelectionSort.sort(input, metrics);
        } else {
            InsertionSort.sort(input, metrics);
        }
    }

    /**
     * Builds one of the four test distributions.
     * Uses a hand-written linear congruential generator (the same constants as
     * java.util.Random) so the numbers are identical on any machine that runs
     * this with the same index number — the experiment has to be reproducible.
     */
    private static int[] generate(String distribution, int n, long seed) {
        int[] a = new int[n];
        switch (distribution) {
            case "sorted":
                for (int i = 0; i < n; i++) {
                    a[i] = i;
                }
                break;
            case "reversed":
                for (int i = 0; i < n; i++) {
                    a[i] = n - i;
                }
                break;
            case "nearly_sorted": {
                for (int i = 0; i < n; i++) {
                    a[i] = i;
                }
                Lcg rng = new Lcg(seed);
                int swaps = Math.max(1, n * NEARLY_SORTED_PERCENT / 100);
                for (int s = 0; s < swaps; s++) {
                    int i = rng.nextInt(n);
                    int j = rng.nextInt(n);
                    int tmp = a[i];
                    a[i] = a[j];
                    a[j] = tmp;
                }
                break;
            }
            default: {   // "random"
                Lcg rng = new Lcg(seed);
                for (int i = 0; i < n; i++) {
                    a[i] = rng.nextInt(10 * n);
                }
            }
        }
        return a;
    }

    private static int[] copyOf(int[] source) {
        int[] out = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            out[i] = source[i];
        }
        return out;
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }
        return true;
    }

    private static long usedHeapBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    /** Minimal linear congruential generator — deterministic, no library RNG. */
    private static class Lcg {
        private long state;

        Lcg(long seed) {
            this.state = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        }

        int nextInt(int bound) {
            state = (state * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int value = (int) (state >>> 17) & Integer.MAX_VALUE;
            return value % bound;
        }
    }
}
