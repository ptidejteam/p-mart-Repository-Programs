package src.ImmutableObject.After;

public final class EmployeeModel {

  //State
  private final String firstName;
  private final String lastName;
  private final String SSN;
  private final String address;
  private final Car car;

  //Constructor
  public EmployeeModel(String fn, String ln, String ssn,
      String addr, Car c) {

    firstName = fn;
    lastName = ln;
    SSN = ssn;
    address = addr;
    car = c;
  }

  //Getters
  public String getFirstName() {
    return firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public String getSSN() {
    return SSN;
  }
  public Car getCar() {
    //return a copy of the car object
    return (Car) car.clone();
  }
  public String getAddress() {
    return address;
  }

}
