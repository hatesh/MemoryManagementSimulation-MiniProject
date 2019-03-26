package CO2017.exercise2.hss26;

public class Process implements Runnable {

    private MemManager memManager;
    private char id;
    private int size;
    private int runtime;
    private int address;

    // Sets up a Process instance
    Process(MemManager m, char i, int s, int r) {
        memManager = m;
        id = i;
        size = s;
        runtime = r;
    }

    // Basic behaviour when a Process thread is started
    public void run() {
        try {
            // Print out a message: 'this + " waiting to run."'
            System.out.println(this + " waiting to run.");
            // Use the instance's MemManager to allocate memory
            memManager.allocate(this);
            // Print out a message: 'this + " running."'
            System.out.println(this + " running.");
            // Sleep for 100 time the process runtime (simulates the process actually running)
            Thread.sleep(100 * runtime);
            // Use the instance's MemManager to free the memory
            memManager.free(this);
            // Print out a message: 'this + " has finished."'
            System.out.println(this + " has finished.");
        } catch (InterruptedException e) { }
    }

    // Accessor for size attribute
    public int getSize() { return size; }
    // Accessor for ID attribute
    public char getId() { return id; }
    // Set the memory address used by this Process
    public void setAddress(int a) { address = a; }
    // Accessor for the Address
    public int getAddress() { return address; }

    public String toString() {
        String A = "", S = "";
        if (address < 0) {
            A = "U";
        } else {
            if (address < 100) A += "0";
            if (address < 10) A += "0";
            A += Integer.toString(address);
        }
        if (size < 10) S += "0";
        S += Integer.toString(size);
        return id + ":" + A + "+" + S;
    }
}
