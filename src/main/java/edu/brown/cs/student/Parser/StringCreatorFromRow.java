package edu.brown.cs.student.Parser;
/** A class used for turning csv of string into arrays of strings */
public class StringCreatorFromRow implements CreatorFromRow<String> {
  @Override
  public String[] create(String[] row) throws FactoryFailureException {
    return row;
  }
}
