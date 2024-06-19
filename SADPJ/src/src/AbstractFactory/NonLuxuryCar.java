package src.AbstractFactory;

public class NonLuxuryCar implements Car {
  private String name;
  public NonLuxuryCar(String cName) {
    name = cName;
  }
  public String getCarName() {
    return name;
  }
  public String getCarFeatures() {
    return "Non-Luxury Car Features ";
  };

} // End of class


