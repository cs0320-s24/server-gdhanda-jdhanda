package edu.brown.cs.student.main.server.handlers.census.exceptions;

public class CountyNotFoundException extends Exception {

  public CountyNotFoundException(String county) {
    super("Could not find county: " + county);
  }
}
