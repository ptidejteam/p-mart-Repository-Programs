package src.AbstractFactory;


public class LuxuryVehicleFactory extends VehicleFactory {

  public Car getCar() {
    return new LuxuryCar("L-C");
  }
  public SUV getSUV() {
    return new LuxurySUV("L-S");
  }
} // End of class


