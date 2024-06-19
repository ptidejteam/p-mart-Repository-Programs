package VirtualProxy;


public class TestClient {

  public static void main(String[] args) {
    ItemManager manager = new ItemManager();
    System.out.println(
      manager.getItemDetails("Commando","Video"));
    System.out.println(
      manager.getItemDetails("Commando","DVD"));
    System.out.println(
      manager.getItemDetails("Jaws","Video"));
    System.out.println(
      manager.getItemDetails("Jaws","Electronics"));
    System.out.println(
      manager.getItemDetails("Interview Tips","CD"));
    System.out.println(
      manager.getItemDetails("Jaws","Video"));
    System.out.println(
      manager.getItemDetails("Interview Tips","CD"));
  }
}
