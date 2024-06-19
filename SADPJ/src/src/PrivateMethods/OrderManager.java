package src.PrivateMethods;

public class OrderManager {
  private int orderID = 0;

  //Meant to be used internally
  private int getNextID() {
    ++orderID;
    return orderID;
  }

  //public method to be used by client objects
  public void saveOrder(String item, int qty) {

    int ID = getNextID();
    System.out.println("Order ID=" + ID + "; Item=" + item +
                       "; Qty=" + qty + " is saved. ");
  }

}
