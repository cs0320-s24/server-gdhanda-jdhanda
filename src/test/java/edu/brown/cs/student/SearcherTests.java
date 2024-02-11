package edu.brown.cs.student;

import edu.brown.cs.student.main.csv.creators.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.SearcherCreator;
import edu.brown.cs.student.main.csv.utilities.CSVParser;
import edu.brown.cs.student.main.csv.utilities.CSVSearcher;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class SearcherTests {

  private final PrintStream standardOut = System.out;
  private final PrintStream standardErr = System.err;

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();

  /**
   * This method is called before every test to redirect standard output to a PrintStream called
   * outputStream
   */
  @BeforeEach
  public void setup() {
    System.setOut(new PrintStream(this.outputStream));
    System.setErr(new PrintStream(this.errorOutputStream));
  }

  /** This method is called after every test to restore standard output */
  @AfterEach
  public void after() {
    System.setOut(this.standardOut);
    System.setErr(this.standardErr);
  }

  /**
   * A basic test to search a CSV file by column header, where the header exists and there is a
   * result.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchWithHeaderWithResult() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchColByIndex("White", searcher.getIndexFromHeader("Data Type"));
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where 'White' was found:\n"
            + "[RI, White, \" $1,058.47 \", 395773.6521, $1.00, 75%]\n");
  }

  /**
   * A basic test to search a CSV file by column header, where the header exists and there is no
   * result.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchWithHeaderNoResult() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchColByIndex("Test", searcher.getIndexFromHeader("Data Type"));
    Assert.assertEquals(this.outputStream.toString(), "Could not find 'Test' in the dataset!\n");
  }

  /**
   * A test to search a CSV file by column header, confirming that search does not return the header
   * row if the value specified is also a header.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchIgnoresHeader() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/duplicates.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchAllData("Middle Name");
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where 'Middle Name' was found:\n" + "[Raj, Middle Name, Dhanda, Raj]\n");
  }

  /**
   * A test for the getIndexFromHeader method, where the header does and does not exist, and testing
   * its interaction with searchColByIndex.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testGetIndexFromHeader() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    // Test getIndexFromHeader independently where header exists and doesn't exist.
    Assert.assertEquals(searcher.getIndexFromHeader("Not A Header"), -1);
    Assert.assertEquals(searcher.getIndexFromHeader("Data Type"), 1);

    // Test getIndexFrom where header doesn't exist and where it does.
    Assert.assertEquals(
        searcher.searchColByIndex("White", searcher.getIndexFromHeader("Not A Header")), false);
    Assert.assertEquals(
        searcher.searchColByIndex("White", searcher.getIndexFromHeader("Data Type")), true);
  }

  /**
   * A basic test to search a CSV file by index on a file where there is no header, testing for
   * valid and invalid index inputs.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchWithoutHeaderByIndex() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/noheader.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, false);

    // Test first with a negative index and an index that is too large.
    Assert.assertEquals(searcher.searchColByIndex("2005", -1), false);
    Assert.assertEquals(searcher.searchColByIndex("2005", 3), false);

    // Test with an accurate input, and test correct printed output.
    Assert.assertEquals(searcher.searchColByIndex("2005", 2), true);
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where '2005' was found:\n" + "[Adelle, Female, 2005]\n" + "[Kate, Female, 2005]\n");
  }

  /**
   * A test for searching the entire dataset for a specified value, where the value exists in the
   * dataset.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchAllDataWithResults() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    Assert.assertEquals(searcher.searchAllData("RI"), true);
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where 'RI' was found:\n"
            + "[RI, White, \" $1,058.47 \", 395773.6521, $1.00, 75%]\n"
            + "[RI, Black, $770.26, 30424.80376, $0.73, 6%]\n"
            + "[RI, Native American/American Indian, $471.07, 2315.505646, $0.45, 0%]\n"
            + "[RI, Asian-Pacific Islander, \" $1,080.09 \", 18956.71657, $1.02, 4%]\n"
            + "[RI, Hispanic/Latino, $673.14, 74596.18851, $0.64, 14%]\n"
            + "[RI, Multiracial, $971.89, 8883.049171, $0.92, 2%]\n");
  }

  /**
   * A test for searching the entire dataset for a specified value, where the value does not exist
   * in the dataset.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchAllDataWithoutResults() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    Assert.assertEquals(searcher.searchAllData("NY"), false);
    Assert.assertEquals(this.outputStream.toString(), "Could not find 'NY' in the dataset!\n");
  }

  /**
   * A test for searching the entire dataset for a specified value, where the specified value
   * appears twice in the same row.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchAllDataNoDuplicates() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/duplicates.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchAllData("Adelle");
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where 'Adelle' was found:\n" + "[Adelle, Krystina, Dhanda, Adelle]\n");
  }

  /**
   * A test for the printResults helper method on an empty list.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testPrintResultsEmpty() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/duplicates.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.printResults(new ArrayList<>(), "No Results");
    Assert.assertEquals(
        this.outputStream.toString(), "Could not find 'No Results' in the dataset!\n");
  }

  /**
   * A test for the printResults helper method on a small list.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testPrintResultsBasic() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/duplicates.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    ArrayList<String> row1 = new ArrayList<>();
    row1.add("First Col");
    row1.add("Second Col");
    ArrayList<String> row2 = new ArrayList<>();
    row2.add("Omg");
    row2.add("So Cool");
    ArrayList<ArrayList<String>> results = new ArrayList<>();
    results.add(row1);
    results.add(row2);
    searcher.printResults(results, "2 Results");
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where '2 Results' was found:\n" + "[First Col, Second Col]\n" + "[Omg, So Cool]\n");
  }

  /**
   * A basic test to search a CSV file by column header, checking that the value inputted by the
   * user is not case-sensitive.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testValueCaseInsensitive() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchColByIndex("wHItE", searcher.getIndexFromHeader("Data Type"));
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where 'wHItE' was found:\n"
            + "[RI, White, \" $1,058.47 \", 395773.6521, $1.00, 75%]\n");
  }

  /**
   * A basic test to search a CSV file by column header, checking that the header inputted by the
   * user is not case-sensitive.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testHeaderCaseInsensitive() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchColByIndex("White", searcher.getIndexFromHeader("dATa TyPE"));
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where 'White' was found:\n"
            + "[RI, White, \" $1,058.47 \", 395773.6521, $1.00, 75%]\n");
  }

  /**
   * A test to confirm that the searcher will not print a row if the specified value is found but
   * not in the specified column.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchByIndexWrongColumn() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchColByIndex("RI", 2);
    Assert.assertEquals(this.outputStream.toString(), "Could not find 'RI' in the dataset!\n");
  }

  /**
   * A test to confirm that the searcher can search for empty data-points if they wish to see which
   * rows have empty columns.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testEmptyDataPoints() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/stars/ten-star.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchAllData("");
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where '' was found:\n"
            + "[1, , 282.43485, 0.00449, 5.36884]\n"
            + "[2, , 43.04329, 0.00285, -15.24144]\n"
            + "[3, , 277.11358, 0.02422, 223.27753]\n"
            + "[118721, , -2.28262, 0.64697, 0.29354]\n");
  }

  /**
   * A test to confirm that the searcher disregards any extra whitespace on either end of the search
   * term inputted by the user.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchTermExtraWhitespace() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/stars/ten-star.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    CSVSearcher searcher = new CSVSearcher(parser, true);

    searcher.searchAllData("                Barnard's Star       ");
    Assert.assertEquals(
        this.outputStream.toString(),
        "Here's where 'Barnard's Star' was found:\n"
            + "[87666, Barnard's Star, -0.01729, -1.81533, 0.14824]\n");
  }
}
