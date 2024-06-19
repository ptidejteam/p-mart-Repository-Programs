package net.suberic.pooka;
import net.suberic.pooka.crypto.CryptoAttachment;
import net.suberic.pooka.crypto.SignedAttachment;
import net.suberic.pooka.filter.FilterAction;
import net.suberic.pooka.gui.MessageProxy;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.event.*;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;

public class MessageInfo {
  // the wrapped Message
  Message message;

  // the source FolderInfo
  FolderInfo folderInfo;

  // if the tableInfo has been loaded yet.
  boolean loaded = false;

  // if the message has been read
  boolean seen = false;

  // if the attachments have been loaded yet.
  boolean attachmentsLoaded = false;

  // the MessageProxy associated with this MessageInfo
  MessageProxy messageProxy;

  // the attachments on the message.
  AttachmentBundle attachments;

  // the CryptoInfo for this Message.
  MessageCryptoInfo cryptoInfo = new MessageCryptoInfo(this);

  // if the Message itself has been loaded via fetch()
  boolean fetched = false;

  public static int FORWARD_AS_ATTACHMENT = 0;
  public static int FORWARD_QUOTED = 1;
  public static int FORWARD_AS_INLINE = 2;

  protected MessageInfo() {
  }

  /**
   * This creates a new MessageInfo from the given FolderInfo and Message.
   */
  public MessageInfo(Message newMessage, FolderInfo newFolderInfo) {
    folderInfo = newFolderInfo;
    message = newMessage;
  }

  /**
   * This loads the Attachment information into the attachments vector.
   */
  public void loadAttachmentInfo() throws MessagingException {
    try {
      // FIXME
      attachments = MailUtilities.parseAttachments(getMessage());
      attachmentsLoaded = true;
      if (Pooka.getProperty("EncryptionManager.autoDecrypt", "false").equalsIgnoreCase("true") &&
          cryptoInfo.isEncrypted()) {
        UserProfile p = getDefaultProfile();
        if (p == null)
          p = Pooka.getPookaManager().getUserProfileManager().getDefaultProfile();

        if (cryptoInfo.autoDecrypt(p)) {
          //attachments = MailUtilities.parseAttachments(getMessage());
        }
      }

      if (Pooka.getProperty("EncryptionManager.autoCheckSig", "false").equalsIgnoreCase("true") &&
          cryptoInfo.isSigned()) {
        if (cryptoInfo.autoCheckSignature((javax.mail.internet.InternetAddress) getMessage().getFrom()[0])) {
          //attachments = MailUtilities.parseAttachments(getMessage());
        }
      }

    } catch (MessagingException me) {
      // if we can't parse the message, try loading it as a single text
      // file.
      try {
        javax.mail.internet.MimeMessage mimeMessage = (javax.mail.internet.MimeMessage)getMessage();
        AttachmentBundle bundle = new AttachmentBundle(mimeMessage);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        java.util.List headerList = new java.util.ArrayList();
        java.util.Enumeration headerEnum = mimeMessage.getAllHeaders();
        while (headerEnum.hasMoreElements()) {
          Header hdr = (Header) headerEnum.nextElement();
          headerList.add(hdr.getName());
        }
        String[] excludeList = (String[]) headerList.toArray(new String[0]);
        mimeMessage.writeTo(baos, excludeList);
        String content = baos.toString("ISO-8859-1");
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setText(content);
        Attachment textPart = new Attachment(mbp);
        bundle.addAttachment(textPart);

        attachments = bundle;
      } catch (Exception e) {
        throw me;
      }
    } catch (java.io.IOException ioe) {
      throw new MessagingException("Error loading Message:  " + ioe.toString(), ioe);
    }

  }

  /**
   * This gets a Flag property from the Message.
   */
  public boolean flagIsSet(String flagName) throws MessagingException {
    if (Thread.currentThread() != getFolderInfo().getFolderThread() && ! (Thread.currentThread() instanceof net.suberic.pooka.thread.LoadMessageThread)) {
      Logger folderLogger = getFolderInfo().getLogger();
      if (folderLogger.isLoggable(Level.WARNING)) {
        folderLogger.log(Level.WARNING, "Accessing Message Flags not on Folder Thread.");
        Thread.currentThread().dumpStack();
      }
    }

    if (flagName.equals("FLAG.ANSWERED") )
      return getMessage().isSet(Flags.Flag.ANSWERED);
    else if (flagName.equals("FLAG.DELETED"))
      return getMessage().isSet(Flags.Flag.DELETED);
    else if (flagName.equals("FLAG.DRAFT"))
      return getMessage().isSet(Flags.Flag.DRAFT);
    else if (flagName.equals("FLAG.FLAGGED"))
      return getMessage().isSet(Flags.Flag.FLAGGED);
    else if (flagName.equals("FLAG.RECENT"))
      return getMessage().isSet(Flags.Flag.RECENT);
    else if (flagName.equals("FLAG.SEEN")) {
      if (folderInfo != null && ! folderInfo.tracksUnreadMessages())
        return true;
      else
        return getMessage().isSet(Flags.Flag.SEEN);
    }

    return false;
  }

  /**
   * This gets the Flags object for the wrapped Message.
   */
  public Flags getFlags() throws MessagingException {
    if (Thread.currentThread() != getFolderInfo().getFolderThread() && ! (Thread.currentThread() instanceof net.suberic.pooka.thread.LoadMessageThread)) {
      Logger folderLogger = getFolderInfo().getLogger();
      if (folderLogger.isLoggable(Level.WARNING)) {
        folderLogger.log(Level.WARNING, "Accessing Message Flags not on Folder Thread.");
        Thread.currentThread().dumpStack();

      }
    }
    return getMessage().getFlags();
  }

  /**
   * Refreshes the flags object.
   */
  public void refreshFlags() throws MessagingException {
    if (Thread.currentThread() != getFolderInfo().getFolderThread() && ! (Thread.currentThread() instanceof net.suberic.pooka.thread.LoadMessageThread)) {
      Logger folderLogger = getFolderInfo().getLogger();
      if (folderLogger.isLoggable(Level.WARNING)) {
        folderLogger.log(Level.WARNING, "Accessing Message Flags not on Folder Thread.");
        Thread.currentThread().dumpStack();

      }
    }
    getFolderInfo().refreshFlags(this);
  }

  /**
   * Refreshes the Headers object.
   */
  public void refreshHeaders() throws MessagingException {
    if (Thread.currentThread() != getFolderInfo().getFolderThread() && ! (Thread.currentThread() instanceof net.suberic.pooka.thread.LoadMessageThread)) {
      Logger folderLogger = getFolderInfo().getLogger();
      if (folderLogger.isLoggable(Level.WARNING)) {
        folderLogger.log(Level.WARNING, "Accessing Message Headers not on Folder Thread.");
        Thread.currentThread().dumpStack();

      }
    }
    getFolderInfo().refreshHeaders(this);
  }

  /**
   * This gets a particular property (From, To, Date, Subject, or just
   * about any Email Header) from the Message.
   */
  public Object getMessageProperty(String prop) throws MessagingException {
    if (getFolderInfo() != null && Thread.currentThread() != getFolderInfo().getFolderThread() && ! (Thread.currentThread() instanceof net.suberic.pooka.thread.LoadMessageThread)) {
      Logger folderLogger = getFolderInfo().getLogger();
      if (folderLogger.isLoggable(Level.WARNING)) {
        folderLogger.log(Level.WARNING, "Getting Message Property not on Folder Thread.");
        Thread.currentThread().dumpStack();

      }
    }
    Message msg = getMessage();
    if (prop.equals("From")) {
      try {
        Address[] fromAddr = msg.getFrom();
        return MailUtilities.decodeAddressString(fromAddr);
      } catch (javax.mail.internet.AddressException ae) {
        return ((MimeMessage) msg).getHeader("From", ",");
      }
    } else if (prop.equalsIgnoreCase("receivedDate")) {
      return msg.getReceivedDate();
    } else if (prop.equalsIgnoreCase("recipients")) {
      return msg.getAllRecipients();
    } else if (prop.equalsIgnoreCase("to")) {
      return MailUtilities.decodeAddressString(msg.getRecipients(Message.RecipientType.TO));
    } else if (prop.equalsIgnoreCase("cc")) {
      return MailUtilities.decodeAddressString(msg.getRecipients(Message.RecipientType.CC));
    } else if (prop.equalsIgnoreCase("bcc")) {
      return MailUtilities.decodeAddressString(msg.getRecipients(Message.RecipientType.BCC));
    } else if (prop.equalsIgnoreCase("Date")) {
      return msg.getSentDate();
    } else if (prop.equalsIgnoreCase("Subject")) {
      return MailUtilities.decodeText(msg.getSubject());
    }

    if (msg instanceof MimeMessage) {
      String hdrVal = ((MimeMessage)msg).getHeader(prop, ",");
      if (hdrVal != null && hdrVal.length() > 0)
        return MailUtilities.decodeText(hdrVal);
    }
    return "";
  }

  /**
   * Gets the Content and inline text content for the Message.
   */
  public String getTextAndTextInlines(String attachmentSeparator, boolean withHeaders, boolean showFullHeaders, int maxLength, String truncationMessage) throws MessagingException, OperationCancelledException {
    try {
      if (!hasLoadedAttachments())
        loadAttachmentInfo();
      return attachments.getTextAndTextInlines(attachmentSeparator, withHeaders, showFullHeaders, maxLength, truncationMessage);
    } catch (FolderClosedException fce) {
      try {
        if (getFolderInfo().shouldBeConnected()) {
          getFolderInfo().openFolder(Folder.READ_WRITE);
          loadAttachmentInfo();
          return attachments.getTextAndTextInlines(attachmentSeparator, withHeaders, showFullHeaders, maxLength, truncationMessage);
        } else {
          throw fce;
        }
      } catch (java.io.IOException ioe) {
        throw new MessagingException(ioe.getMessage());
      }
    } catch (java.io.IOException ioe) {
      ioe.printStackTrace();
      throw new MessagingException(ioe.getMessage());
    }
  }

  /**
   * Gets the Content and inline text content for the Message.
   */
  public String getTextAndTextInlines(String attachmentSeparator, boolean withHeaders, boolean showFullHeaders) throws MessagingException, OperationCancelledException  {
    return getTextAndTextInlines(attachmentSeparator, withHeaders, showFullHeaders, getMaxMessageDisplayLength(), getTruncationMessage());
  }

  /**
   * Gets the Content and inline text content for the Message.
   */
  public String getTextAndTextInlines(boolean withHeaders, boolean showFullHeaders) throws MessagingException, OperationCancelledException  {
    return getTextAndTextInlines(getAttachmentSeparator(), withHeaders, showFullHeaders, getMaxMessageDisplayLength(), getTruncationMessage());
  }

  /**
   * Gets the Text part of the Content of this Message.  If no real text
   * content is found, returns the html content.  If there's none of that,
   * either, then returns null.
   */
  public String getTextPart(boolean withHeaders, boolean showFullHeaders, int maxLength, String truncationMessage) throws MessagingException, OperationCancelledException {
    try {
      if (!hasLoadedAttachments())
        loadAttachmentInfo();
      String returnValue = attachments.getTextPart(withHeaders, showFullHeaders, maxLength, truncationMessage);
      if (returnValue != null)
        return returnValue;
      else
        return getHtmlPart(withHeaders, showFullHeaders, maxLength, getHtmlTruncationMessage());
    } catch (FolderClosedException fce) {
      try {
        if (getFolderInfo().shouldBeConnected()) {
          getFolderInfo().openFolder(Folder.READ_WRITE);
          loadAttachmentInfo();
          String returnValue = attachments.getTextPart(withHeaders, showFullHeaders, maxLength, truncationMessage);
          if (returnValue != null)
            return returnValue;
          else
            return getHtmlPart(withHeaders, showFullHeaders, maxLength, getHtmlTruncationMessage());
        } else {
          throw fce;
        }
      } catch (java.io.IOException ioe) {
        throw new MessagingException(ioe.getMessage());
      }
    } catch (java.io.IOException ioe) {
      throw new MessagingException(ioe.getMessage());
    }
  }

  /**
   * Gets the Text part of the Content of this Message.  If no real text
   * content is found, returns the html content.  If there's none of that,
   * either, then returns null.
   */
  public String getTextPart(boolean withHeaders, boolean showFullHeaders) throws MessagingException, OperationCancelledException  {
    return getTextPart(withHeaders, showFullHeaders, getMaxMessageDisplayLength(), getTruncationMessage());
  }

  /**
   * Gets the Html part of the Content of this Message.
   */
  public String getHtmlPart(boolean withHeaders, boolean showFullHeaders, int maxLength, String truncationMessage) throws MessagingException, OperationCancelledException  {
    try {
      if (!hasLoadedAttachments())
        loadAttachmentInfo();
      return attachments.getHtmlPart(withHeaders, showFullHeaders, maxLength, truncationMessage);
    } catch (FolderClosedException fce) {
      try {
        if (getFolderInfo().shouldBeConnected()) {
          getFolderInfo().openFolder(Folder.READ_WRITE);
          loadAttachmentInfo();
          return attachments.getHtmlPart(withHeaders, showFullHeaders, maxLength, truncationMessage);
        } else {
          throw fce;
        }
      } catch (java.io.IOException ioe) {
        throw new MessagingException(ioe.getMessage());
      }
    } catch (java.io.IOException ioe) {
      throw new MessagingException(ioe.getMessage());
    }
  }

  /**
   * Gets the Html part of the Content of this Message.
   */
  public String getHtmlPart(boolean withHeaders, boolean showFullHeaders) throws MessagingException, OperationCancelledException  {
    return getHtmlPart(withHeaders, showFullHeaders, getMaxMessageDisplayLength(), getTruncationMessage());
  }

  /**
   * Gets the Content and inline text content for the Message.
   */
  public String getHtmlAndTextInlines(String attachmentSeparator, boolean withHeaders, boolean showFullHeaders, int maxLength, String truncationMessage) throws MessagingException, OperationCancelledException  {
    try {
      if (!hasLoadedAttachments())
        loadAttachmentInfo();
      return attachments.getHtmlAndTextInlines(attachmentSeparator, withHeaders, showFullHeaders, maxLength, truncationMessage);
    } catch (FolderClosedException fce) {
      try {
        if (getFolderInfo().shouldBeConnected()) {
          getFolderInfo().openFolder(Folder.READ_WRITE);
          loadAttachmentInfo();
          return attachments.getHtmlAndTextInlines(attachmentSeparator, withHeaders, showFullHeaders, maxLength, truncationMessage);
        } else {
          throw fce;
        }
      } catch (java.io.IOException ioe) {
        throw new MessagingException(ioe.getMessage());
      }
    } catch (java.io.IOException ioe) {
      throw new MessagingException(ioe.getMessage());
    }
  }

  /**
   * Gets the Content and inline text content for the Message.
   */
  public String getHtmlAndTextInlines(String attachmentSeparator, boolean withHeaders, boolean showFullHeaders) throws MessagingException, OperationCancelledException  {
    return getHtmlAndTextInlines(attachmentSeparator, withHeaders, showFullHeaders, getMaxMessageDisplayLength(), getHtmlTruncationMessage());
  }

  /**
   * Gets the Content and inline text content for the Message.
   */
  public String getHtmlAndTextInlines(boolean withHeaders, boolean showFullHeaders) throws MessagingException, OperationCancelledException  {
    return getHtmlAndTextInlines(getHtmlAttachmentSeparator(), withHeaders, showFullHeaders, getMaxMessageDisplayLength(), getHtmlTruncationMessage());
  }

  /**
   * Gets the raw RFC 822 message.
   */
  public String getRawText() throws MessagingException {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      MimeMessage mm = (MimeMessage) getMessage();
      mm.writeTo(baos);
      return baos.toString();
    } catch (IOException ioe) {
      MessagingException returnValue = new MessagingException("Error reading Message Stream", ioe);
      throw returnValue;
    }
  }

  /**
   * Moves the Message into the target Folder.
   */
  public void moveMessage(FolderInfo targetFolder, boolean expunge) throws MessagingException, OperationCancelledException  {
    try {
      folderInfo.copyMessages(new MessageInfo[] { this }, targetFolder);
    } catch (MessagingException me) {
      MessagingException returnValue = new MessagingException (Pooka.getProperty("error.Message.CopyErrorMessage", "Error:  could not copy messages to folder:  ") + targetFolder.toString() +"\n", me);
      throw returnValue;
    }

    try {
      remove(expunge);
    } catch (MessagingException me) {
      MessagingException returnValue = new MessagingException(Pooka.getProperty("error.Message.RemoveErrorMessage", "Error:  could not remove messages from folder:  ") + targetFolder.toString() +"\n", me);
      throw returnValue;
    }
  }

  /**
   * Copies the Message into the target Folder.
   */
  public void copyMessage(FolderInfo targetFolder) throws MessagingException, OperationCancelledException  {
    try {
      folderInfo.copyMessages(new MessageInfo[] { this }, targetFolder);
    } catch (MessagingException me) {
      MessagingException returnValue = new MessagingException (Pooka.getProperty("error.Message.CopyErrorMessage", "Error:  could not copy messages to folder:  ") + targetFolder.toString() +"\n", me);
      throw returnValue;
    }

  }

  /**
   * A convenience method which sets autoExpunge by the value of
   * Pooka.autoExpunge, and then calls moveMessage(targetFolder, autoExpunge)
   * with that value.
   */
  public void moveMessage(FolderInfo targetFolder) throws MessagingException, OperationCancelledException  {
    moveMessage(targetFolder, Pooka.getProperty("Pooka.autoExpunge", "true").equals("true"));
  }

  /**
   * Bounces this message to another user.
   */
  public void bounceMessage(Address[] recipientList) throws javax.mail.MessagingException {

    MimeMessage mm = (MimeMessage)getMessage();
    MimeMessage mmClone = new MimeMessage(mm);

    NewMessageInfo nmi = new NewMessageInfo(mmClone);
    net.suberic.pooka.gui.NewMessageProxy nmp = new net.suberic.pooka.gui.NewMessageProxy(nmi);
    java.util.HashMap messageMap = new java.util.HashMap();
    messageMap.put(mmClone, recipientList);

    nmi.setSendMessageMap(messageMap);

    final NewMessageInfo final_nmi = nmi;

    UserProfile p = getDefaultProfile();
    if (p == null)
      p = Pooka.getPookaManager().getUserProfileManager().getDefaultProfile();

    if (p != null && p.getMailServer() != null) {
      final OutgoingMailServer mailServer = p.getMailServer();
      mailServer.mailServerThread.addToQueue(new javax.swing.AbstractAction() {
          public void actionPerformed(java.awt.event.ActionEvent ae) {
            mailServer.sendMessage(final_nmi);
          }
        }, new java.awt.event.ActionEvent(this, 0, "message-send"));
    }
  }

  /**
   * Deletes the Message from the current Folder.  If a Trash folder is
   * set, this method moves the message into the Trash folder.  If no
   * Trash folder is set, this marks the message as deleted.  In addition,
   * if the autoExpunge variable is set to true, it also expunges
   * the message from the mailbox.
   */
  public void deleteMessage(boolean autoExpunge) throws MessagingException, OperationCancelledException  {
    FolderInfo trashFolder = getFolderInfo().getTrashFolder();
    if ((getFolderInfo().useTrashFolder()) && (trashFolder != null) && (trashFolder != getFolderInfo())) {
      try {
        moveMessage(trashFolder, autoExpunge);
      } catch (MessagingException me) {
        throw new MessagingException(Pooka.getProperty("error.Messsage.DeleteNoTrashFolder", "No trash folder available."),  me);
      }
    } else {

      // actually remove the message, if we haven't already moved it.

      try {
        remove(autoExpunge);
      } catch (MessagingException me) {
        me.printStackTrace();
        throw new MessagingException(Pooka.getProperty("error.Message.DeleteErrorMessage", "Error:  could not delete message.") +"\n", me);
      }
    }

    if (getMessageProxy() != null)
      getMessageProxy().close();
  }

  /**
   * A convenience method which sets autoExpunge by the value of
   * Pooka.autoExpunge, and then calls deleteMessage(boolean autoExpunge)
   * with that value.
   */
  public void deleteMessage() throws MessagingException, OperationCancelledException {
    deleteMessage(Pooka.getProperty("Pooka.autoExpunge", "true").equals("true"));
  }

  /**
   * This actually marks the message as deleted, and, if autoexpunge is
   * set to true, expunges the folder.
   *
   * This should not be called directly; rather, deleteMessage() should
   * be used in order to ensure that the delete is done properly (using
   * trash folders, for instance).  If, however, the deleteMessage()
   * throws an Exception, it may be necessary to follow up with a call
   * to remove().
   */
  public void remove(boolean autoExpunge) throws MessagingException, OperationCancelledException {
    Message m = getMessage();
    if (m != null) {
      m.setFlag(Flags.Flag.DELETED, true);
      if ( autoExpunge ) {
        folderInfo.expunge();
      }
    }
  }

  /**
   * This puts the reply prefix 'prefix' in front of each line in the
   * body of the Message.
   */
  public String prefixMessage(String originalMessage, String prefix, String intro) {
    StringBuffer newValue = new StringBuffer(originalMessage);

    int currentCR = originalMessage.lastIndexOf('\n', originalMessage.length());
    while (currentCR != -1) {
      newValue.insert(currentCR+1, prefix);
      currentCR=originalMessage.lastIndexOf('\n', currentCR-1);
    }
    newValue.insert(0, prefix);
    newValue.insert(0, intro);

    return newValue.toString();
  }

  /**
   * This parses a message line using the current Message as a model.
   * The introTemplate will be of the form 'On %d, %n wrote', or
   * something similar.  This method uses the Pooka.parsedString
   * characters to decide which strings to substitute for which
   * characters.
   */
  public String parseMsgString(MimeMessage m, String introTemplate, boolean addLF) {
    StringBuffer intro = new StringBuffer(introTemplate);
    int index = introTemplate.lastIndexOf('%', introTemplate.length());
    try {
      while (index > -1) {
        try {
          char nextChar = introTemplate.charAt(index + 1);
          String replaceMe = null;
          if (nextChar == Pooka.getProperty("Pooka.parsedString.nameChar", "n").charAt(0)) {

            Address[] fromAddresses = m.getFrom();
            if (fromAddresses.length > 0 && fromAddresses[0] != null) {
              replaceMe = MailUtilities.decodeAddressString(fromAddresses);
              if (replaceMe == null)
                replaceMe = "";
              intro.replace(index, index +2, replaceMe);
            }
          } else if (nextChar == Pooka.getProperty("Pooka.parsedString.dateChar", "d").charAt(0)) {
            replaceMe = Pooka.getDateFormatter().fullDateFormat.format(m.getSentDate());
            if (replaceMe == null)
              replaceMe = "";
            intro.replace(index, index + 2, replaceMe);
          } else if (nextChar == Pooka.getProperty("Pooka.parsedString.subjChar", "s").charAt(0)) {
            replaceMe = m.getSubject();
            if (replaceMe == null)
              replaceMe = "";
            intro.replace(index, index + 2, replaceMe);
          } else if (nextChar == '%') {
            intro.replace(index, index+1, "%");
          }
          index = introTemplate.lastIndexOf('%', index -1);
        } catch (StringIndexOutOfBoundsException e) {
          index = introTemplate.lastIndexOf('%', index -1);
        }
      }
    } catch (MessagingException me) {
      return null;
    }

    if (addLF)
      if (intro.charAt(intro.length()-1) != '\n')
        intro.append('\n');

    return intro.toString();
  }

  /**
   * This populates a message which is a reply to the current
   * message.
   */
  public NewMessageInfo populateReply(boolean replyAll, boolean withAttachments)
    throws MessagingException, OperationCancelledException  {
    MimeMessage newMsg = (MimeMessage) getMessage().reply(replyAll);

    MimeMessage mMsg = (MimeMessage) getMessage();

    String textPart = getTextPart(false, false, getMaxMessageDisplayLength(), getTruncationMessage());
    if (textPart == null) {
      textPart = "";
    }

    if (isHtml()) {
      net.suberic.pooka.htmlparser.PookaStringBean psb = new net.suberic.pooka.htmlparser.PookaStringBean();
      psb.setContent(textPart, null);
      textPart = psb.getStrings();
    }

    if (textPart == null) {
      textPart = "";
    }

    UserProfile up = getDefaultProfile();
    if (up == null)
      up = Pooka.getPookaManager().getUserProfileManager().getDefaultProfile();

    String parsedText;
    String replyPrefix;
    String parsedIntro;

    if (up != null && up.getMailProperties() != null) {
      replyPrefix = up.getMailProperties().getProperty("replyPrefix", Pooka.getProperty("Pooka.replyPrefix", "> "));
      parsedIntro = parseMsgString(mMsg, up.getMailProperties().getProperty("replyIntro", Pooka.getProperty("Pooka.replyIntro", "On %d, %n wrote:")), true);
    } else {
      replyPrefix = Pooka.getProperty("Pooka.replyPrefix", "> ");
      parsedIntro = parseMsgString(mMsg, Pooka.getProperty("Pooka.replyIntro", "On %d, %n wrote:"), true);
    }
    parsedText = prefixMessage(textPart, replyPrefix, parsedIntro);
    newMsg.setText(parsedText);

    if (replyAll && Pooka.getProperty("Pooka.excludeSelfInReply", "true").equalsIgnoreCase("true")) {
      up.removeFromAddress(newMsg);
    }

    NewMessageInfo returnValue = new NewMessageInfo(newMsg);

    if (withAttachments) {
      returnValue.attachments = new AttachmentBundle();
      returnValue.attachments.addAll(attachments);
      returnValue.attachmentsLoaded=true;
    }

    return returnValue;
  }

  /**
   * This populates a message which is a reply to the current
   * message.
   */
  public NewMessageInfo populateReply(boolean replyAll)
    throws MessagingException, OperationCancelledException  {
    return populateReply(replyAll, false);
  }

  /**
   * This populates a new message which is a forwarding of the
   * current message.
   */
  public NewMessageInfo populateForward(boolean withAttachments, int method)
    throws MessagingException, OperationCancelledException  {
    MimeMessage mMsg = (MimeMessage) getMessage();
    MimeMessage newMsg = new MimeMessage(Pooka.getDefaultSession());

    String parsedText = "";

    if (method == FORWARD_QUOTED) {
      String textPart = getTextPart(false, false, getMaxMessageDisplayLength(), getTruncationMessage());

      UserProfile up = getDefaultProfile();
      if (up == null) {
        up = Pooka.getPookaManager().getUserProfileManager().getDefaultProfile();
      }

      String forwardPrefix;
      String parsedIntro;

      if (up != null && up.getMailProperties() != null) {
        forwardPrefix = up.getMailProperties().getProperty("forwardPrefix", Pooka.getProperty("Pooka.forwardPrefix", "> "));
        parsedIntro = parseMsgString(mMsg, up.getMailProperties().getProperty("forwardIntro", Pooka.getProperty("Pooka.forwardIntro", "Forwarded message from %n:")), true);
      } else {
        forwardPrefix = Pooka.getProperty("Pooka.forwardPrefix", "> ");
        parsedIntro = parseMsgString(mMsg, Pooka.getProperty("Pooka.forwardIntro", "Forwarded message from %n:"), true);
      }
      parsedText = prefixMessage(textPart, forwardPrefix, parsedIntro);

    } else if (method == FORWARD_AS_INLINE) {

      String textPart = getTextPart(true, false, getMaxMessageDisplayLength(), getTruncationMessage());

      parsedText = Pooka.getProperty("Pooka.forwardInlineIntro", "----------  Original Message  ----------\n") + textPart;

    }

    newMsg.setText(parsedText);
    newMsg.setSubject(parseMsgString(mMsg, Pooka.getProperty("Pooka.forwardSubject", "Fwd:  %s"), false));

    NewMessageInfo returnValue = new NewMessageInfo(newMsg);

    // handle attachments.
    if (method == FORWARD_AS_ATTACHMENT) {
      UpdatableMBP mbp = new UpdatableMBP();
      mbp.setContent(getRealMessage(), "message/rfc822");
      mbp.updateMyHeaders();
      String subject = (String) getMessageProperty("Subject");
      if (subject != null && subject.length() > 0) {
        mbp.setFileName(subject);
      } else {
        mbp.setFileName("forwarded message");
      }
      mbp.setDisposition(Part.ATTACHMENT);

      AttachmentBundle returnAttachments = returnValue.getAttachmentBundle();
      Attachment messageAttachment = new MBPAttachment(mbp);
      returnAttachments.addAttachment(messageAttachment, false);


    } else if (withAttachments) {
      returnValue.attachments = new AttachmentBundle();
      Vector fromAttachments = attachments.getAttachments();
      if (fromAttachments != null) {
        AttachmentBundle returnAttachments = returnValue.getAttachmentBundle();
        for (int i = 0; i < fromAttachments.size(); i++) {
          Attachment current = (Attachment) fromAttachments.elementAt(i);
          Attachment newAttachment = null;

          MimeBodyPart mbp = new MimeBodyPart();
          mbp.setDataHandler(current.getDataHandler());
          newAttachment = new MBPAttachment(mbp);
          returnAttachments.addAttachment(newAttachment, false);
        }
      }
    }

    return returnValue;
  }

  /**
   * This populates a new message which is a forwarding of the
   * current message.
   */
  public NewMessageInfo populateForward()
    throws MessagingException, OperationCancelledException  {
    return populateForward(false, FORWARD_QUOTED);
  }

  /**
   * Runs folder filters on this MessageInfo.
   */
  public void runBackendFilters() {
    FolderInfo fi = getFolderInfo();
    java.util.LinkedList list = new java.util.LinkedList();
    list.add(getMessageProxy());
    fi.applyFilters(list);
  }

  /**
   * Runs the configured spam action on this message.
   */
  public void runSpamAction() {
    FilterAction spamFilter = null;
    try {
      spamFilter = MessageFilter.generateFilterAction("Pooka.spamAction");
    } catch (Exception e) {
      int configureNow = Pooka.getUIFactory().showConfirmDialog("Spam action currently not configured.  Would you like to configure it now?", "Configure Spam action", javax.swing.JOptionPane.YES_NO_OPTION);
      if (configureNow == javax.swing.JOptionPane.YES_OPTION) {
        // show configure screen.
        Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("Preferences.Spam.label", "Spam"), "Pooka.spamAction");
      }

    }
    if (spamFilter != null) {
      Vector v = new Vector();
      v.add(this.getMessageProxy());
      java.util.List removed = spamFilter.performFilter(v);
      if (removed != null && removed.size() > 0) {
        try {
          getFolderInfo().expunge();
        } catch (Exception me) {
          // throw it away
        }
      }
      return;
    }
  }

  /**
   *  Caches the current messages.
   */
  public void cacheMessage() throws MessagingException {
    FolderInfo fi = getFolderInfo();
    if (fi != null && fi instanceof net.suberic.pooka.cache.CachingFolderInfo) {
      ((net.suberic.pooka.cache.CachingFolderInfo) fi).cacheMessage(this, net.suberic.pooka.cache.MessageCache.MESSAGE);

    }
  }

  /**
   * As specified by interface net.suberic.pooka.UserProfileContainer.
   *
   * If the MessageInfo's folderInfo is set, this returns the
   * DefaultProfile of that folderInfo.  If not, returns null.
   */
  public UserProfile getDefaultProfile() {
    if (getFolderInfo() != null) {
      return getFolderInfo().getDefaultProfile();
    } else
      return null;
  }

  /**
   * Saves the message to the given filename.
   */
  public void saveMessageAs(File saveFile) throws MessagingException{
    try {
      FileOutputStream fos = new FileOutputStream(saveFile);
      ((MimeMessage)getMessage()).writeTo(fos);
    } catch (IOException ioe) {
      MessagingException me = new MessagingException(Pooka.getProperty("error.errorCreatingAttachment", "Error attaching message"));
      me.setNextException(ioe);
      throw me;

    }
  }

  /**
   * Adds the sender of the message to the current AddressBook, if any.
   */
  public String addAddress(AddressBook book, boolean useVcard) throws MessagingException {
    String returnValue = null;
    boolean found = false;
    if (useVcard) {
      Attachment vcard = null;

      // see if there's a Vcard attachment on here.
      Vector attachList = getAttachments();
      if (attachList != null) {
        for (int i = 0; i < attachList.size() && vcard==null; i++) {
          Attachment current = (Attachment)attachList.get(i);
          if (current.getMimeType().match("text/x-vcard")) {
            vcard = current;
          }
        }

        if (vcard != null) {
          try {
            String vcardText = (String) vcard.getContent();
            BufferedReader reader = new BufferedReader(new StringReader(vcardText));
            net.suberic.pooka.vcard.Vcard addressEntry = net.suberic.pooka.vcard.Vcard.parse(reader);
            book.addAddress(addressEntry);
            returnValue = addressEntry.getID();

            found = true;
          } catch (Exception e) {
            // if we get any exceptions parsing the Vcard, just fall back to
            // using the fromAddress.  do print out a debugging message,
            // though.
            getMessageProxy().showError(Pooka.getProperty("error.parsingVcard", "Error parsing Vcard"), e);
          }
        }
      }
    }

    if (!found) {
      Address[] fromAddresses = getMessage().getFrom();
      javax.mail.internet.InternetAddress addr = (javax.mail.internet.InternetAddress) fromAddresses[0];

      // let's not support multiple froms.
      //AddressBookEntry entry = new net.suberic.pooka.vcard.Vcard(new java.util.Properties());
      AddressBookEntry entry = book.newAddressBookEntry();

      String personalName = addr.getPersonal();
      if (personalName == null)
        personalName = addr.getAddress();

      entry.setPersonalName(personalName);
      entry.setAddress(addr);
      book.addAddress(entry);
      returnValue = entry.getID();
    }

    return returnValue;
  }

  /**
   * Returns the Message that this MessageInfo is wrapping.
   */
  public Message getMessage() {
    return message;
  }

  /**
   * Returns the real, modifiable message that this MessageInfo is
   * wrapping.
   */
  public Message getRealMessage() throws MessagingException {
    if (getFolderInfo() != null) {
      return getFolderInfo().getRealMessage(this);
    }
    return message;
  }

  /**
   * Returns the FolderInfo to which this message belongs.
   */
  public FolderInfo getFolderInfo() {
    return folderInfo;
  }

  /**
   * Convenience method.  Returns whether or not that message has been
   * seen.
   */
  public boolean isSeen() {

    if (folderInfo != null && ! folderInfo.tracksUnreadMessages()) {
      return true;
    } else
      try {
        return flagIsSet("FLAG.SEEN");
      } catch (MessagingException me) {
        return true;
      }
  }

  /**
   * Sets the seen parameter to the newValue.  This basically calls
   * setFlag(Flags.Flag.SEEN, newValue) on the wrapped Message.
   */
  public void setSeen(boolean newValue) throws MessagingException {
    if (folderInfo != null && ! folderInfo.tracksUnreadMessages())
      return;
    else {
      boolean seen = isSeen();
      if (newValue != seen) {
        //Message m = getRealMessage();
        Message m = getMessage();
        m.setFlag(Flags.Flag.SEEN, newValue);
        getFolderInfo().fireMessageChangedEvent(new MessageChangedEvent(this, MessageChangedEvent.FLAGS_CHANGED, getMessage()));
      }
    }
  }

  public boolean isLoaded() {
    return loaded;
  }

  /**
   * This sets the loaded value for the MessageInfo to false.   This
   * should be called only if the TableInfo of the Message has been
   * changed and needs to be reloaded.
   */
  public void unloadTableInfo() {
    loaded=false;
  }

  public boolean hasLoadedAttachments() {
    return attachmentsLoaded;
  }

  boolean mHasAttachments = false;
  boolean mHasCheckedAttachments = false;

  /**
   * Returns whether or not this message has attachments.
   */
  public boolean hasAttachments() throws MessagingException {
    return hasAttachments(true);
  }

  /**
   * Returns whether or not this message has attachments.
   */
  public boolean hasAttachments(boolean inclusiveCryptoAttach) throws MessagingException {
      //if (mHasCheckedAttachments) {
      //  return mHasAttachments;
      //} else {
        if (hasLoadedAttachments()) {
          Vector attachs = getAttachments(inclusiveCryptoAttach);

          if (attachs != null && attachs.size() > 0)
            mHasAttachments = true;

          mHasCheckedAttachments = true;

          return mHasAttachments;

        } else {
          try {
            javax.mail.internet.ContentType type = new javax.mail.internet.ContentType(getMessage().getContentType());
            if (new String("multipart").equalsIgnoreCase(type.getPrimaryType()) && ! new String("alternative").equalsIgnoreCase(type.getSubType())) {
              return true;
            } else {
              return false;
            }
          } catch (javax.mail.internet.ParseException pe) {
            if (Pooka.isDebug()) {
              System.out.println("unable to parse content-type:  " + getMessage().getContentType());
            }
            mHasAttachments = false;
          }
        }
      //}

      return mHasAttachments;
    }

  /**
   * Returns whether or not this message has encryption on it.
   */
  public boolean hasEncryption() throws MessagingException {
    return (cryptoInfo.isEncrypted() || cryptoInfo.isSigned());
  }

  /**
   * Returns the cryptoInfo object for this MessageInfo.
   */
  public MessageCryptoInfo getCryptoInfo() {
    return cryptoInfo;
  }

  /**
   * Returns the attachments for this MessageInfo.  If the attachments
   * have not yet been loaded, attempts to load the attachments.
   * are considered.
   */
  public Vector getAttachments() throws MessagingException {
    return getAttachments(true);
  }

  /**
   * Returns the attachments for this MessageInfo.  If the attachments
   * have not yet been loaded, attempts to load the attachments.
   * @param inclusiveCryptoAttach: indicates whether the crypto attachments
   * are considered.
   */
  public Vector getAttachments(boolean inclusiveCryptoAttach) throws MessagingException {
    if (!hasLoadedAttachments())
      loadAttachmentInfo();

    Vector atts = attachments.getAttachments(getMaxMessageDisplayLength());

    if ((!inclusiveCryptoAttach) && atts != null && atts.size() > 0) {
        for (int i = 0; i < atts.size() ; i++) {
          Attachment attach = (Attachment) atts.elementAt(i);
          if(attach instanceof CryptoAttachment || attach instanceof SignedAttachment){
            atts.remove(attach);
            i--;
          }
        }
    }

    return atts;

  }

  /**
   * Returns the AttachmentBundle for this MessageInfo.
   */
  AttachmentBundle getAttachmentBundle() throws MessagingException {
    if (hasLoadedAttachments())
      return attachments;
    else {
      loadAttachmentInfo();
      return attachments;
    }
  }

  /**
   * Returns the MessageProxy for this MessageInfo, if any.
   */
  public MessageProxy getMessageProxy() {
    return messageProxy;
  }

  /**
   * Sets the primary MessageProxy for this MessageInfo.
   */
  public void setMessageProxy(MessageProxy newMp) {
    messageProxy = newMp;
  }

  /**
   * Returns the default maximum display length for this MessageInfo.
   */
  public int getMaxMessageDisplayLength() {
    int displayLength = 10000;
    try {
      displayLength = Integer.parseInt(Pooka.getProperty("Pooka.attachmentDisplayMaxLength", "100000"));
    } catch (NumberFormatException nfe) {
    }
    return displayLength;
  }

  /**
   * Returns the message to use if the message is displayed truncated (if the
   * MaxMessageDisplayLenght is exceeded.
   */
  public String getTruncationMessage() {
    return Pooka.getProperty("Pooka.messageTruncation", "------ Message truncated ------");
  }

  /**
   * Returns the message to use if the message is displayed truncated (if the
   * MaxMessageDisplayLenght is exceeded.
   */
  public String getHtmlTruncationMessage() {
    return Pooka.getProperty("Pooka.html.messageTruncation", "<br><br><b>------ Message truncated ------</b><br><br>");
  }

  public String getAttachmentSeparator() {
    return Pooka.getProperty("Pooka.attachmentSeparator", "\n\n");
  }

  public String getHtmlAttachmentSeparator() {
    return Pooka.getProperty("Pooka.html.attachmentSeparator", "<br><hr><br>");
  }

  /**
   * Returns whether or not this message has an HTML version available.
   */
  public boolean containsHtml() throws MessagingException {
    if (!hasLoadedAttachments())
      loadAttachmentInfo();

    return attachments.containsHtml();
  }

  /**
   * Returns true if the main content of this message exists only as
   * HTML.
   */
  public boolean isHtml() throws MessagingException {
    if (!hasLoadedAttachments())
      loadAttachmentInfo();

    return attachments.isHtml();
  }

  /**
   * Returns whether or not the underlying Message object has been
   * fetch()ed from the server yet.
   */
  public boolean hasBeenFetched() {
    return fetched;
  }

  /**
   * Sets whether or not the underlying Message has been fetch()ed from
   * the server.
   */
  public void setFetched(boolean newValue) {
    fetched = newValue;
  }

  /**
   * Returns the headerlines of the contained message
   */
  public Vector getHeaderLines() throws MessagingException{
      if (!hasLoadedAttachments())
        loadAttachmentInfo();

    return attachments.headerLines;
  }
}







