package edu.brown.cs.student.main.server.handlers.census;

import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;
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
  private Map<String, String> stateCodes;
  private boolean haveStateCodes;
  private GenericCache<String, CensusData> censusCache;

  public BroadbandHandler() {
    this.haveStateCodes = false;
    this.stateCodes = new HashMap<>();
    this.censusCache = new GenericCache<>(true, 25, true, 10);
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
      CensusData censusData;
      if ((censusData = this.censusCache.get((state + county))) == null) {
        censusData = this.censusRequest(state, county);
        this.censusCache.put((state + county), censusData);
      }

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

  private CensusData censusRequest(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {

    // Get state and county code, and use them to query the census.
    List<String> codes = this.getCodes(state, county);
    List<List<String>> results =
        CensusAPIUtilities.queryCensus(
            "/data/2021/acs/acs1/subject/variables?"
                + "get=NAME,S2802_C03_022E&for=county:"
                + codes.get(1)
                + "&in=state:"
                + codes.get(0));

    // Construct a CensusData object with the results and return it.
    List<String> data = results.get(1);
    return new CensusData(data.get(0), data.get(1), data.get(2), data.get(3));
  }

  private List<String> getCodes(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {

    // Check and load state codes.
    if (!this.haveStateCodes) {
      this.fetchAllStateCodes();
      this.haveStateCodes = true;
    }

    // Get State code from stateCodes instance variable.
    if (!this.stateCodes.containsKey(state)) {
      throw new StateNotFoundException(state);
    }
    String stateCode = this.stateCodes.get(state);

    // Use CensusAPIUtilities to query the census.
    List<List<String>> counties =
        CensusAPIUtilities.queryCensus(
            "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);

    // Look for the county with the corresponding name.
    String countyCode = "";
    for (List<String> list : counties) {
      if (list.get(0).equals(county + ", " + state)) {
        countyCode = list.get(2);
      }
    }

    // Make sure county was found.
    if (!(countyCode.length() > 0)) {
      throw new CountyNotFoundException(county);
    }

    // Return a list of the county and state codes.
    return List.of(stateCode, countyCode);
  }

  private void fetchAllStateCodes() throws IOException, DatasourceException {
    // Query the census for the state codes.
    List<List<String>> results =
        CensusAPIUtilities.queryCensus("/data/2010/dec/sf1?get=NAME&for=state:*");

    // Convert the list to an instance variable hashmap and remove the header.
    for (List<String> list : results) {
      this.stateCodes.put(list.get(0), list.get(1));
    }
    this.stateCodes.remove("NAME");
  }

  private String getTime() {
    LocalDateTime dateAndTime = LocalDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return dateAndTime.format(format);
  }
}
