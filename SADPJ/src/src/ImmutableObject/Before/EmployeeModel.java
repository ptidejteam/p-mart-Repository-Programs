package src.ImmutableObject.Before;

public class EmployeeModel {

  //State
  private String firstName;
  private String lastName;
  private String SSN;
  private String address;
  private Car car;

  //Constructor
  public EmployeeModel(String fn, String ln, String ssn,
      String addr, Car c) {

    firstName = fn;
    lastName = ln;
    SSN = ssn;
    address = addr;
    car = c;
  }


  //Setters
  public void setFirstName(String fname) {
    firstName = fname;
  }
  public void setLastName(String lname) {
    lastName = lname;
  }
  public void setSSN(String ssn) {
    SSN = ssn;
  }
  public void setCar(Car c) {
    car = c;
  }
  public void setAddress(String addr) {
    address = addr;
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
    return car;
  }
  public String getAddress() {
    return address;
  }

}
