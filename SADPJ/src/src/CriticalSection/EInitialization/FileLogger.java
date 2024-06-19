package src.CriticalSection.EInitialization;
public class FileLogger implements Logger {

  //Early Initialization
  private static FileLogger logger = new FileLogger();

  private FileLogger() {
  }

  public static FileLogger getFileLogger() {
    return logger;
  }
  public synchronized void log(String msg) {

    FileUtil futil = new FileUtil();
    futil.writeToFile("log.txt",msg, true, true);
  }
}
