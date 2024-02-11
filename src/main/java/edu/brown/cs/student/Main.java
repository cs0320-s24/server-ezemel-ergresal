package edu.brown.cs.student;

import edu.brown.cs.student.Parser.CreatorFromRow;
import edu.brown.cs.student.Parser.FactoryFailureException;
import edu.brown.cs.student.Parser.ParseCSV;
import edu.brown.cs.student.Parser.StringCreatorFromRow;
import edu.brown.cs.student.Searcher.SearchCSV;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/** The Main class of our project. This is where execution begins. */
public final class Main {

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) throws IOException, FactoryFailureException {
    new Main(args).run();
  }

  private String[] myReader;

  private Main(String[] args) {
    this.myReader = args;
  }

  /**
   * This run function offers a REPL for users to input a file, and then begin searching for words
   * within the data file. Here, there is a default string type for the user to input, since we
   * assume they are reading from a CSV text file. If the user desires more user-specific types,
   * they may create their own calls to the run function, using a different class (or,
   * theoretically, in the main class itself). This enables each of the inputs to the parser and
   * searcher to be more customizable to programming users, who may want to specify different types
   * and places from which to read data. If the user wishes to search through their own string, they
   * must implement their own call to CSV Parser and then to SearchCSV
   *
   * @param <T>
   * @throws IOException
   * @throws FactoryFailureException
   */
  private <T> void run() throws IOException {
    String currentPath = new java.io.File(".").getCanonicalPath();

    System.out.println(
        "Welcome to search!\nPlease enter a file path starting after: " + currentPath + "/data");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String fileName = currentPath + "/data" + reader.readLine().strip();
    CreatorFromRow<String> myCreator = new StringCreatorFromRow();
    System.out.println(
        "Are there column headers in this file? (input 'y'/'n')"
            + " First row will only be used as column headers if y");
    String columnHeaders = reader.readLine();
    Boolean columnHeadersBoolean;
    if (columnHeaders.equals("y")) {
      columnHeadersBoolean = true;
    } else if (columnHeaders.equals("n")) {
      columnHeadersBoolean = false;
    } else {
      System.err.println("Invalid input. Please rerun the program.");
      return;
    }
    ParseCSV<String> fileReader;
    try {
      fileReader = new ParseCSV<String>(new FileReader(fileName), myCreator, columnHeadersBoolean);
    } catch (FileNotFoundException f) {
      System.err.println("File not found. Please rerun the program.");
      return;
    }

    while (true) {
      System.out.println("Thanks. Please enter a word for which you'd like to search");
      Object searchedObject = reader.readLine();
      if (searchedObject.equals("stop")) {
        return;
      }
      System.out.println(
          "Please input the column in which you want to search. "
              + "This may be either the column header or the index of the column"
              + "(Or hit enter if you'd like to search the entire dataset)");
      Object column = reader.readLine();
      if (column.equals("stop")) {
        return;
      }
      List<List<String>> replFileData = fileReader.getParsedData();
      SearchCSV<String> myReplSearcher = new SearchCSV<>();
      myReplSearcher.startSearcher(
          replFileData,
          fileReader.getColumnHeaders(),
          searchedObject,
          columnHeadersBoolean,
          column);
    }
  }
}
