package edu.brown.cs.student.main.server.handlers.csvhandlers;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {

  public LoadCSVHandler(CSVDatasource state) {}

  @Override
  public Object handle(Request request, Response response) {
    // Store the filepath and header of the request.
    String path = request.queryParams("filepath");
    String header = request.queryParams("header");

    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();
    try {

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("filepath", path);
      responseData.put("header", header);
      responseData.put("csv-data", "test-load");

    } catch (Exception e) {
      // Add descriptive error message to the result.
      e.printStackTrace();
      responseData.put("result", "error");
      responseData.put("caused by", e.getMessage());
    }
    return responseData;
  }
}
