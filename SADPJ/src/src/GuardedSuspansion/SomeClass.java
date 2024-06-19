package src.GuardedSuspansion;
public class SomeClass {

  synchronized void guardedMethod() {
    while (!preCondition()) {
      try {
        //Continue to wait
        wait();
        //...
      } catch (InterruptedException e) {
        //...
      }
    }
    //Actual task implementation
  }

  synchronized void alterObjectStateMethod() {
    //Change the object state
    //.....

    //Inform waiting threads
    notify();
  }
  private boolean preCondition() {
    //...
    return false;
  }
}
