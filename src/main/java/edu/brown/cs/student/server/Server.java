package edu.brown.cs.student.server;

import static spark.Spark.after;

import edu.brown.cs.student.Broadband.BroadbandHandler;
import edu.brown.cs.student.Broadband.StateCache;
import edu.brown.cs.student.SharedData;
import java.util.ArrayList;
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

  private SharedData sharedData;

  /**
   * constructor for server class wherein main calls the server, so the sharedData
   * can act as a field
   * @param toUse
   */
  public Server(SharedData toUse) {
    this.sharedData = toUse;

    int port = 3232;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    LoadCSVHandler loadCSVHandler = new LoadCSVHandler(sharedData);

    Spark.get("loadcsv", loadCSVHandler);

    Spark.get("viewcsv",
        new ViewCSVHandler(sharedData));
    Spark.get("searchcsv",
        new SearchCSVHandler(sharedData));
    Spark.get("broadband", new BroadbandHandler(new StateCache()));
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started at http://localhost:" + port);
  }

  /**
   * main method which calls Server so as to inject dependency
   * @param args
   */
  public static void main(String[] args) {
    Server server = new Server(new SharedData(new ArrayList<>(), new ArrayList<>()));
    // Notice that this runs, but the program continues executing. Why
    // do you think that is? (We'll address this in a couple of weeks.)
    System.out.println("Server started; exiting main...");
  }
}