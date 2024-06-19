package net.suberic.pooka;
import net.suberic.pooka.gui.*;
import net.suberic.util.thread.ActionThread;
import javax.mail.*;
import java.util.*;

/**
 * The basics for an outgoing mail queue.
 *
 * This class is basically a placeholder for now, since i realized that if
 * you're connected all the time, you don't need a send queue, and if you're
 * running in disconnected mode, this queue won't do.
 */
public class MailQueue {
  Hashtable transportTable;
  Session session;
  
  ActionThread thread = new ActionThread(Pooka.getProperty("thread.sendMailThread", "Send Mail Thread"));
  
  public MailQueue(Session newSession) {
    session = newSession;
    transportTable = new Hashtable();
    thread.start();
  }
  
  /**
   * This adds the Message to the queue associated with the given 
   * transportURL.  If sendPrecommand != "", then sendPrecommand will be executed before 
   * attempting to send each message. If Message.sendImmediately is set to true, then this
   * will also go ahead and try to send all queued Messages using
   * sendQueued().
   *
   * Note that at the moment, only Message.sendImmediately is supported.
   */
  public void sendMessage(NewMessageInfo pNmi, URLName pTransportURL, String sendPrecommand) throws MessagingException {
    /*
      Vector transportQueue = (Vector)transportTable.get(transportURL);
      if (transportQueue == null) {
      transportQueue = new Vector();
      transportTable.put(transportURL, transportQueue);
      }
      
      transportQueue.add(m);
      if (Pooka.getProperty("Message.sendImmediately", "false").equals("true")) {
      sendQueued();
      }
    */
    
    final URLName transportURL = pTransportURL;
    final NewMessageInfo nmi = pNmi;
    final String precommand = sendPrecommand;
    
    if (Pooka.getProperty("Message.sendImmediately", "false").equalsIgnoreCase("true")) {
      thread.addToQueue(new net.suberic.util.thread.ActionWrapper(new javax.swing.AbstractAction() {
	  
	  public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	    try {
	      Transport sendTransport;
	      if (precommand.length() > 0) {
                try {
		    System.out.println("MailQueue: Running precommand: " + precommand);
                    Process p = Runtime.getRuntime().exec(precommand);
                    p.waitFor();
                } catch (Exception ex)
                {
                    System.out.println("Could not run SMTP precommand:");
                    ex.printStackTrace();
                }
              }
              System.out.println("MailQueue: Trying to connect.."); 
	      sendTransport = session.getTransport(transportURL); 
	      sendTransport.connect();
	      
	      Message m = nmi.getMessage();
	      sendTransport.sendMessage(m, m.getAllRecipients());
	      
	      ((NewMessageProxy)nmi.getMessageProxy()).sendSucceeded(true);
	    } catch (MessagingException me) {
	      ((NewMessageProxy)nmi.getMessageProxy()).sendFailed(null,me);
	    }
	  }
	}, thread), new java.awt.event.ActionEvent(nmi, 1, "message-send"));
      
      
    }	      
    
  }
  
  /**
   * This will try to send all the Messages currently in the queue.  If 
   * it encounters any major errors (i.e being unable to connect to the
   * mailserver), it will more or less exit out, throwing a 
   * MessagingException saying why it failed.  If it manages to send some
   * Messages but not others, it will send the ones that it can, and return
   * a MessagingException with all the individual sub-exceptions tacked
   * on to it using MessagingException.setNextException().
   *
   * Note:  do not use this method.  i am now just keeping it here as
   * a placeholder until i implement a real outgoing message queue.
   */
  public void sendQueued() throws MessagingException {
    Enumeration keys = transportTable.keys();
    URLName transportURL;
    Transport sendTransport;
    Vector sendQueue;
    Message msg;
    boolean error = false;
    
    while (keys.hasMoreElements()) {
      transportURL = (URLName)keys.nextElement();
      sendQueue = (Vector)transportTable.get(transportURL);
      
      if (!(sendQueue.isEmpty())) {
	MessagingException returnException = null;
	sendTransport = session.getTransport(transportURL); 
	sendTransport.connect();
	
	for (int i=sendQueue.size()-1 ; i >= 0; i--) {
	  msg = (Message)sendQueue.elementAt(i);
	  try {
	    sendTransport.sendMessage(msg, msg.getAllRecipients());
	    sendQueue.removeElementAt(i);
	  } catch (SendFailedException sfe) {
	    if (returnException == null) 
	      returnException = sfe;
	    else
	      returnException.setNextException(sfe);
	  } catch (MessagingException me) {
	    if (returnException == null)  
	      returnException = me;
	    else
	      returnException.setNextException(me);
	  }
	}
	if (returnException != null)
	  throw returnException;
      }
    }
  }
}
    




