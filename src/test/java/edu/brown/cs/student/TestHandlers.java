//package edu.brown.cs.student;
//
//import static org.testng.AssertJUnit.assertEquals;
//
//import com.squareup.moshi.JsonAdapter;
//import com.squareup.moshi.Moshi;
//import com.squareup.moshi.Types;
//import edu.brown.cs.student.Broadband.BroadbandHandler;
//import edu.brown.cs.student.Broadband.Response;
//import edu.brown.cs.student.server.LoadCSVHandler;
//import edu.brown.cs.student.server.SearchCSVHandler;
//import edu.brown.cs.student.server.Server;
//import edu.brown.cs.student.server.ViewCSVHandler;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.Buffer;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.junit.Before;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import spark.Request;
//import spark.Spark;
//
//public class TestHandlers {
//
//  private final JsonAdapter<Map<String, Object>> adapter;
//  private SharedData sharedData;
//  private Server server;
//  private static int port = 3232;
//
//  public TestHandlers() {
//    Moshi moshi = new Moshi.Builder().build();
//    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
//    adapter = moshi.adapter(type);
//  }
//
////  @BeforeAll
////  public static void setup_before_everything() {
////    Spark.port(1025);
////    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
////  }
//
//  @Before
//  public void setup() {
//    this.server = new Server(sharedData);
//  }
//
//  @AfterEach
//  public void teardown() {
//    // Gracefully stop Spark listening on both endpoints after each test
////    Spark.unmap("loadcsv");
////    Spark.unmap("searchcsv");
////    Spark.unmap("viewcsv");
////    Spark.unmap("broadband");
//    this.server = null;
//    Spark.awaitStop(); // don't proceed until the server is stopped
//  }
//
//  //  @Test
////  public void testViewCSVHandlerSuccess() throws IOException {
////    Request mockRequest = mock(Request.class);
////    Response mockResponse = mock(Response.class);
////
////    // Define behavior for the mock objects
////    when(mockRequest.queryParams("param")).thenReturn("value");
////
////    // Invoke your server handler method
////    new ViewCSVHandler(server.getSharedData()).handle(mockRequest, mockResponse);
////
////    // Verify the behavior of the handler
////    assertEquals(mockResponse, 200);
////    assertEquals(mockResponse.serialize(), "Expected Response Body");
////  }
//  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
//    URL requestURL = new URL("http://localhost:+" + port + "/" + apiCall);
//    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
//
//    clientConnection.setRequestMethod("GET");
//
//    clientConnection.connect();
//    return clientConnection;
//  }
//
//  /**
//   * testing whether handlers throw errors or not
//   *
//   * @throws IOException
//   */
//  @Test
//  public void testLoadHandler() throws IOException {
//    HttpURLConnection clientConnectionLoad = tryRequest(
//        "loadcsv?filename=/stars/ten-star.csv&columnheaders=true");
////loading csv
//    assertEquals(200, clientConnectionLoad.getResponseCode());
//    InputStream inputStream = clientConnectionLoad.getInputStream();
//    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//    String output = reader.readLine();
//    reader.close();
//    inputStream.close();
//    assertEquals(output, "/stars/ten-star.csv loaded successfully!");
//    // invalid file name
//    HttpURLConnection clientConnectionLoadInvalidFileName = tryRequest(
//        "loadcsv?filename=/star");
//    assertEquals(200, clientConnectionLoadInvalidFileName.getResponseCode());
//    InputStream inputStream2 = clientConnectionLoadInvalidFileName.getInputStream();
//    BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
//    String output2 = reader2.readLine();
//    reader2.close();
//    inputStream2.close();
//    assertEquals(output2,
//        "{\"response_type\":\"Error: Specified file not found in the protected data directory.\"}");
//// new data set, ri_income
//    HttpURLConnection clientConnectionRI = tryRequest(
//        "loadcsv?filename=/census/ri_income.csv");
//    assertEquals(200, clientConnectionRI.getResponseCode());
//    InputStream inputStream3 = clientConnectionRI.getInputStream();
//    BufferedReader reader3 = new BufferedReader(new InputStreamReader(inputStream3));
//    String output3 = reader3.readLine();
//    reader3.close();
//    inputStream3.close();
//    assertEquals(output3, "/census/ri_income.csv loaded successfully!");
//// no parameters, no file name
//    HttpURLConnection clientConnectionNoFile = tryRequest(
//        "loadcsv");
//    assertEquals(200, clientConnectionNoFile.getResponseCode());
//    InputStream inputStream4 = clientConnectionNoFile.getInputStream();
//    BufferedReader reader4 = new BufferedReader(new InputStreamReader(inputStream4));
//    String output4 = reader4.readLine();
//    reader4.close();
//    inputStream4.close();
//    assertEquals(output4, "{error_bad_request=no file name specified}");
//  }
//
//  @Test
//  public void testViewHandler() throws IOException {
//// viewing csv
//    HttpURLConnection clientConnectionNoFile = tryRequest(
//        "viewcsv");
//    assertEquals(200, clientConnectionNoFile.getResponseCode());
//    InputStream inputStream4 = clientConnectionNoFile.getInputStream();
//    BufferedReader reader4 = new BufferedReader(new InputStreamReader(inputStream4));
//    String output4 = reader4.readLine();
//    reader4.close();
//    inputStream4.close();
//    assertEquals(output4,
//        "{\"columnHeaders\":[\"StarID\",\"ProperName\",\"X\",\"Y\",\"Z\"],\"data\":[[\"0\",\"Sol\",\"0\",\"0\",\"0\"],[\"1\",\"\",\"282.43485\",\"0.00449\",\"5.36884\"],[\"2\",\"\",\"43.04329\",\"0.00285\",\"-15.24144\"],[\"3\",\"\",\"277.11358\",\"0.02422\",\"223.27753\"],[\"3759\",\"96 G. Psc\",\"7.26388\",\"1.55643\",\"0.68697\"],[\"70667\",\"Proxima Centauri\",\"-0.47175\",\"-0.36132\",\"-1.15037\"],[\"71454\",\"Rigel Kentaurus B\",\"-0.50359\",\"-0.42128\",\"-1.1767\"],[\"71457\",\"Rigel Kentaurus A\",\"-0.50362\",\"-0.42139\",\"-1.17665\"],[\"87666\",\"Barnard's Star\",\"-0.01729\",\"-1.81533\",\"0.14824\"],[\"118721\",\"\",\"-2.28262\",\"0.64697\",\"0.29354\"]]}> but was:</stars/ten-star.csv loaded successfully");
//  }
//
//  @Test
//  public void testSearchHandler() throws IOException {
//// searching object, found
//    HttpURLConnection clientConnectionSearch = tryRequest("searchcsv?object=Sol");
//    assertEquals(200, clientConnectionSearch.getResponseCode());
//// searching object, not found
//    HttpURLConnection clientConnectionSearch2 = tryRequest("searchcsv?object=NULL");
//    assertEquals(200, clientConnectionSearch2.getResponseCode());
//// searching object, no parameters
//    HttpURLConnection clientConnectionSearch3 = tryRequest("searchcsv?");
//    assertEquals(200, clientConnectionSearch3.getResponseCode());
//// searching object, column out of bounds / invalid
//    HttpURLConnection clientConnectionSearch4 = tryRequest("searchcsv?object=Sol&column=10");
//    assertEquals(200, clientConnectionSearch4.getResponseCode());
//// searching object, column in bounds, found
//    HttpURLConnection clientConnectionSearch5 = tryRequest("searchcsv?object=Sol&column=1");
//    assertEquals(200, clientConnectionSearch5.getResponseCode());
//// searching object, column in bounds, not found
//    HttpURLConnection clientConnectionSearch6 = tryRequest("searchcsv?object=Sol&column=0");
//    assertEquals(200, clientConnectionSearch6.getResponseCode());
//
////    InputStream inputStream = clientConnectionView.getInputStream();
////    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
////    String output = reader.readLine();
////    reader.close();
////    inputStream.close();
////    Moshi moshi = new Moshi.Builder().build();
////    // Initializes an adapter to an Activity class then uses it to parse the JSON.
////    JsonAdapter<String> adapter = moshi.adapter(String.class);
////    String activity = adapter.fromJson(output);
//  }
//
//  @Test
//  public void testBroadbandHandler() throws IOException {
//    // bad county
//    HttpURLConnection clientBroadband = tryRequest(
//        "broadband?state=new+york&county=null");
//    assertEquals(200, clientBroadband.getResponseCode());
//
////    bad state
//    HttpURLConnection clientBroadband2 = tryRequest(
//        "broadband?state=null&county=westchester+county");
//    assertEquals(200, clientBroadband2.getResponseCode());
//
//// neither state nor county
//    HttpURLConnection clientBroadband3 = tryRequest(
//        "broadband");
//    assertEquals(200, clientBroadband3.getResponseCode());
//
//// no county
//    HttpURLConnection clientBroadband4 = tryRequest(
//        "broadband?state=new+york");
//    assertEquals(200, clientBroadband4.getResponseCode());
//
//// no state
//    HttpURLConnection clientBroadband5 = tryRequest(
//        "broadband?county=westchester+county");
//    assertEquals(200, clientBroadband5.getResponseCode());
//
//
//  }
//}