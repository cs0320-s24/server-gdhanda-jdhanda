package edu.brown.cs.student.main.server.handlers.census;

import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;

public interface BroadbandDatasource {

  public CensusData getBroadbandData(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException;
}
