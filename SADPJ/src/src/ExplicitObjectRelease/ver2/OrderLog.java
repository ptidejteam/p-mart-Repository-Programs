package src.ExplicitObjectRelease.ver2;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class OrderLog {

  public void log (Order order) {
    PrintWriter dataOut = null;
    try {
      dataOut =
        new PrintWriter (new FileWriter("order.txt"));
      String dataLine =
        order.getID() + "," + order.getItem() +
        "," + order.getQty();

      dataOut.println(dataLine);
      dataOut.close(); //duplicate code
    } catch (IOException e) {
      System.err.println("IOException Occurred: " +
                         e.getMessage());
    }
    catch (NullPointerException ne) {
      dataOut.close(); //duplicate code
    }
  }
}

