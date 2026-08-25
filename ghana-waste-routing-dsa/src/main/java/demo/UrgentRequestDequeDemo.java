package demo;

import structures.MyDeque;

/** Demonstrates inserting an urgent request ahead of normal work. */
public final class UrgentRequestDequeDemo {
    private UrgentRequestDequeDemo() { }

    public static void main(String[] args) {
        MyDeque<String> requests = new MyDeque<>();
        requests.addRear("SR145 - routine bin collection");
        requests.addRear("SR146 - scheduled drain cleaning");
        System.out.println("Before urgent insertion: " + format(requests));
        requests.addFront("SR147 - URGENT hospital waste overflow");
        System.out.println("After urgent insertion:  " + format(requests));
        System.out.println("Next request dispatched: " + requests.removeFront());
    }

    private static String format(MyDeque<String> deque) {
        Object[] values = deque.toArrayFrontToRear();
        StringBuilder result = new StringBuilder("front [");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) result.append(", ");
            result.append(values[i]);
        }
        return result.append("] rear").toString();
    }
}
