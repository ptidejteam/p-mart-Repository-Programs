package VirtualProxy;

import java.util.Hashtable;

public class CAR {
  private static CAR car;
  private Hashtable groups;

  public static CAR getCAR() {
    if (car == null)
      car = new CAR();
    return car;
  }

  private CAR() {
    groups = new Hashtable();
  }

  public CARGroup createGroup(String name) {
    CARGroup group = (CARGroup) groups.get(name);
    if (group == null) {
      group = new CARGroup(name);
      groups.put(name, group);
    }
    return group;
  }

  public void deleteGroup(String name) {
  }


  class CARGroup {

    private Hashtable attributes;
    private String name;

    private CARGroup(String grpName) {
      name = grpName;
      attributes = new Hashtable();
    }

    public void setAttribute(String name, Object val) {
      attributes.put(name, val);
    }

    public Object getAttribute(String name) {
      return attributes.get(name);
    }
  }

}

