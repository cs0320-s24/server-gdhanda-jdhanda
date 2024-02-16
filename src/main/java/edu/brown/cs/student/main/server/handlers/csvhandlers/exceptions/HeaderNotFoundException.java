package edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions;

import java.util.ArrayList;

/** Basic Exception to be thrown when searching a CSV for a header that does not exist. */
public class HeaderNotFoundException extends Exception {

  /** Create a new Exception with the correct error message for no header in file. */
  public HeaderNotFoundException() {
    super("The specified CSV File has no header row!");
  }

  /**
   * Alternate constructor if header row exists but invalid string given.
   *
   * @param header is the name of the header not found.
   * @param headerRow is the row of headers in the csv.
   */
  public HeaderNotFoundException(String header, ArrayList<String> headerRow) {
    super(
        "Header not found for the value "
            + header
            + "! The headers for this CSV file are: "
            + headerRow.toString());
  }
}
