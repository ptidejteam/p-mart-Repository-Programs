package net.suberic.pooka.thread;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.OperationCancelledException;
import net.suberic.pooka.Pooka;
import net.suberic.util.thread.*;
import javax.mail.*;
import java.util.*;
import java.util.logging.*;
import java.awt.event.ActionEvent;

/**
 * This class polls the underlying Folder of a FolderInfo in order to make
 * sure that the UnreadMessageCount is current and to make sure that the
 * Folder stays open.
 */
public class FolderTracker extends Thread {
  private Vector mUpdateInfos = new Vector();
  private CheckFolderAction mAction = new CheckFolderAction();
  private long mTrackerNextUpdateTime = -1;
  private boolean mStopped = false;
  private Logger mLogger = null;

  /**
   * Stores the information about the Folder to be updated.
   */
  private class UpdateInfo {
    // the Folder for this UpdateInfo
    FolderInfo folder;
    // milliseconds between checks
    long updateCheckMilliseconds;
    // next time to update.
    long nextFolderUpdate;
    // whether or not we're waiting on this to complete.
    boolean updateRunning = false;

    /**
     * Creates a new UpdateInfo for FolderInfo <code>info</code>, with
     * <code>updateCheck</code> milliseconds between checks.
     */
    public UpdateInfo(FolderInfo info, long updateCheck) {
      folder = info;
      updateCheckMilliseconds = updateCheck;
      nextFolderUpdate = Calendar.getInstance().getTime().getTime() + updateCheckMilliseconds;
    }

    /**
     * Updates the Folder.
     */
    public void update() {
      getLogger().fine("creating update action for folder " + folder.getFolderID());
      updateRunning = true;
      folder.getFolderThread().addToQueue(getAction(), new ActionEvent(this, 1, "folder-check - " + folder.getFolderID()), ActionThread.PRIORITY_LOW);
    }

    /**
     * Calculates the new next update time.
     */
    public void newUpdateTime() {
      updateRunning = false;
      nextFolderUpdate = Calendar.getInstance().getTime().getTime() + updateCheckMilliseconds;
      getLogger().finer("calculating new update time for " + folder.getFolderID() + ":  " + nextFolderUpdate);
      updateTrackerNextTime(nextFolderUpdate);
    }

    /**
     * Checks to see if we should run an update now.
     */
    public boolean shouldUpdate(long currentTime) {
      return (! updateRunning && nextFolderUpdate <= currentTime) ;
    }

    /**
     * Returns the nextFolderUpdate time for this Info.
     */
    public long getNextFolderUpdate() { return nextFolderUpdate; }

    /**
     * Returns the FolderInfo for this Info.
     */
    public FolderInfo getFolderInfo() { return folder; }

    /**
     * Returns whether or not this is waiting on an outstaning update
     * action.
     */
    public boolean isUpdateRunning() { return updateRunning; }

  } // end UpdateInfo.


  /**
   * This creates a new FolderTracker from a FolderInfo object.
   */
  public FolderTracker() {
    super("Folder Tracker thread");
    this.setPriority(1);
  }

  // list management.

  /**
   * This adds a FolderInfo to the FolderTracker.
   */
  public void addFolder(FolderInfo newFolder) {
    if (newFolder == null)
      return ;

    getLogger().fine("adding folder " + newFolder.getFolderID());
    long updateCheckMilliseconds;
    String updateString = Pooka.getProperty("Pooka.updateCheckMilliseconds", "60000");

    if (newFolder.getParentStore() != null) {
      updateString = Pooka.getProperty(newFolder.getFolderProperty() + ".updateCheckSeconds", Pooka.getProperty(newFolder.getParentStore().getStoreProperty() + ".updateCheckSeconds", Pooka.getProperty("Pooka.updateCheckSeconds", "300")));
    }
    try {
      updateCheckMilliseconds = Long.parseLong(updateString) *1000;
    } catch (Exception e) {
      updateCheckMilliseconds = 60000;
    }

    UpdateInfo info = new UpdateInfo(newFolder, updateCheckMilliseconds);
    mUpdateInfos.add(info);
    updateTrackerNextTime(info.getNextFolderUpdate());
  }

  /**
   * This removes a FolderInfo from the FolderTracker.
   */
  public void removeFolder(FolderInfo folder) {
    if (folder == null)
      return;

    getLogger().fine("removing folder " + folder.getFolderID() + " from tracker.");

    for (int i = 0 ; i < mUpdateInfos.size() ; i++)
      if (((UpdateInfo) mUpdateInfos.elementAt(i)).folder == folder)
        mUpdateInfos.removeElementAt(i);
  }

  // end folder administration

  // next update time administration

  /**
   * Adds a new update time.  This assumes that there's already a
   * valid update time, and that we're just making sure that the
   * new time isn't sooner than that.
   */
  public synchronized void updateTrackerNextTime(long pTime) {
    getLogger().finer("updating tracker next time with new value " + pTime + ", old value " + mTrackerNextUpdateTime);
    if (pTime < mTrackerNextUpdateTime) {
      mTrackerNextUpdateTime = pTime;
      getLogger().finer("new time is newer than old time; interrupting thread.");
      interrupt();
    }
  }

  /**
   * This returns the next update time.  This assumes that the old
   * update time is no longer valid, and that we should recalculate
   * a new update time.
   */
  public synchronized long calculateNextUpdateTime(long currentTime) {
    getLogger().finer("calculating next update time.");

    long nextTime = -1;
    Iterator iter = mUpdateInfos.iterator();
    while (iter.hasNext()) {
      UpdateInfo current = (UpdateInfo) iter.next();
      if (! current.isUpdateRunning()) {
        if (nextTime == -1)
          nextTime = current.getNextFolderUpdate();
        else
          nextTime = Math.min(nextTime, current.getNextFolderUpdate());
      }
    }

    if (nextTime == -1)
      nextTime = currentTime + 120000;

    mTrackerNextUpdateTime = nextTime;
    getLogger().finer("new next update time:  " + mTrackerNextUpdateTime);

    return mTrackerNextUpdateTime;
  }

  // end update time admin

  // main method(s)

  /**
   * This runs the thread, running checkFolder() every
   * updateCheckMilliseconds until the thread is interrupted.
   */
  public void run() {
    while (true && ! mStopped) {
      try {
        getLogger().fine("running folder tracker update.");

        long currentTime = Calendar.getInstance().getTime().getTime();
        updateFolders(currentTime);
        long sleepTime = calculateNextUpdateTime(currentTime) - currentTime;
        if (sleepTime > 0) {
          getLogger().finer("sleeping for " + sleepTime + " milliseconds.");

          sleep(sleepTime);
        } else {
          getLogger().finer("sleep time is negative; not sleeping.");
        }
      } catch (InterruptedException ie) {
        // on interrupt, just continue.
        getLogger().finer("caught InterruptedException.");
      }
    }

    getLogger().fine("Stopped.  Shutting down Folder Tracker.");
  }

  /**
   * Goes through the list of folders and updates the ones that are
   * due for an update.
   */
  public void updateFolders(long currentTime) {
    for (int i = 0; i < mUpdateInfos.size(); i++) {
      UpdateInfo info = (UpdateInfo)mUpdateInfos.elementAt(i);
      if (info.shouldUpdate(currentTime))
        info.update();
    }
  }

  // end main methods

  // thread control

  /**
   * Singals that the tracker thread should stop.
   */
  public void setStopped(boolean pStopped) {
    mStopped = pStopped;
    getLogger().fine("setting FolderTracker stopped to " + mStopped);
    if (mStopped == true)
      interrupt();
  }

  // end thread control

  // action section

  /**
   * This returns the action to run when it's time to update the folder.
   */
  public javax.swing.Action getAction() {
    return mAction;
  }

  /**
   * The Action that's put in the queue for checking the folder
   * status.
   */
  public class CheckFolderAction extends javax.swing.AbstractAction {
    public CheckFolderAction() {
      super("folder-check");
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      UpdateInfo info = (UpdateInfo) e.getSource();
      try {
        getLogger().fine("running checkFolder on " + info.getFolderInfo().getFolderID());
        info.getFolderInfo().checkFolder();
      } catch (OperationCancelledException oce) {
      } catch (MessagingException me) {
        // ignore; only show if we're debugging.
        if (getLogger().isLoggable(Level.FINE)) {
          getLogger().fine("caught exception checking folder " + info.getFolderInfo().getFolderID() + ":  " + me);
          me.printStackTrace();
        }
      } finally {
        info.newUpdateTime();
      }
    }
  }

  // end action section

  // logging

  /**
   * Gets the Logger for this class.
   */
  public Logger getLogger() {
    if (mLogger == null) {
      mLogger = java.util.logging.Logger.getLogger("Pooka.debug.folderTracker");
    }

    return mLogger;
  }
}
