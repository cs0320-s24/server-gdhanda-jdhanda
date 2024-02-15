package edu.brown.cs.student.main.server.handlers.csvhandlers;

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
    try {

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("csv-data", "test-view");

    } catch (Exception e) {
      // Add descriptive error message to the result.
      e.printStackTrace();
      responseData.put("result", "error");
      responseData.put("caused by", e.getMessage());
    }
    return responseData;
  }
}
