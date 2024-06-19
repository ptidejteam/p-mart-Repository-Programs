package COMMAND.txt;

public class AddCommand implements CommandInterface {
  Item item;
  Category cat;

  public AddCommand(Item i, Category c) {
    item = i;
    cat = c;
  }
  public void execute() {
    item.add(cat);
    cat.add(item);
  }
}
