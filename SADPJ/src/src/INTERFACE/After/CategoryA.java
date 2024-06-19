package src.INTERFACE.After;

public class CategoryA implements SalaryCalculator {
  double baseSalary;
  double OT;

  public CategoryA(double base, double overTime) {
    baseSalary = base;
    OT = overTime;
  }

  public double getSalary() {
    return (baseSalary + OT);
  }
}
