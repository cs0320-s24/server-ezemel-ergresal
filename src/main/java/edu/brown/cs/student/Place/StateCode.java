package edu.brown.cs.student.Place;

public class StateCode {
  private String name;
  private String state;


  public StateCode(String name, String state) {
    this.name = name;
    this.state = state;
  }

  public String getState() {
    return state;
  }

  public String getName() {
    return name;
  }
}
