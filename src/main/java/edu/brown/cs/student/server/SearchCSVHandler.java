package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.Searcher.SearchCSV;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for loading the csv. Simply loads data content into
 */
public class SearchCSVHandler<T> implements Route {

  private List<List<String>> data;
  private List<String> ch;

  public SearchCSVHandler(List<List<String>> data, List<String> columnHeaders) {
    this.data = data;
    this.ch = columnHeaders;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    SearchCSV<String> searcherData = new SearchCSV<>();
    String object = request.queryParams("object");
    String column = request.queryParams("column");
    if (object == null){
      return new ObjectNotFoundResponse(responseMap).serialize();
    }
    Boolean columnHeaders = !(this.ch.isEmpty());
    List<List<String>> foundRows = searcherData.startSearcher(this.data, this.ch, object, columnHeaders, column);

    responseMap.put("found rows", foundRows);
    return new ObjectFoundResponse(responseMap).serialize();
  }

  public record ObjectFoundResponse(String response_type, Map<String, Object> responseMap) {

    public ObjectFoundResponse(Map<String, Object> responseMap) {
      this("Object found", responseMap);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchCSVHandler.ObjectFoundResponse> adapter = moshi.adapter(
            SearchCSVHandler.ObjectFoundResponse.class);
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

  /**
   * Response object to send if someone requested soup from an empty Menu
   */
  public record ObjectNotFoundResponse(String response_type) {

    public ObjectNotFoundResponse(Map<String, Object> responseMap) {
      this("Object not found");
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SearchCSVHandler.ObjectNotFoundResponse.class).toJson(this);
    }
  }


}