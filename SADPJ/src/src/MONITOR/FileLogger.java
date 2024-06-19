package src.MONITOR;


import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLogger {

  public synchronized void log(String msg) {
    DataOutputStream dos = null;
    try {
      dos = new DataOutputStream(
              new FileOutputStream("log.txt",true));
      dos.writeBytes(msg);
      dos.close();
    } catch (FileNotFoundException ex) {
      //
    }
    catch (IOException ex) {
      //
    }
  }
}
