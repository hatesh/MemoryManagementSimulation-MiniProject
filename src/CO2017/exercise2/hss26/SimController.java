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
 * Controller Class for the simulation.
 * <p> This should be invoked with 3 command line arguments for:
 * <ul>
 * <li> The mode of operation: {@code f} for first fit; {@code b} for
 * best fit; or {@code w} for worst fit.
 * <li> The total memory size for the {@link MemManager}
 * <li> Filename of simulated {@link Process} data.
 * </ul>
 * <p>
 * <strong>DO NOT IMPLEMENT THIS CLASS</strong> Use the version 
 * <a href="https://campus.cs.le.ac.uk/teaching/resources/CO2017/exercise2/SimController.java.xml">available here</a>
 * <p>
 *
 * @author Gilbert Laycock
 * @version $Id: SimController.java 1397 2019-02-08 17:03:43Z gtl1 $
 */
public class SimController implements Runnable {
  private static ThreadPoolExecutor _ex;
  private static MemManager _mm;

  /**
   * Run method for a watcher thread.
   *
   * Once per second, check if the state of the {@link MemManager} has
   * changed, and if so print it out using its own {@link
   * MemManager#toString} method.  
   * <p> 
   * When the {@code ThreadPoolExecutor} has terminated, print out the
   * status of the {@link MemManager} once more before returning.
   */
  @Override
  public void run() {
    while (!_ex.isTerminated()) {
      if (_mm.isChanged()) System.out.println(_mm);
      try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
    System.out.println(_mm);
  }

  /**
   * Main simulator program
   
   * <ul>
   * <li>Process the command line arguments.
   * <li>Create an appropriate {@link MemManager} instance and print a
   *     message "{@code Policy: PPPPP fit}" indicating which policy
   *     will be used, so "PPPPP" will be one of "BEST", "WORST" or "FIRST".
   * <li>Create a suitable {@code ThreadPoolExecutor}
   * <li>Use {@code start} to invoke the {@link #run} method and start
   *     a watcher thread.
   * <li>Create a thread for the {@link QueueHandler} and start it so
   *     that it processes the input file, creating a {@code Process} 
   *     instance for each line.
   * <li>Wait for the watcher and queue handling threads to complete
   *     (use {@code join}), and for the {@code ThreadPoolExecutor} to
   *     terminate.
   * <li>Print out the termination message "All threads have terminated".
   * </ul>
   *
   * @param args Command line arguments:
   * <ul>
   * <li> The mode of operation; a single character representing:
   * "{@code f}" for first fit; "{@code b}" for best fit; or "{@code w}" for
   * worst fit.
   * <li> The total memory size for the {@link MemManager}
   * <li> Filename of simulated {@link Process} data
   * </ul>
   */
  public static void main (String[] args) {
    if (args.length!=3){
      System.err.println("Usage: SimController [f|w|b] <size> <data>");
      System.exit(1);
    }

    // use a CachedThreadPool so that new threads are created as
    // required.
    _ex = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    final char policy      = args[0].charAt(0);
    final int msize        = Integer.parseInt(args[1]);
    final String fname     = args[2];

    switch (policy) {
    case 'b':
      _mm = new BestFitMemManager(msize);
      System.out.println("Policy: BEST fit");
      break;
    case 'w':
      _mm = new WorstFitMemManager(msize);
      System.out.println("Policy: WORST fit");
      break;
    case 'f':
      _mm = new FirstFitMemManager(msize);
      System.out.println("Policy: FIRST fit");
      break;
    default:
      System.err.println("Usage: SimController [f|w|b] <size> <data>");
      System.exit(1);
    }
    

    // start the watcher thread (do NOT add to _ex)
    Thread watcher = new Thread(new SimController());
    watcher.start();

    // create and start the MMUhandler (do NOT add to _ex)
    Thread qh =
      new Thread(new QueueHandler(_ex,_mm,fname));

    qh.start();

    try {
      // wait for the handlers to finish their work
      qh.join();

      // wait for _ex to terminate, and then shut it down
      _ex.awaitTermination(5L,TimeUnit.SECONDS);
      _ex.shutdown();

      // wait for watcher to finish
      watcher.join();
    } catch(InterruptedException e) {};
    System.out.println("All threads have terminated");

  }
}
