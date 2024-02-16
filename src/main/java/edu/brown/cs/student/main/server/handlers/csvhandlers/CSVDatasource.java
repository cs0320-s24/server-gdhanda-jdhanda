package edu.brown.cs.student.main.server.handlers.csvhandlers;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.exceptions.InvalidIndexException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.CSVNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.HeaderNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.InvalidFilepathException;
import java.io.IOException;
import java.util.ArrayList;

public interface CSVDatasource {

  void loadCSV(String filepath, boolean hasHeader)
      throws IOException, FactoryFailureException, InvalidFilepathException;

  ArrayList<ArrayList<String>> searchCSV(
      String value, String header, boolean byIndex, boolean byValue)
      throws CSVNotFoundException, InvalidIndexException, HeaderNotFoundException;

  ArrayList<ArrayList<String>> viewCSV() throws CSVNotFoundException;
}
