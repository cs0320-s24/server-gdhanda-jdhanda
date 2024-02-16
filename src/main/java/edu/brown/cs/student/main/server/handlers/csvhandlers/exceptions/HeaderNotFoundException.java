package edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions;

public class HeaderNotFoundException extends Exception {

  public HeaderNotFoundException(String header) {
    super("Header not found for the value \"" + header + "\"!");
  }
}
