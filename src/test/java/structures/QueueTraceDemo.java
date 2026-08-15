import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import structures.QueueCircular;

/**
 * Front/rear movement trace for QueueCircular.
 * Owner: Wiafe Franklin Asare
 *
 * Runs a scripted sequence of operations against a capacity-5 circular queue
 * loaded with the first real service requests from the dataset, and records
 * front, rear, size and the physical array contents after every step. Also
 * simulates what a plain non-circular array queue would do at the same step,
 * which is where the false-overflow problem shows up.
 *
 *   javac -d bin src/main/java/structures/*.java src/test/java/structures/*.java
 *   java -cp bin QueueTraceDemo
 *
 * Writes:
 *   results/csv/queue_front_rear_trace.csv   raw trace
 *   report/queue-trace.md                    same trace as a markdown table
 *
 * Run it from the repository root so the relative paths resolve.
 */
public class QueueTraceDemo {

    private static final int CAPACITY = 5;
    private static final int REQUESTS_NEEDED = 8;

    /** One row of the trace table. */
    private static class Step {
        int number;
        String operation;
        String result;
        int front;
        int rear;
        int size;
        String slots;
        String fifo;
        String linear;
        String note;
    }

    private static final Step[] steps = new Step[64];
    private static int stepCount = 0;

    // The plain non-circular array queue we simulate alongside, for comparison.
    private static int linearFront = 0;
    private static int linearNextFree = 0;

    public static void main(String[] args) throws IOException {
        String dataPath = args.length > 0 ? args[0] : findDataFile();
        String[][] requests = loadRequests(dataPath, REQUESTS_NEEDED);

        System.out.println("=== QueueCircular front/rear movement trace ===");
        System.out.println("capacity = " + CAPACITY + ", growable = false");
        System.out.println("data     = " + (dataPath == null ? "(not found, using placeholder ids)" : dataPath));
        System.out.println();
        System.out.println("Legend - the requests loaded into the queue, in arrival order:");
        for (int i = 0; i < requests.length; i++) {
            System.out.println("  R" + requests[i][0]
                    + "  requestId=" + requests[i][0]
                    + "  source=" + requests[i][1]
                    + "  category=" + requests[i][3]
                    + "  urgency=" + requests[i][4]
                    + "  submitted=" + requests[i][5]);
        }
        System.out.println();

        QueueCircular<String> q = new QueueCircular<String>(CAPACITY);
        String[] label = new String[requests.length];
        for (int i = 0; i < requests.length; i++) {
            label[i] = "R" + requests[i][0];
        }

        record(q, "initialise", "queue created", "empty ring, rear parked one slot behind front");

        // Fill the ring right up to capacity.
        for (int i = 0; i < 5; i++) {
            enqueue(q, label[i], i == 4 ? "queue is now full" : "rear steps forward one slot");
        }

        // Overflow while full.
        enqueue(q, label[5], "rejected: no free slot, front and rear both hold still");

        // Free two slots at the front.
        dequeue(q, "front steps forward, slot 0 is now free");
        dequeue(q, "front steps forward, slot 1 is now free");

        // The wrap-around: rear rolls off the end into the freed slots.
        enqueue(q, label[5], "WRAP: rear moves from slot 4 to slot 0 and reuses it");
        enqueue(q, label[6], "rear continues into slot 1, queue is full again");

        // Overflow again, this time in a wrapped state.
        enqueue(q, label[7], "rejected: full again even though rear is behind front");

        // Drain everything, wrapping front on the way.
        dequeue(q, "front steps forward");
        dequeue(q, "front steps forward");
        dequeue(q, "WRAP: front moves from slot 4 to slot 0");
        dequeue(q, "front steps forward");
        dequeue(q, "last request served, queue is empty again");

        // Underflow on an empty queue.
        dequeue(q, "rejected: nothing to serve, indices hold still");

        printTable();
        printSummary(q);
        writeCsv(new File("results/csv/queue_front_rear_trace.csv"));
        writeMarkdown(new File("report/queue-trace.md"), requests, q);
    }

    // ------------------------------------------------------- traced operations

    private static void enqueue(QueueCircular<String> q, String item, String note) {
        String result;
        try {
            q.enqueue(item);
            result = "accepted " + item;
        } catch (IllegalStateException e) {
            result = "REJECTED " + item + " (overflow)";
        }
        // Same operation against a non-circular array queue.
        if (linearNextFree < CAPACITY) {
            linearNextFree++;
        }
        record(q, "enqueue(" + item + ")", result, note);
    }

    private static void dequeue(QueueCircular<String> q, String note) {
        String result;
        try {
            result = "served " + q.dequeue();
        } catch (IllegalStateException e) {
            result = "REJECTED (underflow)";
        }
        if (linearFront < linearNextFree) {
            linearFront++;
        }
        record(q, "dequeue()", result, note);
    }

    private static void record(QueueCircular<String> q, String operation, String result, String note) {
        Step s = new Step();
        s.number = stepCount;
        s.operation = operation;
        s.result = result;
        s.front = q.frontIndex();
        s.rear = q.rearIndex();
        s.size = q.size();
        s.slots = renderSlots(q);
        s.fifo = renderFifo(q);
        s.linear = renderLinear();
        s.note = note;
        steps[stepCount++] = s;
    }

    // ------------------------------------------------------------- rendering

    /** Physical array contents, with > marking front and < marking rear. */
    private static String renderSlots(QueueCircular<String> q) {
        Object[] raw = q.rawSlots();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < raw.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            boolean isFront = !q.isEmpty() && i == q.frontIndex();
            boolean isRear = !q.isEmpty() && i == q.rearIndex();
            sb.append(isFront ? ">" : " ");
            sb.append(raw[i] == null ? "--" : raw[i].toString());
            sb.append(isRear ? "<" : " ");
        }
        return sb.append(']').toString();
    }

    /** Logical contents, front first. */
    private static String renderFifo(QueueCircular<String> q) {
        Object[] items = q.toArray();
        if (items.length == 0) {
            return "(empty)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(items[i]);
        }
        return sb.toString();
    }

    /**
     * What a plain non-circular array queue looks like at this step. Once its
     * rear runs off the end it reports full even while slots sit free at the
     * front — that is the false overflow a circular queue exists to prevent.
     */
    private static String renderLinear() {
        int held = linearNextFree - linearFront;
        int wasted = linearFront;
        String state;
        if (linearNextFree < CAPACITY) {
            state = "accepting";
        } else if (held == CAPACITY) {
            state = "FULL (genuinely)";
        } else if (held == 0) {
            state = "EXHAUSTED - empty yet cannot accept";
        } else {
            state = "FULL - FALSE OVERFLOW";
        }
        return "front=" + linearFront + " rear=" + linearNextFree
                + " held=" + held + " wasted=" + wasted + "  " + state;
    }

    // ---------------------------------------------------------------- output

    private static void printTable() {
        System.out.println("Step  Operation        Result                        front rear size  Backing array (>front, rear<)");
        System.out.println("----  ---------------  ----------------------------  ----- ---- ----  -----------------------------------");
        for (int i = 0; i < stepCount; i++) {
            Step s = steps[i];
            System.out.println(pad(String.valueOf(s.number), 4) + "  "
                    + pad(s.operation, 15) + "  "
                    + pad(s.result, 28) + "  "
                    + pad(String.valueOf(s.front), 5) + " "
                    + pad(String.valueOf(s.rear), 4) + " "
                    + pad(String.valueOf(s.size), 4) + "  "
                    + s.slots);
        }
        System.out.println();
        System.out.println("Notes per step:");
        for (int i = 0; i < stepCount; i++) {
            System.out.println("  " + pad(String.valueOf(steps[i].number), 3) + " " + steps[i].note);
        }
        System.out.println();
        System.out.println("Same sequence on a NON-circular array queue (for comparison):");
        for (int i = 0; i < stepCount; i++) {
            System.out.println("  " + pad(String.valueOf(steps[i].number), 3) + " "
                    + pad(steps[i].operation, 15) + " " + steps[i].linear);
        }
    }

    private static void printSummary(QueueCircular<String> q) {
        System.out.println();
        System.out.println("Counters after the trace:");
        System.out.println("  enqueues accepted : " + q.enqueueOps());
        System.out.println("  dequeues served   : " + q.dequeueOps());
        System.out.println("  wrap-arounds      : " + q.wrapArounds());
        System.out.println("  enqueues rejected : " + q.rejectedEnqueues());
        System.out.println("  final state       : " + q);
        System.out.println("  invariant holds   : " + q.checkInvariant());
    }

    private static void writeCsv(File file) throws IOException {
        ensureParent(file);
        PrintWriter out = new PrintWriter(file, "UTF-8");
        try {
            out.println("step,operation,result,front,rear,size,backingArray,fifoContents,note");
            for (int i = 0; i < stepCount; i++) {
                Step s = steps[i];
                out.println(s.number + "," + csv(s.operation) + "," + csv(s.result) + ","
                        + s.front + "," + s.rear + "," + s.size + ","
                        + csv(s.slots) + "," + csv(s.fifo) + "," + csv(s.note));
            }
        } finally {
            out.close();
        }
        System.out.println();
        System.out.println("wrote " + file.getPath());
    }

    private static void writeMarkdown(File file, String[][] requests, QueueCircular<String> q)
            throws IOException {
        ensureParent(file);
        PrintWriter out = new PrintWriter(file, "UTF-8");
        try {
            out.println("# Queue trace table — front/rear movement");
            out.println();
            out.println("Owner: Wiafe Franklin Asare");
            out.println();
            out.println("Generated by `QueueTraceDemo`. Do not edit by hand — re-run");
            out.println("`java -cp bin QueueTraceDemo` from the repository root to refresh.");
            out.println();
            out.println("Structure under trace: `structures.QueueCircular`, capacity "
                    + CAPACITY + ", fixed size (not growable).");
            out.println();
            out.println("## Requests used");
            out.println();
            out.println("Taken in arrival order from `data/service_requests.csv`.");
            out.println();
            out.println("| Label | requestId | source | destination | category | urgency | timeSubmitted |");
            out.println("|---|---|---|---|---|---|---|");
            for (int i = 0; i < requests.length; i++) {
                out.println("| R" + requests[i][0] + " | " + requests[i][0] + " | " + requests[i][1]
                        + " | " + requests[i][2] + " | " + requests[i][3] + " | " + requests[i][4]
                        + " | " + requests[i][5] + " |");
            }
            out.println();
            out.println("## Trace");
            out.println();
            out.println("`>` marks the slot `front` points at, `<` marks the slot `rear` points at,");
            out.println("`--` is a free slot.");
            out.println();
            out.println("| Step | Operation | Result | front | rear | size | Backing array | Queue (front → rear) | What moved |");
            out.println("|---|---|---|---|---|---|---|---|---|");
            for (int i = 0; i < stepCount; i++) {
                Step s = steps[i];
                out.println("| " + s.number + " | `" + s.operation + "` | " + s.result + " | "
                        + s.front + " | " + s.rear + " | " + s.size + " | `" + s.slots + "` | "
                        + s.fifo + " | " + s.note + " |");
            }
            out.println();
            out.println("## Counters after the trace");
            out.println();
            out.println("| Counter | Value |");
            out.println("|---|---|");
            out.println("| enqueues accepted | " + q.enqueueOps() + " |");
            out.println("| dequeues served | " + q.dequeueOps() + " |");
            out.println("| wrap-arounds (front or rear rolling past the last slot) | " + q.wrapArounds() + " |");
            out.println("| enqueues rejected (overflow) | " + q.rejectedEnqueues() + " |");
            out.println("| invariant `rear == (front + size - 1) mod capacity` holds | " + q.checkInvariant() + " |");
            out.println();
            out.println("## The same sequence on a non-circular array queue");
            out.println();
            out.println("A plain array queue advances `rear` but never wraps it. Once `rear`");
            out.println("reaches the end of the array it reports the queue full even though the");
            out.println("dequeued slots at the front are sitting empty. `wasted` counts those");
            out.println("unreachable slots.");
            out.println();
            out.println("| Step | Operation | Non-circular queue state |");
            out.println("|---|---|---|");
            for (int i = 0; i < stepCount; i++) {
                out.println("| " + steps[i].number + " | `" + steps[i].operation + "` | "
                        + steps[i].linear + " |");
            }
        } finally {
            out.close();
        }
        System.out.println("wrote " + file.getPath());
    }

    // ----------------------------------------------------------------- helpers

    private static void ensureParent(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static String findDataFile() {
        String[] candidates = {
            "data/service_requests.csv",
            "db/seed/service_requests.csv",
            "../data/service_requests.csv",
            "../../data/service_requests.csv",
        };
        for (int i = 0; i < candidates.length; i++) {
            if (new File(candidates[i]).isFile()) {
                return candidates[i];
            }
        }
        return null;
    }

    /**
     * Reads the first {@code wanted} data rows. Falls back to placeholder rows
     * if the CSV is missing, so the trace still runs on a fresh checkout.
     */
    private static String[][] loadRequests(String path, int wanted) throws IOException {
        String[][] rows = new String[wanted][];
        int found = 0;
        if (path != null) {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            try {
                reader.readLine(); // header
                String line;
                while (found < wanted && (line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",", -1);
                    if (parts.length < 8) {
                        System.out.println("  skipping malformed row: " + line);
                        continue;
                    }
                    rows[found++] = parts;
                }
            } finally {
                reader.close();
            }
        }
        while (found < wanted) {
            int id = found + 1;
            rows[found++] = new String[] {
                String.valueOf(id), "?", "?", "UNKNOWN", "UNKNOWN", "?", "?", "?"
            };
        }
        return rows;
    }

    private static String csv(String value) {
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String pad(String value, int width) {
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
