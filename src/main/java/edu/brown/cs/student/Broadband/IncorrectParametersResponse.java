package edu.brown.cs.student.Broadband;

import com.squareup.moshi.Moshi;

import java.util.Map;
import java.util.Set;

public record IncorrectParametersResponse(String error, Map<String, Object> responseMap) {

  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(edu.brown.cs.student.Broadband.IncorrectParametersResponse.class).toJson(this);
  }
}
//  /**
//   * Response object to send if state param is invalid
//   */
//  public record NoBroadbandDataStateResponse(String invalid_state, String state, Map<String, Object> responseMap) {
//
//
//    public NoBroadbandDataStateResponse(String state, Map<String, Object> responseMap) {
//      this("State not found", state, responseMap);
//    }
//
//    /**
//     * @return this response, serialized as Json
//     */
//    public String serialize() {
//      Moshi moshi = new Moshi.Builder().build();
//      return moshi.adapter(edu.brown.cs.student.Broadband.NoBroadbandDataStateResponse.class).toJson(this);
//    }
//  }
//}
