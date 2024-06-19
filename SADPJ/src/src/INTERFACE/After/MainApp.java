package src.INTERFACE.After;


public class MainApp {
  public static void main(String [] args) {
    SalaryCalculator c = new CategoryA(10000, 200);
    Employee e = new Employee ("Jennifer",c);
    e.display();

    c = new CategoryB(20000, 800);
    e = new Employee ("Shania",c);
    e.display();
  }
}
