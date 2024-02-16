package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.census.BroadbandDatasource;
import edu.brown.cs.student.main.server.handlers.census.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.census.CensusAPISource;
import edu.brown.cs.student.main.server.handlers.census.caching.CachingCensusSource;
import edu.brown.cs.student.main.server.handlers.csvhandlers.CSVDatasource;
import edu.brown.cs.student.main.server.handlers.csvhandlers.CSVSharedSource;
import edu.brown.cs.student.main.server.handlers.csvhandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.csvhandlers.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.csvhandlers.ViewCSVHandler;
import spark.Spark;

/**
 * The main Server class, it starts the server, handles the endpoints, and sets access to the server.
 */
public class Server {

  private CSVDatasource csvSource; // Will handle the csv data.
  private BroadbandDatasource broadbandSource; // Will handle the Census API data.

  /**
   * Initializes instance variables for the csv source and broadband source and calls the run method.
   *
   * @param csvSource will handle the csv data.
   * @param broadbandSource will handle the Census API data.
   */
  private Server(CSVDatasource csvSource, BroadbandDatasource broadbandSource) {
    this.csvSource = csvSource;
    this.broadbandSource = broadbandSource;
    this.run();
  }

  /**
   * Main method creates a server.
   *
   * @param args is unused.
   */
  public static void main(String[] args) {
    new Server(new CSVSharedSource(), new CachingCensusSource(new CensusAPISource()));
  }

  /**
   * Starts the server on port 3232, sets access control, assigns handlers to the endpoints, and
   * notifies the terminal of success.
   */
  private void run() {
    int port = 3232;
    Spark.port(port);

    // Only allow access from certain addresses.
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "http://localhost:3232");
          response.header("Access-Control-Allow-Methods", "GET");
        });

    // Setting up the handler for the GET /____csv and /broadband endpoints
    Spark.get("loadcsv", new LoadCSVHandler(this.csvSource));
    Spark.get("searchcsv", new SearchCSVHandler(this.csvSource));
    Spark.get("viewcsv", new ViewCSVHandler(this.csvSource));
    Spark.get("broadband", new BroadbandHandler(this.broadbandSource));
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
    System.out.println(
        "Valid endpoints are \"loadcsv\", \"searchcsv\", " + "\"viewcsv\", and \"broadband\"!");
  }
}
