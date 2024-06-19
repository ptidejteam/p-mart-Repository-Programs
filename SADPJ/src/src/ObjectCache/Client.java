package src.ObjectCache;
public class Client {
  public static void main(String[] args) {
    ItemManager manager = new ItemManager();

    manager.activate("1001001000");
    manager.activate("1001001001");
    manager.activate("1001001002");
    manager.activate("1001001000");
    manager.activate("1001001004");
    manager.activate("1001001005");
    manager.activate("1001001006");
    manager.activate("1001001002");
    manager.activate("1001001004");
    manager.activate("1001001002");
    manager.activate("1001001000");
  }
}
