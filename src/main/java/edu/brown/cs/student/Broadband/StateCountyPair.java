package edu.brown.cs.student.Broadband;

import java.util.Objects;

public record StateCountyPair(String state, String county) {

  @Override
  public boolean equals(Object o) {
    if (this == o) // direct equality
    return true;
    if (o == null || getClass() != o.getClass()) // check o isn't null or a different class
    return false;
    StateCountyPair comPair = (StateCountyPair) o;
    return comPair.county().equals(this.county) && comPair.state().equals(this.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, county);
  }
}
