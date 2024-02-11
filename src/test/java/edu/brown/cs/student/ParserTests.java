package edu.brown.cs.student;

import edu.brown.cs.student.main.csv.creators.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.IntegerArrayCreator;
import edu.brown.cs.student.main.csv.creators.SearcherCreator;
import edu.brown.cs.student.main.csv.creators.star.Star;
import edu.brown.cs.student.main.csv.creators.star.StarCreator;
import edu.brown.cs.student.main.csv.utilities.CSVParser;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class ParserTests {

  /**
   * A basic test for the parser, to confirm that it retains all data.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testBasicParse() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/pets.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    ArrayList<ArrayList<String>> results = parser.parse();

    // Check various values of results.
    Assert.assertEquals(results.size(), 8);
    Assert.assertEquals(results.get(0).size(), 3);
    Assert.assertEquals(results.get(1).get(1), "Mabel");
    ArrayList<String> row5 = new ArrayList<>();
    row5.add("Hedgehog");
    row5.add("Knox");
    row5.add("4");
    Assert.assertEquals(row5, results.get(4));
  }

  /**
   * A basic test to confirm that the parser can handle empty columns without creating row lists of
   * an incorrect size.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testBasicParseEmptyItems() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/pets.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    ArrayList<ArrayList<String>> results = parser.parse();

    // Check various values of results.
    Assert.assertEquals(results.size(), 8);
    Assert.assertEquals(results.get(2).size(), 3);
    Assert.assertEquals(results.get(2).get(0), "");
    ArrayList<String> row3 = new ArrayList<>();
    row3.add("");
    row3.add("Grace");
    row3.add("12");
    Assert.assertEquals(row3, results.get(2));
  }

  /**
   * A basic test to confirm that the can properly throw a FactoryFailureException when a row is not
   * properly formatted in the CSV File.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testMalformedRows() throws IOException {
    FileReader reader = new FileReader("data/malformed/malformed_signs.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);

    Assert.assertThrows(FactoryFailureException.class, () -> parser.parse());
  }

  /**
   * A test to confirm that the can properly throw a FactoryFailureException when a row is not
   * properly formatted with a different CreatorFromRow.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testMalformedRowsStarCreator() throws IOException {
    FileReader reader = new FileReader("data/malformed/malformed_stars.csv");
    CSVParser<Star> parser = new CSVParser<>(new StarCreator(), reader);

    Assert.assertThrows(FactoryFailureException.class, () -> parser.parse());
  }

  /**
   * A test to confirm that the Regular Expression used to parse the files is not entirely correct,
   * in that it does not remove quotation marks from longer strings with internal commas (where
   * quotation marks are temporarily used in the CSV).
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testREGEX() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    ArrayList<ArrayList<String>> results = parser.parse();

    // Confirm that the quotation marks were not removed as they should be.
    Assert.assertEquals(results.get(1).get(2), "\" $1,058.47 \"");
    Assert.assertFalse(results.get(1).get(2).equals("$1,058.47"));
  }

  /**
   * A test to confirm that the parse method removes leading and trailing whitespace from all the
   * items in the CSV.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testTrim() throws IOException, FactoryFailureException {
    FileReader reader = new FileReader("data/tests/whitespace.csv");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    ArrayList<ArrayList<String>> results = parser.parse();

    // Confirm that the white space was properly removed.
    Assert.assertEquals(results.get(1).get(0), "1");
    Assert.assertEquals(results.get(1).get(1), "2");
    Assert.assertEquals(results.get(1).get(2), "3");
  }

  /**
   * A test to confirm that the parse method functions properly when handed different subclasses of
   * Java's Reader Class.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testStringReader() throws IOException, FactoryFailureException {
    StringReader reader =
        new StringReader(
            "Species, Name, Age\n"
                + "Dog, Mabel, 13\n"
                + " , Grace, 12\n"
                + "Dog, Brady, 8\n"
                + "Hedgehog, Knox, 4\n"
                + "Bunny, Cinnamon, 5\n"
                + "Hamster, , 2\n"
                + "Hamster, Chicken, 2\n");
    CSVParser<ArrayList<String>> parser = new CSVParser<>(new SearcherCreator(), reader);
    ArrayList<ArrayList<String>> results = parser.parse();

    // Check various values of results.
    Assert.assertEquals(results.size(), 8);
    Assert.assertEquals(results.get(0).size(), 3);
    Assert.assertEquals(results.get(1).get(1), "Mabel");
    ArrayList<String> row5 = new ArrayList<>();
    row5.add("Hedgehog");
    row5.add("Knox");
    row5.add("4");
    Assert.assertEquals(row5, results.get(4));
  }

  /**
   * A test to confirm that the parse method functions properly when handed different subclasses of
   * Java's Reader Class.
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testDifferentCreatorFromRow() throws IOException, FactoryFailureException {
    // Test with StarCreator
    FileReader reader = new FileReader("data/stars/ten-star.csv");
    CSVParser<Star> parser = new CSVParser<>(new StarCreator(), reader);
    ArrayList<Star> results = parser.parse();

    // Check various values of results.
    Assert.assertEquals(results.size(), 11);
    Assert.assertEquals(results.get(6).getID(), "70667");
    Assert.assertEquals(results.get(3).getName(), "");
    String[] coords = {"0", "0", "0"};
    Assert.assertEquals(results.get(1).getCoords(), coords);

    // Test with IntegerArrayCreator
    FileReader reader2 = new FileReader("data/tests/integers.csv");
    CSVParser<Integer[]> parser2 = new CSVParser<>(new IntegerArrayCreator(), reader2);
    ArrayList<Integer[]> results2 = parser2.parse();

    // Check various values of results.
    Assert.assertEquals(results2.size(), 4);
    Assert.assertEquals(results2.get(0).length, 3);
  }
}
