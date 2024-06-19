package net.suberic.util.cache;

/**
 * This represents a cached object.  It stores the object itself, plus
 * the size and last accessed time.
 */
public class SizedCacheEntry {

  // the stored object itself.  note that this could be a reference to
  // another object, or a source from which the object may be accessed.
  protected Object cachedValue;

  // the last accessed time
  protected long lastAccessedTime;

  // the size that the cachedValue occupies in the cache.
  protected long size = 0;

  /**
   * Creates a new, empty SizedCacheEntry.
   */
  public SizedCacheEntry() {
  }

  /**
   * Creates a new SizedCacheEntry containing value which has been most
   * recently accessed now.
   */
  public SizedCacheEntry(Object value) {
    cachedValue = value;
    // yeah, whatever...
    size = value.toString().length();
    touchEntry();
  }

  /**
   * This gets the cached value.  Implementations may vary for this.
   */
  public Object getCachedValue() {
    return cachedValue;
  }

  /**
   * Deletes this entry.  Should be called in order to clean up entries
   * for which simple removal from memory is insufficient.
   *
   * The default implementation does nothing; subclasses should override
   * this method if cleanup is required.
   */
  public boolean removeFromCache() {
    return true;
  }

  /**
   * Touches the SizedCacheEntry, making its last accessed time now.
   */
  public void touchEntry() {
    lastAccessedTime = System.currentTimeMillis();
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

