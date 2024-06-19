package src.ChainOfResponsability;
public class PRManager {

  private BranchManager branchManager;
  private RegionalDirector regionalDirector;
  private VicePresident vicePresident;
  private PresidentCOO coo;

  public static void main(String[] args) {
    PRManager manager = new PRManager();
    manager.createAuthorizationFlow();

    PurchaseRequest request =
      new PurchaseRequest(1, "Office Supplies",10000);
    manager.branchManager.authorize(request);

    request = new PurchaseRequest(2, "HardWare Procurement",
              175000);
    manager.branchManager.authorize(request);
    request = new PurchaseRequest(3, "AD Campaign",800000);
    manager.branchManager.authorize(request);

  }

  public void createAuthorizationFlow() {
    branchManager = new BranchManager("Robin");
    regionalDirector = new RegionalDirector("Oscar");
    vicePresident = new VicePresident("Kate");
    coo = new PresidentCOO("Drew");

    branchManager.setNextHandler(regionalDirector);
    regionalDirector.setNextHandler(vicePresident);
    vicePresident.setNextHandler(coo);
  }

} // End of class

class PurchaseRequest {

  private int ID;
  private String description;
  private double amount;

  public PurchaseRequest(int id, String desc, double amt) {
    ID = id;
    description = desc;
    amount = amt;
  }

  public double getAmount() {
    return amount;
  }

  public String toString() {
    return ID + ":" + description;
  }

}
