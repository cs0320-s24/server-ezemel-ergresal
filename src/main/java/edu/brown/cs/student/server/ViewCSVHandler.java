package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for loading the csv. Simply loads data content into
 */
public class ViewCSVHandler implements Route {

  private List<List<String>> data;
  private List<String> ch;

  public ViewCSVHandler(List<List<String>> data, List<String> columnHeaders) {
    this.data = data;
    this.ch = columnHeaders;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
//    Map<String, Object> responseMap = new HashMap<>();
//    ;
//    responseMap.put("data", this.data);
//    responseMap.put("columnHeaders", this.ch);
    return this.ch + "\n" + this.data; //rn they are both null
  }

  public record parsedData(List<List<String>> data, List<String> columnHeaders) {

    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<parsedData> adapter = moshi.adapter(parsedData.class);
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