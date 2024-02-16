package edu.brown.cs.student.main.csv.creators;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import java.util.List;

/** An implementation of CreatorFromRow to test various inputs in CSVParser. */
public class IntegerArrayCreator implements CreatorFromRow<Integer[]> {
  private int length;

  /** A constructor to initialize the length field. */
  public IntegerArrayCreator() {
    this.length = 0;
  }

  /** An implementation of create as defined in CreatorFromRow. */
  @Override
  public Integer[] create(List<String> row) throws FactoryFailureException {
    if (row.size() != this.length) {
      throw new FactoryFailureException("Incorrect Number of Items:", row);
    }
    Integer[] result = new Integer[this.length];
    for (int i = 0; i < this.length; i++) {
      result[i] = Integer.valueOf(row.get(i));
    }
    return result;
  }

  /** An implementation of setRowSize as defined in CreatorFromRow. */
  @Override
  public void setRowSize(int length) {
    this.length = length;
  }
}
