package CO2017.exercise2.hss26;

public class BestFitMemManager extends MemManager {
    BestFitMemManager(int s) { super(s); }
    protected int findSpace(int s) {
        int pos = 0;
        int size = 0;
        for (int i = 0; i < _memory.length; i++) {
            if (countFreeSpacesAt(i) >= s) {
                if (countFreeSpacesAt(i) < size || (pos == 0 && size == 0)) {
                    size = countFreeSpacesAt(i);
                    pos = i;
                }
                i += countFreeSpacesAt(i) - 1;
            }
        }
        return pos;
    }
}
