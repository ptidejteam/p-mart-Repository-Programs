package src.AggregateEnforcer.Approach2;
public class Computer {

  //Constituting Object
  private CPU cpu;

  private String name;

  //Constructor
  public Computer(String n) {
    name = n;
    cpu = new CPU("Intel");
  }

  public boolean start() {
    //...
    System.out.println("CPU activated");
    return true;
  }
  public boolean executeTask() {
    //...
    System.out.println("CPU is Executing the Task");
    return true;
  }

  public boolean stop() {
    //...
    System.out.println("CPU is stopped");
    return true;
  }
}
class CPU {
  private String name;

  public CPU(String n) {
    name = n;
  }
}
