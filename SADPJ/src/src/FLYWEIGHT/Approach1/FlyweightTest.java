package src.FLYWEIGHT.Approach1;


import java.util.StringTokenizer;
import java.util.Vector;

public class FlyweightTest {

  public static void main(String[] args) throws Exception {
    Vector empList = initialize();
    FlyweightFactory factory =
      FlyweightFactory.getInstance();

    for (int i = 0; i < empList.size(); i++) {
      String s = (String) empList.elementAt(i);
      StringTokenizer st = new StringTokenizer(s, ",");
      String name = st.nextToken();
      String title = st.nextToken();
      String division = st.nextToken();

      FlyweightIntr flyweight =
        factory.getFlyweight(division);
      //associate the flyweight
      //with the extrinsic data object.
      VCard card = new VCard(name, title, flyweight);
      card.print();
    }
  }

  private static Vector initialize() {
    //for simplicity values are being hardcoded.

    Vector v = new Vector();
    v.add("name1,title1,North");
    v.add("name2,title2,South");
    v.add("name3,title1,North");
    v.add("name4,title3,East");
    v.add("name5,title4,East");
    v.add("name6,title2,East");
    v.add("name7,title1,West");
    v.add("name8,title3,West");
    v.add("name9,title1,West");
    v.add("name10,title6,South");
    v.add("name11,title5,North");
    v.add("name12,title1,North");

    return v;
  }
}
