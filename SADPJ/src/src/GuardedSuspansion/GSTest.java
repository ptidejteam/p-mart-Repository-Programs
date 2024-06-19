package src.GuardedSuspansion;
public class GSTest {
  public static void main(String[] args) {
    ParkingLot parking = new ParkingLot();
    new Member("Member1", parking);
    new Member("Member2", parking);
    new Member("Member3", parking);
    new Member("Member4", parking);
    new Member("Member5", parking);
    new Member("Member6", parking);
  }

} class ParkingLot {
  //Assume 4 parking slots for simplicity
  public static final int MAX_CAPACITY = 4;
  private int totalParkedCars = 0;

  public synchronized void park(String member) {
    while (totalParkedCars >= MAX_CAPACITY) {
      try {
        System.out.println(" The parking lot is full " +
                           member + " has to wait ");
        wait();
      } catch (InterruptedException e) {
        //
      }
    }
    //precondition is true
    System.out.println(member + " has parked");
    totalParkedCars = totalParkedCars + 1;
  }
  public synchronized void leave(String member) {
    totalParkedCars = totalParkedCars - 1;
    System.out.println(member +
                       " has left, notify a waiting member");
    notify();
  }
}

class Member extends Thread {
  private ParkingLot parking;
  private String name;

  Member(String n, ParkingLot p) {
    name = n;
    parking = p;
    start();
  }
  public void run() {
    System.out.println(name + " is ready to park");
    parking.park(name);
    try {
      sleep(500);
    } catch (InterruptedException e) {
      //
    }
    //leave after 500ms
    parking.leave(name);
  }
}
