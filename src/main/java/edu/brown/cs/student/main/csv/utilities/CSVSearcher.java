package edu.brown.cs.student.main.csv.utilities;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.exceptions.InvalidIndexException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class that works with a CSVParser in order to parse and search a CSV File whose rows are
 * formatted as an Array of Strings.
 */
public class CSVSearcher {
  private final ArrayList<ArrayList<String>> data;
  private boolean hasHeader;

  /**
   * The constructor parses the file and initializes instance variables.
   *
   * @param parser The CSVParser used to parse the data.
   * @param hasHeader A boolean for whether the CSV file has a header.
   * @throws FactoryFailureException If the parser encounters malformed rows.
   * @throws IOException If the parser has issues while reading the file.
   */
  public CSVSearcher(CSVParser<ArrayList<String>> parser, boolean hasHeader)
      throws FactoryFailureException, IOException {
    this.data = parser.parse();
    this.hasHeader = hasHeader;
  }

  /**
   * A method used in the REPL. Returns a column index given the String value of one of the CSV
   * file's header rows.
   *
   * @param column The header value to find an index for.
   * @return The index of the header if it is found, -1 otherwise.
   */
  public int getIndexFromHeader(String column) {
    for (int i = 0; i < data.get(0).size(); i++) {
      if (data.get(0).get(i).equalsIgnoreCase(column)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * A method used in the REPL. Searches the data and returns all rows that contain at least one
   * instance of the specified value.
   *
   * @param value The String value to find in the CSV data.
   * @return An ArrayList of ArrayLists of Strings with the rows found.
   */
  public ArrayList<ArrayList<String>> searchAllData(String value) {
    ArrayList<ArrayList<String>> results = new ArrayList<>();
    for (int i = (this.hasHeader) ? 1 : 0; i < data.size(); i++) {
      for (int j = 0; j < data.get(0).size(); j++) {
        if (data.get(i).get(j).equalsIgnoreCase(value.trim())) {
          results.add(data.get(i));
          break;
        }
      }
    }
    return results;
  }

  /**
   * A method used in the REPL. Searches the data and returns all rows that contain the specified
   * value in the specified column.
   *
   * @param value The String value to find in the CSV data.
   * @param index The column index in which to search for the value.
   * @return An ArrayList of ArrayLists of Strings with the rows found.
   */
  public ArrayList<ArrayList<String>> searchColByIndex(String value, int index)
      throws InvalidIndexException {
    // Check valid index.
    if (index < 0 || index >= data.get(0).size()) {
      throw new InvalidIndexException("Index \"" + index + "\" is not valid!");
    }
    // Search through data.
    ArrayList<ArrayList<String>> results = new ArrayList<>();
    for (int i = (this.hasHeader) ? 1 : 0; i < data.size(); i++) {
      if (data.get(i).get(index).equalsIgnoreCase(value.trim())) {
        results.add(data.get(i));
      }
    }
    return results;
  }

  /**
   * A getter method for the parsed csv data. Creates a defensive copy of the data to avoid
   * tampering.
   *
   * @return the data parsed with the parser given in the constructor.
   */
  public ArrayList<ArrayList<String>> getData() {
    return new ArrayList<>(this.data);
  }
}
