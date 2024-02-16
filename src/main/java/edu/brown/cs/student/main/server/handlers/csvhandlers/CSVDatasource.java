package edu.brown.cs.student.main.server.handlers.csvhandlers;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.exceptions.InvalidIndexException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.CSVNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.HeaderNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.InvalidFilepathException;
import java.io.IOException;
import java.util.List;

/** Interface to define the three actionable methods on a CSV datasource. */
public interface CSVDatasource {

  /**
   * Given a filepath, load the CSV, including headers if applicable.
   *
   * @param filepath is the filepath to the CSV.
   * @param hasHeader is whether the CSV contains headers.
   * @throws IOException
   * @throws FactoryFailureException
   * @throws InvalidFilepathException
   */
  void loadCSV(String filepath, boolean hasHeader)
      throws IOException, FactoryFailureException, InvalidFilepathException;

  /**
   * Given a string to search for and a header or index to search through, use the byIndex or
   * byValue boolean parameters to determine and execute the proper search method.
   *
   * @param value is the string to search for.
   * @param header is the header or column index to search in.
   * @param byIndex is whether the search will be done on a specific column.
   * @param byValue is whether the header is a column name.
   * @return a two-dimensional array with the matching rows and their components.
   * @throws CSVNotFoundException
   * @throws InvalidIndexException
   * @throws HeaderNotFoundException
   */
  List<List<String>> searchCSV(String value, String header, boolean byIndex, boolean byValue)
      throws CSVNotFoundException, InvalidIndexException, HeaderNotFoundException;

  /**
   * Will return the entire CSV file to be viewed.
   *
   * @return a two-dimensional list representing each row and its components.
   * @throws CSVNotFoundException
   */
  List<List<String>> viewCSV() throws CSVNotFoundException;
}
