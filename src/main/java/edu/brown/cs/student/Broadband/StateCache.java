package edu.brown.cs.student.Broadband;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;


public class StateCache {

  /**
   * A class that wraps a FileServer instance and caches responses
   * for efficiency. Notice that the interface hasn't changed at all.
   * This is an example of the proxy pattern; callers will interact
   * with the CachedFileServer, rather than the "real" data source.
   *
   * This version uses a Guava cache class to manage the cache.
   */
//  public class CachedFileSearcher implements Searcher<String,String> {
//    private final Searcher<String,String> wrappedSearcher;
    private final LoadingCache<StateCountyPair, Response> cache;

    /**
     * Proxy class: wrap an instance of Searcher (of any kind) and cache
     * its results.
     *
     * There are _many_ ways to implement this! We could use a plain
     * HashMap, but then we'd have to handle "eviction" ourselves.
     * Lots of libraries exist. We're using Guava here, to demo the
     * strategy pattern.
     *
     * @param
     */

  /**
   * No parameters: cache will not evict entries.
   */
  public StateCache() {

      // Look at the docs -- there are lots of builder parameters you can use
      //   including ones that affect garbage-collection (not needed for Server).
      this.cache = CacheBuilder.newBuilder()
          // Keep statistical info around for profiling purposes
          .recordStats()
          .build(
                  // Strategy pattern: how should the cache behave when
                  // it's asked for something it doesn't have?
                  new CacheLoader<>() {
                    @Override
                    public Response load(StateCountyPair key) throws IOException {
                      // If this isn't yet present in the cache, load it:
                      return searchAPI(key);
                    }
                  });
    }

  /**
   * Allows for specifying maximum entries and time to evict after writing. To in essence avoid caching, set maxSize to
   * 0.
   * @param maxSize - maximum number of entries in the cache
   * @param minutesToEvict - time in minutes before an entry is evicted
   */

  public StateCache(int maxSize, int minutesToEvict) {

      // Look at the docs -- there are lots of builder parameters you can use
      //   including ones that affect garbage-collection (not needed for Server).
      this.cache = CacheBuilder.newBuilder()
              // How many entries maximum in the cache?
              .maximumSize(maxSize)
              // How long should entries remain in the cache?
              .expireAfterWrite(minutesToEvict, TimeUnit.MINUTES)
              // Keep statistical info around for profiling purposes
              .recordStats()
              .build(
                      // Strategy pattern: how should the cache behave when
                      // it's asked for something it doesn't have?
                      new CacheLoader<>() {
                        @Override
                        public Response load(StateCountyPair key) throws IOException {
                          // If this isn't yet present in the cache, load it:
                          return searchAPI(key);
                        }
                      });
    }

  public StateCache(int maxSize) {

    // Look at the docs -- there are lots of builder parameters you can use
    //   including ones that affect garbage-collection (not needed for Server).
    this.cache = CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(maxSize)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                    // Strategy pattern: how should the cache behave when
                    // it's asked for something it doesn't have?
                    new CacheLoader<>() {
                      @Override
                      public Response load(StateCountyPair key) throws IOException {
                        // If this isn't yet present in the cache, load it:
                        return searchAPI(key);
                      }
                    });
  }

    public Response get(String stateCode, String countyName) throws IllegalArgumentException {
      // "get" is designed for concurrent situations; for today, use getUnchecked:

      Response result = cache.getUnchecked(new StateCountyPair(stateCode, countyName));
      // For debugging and demo (would remove in a "real" version):
      System.out.println(cache.stats());
      return result;
    }

    private Response searchAPI(StateCountyPair target) throws IllegalArgumentException, IOException {
      URL requestURL = new URL(
              String.format("https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:%s",
                      target.state()));
      HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line = reader.readLine(); // don't want to parse the first row (headers)
//      if (line.equals("error: invalid 'for' argument")) {
//        throw new IllegalArgumentException("Invalid county.");
//      }
//      line = reader.readLine();
        while ((line = reader.readLine()) != null) {
          String[] parts = line.split(",");
//          if (parts.length >= 4) {
          String countyName = parts[0].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          String stateName = parts[1].replaceAll("\"", "").trim();
          String countyPercentage = parts[2].replaceAll("\"", "").trim();
//          String stateCodee = parts[3].replaceAll("\"", "").trim();

//          String countyCode = parts[4].replaceAll("\"", "").trim().replaceAll("\\[|\\]", "");
          if(countyName.toLowerCase().equals(target.county())) {
            reader.close();
            return new BroadbandResponse(LocalDateTime.now().toString(), countyName, stateName,
                    countyPercentage);
          }
        }
      throw new IllegalArgumentException("Invalid county.");
//              List.of(countyName, stateName, countyPercentage, stateCode, countyCode));
//            if (countyName.toLowerCase().equals(county_name)) {
////            correctCountyCode = countyCode;
//
//              return countyCode;
//            }
////          }
//
////        }
//        reader.close();
//        throw new IllegalArgumentException("aahahahahahahahah");
    }

    // This would have been a more direct way to start on building a proxy
    //  (but I like using Guava's cache)
    /*
    public Collection<String> search(String target) {
        // Pass through: call the wrapped object
        return this.wrappedSearcher.searchLines(target);
    }
     */
}


