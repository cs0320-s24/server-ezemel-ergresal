package edu.brown.cs.student.Broadband.Datasources;

import java.util.Map;

/**
 * Datasource interface to be used by caching and mock sources. Allows for dependency injection and abstraction.
 */
public interface Datasource {
  public Object query(String state, String county, Map<String, Object> responseMap);
}
