package src.CountingProxy;
import java.util.Vector;

public class MainApp {
  public static void main(String[] args) {
    OrderIF order = new OrderProxy();
    Vector v = order.getAllOrders();
    v = order.getAllOrders();
    v = order.getAllOrders();
    v = order.getAllOrders();
  }
}
