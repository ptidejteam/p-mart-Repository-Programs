package src.ExplicitObjectRelease.ver3;
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
    } catch (Exception e) {

      //Identify the type of runtime
      // exception occurred.
      if (e instanceof NullPointerException) {
        dataOut.close(); //duplicate code
      }
      if (e instanceof IOException) {
        System.err.println("IOException Occurred: ");
      }
    }
  }
}

