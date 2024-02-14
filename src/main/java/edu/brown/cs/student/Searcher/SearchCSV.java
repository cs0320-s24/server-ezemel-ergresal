package edu.brown.cs.student.Searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchCSV<T> {

  private Set<Integer> rowChecked = new HashSet<>(); // global variable so doesn't change upon recursion
  private List<List<T>> rowsFound;


  /**
   * constructor for if column not specified, this means we will search all columns
   *
   * @param data
   * @param object
   * @return boolean, whether or not element was found in any of the columns
   * @throws IOException
   */
  public Boolean startSearcher(List<List<T>> data, Object object) {
    List<T> columnHeaders = new ArrayList<>();
    return startSearcher(data, columnHeaders, object, false, -1);
  }

  /**
   * figures out which column should be searched, and then calls search on this column
   *
   * @param data                 to search through
   * @param columnHeaders        a list of columnHeaders if columnHeaders is true. Irrelevant if
   *                             columnHeaders is false
   * @param object               to be searched for
   * @param columnHeadersBoolean whether or not the data has a row of columnHeaders. This only stops
   *                             the
   * @param column               the column number to search through. -1 if all columns
   * @return whether or not item was found in the specified column
   * @throws IOException
   */
  public Boolean startSearcher(
      List<List<T>> data,
      List<T> columnHeaders,
      Object object,
      Boolean columnHeadersBoolean,
      Object column) {
    rowsFound = new ArrayList<>();
    if (columnHeadersBoolean && columnHeaders.isEmpty()) {
      System.err.println(
          "No available column headers. Try searching without column specifier or using index instead.");
      return false;
    }
    int columnI = -1;
    if (column == null){
      column = -1;
    }
    if (column.equals(-1) || column.equals("")) { // check if input was -1 (nothing specified)
      columnI = -1; // this way we will search each column
    } else {
      try { // try to identify column
        columnI = (int) column; // cast to integer from integer (presumably)
        if (columnI < -1
            || columnI >= data.get(0).size()) { // If column number is below -1 or too large, exit
          System.err.println(
              "Column "
                  + columnI
                  + " doesn't exist"); // User may put either nothing or -1 to search all columns
          return false;
        }
      } catch (NumberFormatException | ClassCastException e) {
      }
      try {
        columnI = Integer.parseInt((String) column); // cast to integer from string
      } catch (NumberFormatException | ClassCastException e) {
        if (columnHeadersBoolean) {
          // If there are column headers, and the indicated column is not an int, this is when we
          // check
          // whether or not the column exists in the first row of the dataset.
          int numCols = columnHeaders.size();
          for (int i = 0; i < numCols; i++) { // check if header equals
            if (column.equals(columnHeaders.get(i))) {
              columnI = i;
            }
          }
        }
        if (columnI == -1) {
          System.err.println("Unable to read file column");
          return false;
        }
      }
    }
    int found = this.search(data, object, columnI, 0);
    this.rowChecked = new HashSet<>(); // resets rowChecked in case another search call made

    if ((found == 0)) {
      System.err.println("'" + object + "'" + " not found");
      return false;
    }
    return true;
  }

  /**
   * figures out which column should be searched, and then calls search on this column
   *
   * @param data     to search through
   * @param object   to be searched for
   * @param column   the column number to search through. -1 if all columns
   * @param numFound number of objects found in data (used for recursion purposees and to print
   *                 'item not found' afterwards if applicable
   * @return number of objects found in specified column (if -1 column, in total dataset)
   * @throws IOException
   */
  private int search(List<List<T>> data, Object object, int column, int numFound) {
    int numCols;
    try {
      numCols = data.get(0).size();
    } catch (IndexOutOfBoundsException e) {
      System.err.println("Empty dataset. ");
      return numFound;
    }
    if (column == -1) { // we want to search each of the columns if column = -1
      for (int i = 0; i < numCols; i++) {
        numFound += search(data, object, i, numFound);
      }
    } else {
      int numRows = data.size();
      for (int j = 0; j < numRows; j++) {
        if (!(this.rowChecked.contains(j) || data.get(j).get(column) == null)) {
          try {
            if (data.get(j).get(column).equals(object)) {
              // Perform comparison on objects
              System.out.println("'" + object + "'" + " found in row " + j + ": " + data.get(j));
              this.rowChecked.add(j);
              this.rowsFound.add(data.get(j));
              numFound++;
              //              break;
            }
          } catch (NullPointerException n) {
            // null pointer found, maybe due to inconsistent column count.
            // we just don't look at these slots
          }
        }
      }
    }
    return numFound;
  }

  public List<List<T>> getRowsFound(List<List<T>> data, List<T> ch, Object object, Boolean columnHeaders, Object column) {
    startSearcher(data, ch, object, columnHeaders, column);
    return this.rowsFound;
  }
}
