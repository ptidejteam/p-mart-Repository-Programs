package src.FactoryMethod.before;


public class ConsoleLogger implements Logger {

  public void log(String msg) {
    System.out.println(msg);
  }

}
