package PROXY.txt;

import java.util.HashMap;

public class Category {

  private HashMap items;
  private String desc;

  public Category(String s) {
    desc = s;
    items = new HashMap();
  }

  public String getDesc() {
    return desc;
  }
  public void add(Item i) {
    items.put(i.getDesc(), i);
    System.out.println("Item '" + i.getDesc() +
                       "' has been added to the '" + 
                       getDesc() + "' Category ");
  }
  public void delete(Item i) {
    items.remove(i.getDesc());
    System.out.println("Item '" + i.getDesc() +
                       "' has been deleted from the '" + 
                       getDesc() + "' Category ");
  }
}
