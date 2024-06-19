package VirtualProxy;


public class DBManager {
  public String getItemDetails(String item, String category) {
    //for simplicity item details are hard coded.
    String value = "Item : " + item + " of " + category;

    return value;
  }

}
