package edu.brown.cs.student.Broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * sends back the broadband data from the ACS Census Data API key : key:
 * 51d2ea8997215acdf626ff79e2cb74c9bc4a56cc
 */
public class BroadbandHandler implements Route {

  private Map<String, String> stateCodes = new HashMap<>();
  private Map<String, Map<String, BroadbandResponse>> parsedStates = new HashMap<>(); // list of data for each state weve parsed
//   maps state to map of county codes to broadband response

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
//    Set<String> params = request.queryParams();
    Map<String, Object> responseMap = new HashMap<>();
    String county = request.queryParams("county");
    String state = request.queryParams("state");
    if (request.queryParams().isEmpty() || !request.queryParams().contains("county")
        || !request.queryParams().contains("state")) {
      request.queryParams().add("error_datasource");
      return new IncorrectParametersResponse("Required parameters: county, state",
          request.queryParams());
    }

    // Creates a hashmap to store the results of the request

    LocalDateTime currentDateTime = LocalDateTime.now();

    try {
      // Sends a request to the API and receives JSON back
//      String placeJson = this.sendRequest(county, state);
////      responseMap.put("date_time", currentDateTime);
////      responseMap.put("county", county);
////      responseMap.put("state", state);
////      responseMap.put("place", placeJson);
//
////      responseMap.put("date_time", this.parsedStates.get(county).getTime());
//////      responseMap.put("broadband response", this.parsedStates.get(county));
////      responseMap.put("county", this.parsedStates.get(county).getCountyName());
////      responseMap.put("state", this.parsedStates.get(county).getState());
//////      responseMap.put("place", placeJson);
////      responseMap.put("percentage broadband/high speed internet access",
////          this.parsedStates.get(county).getPercentageBroadband());
//
//      return placeJson;
//    } catch (
//        Exception e) { // if county, state are sting names, need to convert them to number codes
//      county = this.stateCodes.get(county.strip().toLowerCase());
      if (this.stateCodes.isEmpty()) {
        fillStateCodeMap();
      }
      state = this.stateCodes.get(state);

      responseMap.put("state", state);
      if (state == null) {
        responseMap.put("result", "error_datasource");
        return new NoBroadbandDataStateResponse(state, responseMap);
      }
      county = fillAndFindCountyData(state, county);
      responseMap.put("county", county);
      if (county.equals("")) {
        responseMap.put("result", "error_datasource");
        return new NoBroadbandDataCountyResponse(county, responseMap);
      }
//      String placeJson = this.sendRequest(county, state);
//      responseMap.put("date_time", this.parsedStates.get(county).getTime());
//      responseMap.put("timezone", this.parsedStates.get(county).getTimeZone());
////      responseMap.put("broadband response", this.parsedStates.get(county));
//      responseMap.put("county", this.parsedStates.get(county).getCountyName());
//      responseMap.put("state", this.parsedStates.get(county).getState());
////      responseMap.put("place", placeJson);
//      responseMap.put("percentage broadband/high speed internet access",
//          this.parsedStates.get(county).getPercentageBroadband());

      responseMap.put("response", this.parsedStates.get(state).get(county).serialize());
      responseMap.put("result", "success");
      return responseMap;
    } catch (Exception e) {
      responseMap.put("result", "error_datasource");
      return new NoBroadbandDataStateResponse(state, responseMap).serialize();
    }

//    catch (Exception e) {
//      e.printStackTrace();
//      // This is a relatively unhelpful exception message. An important part of this sprint will be
//      // in learning to debug correctly by creating your own informative error messages where Spark
//      // falls short.
//      responseMap.put("result", e);
//    }
//    return responseMap;
  }


  private String sendRequest(String county, String state)
      throws URISyntaxException, IOException, InterruptedException {
//    https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&
//     first neeed to convert county and state into number codes
//    URL requestURL = new URL("https", "api.census.gov",
//        http://localhost:3232/broadband?state=36&county=059

    String urlString = String.format(
        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:%s&in=state:%s",
        county, state);
    System.out.println(urlString);
    URI requestURI = new URI(urlString);

    // Build the HTTP request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(requestURI)
        .GET()
        .build();

    // Send the HTTP request and store the response
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println(response);
    System.out.println(response.body());

    // deserialize the JSON response if needed
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<List> adapter = moshi.adapter(List.class);
    List<List<String>> responseData = adapter.fromJson(response.body());
//    BroadbandResponse responseData = adapter.fromJson(response.body());

    return response.body();
//    return broadbandResponse.toString();
  }

  /**
   * Private helper method; throws IOException so different callers can handle differently if
   * needed. //TODO: change filenotfoundexception
   */
  private static HttpURLConnection connect(URL requestURL)
      throws FileNotFoundException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new FileNotFoundException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200) {
      throw new FileNotFoundException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    }
    return clientConnection;
  }

  private void fillStateCodeMap() {
    try {

      URL requestURL = new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          // Assuming the first part is the state name and the second part is the state code
          String stateName = parts[0].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          ;
          String stateCode = parts[1].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          ;
          // Add to stateCodes map
          parsedStates.put(stateCode, new HashMap<>());
          stateCodes.put(stateName.toLowerCase(), stateCode);
        }
      }
      reader.close();
    } catch (Exception e) {
    }
  }

  private String fillAndFindCountyData(String stateCode, String county_name) {
    try {
      URL requestURL = new URL(
          "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"
              + stateCode);
      HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      reader.readLine(); // don't want to parse the first row (headers)
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length >= 4) {
          String countyName = parts[0].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          String stateName = parts[1].replaceAll("\"", "").trim();
          String countyPercentage = parts[2].replaceAll("\"", "").trim();
//          String stateCodee = parts[3].replaceAll("\"", "").trim();

          String countyCode = parts[4].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          parsedStates.get(stateCode).put(countyCode,
             new BroadbandResponse(LocalDateTime.now().toString(), countyName, stateName,
                  countyPercentage));
//              List.of(countyName, stateName, countyPercentage, stateCode, countyCode));
          if (countyName.toLowerCase().equals(county_name)) {
//            correctCountyCode = countyCode;
            reader.close();
            return countyCode;
          }
        }
      }
      reader.close();
      return "";
    } catch (Exception e) {
      return "";
    }
  }

//  https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:45

}
