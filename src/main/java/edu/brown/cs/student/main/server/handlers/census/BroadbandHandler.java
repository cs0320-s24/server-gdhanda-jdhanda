package edu.brown.cs.student.main.server.handlers.census;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;
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
    this.censusCache = new GenericCache<>(true, 100, true, 60);
  }

  @Override
  public Object handle(Request request, Response response) {

    // store the state and county of the request
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> responseData = new HashMap<>();
    try {
      CensusData censusData;
      if ((censusData = this.censusCache.get((state + county))) == null) {
        censusData = this.censusRequest(state, county);
        this.censusCache.put((state + county), censusData);
      }

      responseData.put("result", "success");
      responseData.put("time", getTime());
      responseData.put("broadband", censusData);

    } catch (Exception e) {
      e.printStackTrace();
      responseData.put("result", "error");
      responseData.put("caused by", e.getMessage());
    }
    return responseData;
  }

  private CensusData censusRequest(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {

    List<String> codes = this.getCodes(state, county);
    List<List<String>> results =
        this.queryCensus(
            "/data/2021/acs/acs1/subject/variables?"
                + "get=NAME,S2802_C03_022E&for=county:"
                + codes.get(1)
                + "&in=state:"
                + codes.get(0));

    return new CensusData(state, county, results.get(1).get(1));
  }

  private List<String> getCodes(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {
    if (!this.haveStateCodes) {
      this.fetchAllStateCodes();
      this.haveStateCodes = true;
    }

    if (!this.stateCodes.containsKey(state)) {
      throw new StateNotFoundException("Could not find state: " + state);
    }
    String stateCode = this.stateCodes.get(state);

    List<List<String>> counties =
        this.queryCensus("/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);

    String countyCode = "";
    String countyState = county + ", " + state;
    for (List<String> list : counties) {
      if (list.get(0).equals(countyState)) {
        countyCode = list.get(2);
      }
    }
    if (!(countyCode.length() > 0)) {
      throw new CountyNotFoundException("Could not find county: " + county);
    }

    return List.of(stateCode, countyCode);
  }

  private void fetchAllStateCodes() throws IOException, DatasourceException {
    List<List<String>> results = this.queryCensus("/data/2010/dec/sf1?get=NAME&for=state:*");

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

  private List<List<String>> queryCensus(String file) throws IOException, DatasourceException {
    URL requestURL = new URL("https", "api.census.gov", file);
    HttpURLConnection clientConnection = this.connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();

    Type listListString = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListString);

    List<List<String>> results =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();

    return results;
  }

  /**
   * Private helper method; throws IOException so different callers can handle differently if
   * needed.
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }
}
