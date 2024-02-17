package edu.brown.cs.student.Server.CSVHandling;

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
 * Handler for searching the csv. parameters: object, the object which will be searched in the file
 * - column: if the columnheaders field is listed as true, then this column will be parsed to - find
 * the data object provided. This may be either a column index or a column object name. - This will
 * fail with the correct error - message if there is no CSV data loaded into the sharedData.
 */
public class SearchCSVHandler<T> implements Route {

  private SharedData sd;

  public SearchCSVHandler(SharedData sharedData) {
    this.sd = sharedData;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    SearchCSV<String> searcherData = new SearchCSV<>();
    if (this.sd.isEmpty()) {
      return new CSVNotLoadedResponse(responseMap).serialize();
    }
    String object = request.queryParams("object");
    String column = request.queryParams("column");
    if (object == null) {
      return new ObjectNotFoundResponse("null", responseMap).serialize();
    }
    Boolean columnHeaders = !(this.sd.getColumnHeaders().isEmpty());
    List<List<String>> foundRows =
        searcherData.getRowsFound(
            sd.getCsvData(), sd.getColumnHeaders(), object, columnHeaders, column);
    responseMap.put("found rows", foundRows);
    responseMap.put("response_type", searcherData.getResponse());
    if (foundRows.isEmpty()) {
      return new ObjectNotFoundResponse(object, responseMap).serialize();
    }
    return new ObjectFoundResponse(object, responseMap).serialize();
  }

  /**
   * This response is returned when the searched object is found in the dataset
   *
   * @param object_found
   * @param searchedObject
   * @param responseMap
   */
  public record ObjectFoundResponse(
      String object_found, String searchedObject, Map<String, Object> responseMap) {
    public ObjectFoundResponse(String searchedObject, Map<String, Object> responseMap) {
      this(null, searchedObject, responseMap);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchCSVHandler.ObjectFoundResponse> adapter =
            moshi.adapter(SearchCSVHandler.ObjectFoundResponse.class);
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

  /** Response object to send if someone requested soup from an empty Menu */
  public record ObjectNotFoundResponse(
      String object_not_found, String searchedObject, Map<String, Object> responseMap) {

    public ObjectNotFoundResponse(String searchedObject, Map<String, Object> responseMap) {
      this(null, searchedObject, responseMap);
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