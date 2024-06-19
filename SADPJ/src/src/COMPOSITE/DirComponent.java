package src.COMPOSITE;

import java.util.Enumeration;
import java.util.Vector;


public class DirComponent extends FileSystemComponent {
  Vector dirContents = new Vector();
  //individual files/sub folders collection

  public DirComponent(String cName) {
    super(cName);
  }

  public void addComponent(FileSystemComponent fc)
  throws CompositeException {
    dirContents.add(fc);
  }

  public FileSystemComponent getComponent(int location)
  throws CompositeException {
    return (FileSystemComponent) dirContents.elementAt(
             location);
  }

  public long getComponentSize() {
    long sizeOfAllFiles = 0;
    Enumeration e = dirContents.elements();
    while (e.hasMoreElements()) {
      FileSystemComponent component =
        (FileSystemComponent) e.nextElement();
      sizeOfAllFiles = sizeOfAllFiles +
                       (component.getComponentSize());
    }
    return sizeOfAllFiles;
  }

} // End of class

