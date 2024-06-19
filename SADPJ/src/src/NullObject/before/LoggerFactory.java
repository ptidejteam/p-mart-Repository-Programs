package src.NullObject.before;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    } catch (FileNotFoundException ex) {
      throw ex;
    }
    catch (IOException e) {
      throw e;
    }
  }

  //Factory Method
  public Logger getLogger() {
    Logger logger = null;
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
