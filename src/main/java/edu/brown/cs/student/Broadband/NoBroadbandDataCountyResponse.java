package edu.brown.cs.student.Broadband;
import com.squareup.moshi.Moshi;
import java.util.Map;
public record NoBroadbandDataCountyResponse(String invalid_state, String state, Map<String, Object> responseMap) implements Response {


    public NoBroadbandDataCountyResponse(String county, Map<String, Object> responseMap) {
      this("County not found", county, responseMap);
    }

    /**
     * @return this response, serialized as Json
     */
    @Override
    public String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(edu.brown.cs.student.Broadband.NoBroadbandDataCountyResponse.class).toJson(this);
    }
  }
