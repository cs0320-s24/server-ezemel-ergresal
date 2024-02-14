package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.Parser.CreatorFromRow;
import edu.brown.cs.student.Parser.ParseCSV;
import edu.brown.cs.student.Parser.StringCreatorFromRow;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for loading the csv. Simply loads data content into
 */
public class LoadCSVHandler implements Route {

  static Map<String, Object> responseMap;
  private List<List<String>> csvData;
  private List<String> columnHeaders;

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filename = request.queryParams("filename");
    String columnHeaders = request.queryParams("columnHeaders");
    Boolean columnHeadersQuery = true;
    if (columnHeaders != null) {
      if (columnHeaders.equals("false")) { //default is false??
        columnHeadersQuery = false;
      }
    }
    String currentPath = new java.io.File(".").getCanonicalPath();
    filename = currentPath + "/data" + filename;
    responseMap = new HashMap<>();
    ParseCSV<String> fileReader;
    CreatorFromRow<String> myCreator = new StringCreatorFromRow();
    try { //maybe we should make parameter for columnHeaders boolean, with default = 1 or something
      fileReader = new ParseCSV<String>(new FileReader(filename), myCreator, columnHeadersQuery);
    } catch (FileNotFoundException f) {
      return new FileNotFoundResponse().serialize();
    }
    this.csvData = fileReader.getParsedData();
    this.columnHeaders = fileReader.getColumnHeaders();
    responseMap.put("data", csvData);
    responseMap.put("columnHeaders", columnHeaders);
    return new FileFoundResponse(responseMap).serialize();
  }

  /**
   * Response object to send, containing a soup with certain ingredients in it
   */
  public record FileFoundResponse(String response_type, Map<String, Object> responseMap) {

    public FileFoundResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<FileFoundResponse> adapter = moshi.adapter(FileFoundResponse.class);
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
  public record FileNotFoundResponse(String response_type) {

    public FileNotFoundResponse() {
      this("error");
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FileNotFoundResponse.class).toJson(this);
    }
  }

  public List<List<String>> getCsvData() {
    return csvData;
  }

  public List<String> getColumnHeaders() {
    return columnHeaders;
  }
}

