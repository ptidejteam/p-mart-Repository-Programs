package src.ImmutableObject.After;

public class Car implements Cloneable {
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }

  }
}
