package edu.brown.cs.student.Broadband;

import com.squareup.moshi.Moshi;

public class BroadbandResponse {
  private String timeZone;
  private String time;
  private String countyName;
  private String state;
  private String percentageBroadband;

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
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(BroadbandResponse.class).toJson(this);
  }
}
