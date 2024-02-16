package edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions;

/** Basic Exception to be thrown when trying to reach a CSV file that is unreachable. */
public class InvalidFilepathException extends Exception {

  /**
   * Creates a new Exception with the message that the filepath is unreachable.
   *
   * @param filepath is the filepath the program attempted to reach.
   */
  public InvalidFilepathException(String filepath) {
    super("Filepath \"" + filepath + "\" is beyond the reach of this program!");
  }
}
