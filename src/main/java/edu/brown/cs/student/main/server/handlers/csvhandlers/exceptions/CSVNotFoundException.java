package edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions;

/** Basic exception to be thrown when there is no CSV found while trying to access one. */
public class CSVNotFoundException extends Exception {

  /** Constructs a new Exception with the message that no CSV is loaded. */
  public CSVNotFoundException() {
    super("There is no CSV file loaded! Try \'loadcsv\'!");
  }
}
