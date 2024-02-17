package edu.brown.cs.student.Broadband;

import java.util.Objects;

/**
 * Custom form of the Pair class that allows for mapping to BroadbandResponses from state and county
 * names in the cache or other data sources.
 *
 * @param state
 * @param county
 */
public record StateCountyPair(String state, String county) {

  /**
   * Equals method overwritten to ensure that comparisons are accurate.
   *
   * @param o the reference object with which to compare.
   * @return
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) // direct equality
    return true;
    if (o == null || getClass() != o.getClass()) // check o isn't null or a different class
    return false;
    StateCountyPair comPair = (StateCountyPair) o;
    return comPair.county().equals(this.county) && comPair.state().equals(this.state);
  }

  /**
   * hashCode overwrite to ensure that hashing is accurate for storage in data source.
   *
   * @return
   */
  @Override
  public int hashCode() {
    return Objects.hash(state, county);
  }
}
