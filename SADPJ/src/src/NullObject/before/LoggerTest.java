package src.NullObject.before;
public class LoggerTest {

  public static void main(String[] args) {
    LoggerFactory factory = new LoggerFactory();
    Logger logger = factory.getLogger();
    if (logger != null) {
      logger.log("A Message to Log");
    }
  }

}
