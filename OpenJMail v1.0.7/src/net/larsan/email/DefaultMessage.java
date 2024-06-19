/*
*  Code: DefaultMessage.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2001 Lars J. Nilsson
*
*       This program is free software; you can redistribute it and/or
*       modify it under the terms of the GNU Lesser General Public License
*       as published by the Free Software Foundation; either version 2.1
*       of the License, or (at your option) any later version.
*
*       This program is distributed in the hope that it will be useful,
*       but WITHOUT ANY WARRANTY; without even the implied warranty of
*       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*       GNU Lesser General Public License for more details.
*
*       You should have received a copy of the GNU Lesser General Public License
*       along with this program; if not, write to the Free Software
*       Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package net.larsan.email;

import java.io.*;
import java.util.*;

import net.larsan.email.encoder.*;

/**
* This class models a default implementation of the Message interface. It denotes an
* email message using the MIME standard. This means the message supports attachments,
* multiple bodies, different character sets and so on. The MIME message standard can
* be found in RFC 1521 / 1522.<p>
*
* The constructor of this class requires a Body object. This is because by default the 
* constructor cannot know the supposed content of the message. Of this it follows that
* to create a valid message you have to: 1) create the body content of you message,
* and 2) create the actual message to wrap around the body.<p>
*
* The default recipient handler used by this class handles every recipient as a part
* of the message header. It will try to enforce the use of the AddressField header field
* class for all recipient related header fields. This means that if you instanciate a
* message using a body where there already exists recipients, the recipient handler will
* attempt to convert the fields of the header to AddressField objects if it can. However, 
* it can be noted that this is a possible bug source: Should the header field which the
* recipient handler tries to convert to an AddressField contain an invalid address we have
* a problem. The recipient handler will ty to ignore this and simply remove the invalid
* address, should this behaviour not be acceptable, users are encouraged to provide their
* own recipient handler implementations.<p>
*
* The default recipient handler uses the "Q" encoding for all address fields and takes 
* a character set parameter in the contructor. This character set must be useable by 
* Java's output stream writer methods. A simple way to determine if a character set 
* name is acceptable is to try to create a new String object using a byte array like this:
*
* <pre>
*   try {
*       new String(new byte[0], "yourencodingname");
*   } catch(UnsupportedEncodingException e) { }
* </pre>
*
* The EmailMessage implements the Serializable interface to provide a simple storage
* mechanism. However: Impementors should try to store the message as text as far as 
* possible since this makes the storage more easily parsed by human operators.<p>
*
* This class is not synchronized.
*
* @author Lars J. Nilsson
* @version 1.0.1 01/12/2001
*/

public class DefaultMessage implements Message, Serializable {
    
    
    /** Version id. */
    
    static final long serialVersionUID = 388905453426319495L;
    
    
    /** Message recipient handler. */
    
    protected RecipientHandler recipientHandler;
    
    
    /** Email message body. (and header...) */
    
    protected Body messageBody;
    
    
    /**
    * Construct a new message object with a custom recipient handler.
    */
    
    public DefaultMessage(Body messageBody, RecipientHandler handler) {
        this.recipientHandler = handler;
        this.messageBody = messageBody;
    }


    /**
    * Construct a new message object with a default recipient handler operating
    * on the provided character set.
    */

    public DefaultMessage(Body messageBody, String defaultCharset) {
        recipientHandler = new DefaultRecipientHandler(defaultCharset);
        this.messageBody = messageBody;
    }

	
	/**
    * Get the sender address if available. The address will be parsed from the 
    * header fields "From" or "Sender". The "From" field represents a human sender
    * and must correspond to a valid email box. The "Sender" field is only needed
    * if a message does not contain a single human sender but rather comes from a
    * group, list or any other process.<p>
    *
    * This method returns null if none of those fields are set or no email address 
    * could be parsed from their value.
    */
    
    public EmailAddress getOriginator() {
        
        Header header = messageBody.getHeader();
        
        if(header.exists("From") || header.exists("Sender")) {
            
            // get field, either the "From" or the "Sender"
            
            HeaderField field = header.get("From");
            
            if(field == null) field = header.get("Sender");
            
            if(field instanceof AddressField) {
                
                Iterator i = ((AddressField)field).getAddresses();
                
                // it's only the first address we want
                
                if(i.hasNext()) return (EmailAddress)i.next();
                else return null;

            } else {
                
                try {
                    
                    // attempt to parse field vaue
                    
                    return EmailAddress.parseAddress(field.getValue());
                    
                } catch(InvalidFormatException e) { return null; }
            }
        
        } else return null;
    }

    
    /** 
    * Get the recipient handler responsible for this message. The handler object is 
    * responsible for mapping message recipients to and from the message object.
    * The recipient handler is also responsible for the recipient presentation in 
    * the header.
    */

	public RecipientHandler getRecipientHandler() {
	    return recipientHandler;
	}


    /**
    * Get the message body. This is also the only way of getting a
    * reference to the message header obejct which can be queried for subjects, 
    * recipients, dates and so on.
    */

	public Body getMessageBody() {
	    return messageBody;
	}
	
	
	/** 
	* Get message header. This method is a shorthand for
	* getMessageBody().getHeader().
	*/
	
	public Header getHeader() {
	   return messageBody.getHeader();
    }


    /** 
    * Write the complete message to an output stream. This method should
    * write the content in a protocoll specific manner to the stream. The
    * message will start with a header section followed by one ore more
    * bodies followed by a canonical line break;
    */

	public void write(OutputStream out) throws IOException {
	    messageBody.write(out);
	}


    /** 
	* Write the message header to an output stream. The header will
	* be written to the stream followed by a canonical line break.
	*/

	public void writeHeader(OutputStream out) throws IOException {
	    messageBody.getHeader().write(out);
	}

    /** 
	* Convert message to string. This method returns null if the message
	* writing should fail for some reason.
    */

	public String toString() {
	   
	   try {
        
           ByteArrayOutputStream ba = new ByteArrayOutputStream();
           BufferedOutputStream bout = new BufferedOutputStream(ba);
           
           this.write(bout);
           
           bout.flush();
           
           return new String(ba.toByteArray());
           
       } catch(IOException e) {
        
           return null;
       }
	}

    /**
    * This is a default implentation of the RecipientHandler interface. This
    * class is maintaining the recipients as a part of the header object of
    * the enclosing message. If it encounters an address header field which
    * is not an instance of the AddressField class it will try to parse
    * the field and convert it to an AddressField instance.
    *
    * @author Lars J. Nilsson
    * @version 1.0 30/03/2001
    */

    protected class DefaultRecipientHandler implements RecipientHandler {
        
        
        /** Default character set for header field encodings. */
        
        private String defaultCharset;
        
        
        /** Default constructor. */
        
        public DefaultRecipientHandler(String charset) {
            defaultCharset = charset;
        }

            
        /** Add recipient to the message. */
            
        public void addRecipient(Recipient recipient) {
            addAddress(recipient.getType(), recipient.getAddress());
        }
        
        
        /** Add multiple recipients to the message. */
        
        public void addRecipients(Recipient[] recipients) {
            for(int i = 0; i < recipients.length; i++) {
                addAddress(recipients[i].getType(), recipients[i].getAddress());
            }
        }
        
        
        /** Set recipients in message. This action removes old recipients. */
        
        public void setRecipients(Recipient[] recipients) {
            clearRecipients();
            addRecipients(recipients);
        }
        
        
        /** Remove all recipients from message. */
            
        public void clearRecipients() {
                
            Header header = getMessageBody().getHeader();
                
            header.remove("To");
            header.remove("Cc");
            header.remove("Bcc");
            
        }
        
        
        /** Remove a certain recipient from the message. */
        
        public void removeRecipient(Recipient recipient) {
                
            Header header = getMessageBody().getHeader();
                
            if(header.exists(RecipientType.translateDecimal(recipient.getType()))) {
                    
                HeaderField field = header.get(RecipientType.translateDecimal(recipient.getType()));
                    
                if(!(field instanceof AddressField)) field = convertField(recipient.getType(), field);
                    
                ((AddressField)field).removeAddress(recipient.getAddress());
                    
            }
        }
        
        
        /** 
        * Remove all recipients of a certain type. The recipient types
        * are contained in the RecipientType class.
        */
        
        public void removeRecipients(int type) {
            getMessageBody().getHeader().remove(RecipientType.translateDecimal(type));
        }
        
        
        /** Get all recipients. */
        
        public Recipient[] getRecipients() {
                
            Header header = getMessageBody().getHeader();
                
            ArrayList tmp = new ArrayList();
                
            // ugly loop, just because I happend to know that the
            // to, cc and bcc identifiers equals 1, 2, and 3...
                
            for(int i = 1; i < 4; i++) {
                    
                if(header.exists(RecipientType.translateDecimal(i))) {
                    
                    HeaderField field = header.get(RecipientType.translateDecimal(i));
                    
                    if(!(field instanceof AddressField)) field = convertField(i, field);
                    
                    Iterator it = ((AddressField)field).getAddresses();
                        
                    while(it.hasNext()) tmp.add(new Recipient(i, (EmailAddress)it.next()));
                        
                }
            }
            
            Recipient[] answer = new Recipient[tmp.size()];
            tmp.toArray(answer);
            return answer;
                
        }
        
            
        /** 
        * Get all recipients of a certain type. The recipient types
        * are contained in the RecipientType class.
        */
        
        public Recipient[] getRecipients(int type) {
                
            Header header = getMessageBody().getHeader();
                
            if(header.exists(RecipientType.translateDecimal(type))) {

                LinkedList tmp = new LinkedList();
                    
                HeaderField field = header.get(RecipientType.translateDecimal(type));

                if(!(field instanceof AddressField)) field = convertField(type, field);
                    
                Iterator i = ((AddressField)field).iterator();

                while(i.hasNext()) tmp.add(new Recipient(type, (EmailAddress)i.next()));

                Recipient[] answer = new Recipient[tmp.size()];
                tmp.toArray(answer);
                return answer;
                    
            } else return new Recipient[0];
        }
        
        
        /** Check for a recipient. */
        
        public boolean contains(Recipient rec) {
            
            Header header = getMessageBody().getHeader();
                
            HeaderField field = header.get(RecipientType.translateDecimal(rec.getType()));
                
            if(field == null) return false;
                            
            if(!(field instanceof AddressField)) field = convertField(rec.getType(), field);
            
            return ((AddressField)field).containsAddress(rec.getAddress());
            
        }
        
        
        /** Check for an address. */
        
        public int contains(EmailAddress address) {
            
            if(contains(new Recipient(RecipientType.TO, address))) return RecipientType.TO;
            else if(contains(new Recipient(RecipientType.CC, address))) return RecipientType.CC;
            else if(contains(new Recipient(RecipientType.BCC, address))) return RecipientType.BCC; 
            else return -1;
            
        }
        
            
        /** Add an adress of a certain recipient type to header. */
            
        private void addAddress(int type, EmailAddress address) {
                
            Header header = getMessageBody().getHeader();
                
            HeaderField field = header.get(RecipientType.translateDecimal(type));
                
            if(field == null) field = new AddressField(RecipientType.translateDecimal(type), new Q_Encoder(defaultCharset));
                
            if(!(field instanceof AddressField)) field = convertField(type, field);
	         
	        ((AddressField)field).addAddress(address);
	            
	        header.set(field);
	           
	    }
	     
	       
	    /** Attempt to convert a header field to an AddressField instance. */
	     
	    private AddressField convertField(int type, HeaderField field) {
	           
	        Header header = getMessageBody().getHeader();
	           
	        AddressField newField = new AddressField(RecipientType.translateDecimal(type), new Q_Encoder(defaultCharset));
                
            // tokenize field by commas: the EmailAddress class will handle the rest...
                
            StringTokenizer str = new StringTokenizer(field.getValue(), ",", false);
	           
	        while(str.hasMoreTokens()) {
	               
	            try {
	                   
	                // attempt to parse...
	                       
	                newField.addAddress(EmailAddress.parseAddress(str.nextToken()));
	                   
	            } catch(InvalidFormatException e) {}
	        }
	         
	        // set the new field to the header
	                
	        header.set(newField);
	            
	        return newField;
	                
	    }
    }
}