package src.CountingProxy;
import java.util.Vector;

public class OrderProxy implements OrderIF {

  private int counter = 0;
  public Vector getAllOrders() {
    Order order = new Order();
    counter++;

    long t1 = System.currentTimeMillis ();
    Vector v = order.getAllOrders();
    long t2 = System.currentTimeMillis();
    long timeDiff = t2 - t1;
    String msg =
      "Iteration=" + counter + "::Time=" + timeDiff +
      "ms";

    //log the message
    FileUtil fileUtil = new FileUtil();
    fileUtil.writeToFile("log.txt",msg, true, true);

    return v;
  }
}
