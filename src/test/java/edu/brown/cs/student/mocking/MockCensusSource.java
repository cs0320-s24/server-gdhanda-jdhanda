package edu.brown.cs.student.mocking;

import edu.brown.cs.student.main.server.handlers.census.BroadbandDatasource;
import edu.brown.cs.student.main.server.handlers.census.CensusData;

/**
 * A datasource that never actually calls the Census API, but always returns a constant census-data
 * value for testing purposes.
 */
public class MockCensusSource implements BroadbandDatasource {
  private final CensusData constantData;

  /**
   * A trivial constructor to initialize the CensusData instance variable to whatever is passed in.
   *
   * @param constantData
   */
  public MockCensusSource(CensusData constantData) {
    this.constantData = constantData;
  }

  /**
   * This method mocks the getBroadbandData method implemented by all Broadband data sources, but
   * always returns a constant CensusData value as specified.
   *
   * @param state is the state to be searched.
   * @param county is the county within the state to be searched.
   * @return the Census Data for the specified state and county.
   */
  public CensusData getBroadbandData(String state, String county) {
    return this.constantData;
  }
}
