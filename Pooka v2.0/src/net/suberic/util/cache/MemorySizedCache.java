package net.suberic.util.cache;

/**
 * A simple memory cache.
 */
public class MemorySizedCache extends AbstractSizedCache {

  /**
   * Creates a new MemorySizedCache.
   */
  public MemorySizedCache(SizedCacheEntryFactory newFactory, long newMaxSize, long newMaxEntrySize) {
    setFactory(newFactory);
    setMaxSize(newMaxSize);
    setMaxEntrySize(newMaxEntrySize);
  }
}
