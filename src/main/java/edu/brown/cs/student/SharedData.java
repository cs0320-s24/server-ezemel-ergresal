package edu.brown.cs.student;

import java.util.List;

public class SharedData {

  private List<List<String>> csvData;
  private List<String> columnHeaders;
  public SharedData(List<List<String>> csvData,List<String> columnHeaders){
    this.csvData = csvData;
    this.columnHeaders = columnHeaders;
  }
  public void put(List<List<String>> csvData,List<String> columnHeaders){
    this.csvData = csvData;
    this.columnHeaders = columnHeaders;
  }

  public List<List<String>> getCsvData() {
    System.out.println("in shareddata:   " +csvData);
    return csvData;
  }

  public List<String> getColumnHeaders() {
    return columnHeaders;
  }
}
