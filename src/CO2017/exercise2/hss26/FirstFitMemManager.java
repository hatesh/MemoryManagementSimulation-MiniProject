package CO2017.exercise2.hss26;

public class FirstFitMemManager extends MemManager {
    // Construct a FirstFitMemManager instance.
    public FirstFitMemManager(int s) { super(s); }
    // Find an address space large enough for s using the first fit strategy
    protected int findSpace(int s) {
        for (int i = 0; i < _memory.length; i++) if (countFreeSpacesAt(i) >= s) return i;
        return -1;
    }

}
