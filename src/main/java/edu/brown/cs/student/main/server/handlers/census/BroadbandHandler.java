package edu.brown.cs.student.main.server.handlers.census;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
  private BroadbandDatasource datasource;

  public BroadbandHandler(BroadbandDatasource datasource) {
    this.datasource = datasource;
  }

  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();

    // Store the state and county of the request.
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    // Check that two parameters were specified.
    if (request.queryParams().size() != 2) {
      responseData.put("result", "error");
      responseData.put("error_type", "invalid number of parameters specified!");
      responseData.put("params_given", request.queryParams());
      responseData.put("params_required", List.of("state", "county"));
      return responseData;
    }

    // Add inputs to the response data.
    responseData.put("query_state", state);
    responseData.put("query_county", county);

    // Check that both path and header were given.
    if (state == null || county == null) {
      responseData.put("result", "error");
      responseData.put("error_type", "missing_parameter");
      responseData.put("error_arg", (state == null) ? "state" : "county");
      return responseData;
    }

    try {
      // Check the cache for requested data, otherwise query the census.
      CensusData censusData = this.datasource.getBroadbandData(state, county);

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("time", getTime());
      responseData.put("data", censusData);

    } catch (Exception e) {
      // Add descriptive error message to the result.
      responseData.put("result", "error");
      String[] parts = e.getClass().toString().split("\\.");
      responseData.put("exception", parts[parts.length - 1]);
      responseData.put("error_type", e.getMessage());
    }
    return responseData;
  }

  private String getTime() {
    LocalDateTime dateAndTime = LocalDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return dateAndTime.format(format);
  }
}
