package src.AbstractParentClass;

public class Consultant extends Employee {
  public String computeCompensation() {
    return ("consultant salary is base + " +
            " allowance + OT - tax deductions");
  }

  public Consultant(String empName, String empID) {
    super(empName, empID);
  }
}
