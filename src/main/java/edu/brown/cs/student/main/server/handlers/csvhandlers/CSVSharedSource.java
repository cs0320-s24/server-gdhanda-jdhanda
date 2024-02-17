package edu.brown.cs.student.main.server.handlers.csvhandlers;

import edu.brown.cs.student.main.csv.creators.SearcherCreator;
import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.exceptions.InvalidIndexException;
import edu.brown.cs.student.main.csv.utilities.CSVParser;
import edu.brown.cs.student.main.csv.utilities.CSVSearcher;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.CSVNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.HeaderNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.InvalidFilepathException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVSharedSource will load the CSV and provide implementations necessary for the CSV handlers to
 * all access the same CSV file through association.
 */
public class CSVSharedSource implements CSVDatasource {

  private boolean fileLoaded; // Has a file been loaded yet.
  private CSVSearcher searcher; // To use for searching the CSV.

  /** Constructor initializes the instance variables. */
  public CSVSharedSource() {
    this.fileLoaded = false;
    this.searcher = null;
  }

  /**
   * Implements loadCSV as specified in CSVDatasource. Attempts to load the CSV with file path
   * protections. Creates a FileReader and CSVParser to build the CSV Searcher.
   *
   * @param filepath is the filepath to the CSV.
   * @param hasHeader is whether the CSV contains headers.
   * @throws IOException
   * @throws FactoryFailureException
   * @throws InvalidFilepathException
   */
  @Override
  public void loadCSV(String filepath, boolean hasHeader)
      throws IOException, FactoryFailureException, InvalidFilepathException {

    // Protect against the user attempting to access external files.
    if (!(filepath.startsWith("data/") || filepath.startsWith("/data/"))) {
      throw new InvalidFilepathException(filepath);
    }

    // Create file-reader, parser, and searcher.
    FileReader reader = new FileReader(filepath);
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    this.searcher = new CSVSearcher(parser, hasHeader);
    this.fileLoaded = true;
  }

  /**
   * Implements searchCSV as specified in CSVDatasource. Checks if the file is loaded and performs
   * the specified search operation on the CSV.
   *
   * @param value is the string to search for.
   * @param header is the header or column index to search in.
   * @param byIndex is whether the search will be done on a specific column.
   * @param byValue is whether the header is a column name.
   * @return a two-dimensional list of matching rows and their values.
   * @throws CSVNotFoundException
   * @throws InvalidIndexException
   * @throws HeaderNotFoundException
   */
  @Override
  public List<List<String>> searchCSV(String value, String header, boolean byIndex, boolean byValue)
      throws CSVNotFoundException, InvalidIndexException, HeaderNotFoundException,
          NumberFormatException {

    if (!fileLoaded) {
      throw new CSVNotFoundException();
    }

    // If searching by index:
    if (byIndex) {
      int index;
      // If searching by header value:
      if (byValue) {
        index = this.searcher.getIndexFromHeader(header);
      } else index = Integer.parseInt(header.trim());
      // Search with the index found above:
      return this.searcher.searchColByIndex(value, index);
    } else {
      // Search the entire dataset:
      return this.searcher.searchAllData(value);
    }
  }

  /**
   * Implements the viewCSV method as specified in CSVDatasource, if the CSV is loaded.
   *
   * @return a two-dimensional array with all the rows in the CSV.
   * @throws CSVNotFoundException
   */
  @Override
  public List<List<String>> viewCSV() throws CSVNotFoundException {
    if (!fileLoaded) {
      throw new CSVNotFoundException();
    }
    return new ArrayList<>(this.searcher.getData());
  }
}
