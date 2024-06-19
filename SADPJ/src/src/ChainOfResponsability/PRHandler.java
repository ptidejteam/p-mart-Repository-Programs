package src.ChainOfResponsability;
public abstract class PRHandler {
  private PRHandler nextHandler;
  private String handlerName;

  public PRHandler(String name) {
    handlerName = name;
  }
  public String getName() {
    return handlerName;
  }
  public abstract boolean authorize(PurchaseRequest request);
  public PRHandler getNextHandler() {
    return nextHandler;
  }
  public void setNextHandler(PRHandler handler) {
    nextHandler = handler;
  };
}
class BranchManager extends PRHandler {
  static double LIMIT = 25000;

  public BranchManager(String name) {
    super(name);
  }
  public boolean authorize(PurchaseRequest request) {
    double amount = request.getAmount();

    if (amount <= LIMIT) {
      System.out.println(" Branch Manager " + getName() +
                         " has authorized the PR - " + request);
      return true;
    } else {
      //forward the request to the next handler
      return getNextHandler().authorize(request);
    }
  }

} // End of class

class RegionalDirector extends PRHandler {
  static double LIMIT = 100000;

  public RegionalDirector(String name) {
    super(name);
  }

  public boolean authorize(PurchaseRequest request) {
    double amount = request.getAmount();

    if (amount <= LIMIT) {
      System.out.println(" Regional Director " + getName() + 
                         " has authorized the PR - " +
                         request);
      return true;
    } else {
      //forward the request to the next handler
      return getNextHandler().authorize(request);
    }
  }

} // End of class

class VicePresident extends PRHandler {
  static double LIMIT = 200000;

  public VicePresident(String name) {
    super(name);
  }

  public boolean authorize(PurchaseRequest request) {
    double amount = request.getAmount();

    if (amount <= LIMIT) {
      System.out.println(" V.P. " + getName() +
                         " has authorized the PR - " + request);
      return true;
    } else {
      //forward the request to the next handler
      return getNextHandler().authorize(request);
    }
  }

} // End of class

class PresidentCOO extends PRHandler {
  static double LIMIT = 400000;

  public PresidentCOO(String name) {
    super(name);
  }

  public boolean authorize(PurchaseRequest request) {
    double amount = request.getAmount();

    if (amount <= LIMIT) {
      System.out.println(" President & COO " + getName() +
                         " has authorized the PR - " + request);
      return true;
    } else {
      System.out.println("PR - " + request +
                         " couldn't be authorized.\n " +
                         "Executive Board needs to be " +
                         "consulted for approval \n" +
                         "reason: Amount too large");
      return false;
    }
  }

} // End of class

