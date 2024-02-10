package edu.brown.cs.student.main.csv.utilities;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments.
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  /**
   * Main method called from main (above).
   *
   * @param args The command line arguments passed in from main().
   */
  private Main(String[] args) {}

  /** Creates a new Utility class to run the REPL. */
  private void run() {
    new Utility().runREPL();
  }
}
