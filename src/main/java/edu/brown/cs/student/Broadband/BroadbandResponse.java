package edu.brown.cs.student.Broadband;

import java.time.LocalDateTime;

public class BroadbandResponse {
//  private LocalDateTime dateTime;
  private String timeZone;
  //add fields for the actual info: "the state and county names your server received", list<list<string>> (?)

  public BroadbandResponse() {
//    this.dateTime = LocalDateTime.now();
    this.timeZone = "EST";
  }
}
