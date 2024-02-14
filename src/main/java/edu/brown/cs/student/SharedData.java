package edu.brown.cs.student;

import java.util.List;

public class SharedData {

  private List<List<String>> csvData;
  private List<String> columnHeaders;
  private boolean isEmpty;
  public SharedData(List<List<String>> csvData,List<String> columnHeaders){
    this.csvData = csvData;
    this.columnHeaders = columnHeaders;
    this.isEmpty = true;
  }
  public void put(List<List<String>> csvData,List<String> columnHeaders){
    this.isEmpty = false;
    this.csvData = csvData;
    this.columnHeaders = columnHeaders;
  }

  public List<List<String>> getCsvData() {
    return csvData;
  }

  public List<String> getColumnHeaders() {
    return columnHeaders;
  }
  public boolean isEmpty() {
    return this.isEmpty;
  }
}
