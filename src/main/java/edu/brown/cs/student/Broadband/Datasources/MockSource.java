package edu.brown.cs.student.Broadband.Datasources;

import edu.brown.cs.student.Broadband.Responses.BroadbandResponse;
import edu.brown.cs.student.Broadband.Responses.NoBroadbandDataCountyResponse;
import edu.brown.cs.student.Broadband.Responses.NoBroadbandDataStateResponse;
import java.time.LocalDateTime;
import java.util.Map;

/** Mock data source only storing information about Solano County, California */
public class MockSource implements Datasource {

  /**
   * Query method only gives a successful response from Solano County, California. Otherwise, gives
   * error responses.
   *
   * @param state
   * @param county
   * @param responseMap
   * @return Response
   */
  @Override
  public Object query(String state, String county, Map<String, Object> responseMap) {
    if (state.equals("california")) {
      if (county.equals("solano county")) {
        responseMap.put("result", "success");
        return new BroadbandResponse(
            LocalDateTime.now().toString(), "solano county", "california", "92.2");
      } else {
        responseMap.put("result", "error_datasource");
        return new NoBroadbandDataCountyResponse(county, responseMap);
      }
    } else {
      responseMap.put("result", "error_datasource");
      return new NoBroadbandDataStateResponse(state, responseMap);
    }
  }
}
