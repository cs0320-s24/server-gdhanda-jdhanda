package edu.brown.cs.student.main.server.handlers.census;

// TODO: Better error handling

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;

public class CensusAPIUtility {

  public static CensusData deserializeCensusData(String censusData) throws IOException {
    Moshi moshi = new Moshi.Builder().build();

    JsonAdapter<CensusData> adapter = moshi.adapter(CensusData.class);

    return adapter.fromJson(censusData);
  }
}
