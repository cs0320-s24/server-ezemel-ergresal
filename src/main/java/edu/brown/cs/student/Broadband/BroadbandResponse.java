package edu.brown.cs.student.Broadband;

import com.squareup.moshi.Moshi;

public class BroadbandResponse implements Response {
  private String timeZone;
  private String time;
  private String countyName;
  private String state;
  private String percentageBroadband;


  /**
   * object which has been parsed from API census data from server
   * @param time
   * @param countyName
   * @param state
   * @param percentageBroadband
   */

  public BroadbandResponse(String time, String countyName, String state, String percentageBroadband) {
    this.timeZone = "EST";
    this.time = time;
    this.countyName = countyName;
    this.state = state;
    this.percentageBroadband = percentageBroadband;
  }

  /**
   * @return this response, serialized as Json
   */
  @Override
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(BroadbandResponse.class).toJson(this);
  }
}
