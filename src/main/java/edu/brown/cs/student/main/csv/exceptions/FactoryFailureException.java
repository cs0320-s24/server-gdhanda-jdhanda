package edu.brown.cs.student.main.csv.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an error provided to catch any error that may occur when you create an object from a row.
 * Feel free to expand or supplement or use it for other purposes.
 */
public class FactoryFailureException extends Exception {
  public final List<String> row;

  /**
   * The constructor passes the message up to super() and stores the row locally.
   *
   * @param message A String describing the exception.
   * @param row The row in which the exception occurred.
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}