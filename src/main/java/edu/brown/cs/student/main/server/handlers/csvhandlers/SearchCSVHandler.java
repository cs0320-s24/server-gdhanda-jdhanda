package edu.brown.cs.student.main.server.handlers.csvhandlers;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  private CSVDatasource sharedCSVData;

  public SearchCSVHandler(CSVDatasource state) {}

  @Override
  public Object handle(Request request, Response response) {
    // Store the state and county of the request.
    String value = request.queryParams("value");
    String index = request.queryParams("index");
    String header = request.queryParams("header");

    // TODO: Ensure correct parameters.

    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();
    try {

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("value", value);

      responseData.put("csv-data", "test-search");

    } catch (Exception e) {
      // Add descriptive error message to the result.
      e.printStackTrace();
      responseData.put("result", "error");
      responseData.put("caused by", e.getMessage());
    }
    return responseData;
  }
}
