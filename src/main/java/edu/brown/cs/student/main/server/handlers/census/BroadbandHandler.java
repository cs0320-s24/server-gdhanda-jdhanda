package edu.brown.cs.student.main.server.handlers.census;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

// TODO: More descriptive exceptions

public class BroadbandHandler implements Route {
  private Map<String, Integer> stateCodes;
  private boolean haveStateCodes;

  public BroadbandHandler() {
    this.haveStateCodes = false;
    this.stateCodes = new HashMap<>();
  }

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
      responseData.put("result", e.getMessage());
    }

    return responseData;
  }

  private String censusRequest(String state, String county)
      throws URISyntaxException, IOException, InterruptedException, StateNotFoundException,
          CountyNotFoundException {

    List<Integer> codes = this.getCodes(state, county);
    System.out.println(codes);

    return state + county;
  }

  private List<Integer> getCodes(String state, String county)
      throws URISyntaxException, IOException, InterruptedException, StateNotFoundException,
          CountyNotFoundException {
    if (!this.haveStateCodes) {
      this.fetchAllStateCodes();
      this.haveStateCodes = true;
    }

    if (!this.stateCodes.containsKey(state)) {
      throw new StateNotFoundException("Could not find state: " + state);
    }
    int stateCode = this.stateCodes.get(state);

    String uri =
        "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode;
    HttpRequest buildCensusAPIRequest = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();

    // Send that API request then store the response in this variable.
    HttpResponse<String> sentCensusAPIResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildCensusAPIRequest, HttpResponse.BodyHandlers.ofString());

    Moshi moshi = new Moshi.Builder().build();

    record County(String NAME, int county) {}
    Type type = Types.newParameterizedType(List.class, County.class);
    JsonAdapter<List<County>> adapter = moshi.adapter(type);

    List<County> counties = adapter.fromJson(sentCensusAPIResponse.body());

    int countyCode = -1;
    for (County cont : counties) {
      if (cont.NAME.equals(county)) {
        countyCode = cont.county;
      }
    }
    if (countyCode < 0) {
      throw new CountyNotFoundException("Could not find county: " + county);
    }

    return List.of(stateCode, countyCode);
  }

  private void fetchAllStateCodes() throws URISyntaxException, IOException, InterruptedException {
    String uri = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*";
    HttpRequest buildCensusAPIRequest = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();

    // Send that API request then store the response in this variable.
    HttpResponse<String> sentCensusAPIResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildCensusAPIRequest, HttpResponse.BodyHandlers.ofString());

    Moshi moshi = new Moshi.Builder().build();

    record State(String NAME, int code) {}
    Type type = Types.newParameterizedType(List.class, State.class);
    JsonAdapter<List<State>> adapter = moshi.adapter(type);

    List<State> states = adapter.fromJson(sentCensusAPIResponse.body());
    for (State state : states) {
      this.stateCodes.put(state.NAME, state.code);
    }
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
