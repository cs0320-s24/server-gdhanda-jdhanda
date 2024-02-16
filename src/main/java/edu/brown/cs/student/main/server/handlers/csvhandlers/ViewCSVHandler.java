package edu.brown.cs.student.main.server.handlers.csvhandlers;

import edu.brown.cs.student.main.server.serializers.MapSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ViewCSVHandler handles the viewcsv endpoint in the Server, attempting to view the CSV file using
 * the shared CSVDatasource.
 */
public class ViewCSVHandler implements Route {
  private final CSVDatasource sharedCSVData; // The shared CSVDatasource

  /**
   * Constructor initializes the datasource for the CSV.
   *
   * @param state is the polymorphic implementation of CSVDatasource.
   */
  public ViewCSVHandler(CSVDatasource state) {
    this.sharedCSVData = state;
  }

  /**
   * Override the handle method specified in Route. Attempts to view the CSV file, returning a
   * descriptive error message for bad inputs or failures, and a hash map including a
   * two-dimensional list of the CSV if successful.
   *
   * @param request contains the parameters of the view request (should be none).
   * @param response is left unused.
   * @return a hash map of response data with the result of the view attempt.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();

    if (request.queryParams().size() > 0) {
      responseData.put("result", "error");
      responseData.put("error_type", "too many parameters!");
      responseData.put("params_given", request.queryParams());
      return new MapSerializer().serialize(responseData);
    }

    try {
      List<List<String>> data = this.sharedCSVData.viewCSV();

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("data", data);

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
