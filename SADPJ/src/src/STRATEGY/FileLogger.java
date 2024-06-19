package src.STRATEGY;
public class FileLogger {

  public void log(String fileName, String msg) {
    FileUtil futil = new FileUtil();
    futil.writeToFile(fileName, msg, true, true);
  }

}
