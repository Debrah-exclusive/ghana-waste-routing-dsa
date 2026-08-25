public class Stack {

    private ServiceRequest[] data;   // we will store ServiceRequests
    private int top;                 // index of the top element
    private int capacity;

    // Constructor
    public Stack(int size) {
        capacity = size;
        data = new ServiceRequest[capacity];
        top = -1;   // empty stack
    }

    // Push: add an item to the top
    public void push(ServiceRequest item) {
        if (isFull()) {
            System.out.println("Stack is full! Cannot push.");
            return;
        }
        top++;
        data[top] = item;
    }

    // Pop: remove and return the top item
    public ServiceRequest pop() {
        if (isEmpty()) {
            System.out.println("Stack is empty! Cannot pop.");
            return null;
        }
        ServiceRequest item = data[top];
        data[top] = null;  // help garbage collection
        top--;
        return item;
    }

    // Peek: look at the top item without removing it
    public ServiceRequest peek() {
        if (isEmpty()) {
            System.out.println("Stack is empty! Nothing to peek.");
            return null;
        }
        return data[top];
    }

    // Check if stack is empty
    public boolean isEmpty() {
        return top == -1;
    }

    // Check if stack is full
    public boolean isFull() {
        return top == capacity - 1;
    }

    // Return current number of items
    public int size() {
        return top + 1;
    }

    // Display the stack (from top to bottom)
    public void display() {
        if (isEmpty()) {
            System.out.println("Stack is empty.");
            return;
        }
        System.out.println("Stack (top → bottom):");
        for (int i = top; i >= 0; i--) {
            System.out.println(data[i]);
        }
    }
}