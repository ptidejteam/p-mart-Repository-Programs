package src.ReadWriteLock;
public class ReadWriteLock {

  private Object lockObj;
  private int totalReadLocksGiven;
  private boolean writeLockIssued;
  private int threadsWaitingForWriteLock;

  public ReadWriteLock() {
    lockObj = new Object();
    writeLockIssued = false;
  }


  /*
    A read lock can be issued if
    	there is no currently issued
    	write lock and
    	there is no thread(s) currently waiting for the
    	write lock
    */

  public void getReadLock() {
    synchronized (lockObj) {
      while ((writeLockIssued) ||
          (threadsWaitingForWriteLock != 0)) {
        try {
          lockObj.wait();
        } catch (InterruptedException e) {
          //
        }
      }
      //System.out.println(" Read Lock Issued");
      totalReadLocksGiven++;
    }
  }

  /*
    A write lock can be issued if
    	there is no currently issued
    	read or write lock
    */

  public void getWriteLock() {
    synchronized (lockObj) {
      threadsWaitingForWriteLock++;

      while ((totalReadLocksGiven != 0) ||
          (writeLockIssued)) {
        try {
          lockObj.wait();
        } catch (InterruptedException e) {
          //
        }
      }
      //System.out.println(" Write Lock Issued");
      threadsWaitingForWriteLock--;
      writeLockIssued = true;

    }
  }



  //used for releasing locks
  public void done() {
    synchronized (lockObj) {

      //check for errors
      if ((totalReadLocksGiven == 0) &&
          (!writeLockIssued)) {
        System.out.println(
          " Error: Invalid call to release the lock");
        return;
      }
      if (writeLockIssued)
        writeLockIssued = false;
      else
        totalReadLocksGiven--;

      lockObj.notifyAll();
    }

  }

}
