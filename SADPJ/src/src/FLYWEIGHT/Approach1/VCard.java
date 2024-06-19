package src.FLYWEIGHT.Approach1;


public class VCard {

  String name;
  String title;
  FlyweightIntr objFW;

  public VCard(String n, String t, FlyweightIntr fw) {
    name = n;
    title = t;
    objFW = fw;
  }

  public void print() {
    System.out.println(name);
    System.out.println(title);
    System.out.println(objFW.getAddress() + "-" +
                       objFW.getCity() + "-" + 
                       objFW.getState() + "-" +
                       objFW.getZip());
    System.out.println("----------------");
  }

}
