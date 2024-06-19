package src.COMPOSITE;



public class FileComponent extends FileSystemComponent {
  private long size;

  public FileComponent(String cName, long sz) {
    super(cName);
    size = sz;
  }

  public long getComponentSize() {
    return size;
  }

} // End of class

