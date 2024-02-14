package edu.brown.cs.student.Broadband;

import static java.lang.FdLibm.Cbrt.F;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.CSVNotLoadedResponse;
import edu.brown.cs.student.Place.Place;
import edu.brown.cs.student.Place.PlaceAPIUtilities;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
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

//    state and county query parameters

  /**
   * This handle method needs to be filled by any class implementing Route. When the path set in
   * edu.brown.cs.examples.moshiExample.server.Server gets accessed, it will fire the handle
   * method.
   *
   * <p>NOTE: beware this "return Object" and "throws Exception" idiom. We need to follow it
   * because
   * the library uses it, but in general this lowers the protection of the type system.
   *
   * @param request  The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  @Override
  public Object handle(Request request, Response response) {
    // If you are interested in how parameters are received, try commenting out and
    // printing these lines! Notice that requesting a specific parameter requires that parameter
    // to be fulfilled.
    // If you specify a queryParam, you can access it by appending ?parameterName=name to the
    // endpoint
    // ex. http://localhost:3232/activity?participants=num
    Set<String> params = request.queryParams();
    String county = request.queryParams("county");
    String state = request.queryParams("state");

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Sends a request to the API and receives JSON back
      String placeJson = this.sendRequest(county, state);

      // Deserializes JSON into an Activity
      Place place = PlaceAPIUtilities.deserializePlace(placeJson);
      // Adds results to the responseMap
      responseMap.put("result", "success");
      responseMap.put("place", place);
      return responseMap;
    } catch (Exception e) {
      e.printStackTrace();
      // This is a relatively unhelpful exception message. An important part of this sprint will be
      // in learning to debug correctly by creating your own informative error messages where Spark
      // falls short.
      responseMap.put("result", "Exception");
    }
    return responseMap;
  }


  private String sendRequest(String county, String state)
      throws URISyntaxException, IOException, InterruptedException {
    // Build a request to this BoredAPI. Try out this link in your browser, what do you see?
    // TODO 1: Looking at the documentation, how can we add to the URI to query based
    // on participant number?
//    https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&
//     first neeed to convert county and state into number codes
    URL requestURL = new URL("https", "api.census.gov",
        "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:45");

    HttpURLConnection clientConnection = connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> BroadBandResponse =
        HttpClient.newBuilder()
            .build()
            .send(requestURL, HttpResponse.BodyHandlers.ofString());

    // What's the difference between these two lines? Why do we return the body? What is useful from
    // the raw response (hint: how can we use the status of response)?
    System.out.println(BroadBandResponse);
    System.out.println(BroadBandResponse.body());

    return BroadBandResponse.body();
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
}
