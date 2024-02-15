package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.Parser.CreatorFromRow;
import edu.brown.cs.student.Parser.ParseCSV;
import edu.brown.cs.student.Parser.StringCreatorFromRow;
import edu.brown.cs.student.SharedData;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

  private SharedData sharedData;

  public LoadCSVHandler(SharedData sharedData) {
    this.sharedData = sharedData;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filename = request.queryParams("filename");
    String columnHeaders = request.queryParams("columnheaders");
    Boolean columnHeadersQuery = false;
    if (columnHeaders != null) {
      if (columnHeaders.equals("true")) { //default is false??
        columnHeadersQuery = true;
      }
    }
    String currentPath = new java.io.File(".").getCanonicalPath();
    filename = currentPath + "/data" + filename;
    this.responseMap = new HashMap<>();
    ParseCSV<String> fileReader;
    CreatorFromRow<String> myCreator = new StringCreatorFromRow();
    try { //maybe we should make parameter for columnHeaders boolean, with default = 1 or something
      fileReader = new ParseCSV<String>(new FileReader(filename), myCreator, columnHeadersQuery);
    } catch (FileNotFoundException f) {
      return new FileNotFoundResponse().serialize();

    } catch (IOException i) {
      return "Error with reader closing/parsing file";
    }
    this.sharedData.put(fileReader.getParsedData(), fileReader.getColumnHeaders());
//    this.csvData = fileReader.getParsedData();
//    this.columnHeaders = fileReader.getColumnHeaders();
//    responseMap.put("data", this.csvData);
//    responseMap.put("columnHeaders", this.columnHeaders);
//    responseMap.put("data", this.csvData);
    return request.queryParams("filename") + " loaded successfully!";
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
   * Response object to send if file is not found
   */
  public record FileNotFoundResponse(String response_type) {

    public FileNotFoundResponse() {
      this("Error: Specified file not found in the protected data directory.");
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FileNotFoundResponse.class).toJson(this);
    }
  }

}

