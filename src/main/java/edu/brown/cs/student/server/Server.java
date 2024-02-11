package edu.brown.cs.student.server;

import static spark.Spark.after;

import java.util.List;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various
 * handlers (2).
 *
 * <p>Notice that the OrderHandler takes in a state (menu) that can be shared if we extended the
 * restaurant They need to share state (a menu). This would be a great opportunity to use dependency
 * injection. If we needed more endpoints, more functionality classes, etc. we could make sure they
 * all had the same shared state.
 */
public class Server {

  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    LoadCSVHandler loadCSVHandler = new LoadCSVHandler();

    Spark.get("loadcsv", loadCSVHandler);

    Spark.init();
    Spark.awaitInitialization();

    List<List<String>> data = loadCSVHandler.getCsvData();
    List<String> columnHeaders = loadCSVHandler.getColumnHeaders();
    //TODO: rn the data and columnHeaders fields are not being initialized correctly, they are null always
    Spark.get("viewcsv", new ViewCSVHandler(data, columnHeaders));
    Spark.get("searchcsv", new SearchCSVHandler(data, columnHeaders));

    System.out.println("Server started at http://localhost:" + port);

  }
}
