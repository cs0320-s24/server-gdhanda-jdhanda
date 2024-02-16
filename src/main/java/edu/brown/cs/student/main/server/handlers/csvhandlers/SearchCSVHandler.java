package edu.brown.cs.student.main.server.handlers.csvhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * SearchCSVHandler handles the searchcsv endpoint in the Server, attempting to search the CSV file
 * for the given parameters using the shared CSVDatasource.
 */
public class SearchCSVHandler implements Route {
  private CSVDatasource sharedCSVData; // The shared CSVDatasource.

  /**
   * Constructor initializes the datasource for the CSV.
   *
   * @param state is the polymorphic implementation of CSVDatasource.
   */
  public SearchCSVHandler(CSVDatasource state) {
    this.sharedCSVData = state;
  }

  /**
   * Override the handle method specified in Route. Attempts to search the CSV file, returning a
   * descriptive error message for bad inputs or failures, and a hash map containing a
   * two-dimensional array of the results if successful.
   *
   * @param request contains the parameters of the search request.
   * @param response is left unused.
   * @return a hash map of response data with the result of the search attempt.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();

    // Store the state and county of the request.
    String value = request.queryParams("value");
    String index = request.queryParams("index");
    String header = request.queryParams("header");

    // Check that either one and two parameters were specified.
    int numParams = request.queryParams().size();
    if (numParams > 2 || numParams < 1) {
      return this.invalidParams(request.queryParams(), responseData);
    }

    // Add inputs to the response data.
    responseData.put("query_value", value);
    responseData.put("query_index", index);
    responseData.put("query_header", header);

    // Check that the value was given.
    if (value == null) {
      responseData.put("result", "error");
      responseData.put("error_type", "missing_parameter");
      responseData.put("error_arg", "value");
      return responseData;
    }

    // Search entire data by default.
    boolean byIndex = false;
    boolean byHeader = false;
    String headerSearch = "";

    if (numParams == 2) {
      if (index == null && header != null) {
        byHeader = true;
        byIndex = true;
        headerSearch = header;
      } else if (index != null && header == null) {
        byIndex = true;
        headerSearch = index;
      } else {
        return this.invalidParams(request.queryParams(), responseData);
      }
    }

    try {
      ArrayList<ArrayList<String>> results =
          this.sharedCSVData.searchCSV(value, headerSearch, byIndex, byHeader);

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("csv-data", results);

    } catch (Exception e) {
      // Add descriptive error message to the result.
      responseData.put("result", "error");
      String[] parts = e.getClass().toString().split("\\.");
      responseData.put("exception", parts[parts.length - 1]);
      responseData.put("error_type", e.getMessage());
    }
    return responseData;
  }

  /**
   * Private helper-method used to build a descriptive return map if the parameters given were
   * invalid.
   *
   * @param params is the parameters
   * @param responseData is the map of response data.
   * @return the updated responseData map.
   */
  private Map<String, Object> invalidParams(Set<String> params, Map<String, Object> responseData) {
    responseData.put("result", "error");
    responseData.put("error_type", "invalid parameters specified!");
    responseData.put("params_given", params);
    responseData.put("params_required", "value");
    responseData.put("optional_params", List.of("index", "header"));
    return responseData;
  }
}
