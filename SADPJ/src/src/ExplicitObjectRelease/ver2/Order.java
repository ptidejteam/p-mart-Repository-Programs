package src.ExplicitObjectRelease.ver2;

public class Order {
  private String ID;
  private String item;
  private int qty;

  public Order(String i, String t, int q) {
    ID = i;
    item = t;
    qty = q;
  }

  public String getID() {
    return ID;
  }

  public String getItem() {
    return item;
  }

  public int getQty() {
    return qty;
  }

}

