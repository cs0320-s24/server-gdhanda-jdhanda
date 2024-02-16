package edu.brown.cs.student.census;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.handlers.census.BroadbandDatasource;
import edu.brown.cs.student.main.server.handlers.census.CensusAPISource;
import edu.brown.cs.student.main.server.handlers.census.CensusData;
import edu.brown.cs.student.main.server.handlers.census.caching.CachingCensusSource;
import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/** UNIT tests for the Census API datasource class. */
public class TestBroadbandSource {

  /**
   * This method tests the real census datasource.
   *
   * @throws DatasourceException
   */
  @Test
  public void testCensusCanLoad_REAL()
      throws DatasourceException, StateNotFoundException, IOException, CountyNotFoundException {
    BroadbandDatasource source = new CensusAPISource();
    CensusData res = source.getBroadbandData("Colorado", "Denver County");
    assertNotNull(res);

    assertEquals(res.name(), "Denver County, Colorado");
    float broadband = Float.parseFloat(res.broadband().substring(0, res.broadband().length() - 1));
    assertTrue(broadband >= 0);
    assertTrue(broadband <= 100);
  }

  /**
   * This method tests the real census datasource using the cache.
   *
   * @throws DatasourceException
   */
  @Test
  public void testCensusCacheCanLoad_REAL()
      throws DatasourceException, StateNotFoundException, IOException, CountyNotFoundException {
    BroadbandDatasource source = new CachingCensusSource(new CensusAPISource());
    CensusData res = source.getBroadbandData("Colorado", "Denver County");
    assertNotNull(res);

    assertEquals(res.name(), "Denver County, Colorado");
    float broadband = Float.parseFloat(res.broadband().substring(0, res.broadband().length() - 1));
    assertTrue(broadband >= 0);
    assertTrue(broadband <= 100);
  }
}
