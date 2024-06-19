package src.AccessorMethods.Customer;

public class Customer {

  private String firstName;
  private String lastName;
  private String address;
  private boolean active;

  public String getFirstName() {
    return firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public String getAddress() {
    return address;
  }
  public boolean isActive() {
    return active;
  }
  public void setFirstName(String newValue) {
    firstName = newValue;
  }
  public void setLastName(String newValue) {
    lastName = newValue;
  }
  public void setAddress(String newValue) {
    address = newValue;
  }
  public void isActive(boolean newValue) {
    active = newValue;
  }
}
