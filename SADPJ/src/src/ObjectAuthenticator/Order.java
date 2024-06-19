package src.ObjectAuthenticator;
public class Order implements OrderIF {
  public void create(String item, int qty) {
    System.out.println(qty + " Units of Item " + item +
                       " has been ordered. ");
  }

}
