package edu.brown.cs.student.main.server.handlers.csvhandlers;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {

  private CSVDatasource sharedCSVData;

  public LoadCSVHandler(CSVDatasource state) {
    this.sharedCSVData = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();

    // Store the filepath and header of the request.
    String path = request.queryParams("filepath");
    String header = request.queryParams("header");

    // Check that two parameters were specified.
    if (request.queryParams().size() != 2) {
      // Bad request! Send an error response.
      responseData.put("result", "error");
      responseData.put("error_type", "invalid number of parameters specified!");
      responseData.put("params_given", request.queryParams());
      responseData.put("params_required", "filepath, header");
      return responseData;
    }

    // Add inputs to the response data.
    responseData.put("query_filepath", path);
    responseData.put("query_header", header);

    // Check that both path and header were given.
    if (path == null || header == null) {
      // Bad request! Send an error response.
      responseData.put("result", "error");
      responseData.put("error_type", "missing_parameter");
      responseData.put("error_arg", (path == null) ? "path" : "header");
      return responseData;
    }

    // Check that header has a valid value.
    if (!(header.equals("true") || header.equals("false"))) {
      responseData.put("result", "error");
      responseData.put("error_type", "invalid header value");
      responseData.put("valid_inputs", "header=true, header=false");
      return responseData;
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
      responseData.put("error_type", e.getMessage());
    }
    return responseData;
  }
}
