package CO2017.exercise2.hss26;

public abstract class MemManager {

    // boolean that records if the state of the memory has changed since the last time toString was invoked
    private volatile boolean _changed;
    // The size of the largest currently free block of memory
    private volatile int _largestSpace;
    // Array representation of the memory
    protected final char[] _memory;

    // Create a new MemManager of specified size
    MemManager(int s) {
        // Initialize the memory contents to an empty value of '.'
        _memory = new char[s];
        for (int i = 0; i < s; i++) _memory[i] = '.';
        // set _largestspace appropriately
        _largestSpace = s;
        // initialise _changed to true
        _changed = true;
    }

    // Return whether the state of memory has changed since the last invocation of toString
    public boolean isChanged() {
        return _changed;
    }

    // Find an address in memory where s amount of space is available.
    // You can assume that there is a space of at least size s available.
    // Therefore you must only call this method if you are sure there is enough space.
    protected abstract int findSpace(int s);

    // Start at address pos and calculate the size of the contiguous empty space beginning there.
    int countFreeSpacesAt(int pos) {
        int counter = 0;
        while (pos + counter < _memory.length && _memory[pos + counter] == '.') counter++;
        return counter;
    }

    // Allocate memory for a process
    public synchronized void allocate(Process p) throws InterruptedException {
        // Use a guard to block (wait) while there is not enough space available.
        while (p.getSize() > _largestSpace) this.wait();
        // Once space is available, use the findSpace method to get a suitable address,
        int startAddress = findSpace(p.getSize());
        // and then write the process's ID character into that memory range.
        for (int i = startAddress; i < startAddress + p.getSize(); i++) _memory[i] = p.getId();
        p.setAddress(startAddress);
        // Re-calculate the value of the _largestSpace,
        _largestSpace = 0;
        for (int i = 0; i < _memory.length; i++) _largestSpace = Math.max(_largestSpace, countFreeSpacesAt(i));
        // and set the _changed flag to indicate that the state of memory has changed.
        _changed = true;
        // Finally, notify any blocked processes that this operation is complete.
        this.notifyAll();
    }

    // Free memory used by a process
    public synchronized void free(Process p) {
        // Reset the contents of the memory array used by the process so that it contains '.' again.
        for (int i = 0; i < _memory.length; i++) if (_memory[i] == p.getId()) _memory[i] = '.';
        // Reset the address allocated to the process to -1.
        p.setAddress(-1);
        // Re-calculate the value of the _largestSpace, and set the _changed flag to indicate that the state of memory has changed.
        for (int i = 0; i < _memory.length; i++) _largestSpace = Math.max(_largestSpace, countFreeSpacesAt(i));
        // and set the _changed flag to indicate that the state of memory has changed.
        this._changed = true;
        // Finally, notify any blocked processes that this operation is complete.
        this.notifyAll();
    }

    // Generate a string representing the state of the Memory.
    public String toString() {
        // Each row represents (up to) 20 memory addresses
        String rtn = "  0|";
        for (int i = 0; i < _memory.length; i++) {
            if (i % 20 == 0 && i > 0) {
                rtn += "|\n"; // End the previous line
                if (i + 1 < _memory.length) { // Ensure the memory isn't finished before adding new line
                    if (i + 1 < 100) rtn += " "; // Pad the address if needed
                    rtn += Integer.toString(i) + "|"; // Add the address to the return
                }
            }
            rtn += _memory[i];
        }
        if (rtn.charAt(rtn.length() - 1) != '\n') rtn += "|\n";
        // Finally the current largest available space in memory should be indicated (number padded to 3 spaces)
        rtn += "ls: ";
        if (_largestSpace < 100) rtn += "0";
        if (_largestSpace < 10) rtn += "0";
        rtn += Integer.toString(_largestSpace);
        // Set _changed to false since printing
        _changed = false;
        // Return built sString
        return rtn;
    }
}
