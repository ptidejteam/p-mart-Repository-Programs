package src.NullObject.after;
import java.util.Properties;

public class LoggerFactory {

  public boolean isFileLoggingEnabled() throws Exception {
    Properties p = new Properties();
    try {
      p.load(ClassLoader.getSystemResourceAsStream(
        "Logger.properties"));
      String fileLoggingValue =
        p.getProperty("FileLogging");
      if (fileLoggingValue.equalsIgnoreCase("ON") == true)
        return true;
      else
        return false;
    } catch (Exception e) {
      throw e;
    }

  }

  //Factory Method
  public Logger getLogger() {
    Logger logger = new NullLogger();
    try {
      if (isFileLoggingEnabled()) {
        logger = new FileLogger();
      } else {
        logger = new ConsoleLogger();
      }
    } catch (Exception e) {
      //
    }
    return logger;
  }

}
