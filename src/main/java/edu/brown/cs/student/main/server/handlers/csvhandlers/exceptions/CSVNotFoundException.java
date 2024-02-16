package edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions;

public class CSVNotFoundException extends Exception {

  public CSVNotFoundException() {
    super("There is no CSV file loaded! Try \'loadcsv\'!");
  }
}
