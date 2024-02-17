package edu.brown.cs.student.Broadband.Responses;

import com.squareup.moshi.Moshi;
import java.util.Map;

/** Response object to send if state param is invalid */
public record NoBroadbandDataStateResponse(
    String invalid_state, String state, Map<String, Object> responseMap) implements Response {

    public NoBroadbandDataStateResponse(String state, Map<String, Object> responseMap) {
      this("state not found", state, responseMap);
    }

  /**
   * @return this response, serialized as Json
   */
  @Override
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(NoBroadbandDataStateResponse.class).toJson(this);
  }
}
