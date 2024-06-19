package net.suberic.util.cache;

/**
 * This represents a cache that will cache objects up to a certain amount
 * of space.  After that space is filled, it will start removing items
 * using a least recently used algorithm.
 */
public interface SizedCache {
  
  /**
   * Adds an object to the Cache.
   */
  public void add(Object key, Object value);

  /**
   * Gets an object from the Cache.  If the object is not available,
   * returns null.
   */
  public Object get(Object key);

  /**
   * Removes an object from the Cache.
   */
  public void invalidateCache(Object key);

  /**
   * Invalidates the entire cache.
   */
  public void invalidateCache();

  /**
   * Gets the size limit for the cache.
   */
  public long getMaxSize();

  /**
   * Gets the size limit for an individual item in the cache.  For obvious
   * reasons, this should probably not be greater than the max size.
   */
  public long getMaxEntrySize();

  /**
   * Gets the current size of the cache.
   */
  public long getSize();

}
