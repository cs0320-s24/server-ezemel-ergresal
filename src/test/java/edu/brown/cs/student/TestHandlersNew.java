package edu.brown.cs.student;

import static org.testng.AssertJUnit.assertEquals;

import edu.brown.cs.student.Broadband.BroadbandHandler;
import edu.brown.cs.student.Broadband.Datasources.StateCache;
import edu.brown.cs.student.Server.CSVHandling.LoadCSVHandler;
import edu.brown.cs.student.Server.CSVHandling.SearchCSVHandler;
import edu.brown.cs.student.Server.CSVHandling.SharedData;
import edu.brown.cs.student.Server.CSVHandling.ViewCSVHandler;
import edu.brown.cs.student.Server.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;
public class TestHandlersNew {

  private SharedData sharedData;
  private Server server;

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() {
    startServer(new SharedData(new ArrayList<>(), new ArrayList<>()));
  }

  public void startServer(SharedData sd) {
    this.sharedData = sd;

    LoadCSVHandler loadCSVHandler = new LoadCSVHandler(sharedData);

    Spark.get("loadcsv", loadCSVHandler);
    Spark.get("viewcsv", new ViewCSVHandler(sharedData));
    Spark.get("searchcsv", new SearchCSVHandler(sharedData));
    Spark.get("broadband", new BroadbandHandler(new StateCache()));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    this.server = null;
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * testing whether handlers throw errors or not
   *
   * @throws IOException
   */
  @Test
  public void testLoadHandler() throws IOException {
    HttpURLConnection clientConnectionLoad =
        tryRequest("loadcsv?filename=/stars/ten-star.csv&columnheaders=true");
    // loading csv
    assertEquals(200, clientConnectionLoad.getResponseCode());
    InputStream inputStream = clientConnectionLoad.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String output = reader.readLine();
    reader.close();
    inputStream.close();
    assertEquals(output, "/stars/ten-star.csv loaded successfully!");
    // invalid file name
    HttpURLConnection clientConnectionLoadInvalidFileName = tryRequest("loadcsv?filename=/star");
    assertEquals(200, clientConnectionLoadInvalidFileName.getResponseCode());
    InputStream inputStream2 = clientConnectionLoadInvalidFileName.getInputStream();
    BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
    String output2 = reader2.readLine();
    reader2.close();
    inputStream2.close();
    assertEquals(
        output2,
        "{\"response_type\":\"Error: Specified file not found in the protected data directory.\"}");
    // new data set, ri_income
    HttpURLConnection clientConnectionRI = tryRequest("loadcsv?filename=/census/ri_income.csv");
    assertEquals(200, clientConnectionRI.getResponseCode());
    InputStream inputStream3 = clientConnectionRI.getInputStream();
    BufferedReader reader3 = new BufferedReader(new InputStreamReader(inputStream3));
    String output3 = reader3.readLine();
    reader3.close();
    inputStream3.close();
    assertEquals(output3, "/census/ri_income.csv loaded successfully!");
    // no parameters, no file name
    HttpURLConnection clientConnectionNoFile = tryRequest("loadcsv");
    assertEquals(200, clientConnectionNoFile.getResponseCode());
    InputStream inputStream4 = clientConnectionNoFile.getInputStream();
    BufferedReader reader4 = new BufferedReader(new InputStreamReader(inputStream4));
    String output4 = reader4.readLine();
    reader4.close();
    inputStream4.close();
    assertEquals(output4, "{error_bad_request=no file name specified}");
  }

  @Test
  public void testViewSearchHandler() throws IOException {
    HttpURLConnection clientConnectionLoader = tryRequest(
        "loadcsv?filename=/stars/ten-star.csv&columnheaders=true");

        tryRequest("loadcsv?filename=/stars/ten-star.csv&columnheaders=true");
    assertEquals(200, clientConnectionLoader.getResponseCode());
    // viewing csv
    HttpURLConnection clientConnectionFile = tryRequest("viewcsv");
    assertEquals(200, clientConnectionFile.getResponseCode());
    InputStream inputStream4 = clientConnectionFile.getInputStream();
    BufferedReader reader4 = new BufferedReader(new InputStreamReader(inputStream4));
    String output4 = reader4.readLine();
    reader4.close();
    inputStream4.close();
    assertEquals(output4,
        "{\"columnHeaders\":[\"StarID\",\"ProperName\",\"X\",\"Y\",\"Z\"],\"data\":[[\"0\",\"Sol\",\"0\",\"0\",\"0\"],[\"1\",\"\",\"282.43485\",\"0.00449\",\"5.36884\"],[\"2\",\"\",\"43.04329\",\"0.00285\",\"-15.24144\"],[\"3\",\"\",\"277.11358\",\"0.02422\",\"223.27753\"],[\"3759\",\"96 G. Psc\",\"7.26388\",\"1.55643\",\"0.68697\"],[\"70667\",\"Proxima Centauri\",\"-0.47175\",\"-0.36132\",\"-1.15037\"],[\"71454\",\"Rigel Kentaurus B\",\"-0.50359\",\"-0.42128\",\"-1.1767\"],[\"71457\",\"Rigel Kentaurus A\",\"-0.50362\",\"-0.42139\",\"-1.17665\"],[\"87666\",\"Barnard's Star\",\"-0.01729\",\"-1.81533\",\"0.14824\"],[\"118721\",\"\",\"-2.28262\",\"0.64697\",\"0.29354\"]]}");

    HttpURLConnection clientConnectionSearch = tryRequest("searchcsv?object=Sol");
    assertEquals(200, clientConnectionSearch.getResponseCode());
    InputStream inputStream = clientConnectionSearch.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String output = reader.readLine();
    reader.close();
    inputStream.close();
    assertEquals(
        output,
        "{\"searchedObject\":\"Sol\",\"responseMap\":{\"found rows\":[[\"0\",\"Sol\",\"0\",\"0\",\"0\"]],\"response_type\":\"'Sol' found in row 0: [0, Sol, 0, 0, 0]\"}}");

    // searching object, not found
    HttpURLConnection clientConnectionSearch2 = tryRequest("searchcsv?object=NULL");
    assertEquals(200, clientConnectionSearch2.getResponseCode());
    InputStream inputStream2 = clientConnectionSearch2.getInputStream();
    BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
    String output2 = reader2.readLine();
    reader2.close();
    inputStream2.close();
    assertEquals(output2, "{\"searchedObject\":\"NULL\",\"responseMap\":{\"found rows\":[],\"response_type\":\"'NULL' not found\"}}");
    // searching object, no parameters
    HttpURLConnection clientConnectionSearch3 = tryRequest("searchcsv?");
    assertEquals(200, clientConnectionSearch3.getResponseCode());
    InputStream inputStream23 = clientConnectionSearch3.getInputStream();
    BufferedReader reader23 = new BufferedReader(new InputStreamReader(inputStream23));
    String output23 = reader23.readLine();
    reader23.close();
    inputStream23.close();
    assertEquals(output23,"{\"searchedObject\":\"null\",\"responseMap\":{}}");
        // searching object, column out of bounds / invalid
    HttpURLConnection clientConnectionSearch4 = tryRequest("searchcsv?object=Sol&column=10");
    assertEquals(200, clientConnectionSearch4.getResponseCode());
    InputStream inputStream234 = clientConnectionSearch4.getInputStream();
    BufferedReader reader234 = new BufferedReader(new InputStreamReader(inputStream234));
    String output234 = reader234.readLine();
    reader234.close();
    inputStream234.close();
    assertEquals(output234,"{\"searchedObject\":\"Sol\",\"responseMap\":{\"found rows\":[],\"response_type\":\"Column 10 doesn't exist. Available columns are: [StarID, ProperName, X, Y, Z]\"}}");
    // searching object, column in bounds, found
    HttpURLConnection clientConnectionSearch5 = tryRequest("searchcsv?object=Sol&column=1");
    assertEquals(200, clientConnectionSearch5.getResponseCode());
    InputStream inputStream22 = clientConnectionSearch5.getInputStream();
    BufferedReader reader22 = new BufferedReader(new InputStreamReader(inputStream22));
    String output22 = reader22.readLine();
    reader22.close();
    inputStream22.close();
    assertEquals(output22,"{\"searchedObject\":\"Sol\",\"responseMap\":{\"found rows\":[[\"0\",\"Sol\",\"0\",\"0\",\"0\"]],\"response_type\":\"'Sol' found in row 0: [0, Sol, 0, 0, 0]\"}}");
    // searching object, column in bounds, not found
    HttpURLConnection clientConnectionSearch6 = tryRequest("searchcsv?object=Sol&column=0");
    assertEquals(200, clientConnectionSearch6.getResponseCode());
    InputStream inputStream21 = clientConnectionSearch6.getInputStream();
    BufferedReader reader21 = new BufferedReader(new InputStreamReader(inputStream21));
    String output21 = reader21.readLine();
    reader21.close();
    inputStream21.close();
    assertEquals(output21,"{\"searchedObject\":\"Sol\",\"responseMap\":{\"found rows\":[],\"response_type\":\"'Sol' not found\"}}");
  }

  @Test
  public void testBroadbandHandler() throws IOException {
    // bad county
    HttpURLConnection clientBroadband = tryRequest("broadband?state=new+york&county=null");
    assertEquals(200, clientBroadband.getResponseCode());
    InputStream inputStream2 = clientBroadband.getInputStream();
    BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
    String output2 = reader2.readLine();
    reader2.close();
    inputStream2.close();
    assertEquals(output2,"NoBroadbandDataStateResponse[invalid_state=state not found, state=new york, responseMap={result=error_datasource}]");
        //    bad state
    HttpURLConnection clientBroadband2 =
        tryRequest("broadband?state=null&county=westchester+county");
    assertEquals(200, clientBroadband2.getResponseCode());
    InputStream inputStream3 = clientBroadband2.getInputStream();
    BufferedReader reader3 = new BufferedReader(new InputStreamReader(inputStream3));
    String output3 = reader3.readLine();
    reader3.close();
    inputStream3.close();
    assertEquals(output3,"NoBroadbandDataStateResponse[invalid_state=state not found, state=null, responseMap={result=error_datasource}]");
        // neither state nor county
    HttpURLConnection clientBroadband3 = tryRequest("broadband");
    assertEquals(200, clientBroadband3.getResponseCode());

    // no county
    HttpURLConnection clientBroadband4 = tryRequest("broadband?state=new+york");
    assertEquals(200, clientBroadband4.getResponseCode());

    // no state
    HttpURLConnection clientBroadband5 = tryRequest("broadband?county=westchester+county");
    assertEquals(200, clientBroadband5.getResponseCode());
    InputStream inputStream5 = clientBroadband5.getInputStream();
    BufferedReader reader5 = new BufferedReader(new InputStreamReader(inputStream5));
    String output5 = reader5.readLine();
    reader5.close();
    inputStream5.close();
    assertEquals(output5,
        "IncorrectParametersResponse[error=Required parameters: county, state, responseMap={result=error_bad_request, current_params=[county]}]");
    // good state
    HttpURLConnection clientBroadband6 = tryRequest(
        "broadband?county=westchester+county&state=New+York");
    assertEquals(200, clientBroadband5.getResponseCode());
    InputStream inputStream = clientBroadband6.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String output = reader.readLine();
    reader.close();
    inputStream.close();
    assertEquals(
        output,
        "NoBroadbandDataStateResponse[invalid_state=state not found, state=new york, responseMap={result=error_datasource}]");
  }
}
