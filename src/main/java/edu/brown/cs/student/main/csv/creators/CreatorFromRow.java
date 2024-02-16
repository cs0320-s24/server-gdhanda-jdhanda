package edu.brown.cs.student.main.csv.creators;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 *
 * <p><T> Generic type of the row to be created.
 */
public interface CreatorFromRow<T> {

  /**
   * A method to convert a row into an object of type T.
   *
   * @param row The list of Strings to convert into the specified type.
   * @return An object of type T.
   * @throws FactoryFailureException When the rows are not properly formatted.
   */
  T create(List<String> row) throws FactoryFailureException;

  /**
   * A method used by Parser to indicate the length of the first row in the CSV. May be used by
   * developers to detect malformed rows when implementing "create".
   *
   * @param length The length of the first row of the CSV file as parsed in CSVParser.
   */
  void setRowSize(int length);
}
