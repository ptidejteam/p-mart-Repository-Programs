package src.AbstractFactory;

public class LuxuryCar implements Car {
  private String name;
  public LuxuryCar(String cName) {
    name = cName;
  }
  public String getCarName() {
    return name;
  }
  public String getCarFeatures() {
    return "Luxury Car Features ";
  };

} // End of class


