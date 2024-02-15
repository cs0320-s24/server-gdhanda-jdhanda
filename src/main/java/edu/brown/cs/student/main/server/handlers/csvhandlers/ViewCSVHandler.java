package edu.brown.cs.student.main.server.handlers.csvhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {

  private CSVDatasource sharedCSVData;

  public ViewCSVHandler(CSVDatasource state) {
    this.sharedCSVData = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();

    if (request.queryParams().size() > 0) {
      // Bad request! Send an error response.
      responseData.put("result", "error");
      responseData.put("error_type", "too many parameters!");
      responseData.put("params_given", request.queryParams());
      return responseData;
    }

    try {
      ArrayList<ArrayList<String>> data = this.sharedCSVData.viewCSV();

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("csv-data", data);

    } catch (Exception e) {
      // Add descriptive error message to the result.
      responseData.put("result", "error");
      responseData.put("error_type", e.getMessage());
    }
    return responseData;
  }
}
