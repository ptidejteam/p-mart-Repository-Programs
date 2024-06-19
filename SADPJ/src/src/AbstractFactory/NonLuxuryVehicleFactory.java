package src.AbstractFactory;


public class NonLuxuryVehicleFactory extends VehicleFactory {

  public Car getCar() {
    return new NonLuxuryCar("NL-C");
  }
  public SUV getSUV() {
    return new NonLuxurySUV("NL-S");
  }
} // End of class


