package edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions;

/** Basic Exception to be thrown when searching a CSV for a header that does not exist. */
public class HeaderNotFoundException extends Exception {

  /**
   * Create a new Exception with the correct error message for header not found.
   *
   * @param header is the name of the header not found.
   */
  public HeaderNotFoundException(String header) {
    super("Header not found for the value \"" + header + "\"!");
  }
}
