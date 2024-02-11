package edu.brown.cs.student.Parser;

/** A class used for turning csv of string into arrays of integers */
public class IntegerCreatorFromRow implements CreatorFromRow<Integer> {
  @Override
  public Integer[] create(String[] row) throws FactoryFailureException {
    Integer[] intArray = new Integer[row.length];
    for (int i = 0; i < row.length; i++) {
      try {
        intArray[i] = Integer.parseInt(row[i].strip());
      } catch (NumberFormatException n) {
        System.out.println(row[i] + " cannot be converted into an integer, replacing with null");
        intArray[i] = null;
      }
    }
    return intArray;
  }
}
