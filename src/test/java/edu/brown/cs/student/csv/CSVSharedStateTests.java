package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.exceptions.InvalidIndexException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.CSVSharedSource;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.CSVNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.HeaderNotFoundException;
import edu.brown.cs.student.main.server.handlers.csvhandlers.exceptions.InvalidFilepathException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/** Unit tests for CSVSharedSource. */
public class CSVSharedStateTests {

  /** This method tests loading. */
  @Test
  public void testLoad()
      throws InvalidFilepathException, IOException, FactoryFailureException, CSVNotFoundException {
    CSVSharedSource state = new CSVSharedSource();
    state.loadCSV("data/census/dol_ri_earnings_disparity.csv", true);
    assertTrue(state.viewCSV().size() > 0);
    assertThrows(
        InvalidFilepathException.class,
        () -> state.loadCSV("census/dol_ri_earnings_disparity.csv", true));
  }

  /** This method tests viewing. */
  @Test
  public void testView()
      throws InvalidFilepathException, IOException, FactoryFailureException, CSVNotFoundException {
    CSVSharedSource state = new CSVSharedSource();
    assertThrows(CSVNotFoundException.class, () -> state.viewCSV());
    state.loadCSV("data/census/dol_ri_earnings_disparity.csv", true);
    assertTrue(state.viewCSV().size() > 0);
  }

  /** This method tests search. */
  @Test
  public void testSearch()
      throws InvalidFilepathException, IOException, FactoryFailureException, CSVNotFoundException,
          InvalidIndexException, HeaderNotFoundException, NumberFormatException {
    CSVSharedSource state = new CSVSharedSource();
    assertThrows(CSVNotFoundException.class, () -> state.searchCSV("", "", false, false));
    state.loadCSV("data/census/dol_ri_earnings_disparity.csv", true);
    assertTrue(state.searchCSV("White", "", false, false).size() == 1);
    assertThrows(InvalidIndexException.class, () -> state.searchCSV("", "-10", true, false));
    assertThrows(NumberFormatException.class, () -> state.searchCSV("", "hi", true, false));
    assertThrows(HeaderNotFoundException.class, () -> state.searchCSV("", "help", true, true));
  }
}
