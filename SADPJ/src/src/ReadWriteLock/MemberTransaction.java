package src.ReadWriteLock;
public class MemberTransaction extends Thread {

  private String name;
  private Item item;
  private String operation;


  public MemberTransaction(String n, Item i, String p) {
    name = n;
    item = i;
    operation = p;
    start();
  }

  public void run() {
    //all members first read the status
    item.getStatus(name);

    if (operation.equals("CheckOut")) {
      System.out.println("\n" + name +
                         " is ready to checkout the item.");
      item.checkOut(name);
      try {
        sleep(1);
      } catch (InterruptedException e) {
        //
      }
      item.checkIn(name);
    }
  }
}
