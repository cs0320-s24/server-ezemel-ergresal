package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.CSVNotLoadedResponse;
import edu.brown.cs.student.SharedData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for loading the csv. Simply loads data content into
 */
public class ViewCSVHandler implements Route {


private SharedData sd;
  public ViewCSVHandler(SharedData sharedData) {
    this.sd = sharedData;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    if(this.sd.isEmpty()) {
      return new CSVNotLoadedResponse(responseMap).serialize();
    }
//    responseMap.put("data", this.data);
//    responseMap.put("columnHeaders", this.ch);
    return new ParsedData(sd.getColumnHeaders(), sd.getCsvData()).serialize();
  }

  public record ParsedData(List<String> columnHeaders, List<List<String>> data) {

    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ParsedData> adapter = moshi.adapter(ParsedData.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        throw e;
      }
    }
  }
}