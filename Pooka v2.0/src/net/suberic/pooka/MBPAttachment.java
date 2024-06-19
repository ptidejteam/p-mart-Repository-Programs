package net.suberic.pooka;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import java.io.*;
import java.util.*;

public class MBPAttachment extends Attachment {
  
  MimeBodyPart mbp;
  
  public MBPAttachment(MimeBodyPart part) throws MessagingException {
    super(part);
    mbp = part;
  }
  
  public MimeBodyPart getMimeBodyPart() {
    return mbp;
  }
  
}

