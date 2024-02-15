package edu.brown.cs.student.main.server.handlers.csvhandlers;

import edu.brown.cs.student.main.csv.creators.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.SearcherCreator;
import edu.brown.cs.student.main.csv.utilities.CSVParser;
import edu.brown.cs.student.main.csv.utilities.CSVSearcher;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.CSVNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.InvalidFilepathException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVDatasource {

  private boolean fileLoaded;
  private CSVSearcher searcher;

  public CSVDatasource() {
    this.fileLoaded = false;
    this.searcher = null;
  }

  public void loadCSV(String filepath, boolean hasHeader)
      throws IOException, FactoryFailureException, InvalidFilepathException {

    // Protect against the user attempting to access external files.
    if (!filepath.contains("data/")) {
      throw new InvalidFilepathException(
          "Filepath \"" + filepath + "\" is beyond the reach of this program!");
    }

    FileReader reader = new FileReader(filepath);
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    this.searcher = new CSVSearcher(parser, hasHeader);
    this.fileLoaded = true;
  }

  public void searchCSV(String value, String header, boolean byIndex, boolean byValue)
      throws CSVNotFoundException {
    if (!fileLoaded) {
      throw new CSVNotFoundException("There is no CSV file loaded! Try \'loadcsv\'!");
    }

    if (byIndex) {
      int index;
      if (byValue) {
        index = this.searcher.getIndexFromHeader(header);
      } else index = Integer.parseInt(header);
      this.searcher.searchColByIndex(value, index);
      // TODO: Change return data and send back to handler.
    } else {
      this.searcher.searchAllData(value);
      // TODO: return data here also
    }
  }

  public ArrayList<ArrayList<String>> viewCSV() throws CSVNotFoundException {
    if (!fileLoaded) {
      throw new CSVNotFoundException("There is no CSV file loaded! Try <loadcsv>!");
    }
    return this.searcher.getData();
  }
}
