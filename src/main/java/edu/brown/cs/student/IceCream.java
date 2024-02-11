package edu.brown.cs.student;

/** Class for ice cream, mainly used for flavor enum. */
public class IceCream {

  public Flavor getFlavor() {
    return flavor;
  }

  public IceCream(String flavor) {
    this.flavor = setFlavor(flavor);
  }

  private Flavor flavor;

  private Flavor setFlavor(String flavor) {

    switch (flavor.toUpperCase().strip()) {
      case "VANILLA":
        return Flavor.VANILLA;
      case "CHOCOLATE":
        return Flavor.CHOCOLATE;
      case "STRAWBERRY":
        return Flavor.STRAWBERRY;
      case "OREO":
        return Flavor.OREO;
      default:
        throw new IllegalArgumentException("Invalid ice cream flavor input");
    }
  }
}
