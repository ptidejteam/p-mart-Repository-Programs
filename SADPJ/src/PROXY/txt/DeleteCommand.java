package PROXY.txt;

public class DeleteCommand implements CommandInterface {
  Item item;
  Category cat;

  public DeleteCommand(Item i, Category c) {
    item = i;
    cat = c;
  }
  public void execute() {
    item.delete(cat);
    cat.delete(item);
  }
}
