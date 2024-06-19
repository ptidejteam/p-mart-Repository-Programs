package src.ReadWriteLock;
public class RWTest {

  public static void main(String[] args) {
    Item item = new Item("CompScience-I");
    new MemberTransaction("Member1", item, "StatusCheck");
    new MemberTransaction("Member2", item, "StatusCheck");
    new MemberTransaction("Member3", item, "CheckOut");
    new MemberTransaction("Member4", item, "CheckOut");
    new MemberTransaction("Member5", item, "CheckOut");
    new MemberTransaction("Member6", item, "StatusCheck");

  }
}
