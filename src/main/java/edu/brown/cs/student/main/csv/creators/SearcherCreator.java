package edu.brown.cs.student.main.csv.creators;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of CreatorFromRow to be used along with CSVSearcher to convert a List of
 * Strings into an ArrayList for more efficient access.
 */
public class SearcherCreator implements CreatorFromRow<ArrayList<String>> {
  private int length;

  /** A constructor to initialize the length field before being updated by CSVParser. */
  public SearcherCreator() {
    this.length = 0;
  }

  /** An implementation of setRowSize as defined in CreatorFromRow. */
  @Override
  public ArrayList<String> create(List<String> row) throws FactoryFailureException {
    if (row.size() != this.length) {
      throw new FactoryFailureException("Incorrect Number of Items:", row);
    }
    return new ArrayList<>(row);
  }

  /** An implementation of setRowSize as defined in CreatorFromRow. */
  @Override
  public void setRowSize(int length) {
    this.length = length;
  }
}
