package net.suberic.util.cache;

/**
 * A class that can create SizedCacheEntries.
 */
public class SimpleSizedCacheEntryFactory {

  public SimpleSizedCacheEntryFactory() {
  }

  /**
   * Create an appropriate SizedCacheEntry.
   */
  public SizedCacheEntry createCacheEntry(Object value) {
    return new SizedCacheEntry(value);
  }

}
