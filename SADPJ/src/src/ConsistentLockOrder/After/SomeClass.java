package src.ConsistentLockOrder.After;
public class SomeClass {

  private ClassA objectA;
  private ClassB objectB;

  public SomeClass() {
    objectA = new ClassA();
    objectB = new ClassB();
  }

  public void Method_A() {
    synchronized (objectA) {
      synchronized (objectB) {
        process_A();
      }
    }
  }

  public void Method_B() {
    synchronized (objectA) {
      synchronized (objectB) {
        process_B();
      }
    }
  }

  private void process_A() {
    //
  }

  private void process_B() {
    //
  }

} class ClassA {
} class ClassB {
}
