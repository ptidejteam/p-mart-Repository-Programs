package net.suberic.util.cache;
import java.util.*;

/**
 * This is a simple implementation of a sized cache.  It uses 
 * SizedCacheEntry objects to store wrap its values and to keep track
 * the items' sizes and last accessed times.
 */
public abstract class AbstractSizedCache {
  
  protected HashMap cacheTable = new HashMap();

  protected LinkedList sortedKeys = new LinkedList();

  protected long size = 0;

  private long maxSize = 0;

  private long maxEntrySize = 0;

  private SizedCacheEntryFactory factory = null;
 
  /**
   * Adds an object to the Cache.
   */
  public synchronized void add(Object key, Object value) {
    if (value == null) {
      invalidateCache(key);
    } else {
      SizedCacheEntry origEntry = (SizedCacheEntry) cacheTable.get(key);
      if (origEntry == null || ! origEntry.equals(value)) {
	invalidateCache(key);
	
	SizedCacheEntry sce = getFactory().createCacheEntry(value);
	
	if (getMaxEntrySize() >= 0 && sce.getSize() < getMaxEntrySize()) {
	  if (getMaxSize() >= 0) {
	    while (sce.getSize() + getSize() > getMaxSize()) {
	      // keep removing the last entry in the table until there's
	      // enough space.
	      Object lastAccessedKey = sortedKeys.getLast();
	      invalidateCache(lastAccessedKey);
	    }
	  }
	  cacheTable.put(key, sce);
	  size += sce.getSize();
	  reorderEntry(key);
	}
      }
    }
  }
    
  /**
   * Gets an object from the Cache.  If the object is not available,
   * returns null.
   */
  public synchronized Object get(Object key) {
    SizedCacheEntry sce = (SizedCacheEntry) cacheTable.get(key);
    if (sce != null) {
      Object returnValue = sce.getCachedValue();
      sce.touchEntry();
      reorderEntry(sce);
      return returnValue;
    } else
      return null;
  }

  /**
   * Removes an object from the Cache.
   */
  public synchronized void invalidateCache(Object key) {
    SizedCacheEntry value = (SizedCacheEntry) cacheTable.get(key);
    if (value != null) {
      size -= value.getSize();
      value.removeFromCache();
    }

    sortedKeys.remove(key);
    cacheTable.remove(key);
  }

  /**
   * Invalidates the entire cache.
   */
  public synchronized void invalidateCache() {
    Iterator it = cacheTable.keySet().iterator();
    while (it.hasNext()) {
      Object key = it.next();
      SizedCacheEntry entry = (SizedCacheEntry) cacheTable.get(key);
      if (entry != null) {
	entry.removeFromCache();
      }
      cacheTable.remove(key);
    }
  }

  /**
   * Reorders the entry in the sortedKeys list. 
   */
  private void reorderEntry(Object key) {
    sortedKeys.remove(key);
    sortedKeys.addFirst(key);
  }

  /**
   * Gets the size limit for the cache.
   */
  public long getMaxSize() {
    return maxSize;
  }

  /**
   * Sets the size limit for the cache.
   */
  public void setMaxSize(long newSize) {
    maxSize = newSize;
  }

  /**
   * Gets the size limit for individual entries in the cache.
   */
  public long getMaxEntrySize() {
    return maxEntrySize;
  }

  /**
   * Sets the size limit for individual entries in the cache.
   */
  public void setMaxEntrySize(long newSize) {
    maxEntrySize = newSize;
  }

  /**
   * Gets the current size of the cache.
   */
  public long getSize() {
    return size;
  }

  /**
   * Gets the SizedCacheEntryFactory for this cache.
   */
  public SizedCacheEntryFactory getFactory() {
    return factory;
  }

  /**
   * Sets the SizedCacheEntryFactory for this cache.
   */
  public void setFactory(SizedCacheEntryFactory newFactory) {
    if (newFactory != null) {
      factory = newFactory;
    }
  }
}
