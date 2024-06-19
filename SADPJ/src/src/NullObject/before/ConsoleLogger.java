package src.NullObject.before;
public class ConsoleLogger implements Logger {

  public void log(String msg) {
    System.out.println(msg);
  }

}
