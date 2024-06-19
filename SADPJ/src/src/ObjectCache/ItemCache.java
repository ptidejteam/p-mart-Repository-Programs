package src.ObjectCache;
import java.util.Vector;

public class ItemCache {
  private final static int Max_cache_size = 5;
  Vector cache;

  public ItemCache() {
    cache = new Vector();
  }

  public String getItem(String code) {
    String barCode = null;
    int pos = cache.indexOf(code);
    if (pos != -1)
      barCode = (String) cache.get(pos);
    return barCode;
  }

  public void addItem(String code) {
    // if the max limit is reached
    // remove the LRU item
    if (cache.size() == Max_cache_size) {
      cache.remove(0);
    }
    cache.add(code);
  }
}
