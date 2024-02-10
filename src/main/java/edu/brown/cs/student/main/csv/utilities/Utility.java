package edu.brown.cs.student.main.csv.utilities;

import edu.brown.cs.student.main.csv.creators.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.SearcherCreator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * The Utility class that handles all functionality relating to the REPL. Will indefinitely prompt
 * the user to search a provided CSV file with specified parameters, until the user quits.
 */
public class Utility {

  /** Trivial constructor for the utility class. */
  public Utility() {}

  /**
   * Once called from Main, this method acts as a REPL for the user to interact with the CSVParser
   * and CSVSearcher to query a CSV file of their choice.
   */
  public void runREPL() {
    // Welcome the user.
    Scanner inputReader;
    System.out.println("Welcome to the CSV Searcher!");

    // Loop indefinitely in response to user input.
    while (true) {
      // Reset the I/O system.
      inputReader = new Scanner(System.in);
      inputReader.reset();
      System.out.println("———————————————————————————————————————————————————");
      System.out.println("Type 'S' to begin a CSV search or 'Q' to quit!");

      switch (inputReader.nextLine()) {
          // Begin the search process if any of the following is provided.
        case "S", "s", "Search", "search":
          System.out.println("Enter the name of the CSV file you wish to query:");
          String filename = inputReader.nextLine();

          // Protect against the user attempting to access external files.
          if (!filename.contains("data/")) {
            System.err.println("Don't attempt to access files in external directory!");
            continue;
          }

          // Attempt to open the user's file, respond accordingly.
          FileReader reader;
          try {
            reader = new FileReader(filename);
          } catch (FileNotFoundException e) {
            System.err.println("File not found. Restarting search.");
            continue;
          }

          // Prompt the user for the string they wish to search for.
          System.out.println("Input the String (not case sensitive) to search for:");
          String value = inputReader.nextLine();

          // Ask the user if their file has a header.
          System.out.println("Does your CSV file have a header row ('y' / 'n'):");
          boolean header;
          switch (inputReader.nextLine()) {
            case "y", "Y", "yes", "Yes":
              header = true;
              break;
            case "n", "N", "no", "No":
              header = false;
              break;
            default:
              System.err.println("Unsupported input. Restarting search.");
              continue;
          }

          // Instantiate a searcher, catch and handle exceptions in the Parser.
          CSVSearcher searcher;
          try {
            searcher = new CSVSearcher(new CSVParser<>(new SearcherCreator(), reader), header);
          } catch (FactoryFailureException e) {
            System.err.println(e.getMessage() + "\n" + e.row);
            continue;
          } catch (IOException e) {
            System.err.println("IOException when parsing CSV. Restarting search.");
            continue;
          }

          // Prompt user for search method.
          System.out.println(
              "Would you like to specify a column to search?\n"
                  + "Type 'no' (to search the entire file), "
                  + "'index' (to search by column index) "
                  + "or 'value' (to search by column header):");
          switch (inputReader.nextLine()) {
            case "n", "N", "no", "No":
              searcher.searchAllData(value);
              break;
            case "v", "V", "val", "Val", "value", "Value":
              if (header) {
                // Re-Prompt until a valid header name is provided.
                System.out.println("Enter a valid header name (not case sensitive):");
                int index;
                while ((index = searcher.getIndexFromHeader(inputReader.nextLine())) < 0) {
                  System.out.println("Invalid header. Try again:");
                }
                searcher.searchColByIndex(value, index);
                break;
              }
              // If header is false, continue to the case below.
              System.out.println("Lack of any header was previously specified.");
            case "i", "I", "index", "Index":
              // Re-Prompt until a valid integer index is provided.
              System.out.println("Enter a valid integer index:");
              while (!searcher.searchColByIndex(value, inputReader.nextInt())) {
                System.out.println("Invalid index. Try again:");
              }
              break;
            default:
              System.out.println("Unsupported input. Restarting search.");
              break;
          }
          break;
        case "Q", "q", "Quit", "quit":
          System.exit(0);
          break;
        default:
          System.out.println("Unsupported input. Restarting search.");
          break;
      }
    }
  }
}
