package src.ConsistentLockOrder;
public class FileSysUtil {

  public void moveContents(Directory src, Directory dest) {
    synchronized (src) {
      synchronized (dest) {
        System.out.println("Contents Moved Successfully");
      }
    }
  }
}
