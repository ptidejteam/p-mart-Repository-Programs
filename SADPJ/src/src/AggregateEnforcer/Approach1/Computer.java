package src.AggregateEnforcer.Approach1;
public class Computer {

  //Constituting Object
  private CPU cpu;

  private String name;

  //Constructor
  public Computer(String n) {
    name = n;
  }

  public boolean start() {
    //...
    initCPU();
    System.out.println("CPU activated");
    return true;
  }
  public boolean executeTask() {
    //...
    initCPU();
    System.out.println("CPU is Executing the Task");
    return true;
  }

  public boolean stop() {
    //...
    initCPU();
    System.out.println("CPU is stopped");
    return true;
  }

  private void initCPU() {
    if (cpu == null) {
      cpu = new CPU("Intel");
    }
  }
}
class CPU {
  private String name;

  public CPU(String n) {
    name = n;
  }
}
