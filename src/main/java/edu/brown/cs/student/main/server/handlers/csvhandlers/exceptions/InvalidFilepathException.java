package edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions;

public class InvalidFilepathException extends Exception {

  public InvalidFilepathException(String filepath) {
    super("Filepath \"" + filepath + "\" is beyond the reach of this program!");
  }
}
