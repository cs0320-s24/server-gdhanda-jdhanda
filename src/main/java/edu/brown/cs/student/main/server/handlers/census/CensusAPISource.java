package edu.brown.cs.student.main.server.handlers.census;

import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensusAPISource implements BroadbandDatasource {
  private Map<String, String> stateCodes;
  private boolean haveStateCodes;

  public CensusAPISource() {
    this.stateCodes = new HashMap<>();
    this.haveStateCodes = false;
  }

  @Override
  public CensusData getBroadbandData(String state, String county)
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
    return new CensusData(data.get(0), data.get(1) + "%", data.get(2), data.get(3));
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
}
