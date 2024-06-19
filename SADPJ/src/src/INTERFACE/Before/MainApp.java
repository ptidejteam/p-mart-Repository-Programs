package src.INTERFACE.Before;


//import CategoryA;
//import Employee;

public class MainApp {
  public static void main(String [] args) {
    CategoryA c = new CategoryA(10000, 200);
    Employee e = new Employee ("Jennifer",c);
    e.display();
  }
}
