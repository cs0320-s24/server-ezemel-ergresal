package edu.brown.cs.student.Broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * sends back the broadband data from the ACS Census Data API key :
 * 51d2ea8997215acdf626ff79e2cb74c9bc4a56cc
 */
public class BroadbandHandler implements Route {

  private Map<String, String> stateCodes = new HashMap<>();
  private List<List<String>> parsedStates = new ArrayList<>(); // list of data for each state weve parsed
//    state and county query parameters

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
    String county = request.queryParams("county");
    String state = request.queryParams("state");

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    LocalDateTime currentDateTime = LocalDateTime.now();

    try {
      // Sends a request to the API and receives JSON back
      String placeJson = this.sendRequest(county, state);
      responseMap.put("date_time", currentDateTime);
      responseMap.put("county", county);
      responseMap.put("state", state);
      responseMap.put("place", placeJson);
      return responseMap;
    } catch (
        Exception e) { // if county, state are sting names, need to convert them to number codes
//      county = this.stateCodes.get(county.strip().toLowerCase());
      if (this.stateCodes.isEmpty()) {
        fillStateCodeMap();
      }
      state = this.stateCodes.get(state);
      county = fillAndFindCountyData(state, county);

      String placeJson = this.sendRequest(county, state);
      responseMap.put("date_time", currentDateTime);
      responseMap.put("county", county);
      responseMap.put("state code", state);
      responseMap.put("place", placeJson);
      return responseMap;
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
      StringBuilder response = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          // Assuming the first part is the state name and the second part is the state code
          String stateName = parts[0].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");;
          String stateCode = parts[1].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");;
          // Add to stateCodes map
          stateCodes.put(stateName.toLowerCase(), stateCode);
        }
      }
      reader.close();
    } catch (Exception e) {
      return;
    }
  }
  private String fillAndFindCountyData(String stateCode, String county_name) {
    try {
      URL requestURL = new URL(
          "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"+stateCode);
      HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      reader.readLine(); // dont want to parse the first row (headers)
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length >= 4) {
          String countyName = parts[0].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          String stateName = parts[1].replaceAll("\"", "").trim();
          String countyPercentage = parts[2].replaceAll("\"", "").trim();
          String stateCdoe = parts[3].replaceAll("\"", "").trim();

          String countyCode = parts[4].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          parsedStates.add(List.of(countyName, stateName, countyPercentage, stateCode, countyCode));
          if (countyName.toLowerCase().equals(county_name)){
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
