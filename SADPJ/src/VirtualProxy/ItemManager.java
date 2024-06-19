package VirtualProxy;


public class ItemManager {
  private CAR car;

  public ItemManager() {
    car = CAR.getCAR();
  }

  public String getItemDetails(String item, String category) {

    String value =
      (String) car.createGroup(category).getAttribute(
        item);

    if (value == null) {
      DBManager objDBManager = new DBManager();
      String details =
        objDBManager.getItemDetails(item, category);
      CAR.CARGroup group = car.createGroup(category);
      group.setAttribute(item, details);
      value = details;
      System.out.println("From DB");
    } else {
      System.out.println("From Cache");
    }
    return value;
  }

}
