package edu.brown.cs.student.Broadband;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * sends back the broadband data from the ACS Census Data API key : key:
 * 51d2ea8997215acdf626ff79e2cb74c9bc4a56cc
 *
 * parameters: state and county... Broadband will error with the correct error response if an incorrect
 * - county or state is inputted into either of these fields.
 * - The way this function works is by first creating a map of state names to state codes. This allows
 * - the method to parse the statename parameter into a stsatecode to be input into the API of
 * - census data.
 *   - Then, using a caching algorithm, the county queries are parsed. Since there are so many
 *   - counties in the country, this helps repeat searches be more time efficient. There are also
 *   - fields in this caching algorithm wherein a developer may specify the
 *   - maxSize, which is the maximum number of entries in the cache
 *     and minutesToEvict - time in minutes before an entry is evicted
 */
public class BroadbandHandler implements Route {


//  private Map<String, Map<String, BroadbandResponse>> parsedStates = new HashMap<>(); // list of data for each state weve parsed
//   maps state to map of county codes to broadband response
  private Datasource source;

  public BroadbandHandler(Datasource source) {
    this.source = source;
//            new StateCache();
  }

//  public BroadbandHandler(int maxEntries, int minutesToEvict) {
//    this.source = new StateCache(maxEntries, minutesToEvict);
//  }
//
//  public BroadbandHandler(int maxEntries) {
//    this.source = new StateCache(maxEntries);
//  }

  /**
   * This handle method needs to be filled by any class implementing Route. When the path set in
   * edu.brown.cs.examples.moshiExample.server.Server gets accessed, it will fire the handle
   * method.
   *
   * <p>NOTE: beware this "return Object" and "throws Exception" idiom. We need to follow it
   * because the library uses it, but in general this lowers the protection of the type system.
   *
   * @param request  The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  @Override
  public Object handle(Request request, Response response)
      throws URISyntaxException, IOException, InterruptedException {
    // If you are interested in how parameters are received, try commenting out and
    // printing these lines! Notice that requesting a specific parameter requires that parameter
    // to be fulfilled.
    // If you specify a queryParam, you can access it by appending ?parameterName=name to the
    // endpoint
    Map<String, Object> responseMap = new HashMap<>();
    // Creates a hashmap to store the results of the request

    if (request.queryParams().isEmpty() || !request.queryParams().contains("county")
            || !request.queryParams().contains("state")) {
      responseMap.put("current_params", request.queryParams());
      responseMap.put("result", "error_bad_request");
      return new IncorrectParametersResponse("Required parameters: county, state", responseMap);
    }
    String county = request.queryParams("county").toLowerCase();
    String state = request.queryParams("state").toLowerCase();

//    try {
//      if (this.stateCodes.isEmpty()) {
//        fillStateCodeMap();
//      }
//      if (this.stateCodes.get(state) == null) {
//        responseMap.put("result", "error_datasource");
//        return new NoBroadbandDataStateResponse(state, responseMap);
//      }
//      String stateCode = this.stateCodes.get(state);
//      if (county.equals("")) {
//        responseMap.put("result", "error_datasource");
//        return new NoBroadbandDataCountyResponse(county, responseMap);
//      }
      return this.source.query(state, county, responseMap);
      //responseMap.put("response", this.source.query(state, county).serialize());
//      responseMap.put("result", "success");
//      return responseMap;
//    } catch (Exception e) {
//      responseMap.put("result", "error_datasource");
////      System.out.println(e.getMessage());
//      return new NoBroadbandDataCountyResponse(county, responseMap).serialize();
//    }
  }



}
