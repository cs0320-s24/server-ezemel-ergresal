package edu.brown.cs.student.server;

import edu.brown.cs.student.Broadband.BroadbandResponse;
import edu.brown.cs.student.Broadband.Datasource;
import edu.brown.cs.student.Broadband.NoBroadbandDataCountyResponse;
import edu.brown.cs.student.Broadband.NoBroadbandDataStateResponse;

import java.time.LocalDateTime;
import java.util.Map;

public class MockSource implements Datasource {

    @Override
    public Object query(String state, String county, Map<String, Object> responseMap) {
        if(state.equals("california")) {
            if(county.equals("solano county")) {
                responseMap.put("result", "success");
                return new BroadbandResponse(LocalDateTime.now().toString(),
                        "solano county","california","92.2");
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
