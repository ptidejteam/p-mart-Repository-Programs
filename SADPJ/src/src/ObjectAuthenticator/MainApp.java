package src.ObjectAuthenticator;

public class MainApp {
  public static void main(String[] args) {
    OrderManager manager = new OrderManager();
    try {
      manager.createOrder("CDs", 10);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
