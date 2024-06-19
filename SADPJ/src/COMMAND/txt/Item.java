package COMMAND.txt;

import java.util.HashMap;

public class Item {

  private HashMap categories;
  private String desc;

  public Item(String s) {
    desc = s;
    categories = new HashMap();
  }

  public String getDesc() {
    return desc;
  }
  public void add(Category cat) {
    categories.put(cat.getDesc(), cat);
  }
  public void delete(Category cat) {
    categories.remove(cat.getDesc());
  }

}
