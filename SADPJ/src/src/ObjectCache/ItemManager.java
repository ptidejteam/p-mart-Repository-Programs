package src.ObjectCache;
public class ItemManager {
  ItemCache cache;
  DBManager manager;

  public ItemManager() {
    cache = new ItemCache();
    manager = new DBManager();
  }

  public void activate(String code) {
    if (cache.getItem(code) != null) {
      System.out.println("Item Already Activated - cache");

    } else {
      if (manager.isActiveItem(code)) {
        System.out.println(
          "Item Already Activated - DB Access");
      } else {
        manager.activateItem(code);
        System.out.println(
          "Item Activated successfully");
        //add to the cache
        cache.addItem(code);
      }
    }
  }
}
class DBManager {
  public boolean isActiveItem(String code) {
    //db access code goes here

    //assume that the item is not already activated.
    return false;
  }

  public void activateItem(String code) {
    //db access code goes here
  }

}
