package src.INTERFACE.Before;


public class CategoryB {
  double salesAmt;
  double baseSalary;
  final static double commission = 0.02;
  public CategoryB(double sa, double base) {
    baseSalary = base;
    salesAmt = sa;
  }

  public double getSalary() {
    return (baseSalary + (commission * salesAmt));
  }
}
