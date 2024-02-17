package edu.brown.cs.student.Server.CSVHandling;

import java.util.List;

/**
 * CSV data wrapper for storage and dependency injection for the different csv handlers for the csv
 * related endpoints.
 */
public class SharedData {

  private List<List<String>> csvData;
  private List<String> columnHeaders;
  private boolean isEmpty;

  /**
   * For initializing with empty lists.
   *
   * @param csvData
   * @param columnHeaders
   */
  public SharedData(List<List<String>> csvData, List<String> columnHeaders) {
    assert (csvData.isEmpty() && columnHeaders.isEmpty());
    this.csvData = csvData;
    this.columnHeaders = columnHeaders;
    this.isEmpty = true;
  }

  /**
   * For inserting data.
   *
   * @param csvData
   * @param columnHeaders
   */
  public void put(List<List<String>> csvData, List<String> columnHeaders) {
    this.isEmpty = false;
    this.csvData = csvData;
    this.columnHeaders = columnHeaders;
  }

  /**
   * Getter for csvData
   *
   * @return
   */
  public List<List<String>> getCsvData() {
    return csvData;
  }

  /**
   * Getter for columnHeaders
   *
   * @return
   */
  public List<String> getColumnHeaders() {
    return columnHeaders;
  }

  /**
   * Getter for isEmpty
   *
   * @return
   */
  public boolean isEmpty() {
    return this.isEmpty;
  }
}
