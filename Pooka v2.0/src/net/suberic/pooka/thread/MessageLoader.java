package net.suberic.pooka.thread;

import net.suberic.pooka.*;
import net.suberic.pooka.event.*;
import net.suberic.pooka.gui.LoadMessageTracker;
import net.suberic.pooka.gui.MessageProxy;
import net.suberic.pooka.gui.FolderInternalFrame;
import net.suberic.pooka.cache.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;

/**
 * This class does the actual loading of the header information from 
 * the messages in the folder.  It also is set up to communicate with
 * a JProgessBar to show how far the loading has gotten.
 *
 * More specifically, this class takes an array of Messages and a 
 * Vector of Strings which are the column values which are to be put
 * into the table.  It then loads the values into a Vector of Vectors,
 * each of which contains the information for the Table for a group
 * of Messages.  It then throws a ChangeEvent to the listening
 * FolderTableModel.  The FolderTableModel can then get the information
 * using the getNewMessages() function.
 */

public class MessageLoader extends Thread {
  private FolderInfo folderInfo;
  private List columnValues;
  private List loadQueue = new LinkedList();
  private List priorityLoadQueue = new LinkedList();
  private List cacheQueue = new LinkedList();
  private List messageLoadedListeners = new LinkedList();
  private int updateMessagesCount = 10;
  private boolean mEnqueued = false;

  private boolean stopped = false;

  public static int NORMAL = 5;
  public static int HIGH = 10;

  /**
   * The Action that will get added to the ActionQueue when there
   * are messages to be loaded.
   */
  public Action mAction = null;

  /**
   * This creates a new MessageLoader from a FolderInfo object.
   */
  public MessageLoader(FolderInfo newFolderInfo) {
    folderInfo = newFolderInfo;
    mAction = new AbstractAction("Load Messages - " + newFolderInfo.getFolderID()) {
	public void actionPerformed(ActionEvent ae) {
	  loadNextBatch();
	}
      };
  }
  
  /**
   * Loads the messages in the queue.
   */
  public void loadNextBatch() {
    MessageProxy mp;
    
    // start this load section.
    int queueSize = getQueueSize();    
    if (! stopped) {
      if (queueSize > 0) {
	folderInfo.getLogger().log(java.util.logging.Level.FINE, folderInfo.getFolderID() + " loading " + queueSize + " messages.");
	
	// get the batch sizes.
	int fetchBatchSize = 50;
	try {
	  fetchBatchSize = Integer.parseInt(Pooka.getProperty("Pooka.fetchBatchSize", "50"));
	} catch (NumberFormatException nfe) {
	}
	
	FetchProfile fetchProfile = getFolderInfo().getFetchProfile();
	
	// get the next batch.
	List messageProxies = retrieveNextBatch(fetchBatchSize);
	List toFetchInfos = new LinkedList();
	
	// go through and find all of the messages that need to be fetched
	// or refetched, and add them to the toFetchInfos list.
	for (int i = 0 ; i < messageProxies.size(); i++) {
	  MessageInfo fetchCheckInfo = ((MessageProxy) messageProxies.get(i)).getMessageInfo();
	  if (! fetchCheckInfo.hasBeenFetched()) {
	    toFetchInfos.add(fetchCheckInfo);
	  }
	}
	
	if (toFetchInfos.size() > 0) {
	  try {
	    MessageInfo[] toFetch = new MessageInfo[toFetchInfos.size()];
	    toFetch = (MessageInfo[]) toFetchInfos.toArray(toFetch);
	    getFolderInfo().fetch(toFetch, fetchProfile);
	  } catch(MessagingException me) {
	    if (folderInfo.getLogger().isLoggable(java.util.logging.Level.WARNING)) {
	      System.out.println("caught error while fetching for folder " + getFolderInfo().getFolderID() + ":  " + me);
	      me.printStackTrace();
	    }
	  }
	}
	
	// now load each individual messageproxy.
	// and refresh each message.
	for (int i = 0 ; i < messageProxies.size(); i++) {
	  mp = (MessageProxy) messageProxies.get(i);
	  try {
	    if (! mp.isLoaded())
	      mp.loadTableInfo();
	    if (mp.needsRefresh()) {
	      mp.refreshMessage();
	    }
	    else if (! mp.matchedFilters()) {
	      mp.matchFilters();
	    }
	  } catch (Exception e) {
	    if (folderInfo.getLogger().isLoggable(java.util.logging.Level.WARNING)) {
	      e.printStackTrace();
	    }
	  }
	}

      } else if (getCacheQueueSize() > 0) {
	try {
	  MessageProxy nextCache = (MessageProxy) cacheQueue.remove(0);
	  if (folderInfo instanceof CachingFolderInfo) {
	    MessageInfo mi = nextCache.getMessageInfo();
	    MimeMessage mimeMessage = (MimeMessage) mi.getMessage();
	    CachingFolderInfo cfi = (CachingFolderInfo) folderInfo;
	    if (cfi.getFolderDisplayUI() != null) {
	      cfi.showStatusMessage(cfi.getFolderDisplayUI(), "caching messages, " + getCacheQueueSize() + " remaining...");
	    }
	    cfi.getCache().cacheMessage(mimeMessage, cfi.getUID(mimeMessage), cfi.getUIDValidity(), SimpleFileCache.MESSAGE, false);
	    if (cfi.getFolderDisplayUI() != null && getCacheQueueSize() == 0) {
	      cfi.clearStatusMessage(cfi.getFolderDisplayUI());
	    }
	  }
	} catch (Exception e) {
	  if (folderInfo.getLogger().isLoggable(java.util.logging.Level.WARNING)) {
	    e.printStackTrace();
	  }
	
	}
      }
    }
    if (! stopped && (getQueueSize() > 0 || getCacheQueueSize() > 0)) {
      enqueue();
    }
  }
  
  /**
   * Fires a new MessageLoadedEvent to each registered MessageLoadedListener.
   */  
  public void fireMessageLoadedEvent(int type, int numMessages, int max) {
    /*
    for (int i = 0; i < messageLoadedListeners.size(); i ++) {
      ((MessageLoadedListener)messageLoadedListeners.get(i)).handleMessageLoaded(new MessageLoadedEvent(this, type, numMessages, max));
    }
    */
  }
  
  /**
   * Adds a MessageLoadedListener to the messageLoadedListener list.
   */
  public void addMessageLoadedListener(MessageLoadedListener newListener) {
    if (messageLoadedListeners.indexOf(newListener) == -1)
      messageLoadedListeners.add(newListener);
  }
  
  /**
   * Removes a MessageLoadedListener from the messageLoadedListener list,
   * if it's in the list.
   */
  public void removeMessageLoadedListener(MessageLoadedListener remListener) {
    if (messageLoadedListeners.indexOf(remListener) > -1)
      messageLoadedListeners.remove(remListener);
  }
  
  /**
   * Adds the MessageProxy(s) to the loadQueue.
   */
  public synchronized void loadMessages(MessageProxy mp) {
    loadMessages(mp, NORMAL);
  }

  /**
   * Adds the MessageProxy(s) to the loadQueue.
   */
  public synchronized void loadMessages(MessageProxy mp, int pPriority) {
    if (pPriority > NORMAL) {
      if (! priorityLoadQueue.contains(mp))
	priorityLoadQueue.add(mp);
      loadQueue.remove(mp);
    } else {
      if (! priorityLoadQueue.contains(mp) && ! loadQueue.contains(mp))
	loadQueue.add(mp);
    }

    if (! isEnqueued())
      enqueue();
  }
  
  /**
   * Adds the MessageProxy(s) to the loadQueue.
   */
  public synchronized void loadMessages(MessageProxy[] mp) {
    loadMessages(mp, NORMAL);
  }

  /**
   * Adds the MessageProxy(s) to the loadQueue.
   */
  public synchronized void loadMessages(MessageProxy[] mp, int pPriority) {
    loadMessages(Arrays.asList(mp), pPriority);
  }
  
  /**
   * Adds the MessageProxy(s) to the loadQueue.
   */
  public synchronized void loadMessages(List mp) {
    loadMessages(mp, NORMAL);
  }

  /**
   * Adds the MessageProxy(s) to the loadQueue.
   */
  public synchronized void loadMessages(List mp, int pPriority) {
    if (mp != null && mp.size() > 0) {
      if (pPriority > NORMAL) {
	loadQueue.removeAll(mp);
	addUniqueReversed(priorityLoadQueue, mp);
      } else {
	List copy = new ArrayList(mp);
	copy.removeAll(priorityLoadQueue);
	addUniqueReversed(loadQueue, copy);
      }
    }
    
    enqueue();
  }
  
  /**
   * Adds the MessageProxy(s) to the cacheQueue.
   */
  public synchronized void cacheMessages(List mp) {
    if (mp != null && mp.size() > 0) {
	addUniqueReversed(cacheQueue, mp);
    }
    
    enqueue();
  }
  
  /**
   * Adds the MessageProxy(s) to the cacheQueue.
   */
  public synchronized void cacheMessages(MessageProxy[] mp) {
    if (mp != null && mp.length > 0) {
	addUniqueReversed(cacheQueue, Arrays.asList(mp));
    }
    
    enqueue();
  }
  
  /**
   * retrieves all the messages from the loadQueue, and resets that
   * List to 0 (an empty List).
   *
   * generally, use retrieveNextBatch() instead.
   */
  public synchronized List retrieveLoadQueue() {
    List returnValue = new LinkedList();
    returnValue.addAll(priorityLoadQueue);
    returnValue.addAll(loadQueue);
    loadQueue = new LinkedList();
    priorityLoadQueue = new LinkedList();
    return returnValue;
  }
  
  /**
   * Adds all of the entries in toAdd to targetList, in reversed order.
   */
  private void addUniqueReversed(List targetList, List toAdd) {
    for (int i = toAdd.size() - 1; i >= 0; i--) {
      Object current = toAdd.get(i);
      if (current != null && ! targetList.contains(current))
	targetList.add(current);
    }
  }

  /**
   * Retrieves the next pCount messages from the queue, or returns null
   * if there are no entries in the queue.
   */
  public synchronized List retrieveNextBatch(int pCount) {
    int plqLength = priorityLoadQueue.size();
    int lqLength = loadQueue.size();

    // check to see if we actually have anything in the queue.
    if (plqLength + lqLength > 0) {
      List returnValue = new LinkedList();

      // adding the priority queue first
      if (plqLength > 0) {
	// if the priority queue is larger than (or the same size as) the 
	// requested count, then just return it.
	if (plqLength >= pCount) {
	  List subList = priorityLoadQueue.subList(0, pCount);
	  returnValue.addAll(subList);
	  subList.clear();
	  return returnValue;
	} else {
	  // just add of the priority queue, and go on.
	  returnValue.addAll(priorityLoadQueue);
	  priorityLoadQueue.clear();
	}
      }

      // add in the normal queue now.
      if (lqLength > 0) {
	int newCount = pCount - plqLength;
	if (lqLength >= newCount) {
	  List subList = loadQueue.subList(0, newCount);
	  returnValue.addAll(subList);
	  subList.clear();
	} else {
	  returnValue.addAll(loadQueue);
	  loadQueue.clear();
	}
      }
      
      return returnValue;
    } else {
      return null;
    }
  }

  /**
   * Adds the action for this MessageLoader to the FolderThread of the
   * Folder.
   */
  public synchronized void enqueue() {
    //FIXME
    if (getFolderInfo() != null && getFolderInfo().getFolderThread() != null && ! getFolderInfo().getFolderThread().getQueue().contains(mAction)) {
      getFolderInfo().getFolderThread().addToQueue(mAction, null, net.suberic.util.thread.ActionThread.PRIORITY_LOW);
    }
  }
  public int getUpdateMessagesCount() {
    return updateMessagesCount;
  }
  
  public void setUpdateMessagesCount(int newValue) {
    updateMessagesCount = newValue;
  }

  /**
   * Returns the total amount left in the queue.
   */
  public synchronized int getQueueSize() {
    return loadQueue.size() + priorityLoadQueue.size();
  }

  /**
   * Returns the total amount left in the cache queue.
   */
  public synchronized int getCacheQueueSize() {
    return cacheQueue.size();
  }

  public List getColumnValues() {
    return columnValues;
  }
  
  public void setColumnValues(List newValue) {
    columnValues=newValue;
  }
  
  public FolderInfo getFolderInfo() {
    return folderInfo;
  }
  
  public boolean isEnqueued() {
    return mEnqueued;
  }

  /**
   * Stops the thread.
   */
  public void stopLoading() {
    stopped = true;
  }
}






