package edu.brown.cs.student.main.server.handlers.census;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

// TODO: More descriptive exception

public class BroadbandHandler implements Route {

  public BroadbandHandler() {}

  @Override
  public Object handle(Request request, Response response) {

    // store the state and county of the request
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> responseData = new HashMap<>();

    try {
      String censusData = this.censusRequest(state, county);

      CensusData censusResponse = CensusAPIUtility.deserializeCensusData(censusData);

      String dateAndTime = getTime();

      responseData.put("result", "success");
      responseData.put("date and time", dateAndTime);
      responseData.put("census data", censusResponse);

    } catch (Exception e) {
      e.printStackTrace();
      responseData.put("result", "Exception");
    }

    return responseData;
  }

  private String censusRequest(String state, String county) {
    return "Hello" + state + county;
  }

  private String getTime() {
    // Get the current date and time
    LocalDateTime currentDateTime = LocalDateTime.now();

    // Define the date and time format
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Format the current date and time using the defined format
    String formattedDateTime = currentDateTime.format(formatter);

    return formattedDateTime;
  }
}
