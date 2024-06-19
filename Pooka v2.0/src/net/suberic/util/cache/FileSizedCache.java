package net.suberic.util.cache;
import java.io.*;

/**
 * A simple file-based cache.
 */
public class FileSizedCache extends AbstractSizedCache {

  // the location of the cache.
  File sourceDirectory = null;

  /**
   * Creates a new FileSizedCache.
   */
  public FileSizedCache(SizedCacheEntryFactory newFactory, long newMaxSize, long newMaxEntrySize, File directory) {
    setFactory(newFactory);
    setMaxSize(newMaxSize);
    setMaxEntrySize(newMaxEntrySize);
    setSourceDirectory(directory);
  }

  
  /**
   * Loads the cache.
   */
  public void loadCache() throws java.io.IOException {

  }
  
  /**
   * The directory in which this cache lives.
   */
  public File getSourceDirectory() {
    return sourceDirectory;
  }

  /**
   * The directory in which this cache lives.
   */
  public void setSourceDirectory(File newDirectory) {
    sourceDirectory = newDirectory;
  }
}
