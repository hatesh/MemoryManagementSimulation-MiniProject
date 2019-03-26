package CO2017.exercise2.hss26;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;

/**
 * Handle a file of incoming Process data.
 * <p>
 * Class to read in a file of Process data, create suitable {@link Process}
 * instances, and add them to the ThreadPool of the
 * {@link SimController}.
 * <p>
 * Attributes of a QueueHandler are:
 * <ul>
 * <li>the {@code ThreadPoolExecutor} used by the {@link SimController}
 * to manage the process threads;
 * <li>the {@link MemManager} instance the processes are sharing;
 * <li>a filename where process data can be read (you may assume the
 * file contains data in the correct format if it exists).
 * </ul>
 * <p><strong>DO NOT IMPLEMENT THIS CLASS</strong> Use the version
 * <a href="https://campus.cs.le.ac.uk/teaching/resources/CO2017/exercise2/SimController.java.xml">available here</a>
 * <p>
 *
 * @author Gilbert Laycock
 * @version $Id: QueueHandler.java 1397 2019-02-08 17:03:43Z gtl1 $
 */
class QueueHandler implements Runnable {
    private final ThreadPoolExecutor _ex;
    private final MemManager _mm;
    private final String _fname;

    /**
     * @param e the {@code ThreadPoolExecutor} used by the {@link SimController}
     * to manage the Process threads
     * @param m the {@link MemManager} instance the processes are using
     * @param f filename where Process data can be read from
     */
    public QueueHandler (ThreadPoolExecutor e,
                         MemManager m,
                         String f) {
        _ex = e;
        _mm = m;
        _fname = f;
        //System.out.println("Queue handler: "+_fname);
    }

    /**
     * Code to read lines from a file of Process data, pause for the
     * specified time*100, then create a {@link Process} instance and add it
     * to the {@code ThreadPool}. See <a
     * href="/teaching/resources/CO2017/exercise2/DataReaderDemo.java">example
     * code</a>
     *
     * <p>
     * Example contents of the input file:
     * <pre>
     * 10:A:50:200
     * 10:B:30:100
     * 10:C:20:100
     * 10:D:35:100
     * 10:E:5:100
     * </pre>
     * Each row describes one process, and the four fields represent (in order):
     * <ul>
     * <li>The pause (or delay) between this process and the previous one;
     * <li>the process ID (you may assume these are suitable unique single chars);
     * <li>the amount of memory needed by the process;
     * <li>the amount of runtime the process will use.
     * </ul>
     *
     * The {@code run} method reads these lines in one by one. For each line, it will
     * <ul>
     * <li>Pause (delay) for 100 times the specified time;
     * <li>create a {@link Process} instance with the supplied values;
     * <li>add the {@link Process} to the {@code ThreadPool} and {@code execute} it.
     * </ul>
     */
    public void run() {
        final Path fpath = Paths.get(_fname);
        try (Scanner file = new Scanner(fpath)) {
            int pause, runtime, size;
            char pid;

            while (file.hasNextLine()) {
                // deal with one process
                Scanner line = new Scanner(file.nextLine());
                line.useDelimiter(":");
                pause   = line.nextInt();
                pid     = line.next().charAt(0);
                size    = line.nextInt();
                runtime = line.nextInt();
                line.close();

                try {
                    Thread.sleep(pause*100);
                } catch (InterruptedException e) {}

                // add the new process to the executor
                Process p = new Process(_mm,pid,size,runtime);
                _ex.execute(p);

            }
            file.close();
        } catch (NoSuchFileException e) {
            System.err.println("File not found: "+_fname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}