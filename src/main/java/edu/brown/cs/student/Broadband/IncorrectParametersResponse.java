package edu.brown.cs.student.Broadband;

import com.squareup.moshi.Moshi;
import java.util.Map;

public record IncorrectParametersResponse(String error, Map<String, Object> responseMap) {

  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(edu.brown.cs.student.Broadband.IncorrectParametersResponse.class).toJson(this);
  }
}