package net.suberic.pooka;
import javax.mail.*;
import java.util.*;
import java.io.*;
import javax.activation.*;
import javax.mail.internet.*;

import net.suberic.pooka.crypto.*;
import net.suberic.crypto.*;
import net.suberic.pooka.gui.NewMessageCryptoInfo;

import java.security.Key;

/**
 * A MessageInfo representing a new message.
 */
public class NewMessageInfo extends MessageInfo {

  // the full map of messages to be sent, if there are different versions
  // of the message that go to different recipients.
  Map mSendMessageMap = new HashMap();
  // the default UserProfile for this Message, if there is one.
  UserProfile mProfile = null;

  /**
   * Creates a NewMessageInfo to wrap the given Message.
   */
  public NewMessageInfo(Message newMessage) {
    message = newMessage;
    attachments = new AttachmentBundle();
    attachmentsLoaded=true;
  }

  /**
   * Sends the new message, using the given Profile, the given
   * InternetHeaders, the given messageText, the given ContentType, and
   * the attachments already set for this object.
   */
  public void sendMessage(UserProfile profile, InternetHeaders headers, NewMessageCryptoInfo cryptoInfo, String messageText, String messageContentType) throws MessagingException {

    try {
      net.suberic.pooka.gui.PookaUIFactory factory = Pooka.getUIFactory();

      MimeMessage mMsg = (MimeMessage) message;

      factory.showStatusMessage(Pooka.getProperty("info.sendMessage.settingHeaders", "Setting headers..."));

      Enumeration individualHeaders = headers.getAllHeaders();
      while(individualHeaders.hasMoreElements()) {
        Header currentHeader = (Header) individualHeaders.nextElement();
        String currentValue =  currentHeader.getValue();
        if (currentValue == null || currentValue.length() == 0) {
          mMsg.removeHeader(currentHeader.getName());
        } else {
          mMsg.setHeader(currentHeader.getName(), currentValue);
        }
      }

      mMsg.setHeader("X-Mailer", Pooka.getProperty("Pooka.xmailer", "Pooka"));

      if (Pooka.getProperty("Pooka.lineWrap", "").equalsIgnoreCase("true"))
        messageText=net.suberic.pooka.MailUtilities.wrapText(messageText);

      // move this to another thread now.
      factory.showStatusMessage(Pooka.getProperty("info.sendMessage.changingThreads", "Sending to message thread..."));

      final UserProfile sProfile = profile;
      final MimeMessage sMimeMessage = mMsg;
      final String sMessageText = messageText;
      final String sMessageContentType = messageContentType;
      final NewMessageCryptoInfo sCryptoInfo = cryptoInfo;

      OutgoingMailServer mailServer = null;
      if (profile != null)
        mailServer = profile.getMailServer();

      if (mailServer != null) {

        final OutgoingMailServer sMailServer = mailServer;
        mailServer.mailServerThread.addToQueue(new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent ae) {
              internal_sendMessage(sProfile, sMimeMessage, sMessageText, sMessageContentType, sCryptoInfo, sMailServer);
            }
          }, new java.awt.event.ActionEvent(this, 0, "message-send"));
      } else {
        // oh well.
        internal_sendMessage(sProfile, sMimeMessage, sMessageText, sMessageContentType, sCryptoInfo, null);
      }

    } catch (MessagingException me) {
      throw me;
    } catch (Throwable t) {
      String cause = t.getMessage();
      if (cause == null)
        cause = t.toString();

      MessagingException me = new MessagingException(cause);
      if (t instanceof Exception)
        me.setNextException((Exception)t);
      throw me;
    }
  }

  /**
   * Does the part of message sending that should really not happen on
   * the AWTEventThread.
   */
  private void internal_sendMessage(UserProfile profile, MimeMessage mMsg, String messageText, String messageContentType, NewMessageCryptoInfo cryptoInfo, OutgoingMailServer mailServer) {
    net.suberic.pooka.gui.PookaUIFactory factory = Pooka.getUIFactory();

    try {
      factory.showStatusMessage(Pooka.getProperty("info.sendMessage.attachingKeys", "Attaching crypto keys (if any)..."));

      // see if we need to add any keys.
      List keyParts = cryptoInfo.createAttachedKeyParts();

      factory.showStatusMessage(Pooka.getProperty("info.sendMessage.addingMessageText", "Parsing message text..."));
      if (keyParts.size() > 0 || (attachments.getAttachments() != null && attachments.getAttachments().size() > 0)) {
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setContent(messageText, messageContentType);
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(mbp);

        if (attachments.getAttachments() != null) {
          // i should really use the text parsing code for this, but...
          String attachmentMessage=Pooka.getProperty("info.sendMessage.addingAttachment.1", "Adding attachment ");
          String ofMessage = Pooka.getProperty("info.sendMessage.addingAttachment.2", " of ");
          int attachmentCount = attachments.getAttachments().size();
          for (int i = 0; i < attachmentCount; i++) {
            factory.showStatusMessage(attachmentMessage + i + ofMessage + attachmentCount);
            Attachment currentAttachment = (Attachment) attachments.getAttachments().get(i);
            if (currentAttachment instanceof MBPAttachment)
              multipart.addBodyPart(((MBPAttachment) currentAttachment).getMimeBodyPart());
            else {
              MimeBodyPart attachmentMbp = new MimeBodyPart();
              //attachmentMbp.setContent(currentAttachment.getContent(), currentAttachment.getMimeType().toString());

              DataHandler dh = currentAttachment.getDataHandler();
              attachmentMbp.setFileName(currentAttachment.getName());
              attachmentMbp.setDescription(currentAttachment.getName());
              attachmentMbp.setDataHandler( dh );
              attachmentMbp.setHeader("Content-Type", currentAttachment.getMimeType().toString());

              multipart.addBodyPart(attachmentMbp);
            }
          }
        }

        for (int i = 0; i < keyParts.size(); i++) {
          multipart.addBodyPart((MimeBodyPart) keyParts.get(i));
        }

        factory.showStatusMessage(Pooka.getProperty("info.sendMessage.savingChangesToMessage", "Saving changes to message..."));
        multipart.setSubType("mixed");
        getMessage().setContent(multipart);
        getMessage().saveChanges();
      } else {
        factory.showStatusMessage(Pooka.getProperty("info.sendMessage.savingChangesToMessage", "Saving changes to message..."));
        getMessage().setContent(messageText, messageContentType);
      }

      getMessage().setSentDate(new java.util.Date(System.currentTimeMillis()));

      // do encryption stuff, if necessary.

      // sigh

      factory.showStatusMessage(Pooka.getProperty("info.sendMessage.encryptMessage", "Handing encryption..."));
      message = cryptoInfo.createEncryptedMessage(profile, (MimeMessage) getMessage());
      this.mSendMessageMap.put(message, message.getAllRecipients());

      boolean sent = false;
      if (mailServer != null) {
        factory.showStatusMessage(Pooka.getProperty("info.sendMessage.sendingMessage", "Sending message to mailserver..."));
        mailServer.sendMessage(this);
        sent = true;
      }

      /*
        if (! sent) {
        if (profile != null) {
        URLName urlName = profile.getSendMailURL();
        String sendPrecommand = profile.getSendPrecommand();
        factory.showStatusMessage(Pooka.getProperty("info.sendMessage.sendingMessage", "Sending message to mailserver..."));
        Pooka.getMainPanel().getMailQueue().sendMessage(this, urlName, sendPrecommand);
        sent = true;
        }
        }
      */

      if (! sent) {
        throw new MessagingException(Pooka.getProperty("error.noSMTPServer", "Error sending Message:  No mail server configured."));
      }
    } catch (MessagingException me) {
      ((net.suberic.pooka.gui.NewMessageProxy) getMessageProxy()).sendFailed(null, me);
    } catch (Throwable t) {
      String cause = t.getMessage();
      if (cause == null)
        cause = t.toString();

      MessagingException me = new MessagingException(cause);
      if (t instanceof Exception)
        me.setNextException((Exception)t);

      ((net.suberic.pooka.gui.NewMessageProxy)getMessageProxy()).sendFailed(mailServer, me);
    }
  }

  /**
   * Converts the given address line into an address line suitable for
   * this NewMessageInfo.  Specifically, this goes through each address
   * in the list and adds the UserProfile's defaultDomain to each entry
   * which doesn't have a domain already.
   */
  public String convertAddressLine(String oldLine, UserProfile p) throws javax.mail.internet.AddressException {
    StringBuffer returnValue = new StringBuffer();
    InternetAddress[] addresses = InternetAddress.parse(oldLine, false);
    for (int i = 0; i < addresses.length; i++) {
      String currentAddress = addresses[i].getAddress();
      if (currentAddress.lastIndexOf('@') < 0) {
        currentAddress = currentAddress + "@" + p.getDefaultDomain();
        addresses[i].setAddress(currentAddress);
      }

      returnValue.append(addresses[i].toString());
      if (i+1 < addresses.length)
        returnValue.append(", ");
    }

    return returnValue.toString();
  }

  /**
   * Saves the NewMessageInfo to the sentFolder associated with the
   * given Profile, if any.  Note that if there is a sent folder to
   * save to, this method will likely just place an action in the
   * queue.
   *
   * @return if there is a sent folder to save to.
   */
  public boolean saveToSentFolder(UserProfile profile) {
    final FolderInfo sentFolder = profile.getSentFolder();
    if (sentFolder != null) {
      try {
        final Message newMessage = new MimeMessage((MimeMessage) getMessage());

        sentFolder.getFolderThread().addToQueue(new net.suberic.util.thread.ActionWrapper(new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
              net.suberic.pooka.gui.PookaUIFactory factory = Pooka.getUIFactory();

              try {
                if (sentFolder.getFolder() == null) {
                  factory.showStatusMessage(Pooka.getProperty("info.sendMessage.openingSentFolder", "Opening sent folder..."));
                  sentFolder.openFolder(Folder.READ_WRITE);
                }

                if (sentFolder.getFolder() == null) {
                  throw new MessagingException("failed to load sent folder " + sentFolder);
                }

                newMessage.setSentDate(java.util.Calendar.getInstance().getTime());
                factory.showStatusMessage(Pooka.getProperty("info.sendMessage.savingToSentFolder", "Saving message to sent folder..."));

                sentFolder.getFolder().appendMessages(new Message[] {newMessage});
              } catch (OperationCancelledException oce) {
                // don't show a message if we cancel.
              } catch (MessagingException me) {
                me.printStackTrace();
                Pooka.getUIFactory().showError(Pooka.getProperty("Error.SaveFile.toSentFolder", "Error saving file to sent folder."), Pooka.getProperty("error.SaveFile.toSentFolder.title", "Error storing message."));
              } finally {
                ((net.suberic.pooka.gui.NewMessageProxy)getMessageProxy()).sendSucceeded(false);
              }
            }
          }, sentFolder.getFolderThread()), new java.awt.event.ActionEvent(this, 1, "message-send"));
      } catch (MessagingException me) {
        me.printStackTrace();
        Pooka.getUIFactory().showError(Pooka.getProperty("Error.SaveFile.toSentFolder", "Error saving file to sent folder."), Pooka.getProperty("error.SaveFile.toSentFolder.title", "Error storing message."));

      }
      return true;
    }

    return false;
  }

  /**
   * Adds an attachment to this message.
   */
  public void addAttachment(Attachment attachment) {
    attachments.addAttachment(attachment, false);
  }

  /**
   * Removes an attachment from this message.
   */
  public int removeAttachment(Attachment part) {
    if (attachments != null) {
      int index = attachments.getAttachments().indexOf(part);
      attachments.removeAttachment(part);
      return index;
    }

    return -1;
  }

  /**
   * Attaches the given File to the message.
   */
  public void attachFile(File f) throws MessagingException {
    attachFile(f, null);
  }

  /**
   * Attaches the given File to the message using the given content type.
   */
  public void attachFile(File f, String contentType) throws MessagingException {
    // borrowing liberally from ICEMail here.

    UpdatableMBP mbp = new UpdatableMBP();

    FileDataSource fds = new FileDataSource(f);

    DataHandler dh = new DataHandler(fds);

    mbp.setFileName(f.getName());

    if (Pooka.getMimeTypesMap().getContentType(f).startsWith("text"))
      mbp.setDisposition(Part.INLINE);
    else
      mbp.setDisposition(Part.ATTACHMENT);

    mbp.setDescription(f.getName());

    mbp.setDataHandler( dh );

    if (contentType == null) {
      String type = dh.getContentType();

      mbp.setHeader("Content-Type", type);
    } else {
      mbp.setHeader("Content-Type", contentType);
    }

    mbp.updateMyHeaders();

    addAttachment(new MBPAttachment(mbp));
  }

  /**
   * Returns the given header on the wrapped Message.
   */
  public String getHeader(String headerName, String delimeter) throws MessagingException {
    return ((MimeMessage)getMessage()).getHeader(headerName, delimeter);
  }

  /**
   * Gets the text part of the wrapped message.
   */
  public String getTextPart(boolean showFullHeaders) {
    try {
      Object content = message.getContent();
      if (content instanceof String)
        return (String) content;
      else if (content instanceof Multipart) {
        AttachmentBundle bundle = MailUtilities.parseAttachments((Multipart) content);
        return bundle.getTextPart(false, false, 10000, getTruncationMessage());
      } else
        return null;
    } catch (java.io.IOException ioe) {
      // since this is a NewMessageInfo, there really shouldn't be an
      // IOException
      return null;
    } catch (MessagingException me) {
      // since this is a NewMessageInfo, there really shouldn't be a
      // MessagingException
      return null;
    }
  }

  /**
   * Marks the message as a draft message and then saves it to the outbox
   * folder given.
   */
  public void saveDraft(FolderInfo outboxFolder, UserProfile profile, InternetHeaders headers, String messageText, String messageContentType) throws MessagingException, OperationCancelledException {
    net.suberic.pooka.gui.PookaUIFactory factory = Pooka.getUIFactory();

    MimeMessage mMsg = (MimeMessage) message;

    factory.showStatusMessage(Pooka.getProperty("info.sendMessage.settingHeaders", "Setting headers..."));

    Enumeration individualHeaders = headers.getAllHeaders();
    while(individualHeaders.hasMoreElements()) {
      Header currentHeader = (Header) individualHeaders.nextElement();
      mMsg.setHeader(currentHeader.getName(), currentHeader.getValue());
    }

    if (Pooka.getProperty("Pooka.lineWrap", "").equalsIgnoreCase("true"))
      messageText=net.suberic.pooka.MailUtilities.wrapText(messageText);

    factory.showStatusMessage(Pooka.getProperty("info.sendMessage.addingMessageText", "Parsing message text..."));
    if (attachments.getAttachments() != null && attachments.getAttachments().size() > 0) {
      MimeBodyPart mbp = new MimeBodyPart();
      mbp.setContent(messageText, messageContentType);
      MimeMultipart multipart = new MimeMultipart();
      multipart.addBodyPart(mbp);

      // i should really use the text parsing code for this, but...
      String attachmentMessage=Pooka.getProperty("info.sendMessage.addingAttachment.1", "Adding attachment ");
      String ofMessage = Pooka.getProperty("info.sendMessage.addingAttachment.2", " of ");
      int attachmentCount = attachments.getAttachments().size();
      for (int i = 0; i < attachmentCount; i++) {
        factory.showStatusMessage(attachmentMessage + i + ofMessage + attachmentCount);
        multipart.addBodyPart(((MBPAttachment)attachments.getAttachments().elementAt(i)).getMimeBodyPart());
      }

      factory.showStatusMessage(Pooka.getProperty("info.sendMessage.savingChangesToMessage", "Saving changes to message..."));
      multipart.setSubType("mixed");
      getMessage().setContent(multipart);
      getMessage().saveChanges();
    } else {
      factory.showStatusMessage(Pooka.getProperty("info.sendMessage.savingChangesToMessage", "Saving changes to message..."));
      getMessage().setContent(messageText, messageContentType);
    }

    getMessage().setSentDate(new java.util.Date(System.currentTimeMillis()));

    getMessage().setFlag(Flags.Flag.DRAFT, true);
    outboxFolder.appendMessages(new MessageInfo[] { this });
  }

  /**
   * The full map of Messages to be sent, which in turn map to the
   * recipients they will go to.  If there is no Address array as the
   * value in the map, then the message goes out to all recipients in
   * the headers.
   */
  public Map getSendMessageMap() {
    return mSendMessageMap;
  }

  /**
   * The full map of Messages to be sent, which in turn map to the
   * recipients they will go to.  If there is no Address array as the
   * value in the map, then the message goes out to all recipients in
   * the headers.
   */
  void setSendMessageMap(Map pSendMessageMap) {
    mSendMessageMap = pSendMessageMap;
  }

  /**
   * As specified by interface net.suberic.pooka.UserProfileContainer.
   *
   * If
   */
  public UserProfile getDefaultProfile() {
    if (mProfile == null)
      return super.getDefaultProfile();
    else
      return mProfile;
  }

  /**
   * Sets the default UserProfile for this NewMessageInfo.  Note that
   * this UserProfile is not necessarily the one that the message will be
   * sent using; rather, it's the one that will be selected in the UI
   * by default.
   */
  public void setDefaultProfile(UserProfile pProfile) {
    mProfile = pProfile;
  }


}
