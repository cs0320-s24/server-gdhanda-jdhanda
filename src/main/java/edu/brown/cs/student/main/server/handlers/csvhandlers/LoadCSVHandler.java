package edu.brown.cs.student.main.server.handlers.csvhandlers;

import edu.brown.cs.student.main.server.MapSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * LoadCSVHandler handles the loadcsv endpoint in the Server, attempting to load the CSV file from
 * the given path via the CSVDataSource.
 */
public class LoadCSVHandler implements Route {
  private CSVDatasource sharedCSVData; // The shared CSVDatasource.

  /**
   * Constructor initializes the datasource for the CSV.
   *
   * @param state is the polymorphic implementation of CSVDatasource.
   */
  public LoadCSVHandler(CSVDatasource state) {
    this.sharedCSVData = state;
  }

  /**
   * Override the handle method specified in Route. Attempts to load the CSV file, returning a
   * descriptive error message for bad inputs or failures, if not successful.
   *
   * @param request contains the parameters of the load request.
   * @param response is left unused.
   * @return a hash map of response data about the result of the load attempt.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();

    // Store the filepath and header of the request.
    String path = request.queryParams("filepath");
    String header = request.queryParams("header");

    // Check that two parameters were specified.
    if (request.queryParams().size() != 2) {
      responseData.put("result", "error");
      responseData.put("error_type", "invalid number of parameters specified!");
      responseData.put("params_given", request.queryParams());
      responseData.put("params_required", List.of("filepath", "header"));
      return new MapSerializer().serialize(responseData);
    }

    // Add inputs to the response data.
    responseData.put("query_filepath", path);
    responseData.put("query_header", header);

    // Check that both path and header were given.
    if (path == null || header == null) {
      responseData.put("result", "error");
      responseData.put("error_type", "missing_parameter");
      responseData.put("error_arg", (path == null) ? "path" : "header");
      return new MapSerializer().serialize(responseData);
    }

    // Check that header has a valid value.
    if (!(header.equals("true") || header.equals("false"))) {
      responseData.put("result", "error");
      responseData.put("error_type", "invalid header value");
      responseData.put("valid_inputs", List.of("true", "false"));
      return new MapSerializer().serialize(responseData);
    }

    try {
      // Load the CSV data.
      boolean headerBool = (header.equals("true")) ? true : false;
      this.sharedCSVData.loadCSV(path, headerBool);

      // Inform the user of successful load.
      responseData.put("result", "success");

    } catch (Exception e) {
      // Add descriptive error message to the result.
      responseData.put("result", "error");
      String[] parts = e.getClass().toString().split("\\.");
      responseData.put("exception", parts[parts.length - 1]);
      responseData.put("error_type", e.getMessage());
    }
    return new MapSerializer().serialize(responseData);
  }
}
