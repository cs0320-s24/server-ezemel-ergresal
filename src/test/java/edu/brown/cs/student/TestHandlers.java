package edu.brown.cs.student;

import static org.testng.AssertJUnit.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.Broadband.BroadbandHandler;
import edu.brown.cs.student.server.LoadCSVHandler;
import edu.brown.cs.student.server.SearchCSVHandler;
import edu.brown.cs.student.server.Server;
import edu.brown.cs.student.server.ViewCSVHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestHandlers {

  private final JsonAdapter<Map<String, Object>> adapter;
  private SharedData sharedData;
  private Server server;
  private int port = 1025;

  public TestHandlers() {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

//  @BeforeAll
//  public static void setup_before_everything() {
//    Spark.port(1025);
//    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
//  }

  @Before
  public void setup() {
    server = new Server(new SharedData(new ArrayList<>(), new ArrayList<>()));
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
//    Spark.unmap("loadcsv");
//    Spark.unmap("searchcsv");
//    Spark.unmap("viewcsv");
    server = null;
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:3232/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testHandlers() throws IOException {
    HttpURLConnection clientConnectionLoad = tryRequest(
        "loadcsv?filename=/stars/ten-star.csv&columnheaders=true");
//loading csv
    assertEquals(200, clientConnectionLoad.getResponseCode());
// viewing csv
    HttpURLConnection clientConnectionView = tryRequest("viewcsv");
    assertEquals(200, clientConnectionView.getResponseCode());
// searching object, found
    HttpURLConnection clientConnectionSearch = tryRequest("searchcsv?object=Sol");
    assertEquals(200, clientConnectionSearch.getResponseCode());
// searching object, not found
    HttpURLConnection clientConnectionSearch2 = tryRequest("searchcsv?object=NULL");
    assertEquals(200, clientConnectionSearch2.getResponseCode());
// searching object, no parameters
    HttpURLConnection clientConnectionSearch3 = tryRequest("searchcsv?");
    assertEquals(200, clientConnectionSearch3.getResponseCode());
// searching object, column out of bounds / invalid
    HttpURLConnection clientConnectionSearch4 = tryRequest("searchcsv?object=Sol&column=10");
    assertEquals(200, clientConnectionSearch4.getResponseCode());
// searching object, column in bounds, found
    HttpURLConnection clientConnectionSearch5 = tryRequest("searchcsv?object=Sol&column=1");
    assertEquals(200, clientConnectionSearch5.getResponseCode());
// searching object, column in bounds, not found
    HttpURLConnection clientConnectionSearch6 = tryRequest("searchcsv?object=Sol&column=0");
    assertEquals(200, clientConnectionSearch6.getResponseCode());

//    InputStream inputStream = clientConnectionView.getInputStream();
//    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//    String output = reader.readLine();
//    reader.close();
//    inputStream.close();
//    Moshi moshi = new Moshi.Builder().build();
//    // Initializes an adapter to an Activity class then uses it to parse the JSON.
//    JsonAdapter<String> adapter = moshi.adapter(String.class);
//    String activity = adapter.fromJson(output);
  }

  @Test
  public void testBroadbandHandlers() throws IOException {
    // bad county
    HttpURLConnection clientBroadband = tryRequest(
        "broadband?state=new+york&county=null");
    assertEquals(200, clientBroadband.getResponseCode());

//    bad state
    HttpURLConnection clientBroadband2 = tryRequest(
        "broadband?state=null&county=westchester+county");
    assertEquals(200, clientBroadband2.getResponseCode());

// neither state nor county
    HttpURLConnection clientBroadband3 = tryRequest(
        "broadband");
    assertEquals(200, clientBroadband3.getResponseCode());

// no county
    HttpURLConnection clientBroadband4 = tryRequest(
        "broadband?state=new+york");
    assertEquals(200, clientBroadband4.getResponseCode());

// no state
    HttpURLConnection clientBroadband5 = tryRequest(
        "broadband?county=westchester+county");
    assertEquals(200, clientBroadband5.getResponseCode());


  }
}