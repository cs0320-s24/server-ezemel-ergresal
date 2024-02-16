package edu.brown.cs.student.Broadband;

import java.util.Map;

public interface Datasource {
  public Object query(String state, String county, Map<String, Object> responseMap);
}
