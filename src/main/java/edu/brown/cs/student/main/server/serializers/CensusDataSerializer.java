package edu.brown.cs.student.main.server.serializers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.handlers.census.CensusData;

/** A class used to serialize user output in broadband handler class. */
public class CensusDataSerializer {
  private final JsonAdapter<CensusData> adapter;

  /** The constructor creates an adapter to convert from CensusData a JSON string. */
  public CensusDataSerializer() {
    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(CensusData.class);
  }

  /**
   * Serializes the input CensusData into a JSON string.
   *
   * @param data the CensusData to serialize.
   * @return the JSON output.
   */
  public String serialize(CensusData data) {
    return this.adapter.toJson(data);
  }
}
