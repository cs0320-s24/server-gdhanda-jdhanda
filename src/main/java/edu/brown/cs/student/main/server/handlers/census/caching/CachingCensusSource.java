package edu.brown.cs.student.main.server.handlers.census.caching;

import edu.brown.cs.student.main.server.handlers.census.BroadbandDatasource;
import edu.brown.cs.student.main.server.handlers.census.CensusData;
import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;

public class CachingCensusSource implements BroadbandDatasource {
  private final BroadbandDatasource original;
  private final GenericCache<String, CensusData> censusCache;

  public CachingCensusSource(BroadbandDatasource original) {
    this.original = original;
    this.censusCache = new GenericCache<>(true, 25, true, 10);
  }

  @Override
  public CensusData getBroadbandData(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {

    CensusData censusData;
    if ((censusData = this.censusCache.get((state + county))) == null) {
      censusData = this.original.getBroadbandData(state, county);
      this.censusCache.put((state + county), censusData);
    }
    return censusData;
  }
}
