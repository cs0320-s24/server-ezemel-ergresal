package edu.brown.cs.student.Server.CSVHandling;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for viewing the csv. - parameters: there are no parameters for this function. This will
 * fail with the correct error - message if there is no CSV data loaded into the sharedData. - This
 * handler will return a json object in the server webpage which includes a data object ( a list of
 * list, which are essentially a list of rows, wherein each row has parsed data separated - by
 * commas, as is parsed in the loadcsv handler)
 */
public class ViewCSVHandler implements Route {

  private SharedData sd;

  public ViewCSVHandler(SharedData sharedData) {
    this.sd = sharedData;
  }

  /**
   * Handle method used to take care of calls to viewcsv, see above comment for particulars
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    if (this.sd.isEmpty()) {
      responseMap.put("response", "error_viewing");
      return new CSVNotLoadedResponse(responseMap).serialize();
    }
    responseMap.put("response", "success");
    return new ParsedData(sd.getColumnHeaders(), sd.getCsvData()).serialize();
  }

  /**
   * record to be returned on successful finding of data, data will be returned
   *
   * @param columnHeaders
   * @param data
   */
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
