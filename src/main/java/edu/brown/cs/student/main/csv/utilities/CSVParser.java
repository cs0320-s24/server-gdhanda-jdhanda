package edu.brown.cs.student.main.csv.utilities;

import edu.brown.cs.student.main.csv.creators.CreatorFromRow;
import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** A generic CSV parser class that parses a CSV file into an ArrayList of the specified type. */
public class CSVParser<T> {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private final BufferedReader bufferedReader;
  private final CreatorFromRow<T> creatorFromRow;

  /**
   * The constructor for CSVParser stores the BufferedReader and CreatorFromRow, for use when
   * parsing the data from the Reader.
   *
   * @param creatorFromRow A generic CreatorFromRow to be specified by the user.
   * @param reader A Java Reader to be wrapped in the BufferedReader and used to parse.
   */
  public CSVParser(CreatorFromRow<T> creatorFromRow, Reader reader) {
    this.creatorFromRow = creatorFromRow;
    this.bufferedReader = new BufferedReader(reader);
  }

  /**
   * Uses the BufferedReader and CreatorFromRow to generate an ArrayList of the specified type with
   * the data from the CSV file.
   *
   * @throws IOException If there is an error reading from the BufferedReader.
   * @throws FactoryFailureException If there is an issue in any row of the CSV.
   * @return An ArrayList containing the parsed CSV data in the specified type.
   */
  public ArrayList<T> parse() throws IOException, FactoryFailureException {
    ArrayList<T> data = new ArrayList<>();
    String line;
    boolean firstRow = true; // Used to track size of first row.
    while ((line = this.bufferedReader.readLine()) != null) {
      String[] parsedRow = this.regexSplitCSVRow.split(line);
      // Update the first row in the creator on the first loop.
      if (firstRow) {
        this.creatorFromRow.setRowSize(parsedRow.length);
        firstRow = false;
      }
      // Use the creator to add the new row to the data.
      data.add(this.creatorFromRow.create(trimRow(parsedRow)));
    }
    this.bufferedReader.close();
    return data;
  }

  /**
   * A helper method to trim the whitespace on each row element and convert the row from an Array to
   * a List.
   *
   * @param row The specific row to format.
   * @return The re-formatted row.
   */
  private List<String> trimRow(String[] row) {
    for (int i = 0; i < row.length; i++) {
      row[i] = row[i].trim();
      if (row[i].startsWith("\"") && row[i].endsWith("\"")) {
        row[i] = row[i].substring(1, row[i].length() - 1);
      }
    }
    return List.of(row);
  }
}
