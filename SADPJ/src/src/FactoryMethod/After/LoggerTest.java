package src.FactoryMethod.After;

public class LoggerTest {

  public static void main(String[] args) {
    LoggerFactory factory = new LoggerFactory();
    Logger logger = (Logger) factory.getLogger();
    logger.log("A Message to Log");
  }

}
