package edu.brown.cs.student.Broadband;

import com.squareup.moshi.Moshi;
import java.util.Map;

  /**
   * Response object to send if state param is invalid
   */
  public record NoBroadbandDataStateResponse(String invalid_state, String state, Map<String, Object> responseMap) {


    public NoBroadbandDataStateResponse(String state, Map<String, Object> responseMap) {
      this("state not found", state, responseMap);
    }

    /**
     * @return this response, serialized as Json
     */
    public String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(NoBroadbandDataStateResponse.class).toJson(this);
    }
  }
//public record YesBroadbandDataStateResponse(String invalid_state, String state, Map<String, Object> responesMap) {
//
//
//  public YesBroadbandDataStateResponse(String state, Map<String, Object> responseMap) {
//    this("State found", state, responseMap);
//  }
//
//  /**
//   * @return this response, serialized as Json
//   */
//  public String serialize() {
//    Moshi moshi = new Moshi.Builder().build();
//    return moshi.adapter(NoBroadbandDataStateResponse.class).toJson(this);
//  }
//}
