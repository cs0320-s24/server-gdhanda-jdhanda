package edu.brown.cs.student.main.server.handlers.census.exceptions;

public class StateNotFoundException extends Exception {

  public StateNotFoundException(String state) {
    super("Could not find state: " + state);
  }
}
