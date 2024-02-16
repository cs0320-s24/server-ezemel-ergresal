package edu.brown.cs.student;

import com.squareup.moshi.Moshi;
import java.util.Map;

/** Response object to send if no csv loaded */
public record CSVNotLoadedResponse(String error_retrieving_data) {

  public CSVNotLoadedResponse(Map<String, Object> responseMap) {
    this("No CSV data loaded.");
  }

  /**
   * @return this response, serialized as Json
   */
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(CSVNotLoadedResponse.class).toJson(this);
  }
}
