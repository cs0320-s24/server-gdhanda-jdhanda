package edu.brown.cs.student.main.server.handlers.census;

public class CountyNotFoundException extends Exception {

  public CountyNotFoundException(String message) {
    super(message);
  }
}
