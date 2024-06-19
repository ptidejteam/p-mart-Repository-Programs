package src.AbstractParentClass;


public class SalesRep extends Employee {
  //variable part behavior
  public String computeCompensation() {
    return ("sales Rep Salry is Base + commission + " +
            " allowance – tax deductions");
  }

  public SalesRep(String empName, String empID) {
    super(empName, empID);
  }

}
