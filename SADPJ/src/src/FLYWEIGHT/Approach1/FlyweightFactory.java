package src.FLYWEIGHT.Approach1;

import java.util.HashMap;

// singleton Flyweight Factory
public class FlyweightFactory {
  private HashMap lstFlyweight;
  private static FlyweightFactory factory =
    new FlyweightFactory();

  private FlyweightFactory() {
    lstFlyweight = new HashMap();
  }

  public synchronized FlyweightIntr getFlyweight(
    String divisionName) {
    if (lstFlyweight.get(divisionName) == null) {
      FlyweightIntr fw = new Flyweight(divisionName);
      lstFlyweight.put(divisionName, fw);
      return fw;
    } else {
      return (FlyweightIntr) lstFlyweight.get(divisionName);
    }
  }
  public static FlyweightFactory getInstance() {
    return factory;
  }

  //Inner flyweight class
  private class Flyweight implements FlyweightIntr {
    private String company;
    private String address;
    private String city;
    private String state;
    private String zip;

    private void setValues(String cmp, String addr,
        String cty, String st, String zp) {

      company = cmp;
      address = addr;
      city = cty;
      state = st;
      zip = zp;

    }

    private Flyweight(String division) {
      // values are hard coded
      //for simplicity
      if (division.equals("North")) {
        setValues("CMP","addr1", "cty1","st1","10000");
      }
      if (division.equals("South")) {
        setValues("CMP","addr2", "cty2","st2","20000");
      }
      if (division.equals("East")) {
        setValues("CMP","addr3", "cty3","st3","30000");
      }
      if (division.equals("West")) {
        setValues("CMP","addr4", "cty4","st4","40000");
      }
    }

    public String getCompany() {
      return company;
    }
    public String getAddress() {
      return address;
    }
    public String getCity() {
      return city;
    }
    public String getState() {
      return state;
    }
    public String getZip() {
      return zip;
    }

  }// end of Flyweight
}// end of FlyweightFactory

