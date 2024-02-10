package edu.brown.cs.student.main;

import java.util.List;

/** An implementation of CreatorFromRow to test various inputs in CSVParser. */
public class StarCreator implements CreatorFromRow<Star> {

  /** A trivial constructor for StarCreator. */
  public StarCreator() {}

  /** An implementation of create as defined in CreatorFromRow. */
  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    if (row.size() != 5) {
      throw new FactoryFailureException("Incorrect Number of Items:", row);
    }
    String[] coords = {row.get(2), row.get(3), row.get(4)};
    return new Star(row.get(0), row.get(1), coords);
  }

  /** An implementation of setRowSize as defined in CreatorFromRow. */
  @Override
  public void setRowSize(int length) {}
}
