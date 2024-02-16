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

public class CSVSharedSource implements CSVDatasource {

  private boolean fileLoaded;
  private CSVSearcher searcher;

  public CSVSharedSource() {
    this.fileLoaded = false;
    this.searcher = null;
  }

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

  @Override
  public ArrayList<ArrayList<String>> searchCSV(
      String value, String header, boolean byIndex, boolean byValue)
      throws CSVNotFoundException, InvalidIndexException, HeaderNotFoundException {

    if (!fileLoaded) {
      throw new CSVNotFoundException();
    }

    // If searching by index:
    if (byIndex) {
      int index;
      // If searching by header value:
      if (byValue) {
        index = this.searcher.getIndexFromHeader(header);
        if (index < 0) {
          throw new HeaderNotFoundException(header);
        }
      } else index = Integer.parseInt(header.trim());
      // Search with the index found above:
      return this.searcher.searchColByIndex(value, index);
    } else {
      // Search the entire dataset:
      return this.searcher.searchAllData(value);
    }
  }

  @Override
  public ArrayList<ArrayList<String>> viewCSV() throws CSVNotFoundException {
    if (!fileLoaded) {
      throw new CSVNotFoundException();
    }
    return this.searcher.getData();
  }
}
