package edu.brown.cs.student.main.server.handlers.csvhandlers;

import edu.brown.cs.student.main.csv.creators.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.SearcherCreator;
import edu.brown.cs.student.main.csv.utilities.CSVParser;
import edu.brown.cs.student.main.csv.utilities.CSVSearcher;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVDatasource {

  private String filepath;
  private boolean fileLoaded;
  private CSVSearcher searcher;

  public CSVDatasource() {
    this.filepath = "";
    this.fileLoaded = false;
  }

  public void loadCSV(String filepath, boolean hasHeader)
      throws IOException, FactoryFailureException {
    // TODO: Add check for valid filepath (within the data folder)!!!

    FileReader reader = new FileReader(filepath);
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    this.searcher = new CSVSearcher(parser, hasHeader);
  }

  public void searchCSV(String value, String header, boolean byIndex, boolean byValue) {
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

  public ArrayList<ArrayList<String>> getData() {
    return this.searcher.getData();
  }
}
