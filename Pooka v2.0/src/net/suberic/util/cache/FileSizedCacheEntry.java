package net.suberic.util.cache;
import java.io.*;

/**
 * This represents a cached object.  It stores the object itself, plus
 * the size and last accessed time.
 *
 * This implementation uses files to store the information in the cache.
 */
public class FileSizedCacheEntry extends SizedCacheEntry {

  // the stored object itself.  note that this could be a reference to
  // another object, or a source from which the object may be accessed.
  protected Object cachedValue;

  // the last accessed time
  protected long lastAccessedTime;

  // the size that the cachedValue occupies in the cache.
  protected long size = 0;

  /**
   * Creates a new FileSizedCacheEntry containing value which has been most
   * recently accessed now.
   */
  public FileSizedCacheEntry(Object value, boolean create, String filename) {
    File f = new File(filename);
    try {
      saveValue(f, value);
    } catch (Exception e) {
    }
    cachedValue = f;

    touchEntry();
  }

  /**
   * This gets the cached value.  Implementations may vary for this.
   */
  public Object getCachedValue() {
    Object o = null;
    try {
      o = loadValue();
    } catch (IOException ioe) {
    }

    return o;
  }

  /**
   * Loads the given value from the source file.
   *
   * Note that this should also set the size.
   */
  public Object loadValue() throws IOException {
    File f = getCacheFile();
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
    size = ois.available();
    Object returnValue = null;
    try {
      returnValue = ois.readObject();
    } catch (ClassNotFoundException cnfe) {
      throw new IOException("Class not found:  " + cnfe);
    }
    ois.close();
    return returnValue;
  }

  /**
   * Saves the given value to the source file.
   *
   * Note that this should also set the size.
   */
  public void saveValue(File f, Object value) throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
    oos.writeObject(value);
    oos.close();
  } 

  /**
   * Deletes this entry.  Should be called in order to clean up entries
   * for which simple removal from memory is insufficient.
   *
   * The default implementation does nothing; subclasses should override
   * this method if cleanup is required.
   */
  public boolean removeFromCache() {
    if (cachedValue instanceof File) {
      ((File) cachedValue).delete();
      return true;
    } else
      return false;
  }

  /**
   * Touches the SizedCacheEntry, making its last accessed time now.
   */
  public void touchEntry() {
    lastAccessedTime = System.currentTimeMillis();
    getCacheFile().setLastModified(lastAccessedTime);
  }

  /**
   * Gets the size of this SizedCacheEntry.
   */
  public long getSize() {
    return size;
  }

  /**
   * Gets the last accessed time for this entry.
   */
  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  /**
   * Gets the File object for the cache.
   */
  public File getCacheFile() {
    return (File) cachedValue;
  }

  /**
   * Compares the underlying value for equality.
   */
  public boolean equals(Object o) {
    if (o != null) {
      Object testValue = null;
      if (o instanceof SizedCacheEntry) {
	testValue = ((SizedCacheEntry)o).getCachedValue();
      } else {
	testValue = o;
      }
      return o.equals(cachedValue);
    }
    
    return (cachedValue == null);
  }
}

