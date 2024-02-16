package edu.brown.cs.student.Parser;

import java.util.List;

/** A class used for turning csv of string into arrays of flavors */
public class FlavorCreatorFromRow implements CreatorFromRow<Flavor> {

  @Override
  public Flavor[] create(String[] row) throws FactoryFailureException {
    if (row.length == 0) {
      throw new FactoryFailureException("Row is empty", List.of(row));
    }
    Flavor[] iCArray = new Flavor[row.length];
    for (int i = 0; i < row.length; i++) {
      iCArray[i] = new IceCream(row[i]).getFlavor();
    }
    return iCArray;
  }
}
