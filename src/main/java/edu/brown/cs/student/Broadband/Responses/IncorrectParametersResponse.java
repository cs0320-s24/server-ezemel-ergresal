package edu.brown.cs.student.Broadband.Responses;

import com.squareup.moshi.Moshi;
import java.util.Map;

/**
 * incorrect parameters response to be used when user doesn't input correct parameters
 *
 * @param error
 * @param responseMap
 */
public record IncorrectParametersResponse(String error, Map<String, Object> responseMap) {

  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi
        .adapter(edu.brown.cs.student.Broadband.Responses.IncorrectParametersResponse.class)
        .toJson(this);
  }
}
