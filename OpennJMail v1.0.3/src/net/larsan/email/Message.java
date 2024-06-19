/*
*  Code: Message.java
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

/**
* This interface is the base identifier for messages and provides
* a basic contract for it's envorinment. This representation of an email message 
* is implementad as an interface for several reasons of which the most important 
* one is this: The email massage behaves and has the same properties as a MIME 
* message body, it has both a header and a body part, and yet it is not a body 
* object and a body object is not an email message. But by using this interface 
* implementors can use Java's powerful polymorphism design to create object
* which can appear as both types.
*
* @author Lars J. Nilsson
* @version 1.2 21/10/2001
*/

public interface Message extends Serializable {
    
    /** Version id. */
    
    static final long serialVersionUID = -1194458295103929928L;
    
    
    /** 
    * Get the address from which this message is sent. This can be either
    * a human sender or a group, a list or other process. The email address object 
    * contains a valid email address and an optional name of the address holder. This 
    * method should return null if the message contain no sender address.
    */
    
    public EmailAddress getOriginator();
    
    
    /**
    * Get the current recipient handler. The handler object is responsible for mapping
    * message recipients to and from the message object. The recipient handler is also
    * responsible for the recipient presentation in the header.
    */
    
    public RecipientHandler getRecipientHandler();
    
    
    /**
    * Get the message body. The body can be a container of other bodies 
    * as well as a content body. This behaviour is similar to the AWT containers
    * and makes the email structure very flexible. The body also contains
    * the message header which can be queried for subjects, recipients
    * and so on.
    */
    
    public Body getMessageBody();
    
    
    /**
    * Get message header. This method is a shortcut for 
    * getMessageBody().getHeader().
    */
    
    public Header getHeader();
    
    
    
    /** 
    * Write the complete message to an output stream. This method should
    * write the content in a protocoll specific manner to the stream. The
    * message will start with a header section followed by one ore more
    * bodies.
    */
	
	public void write(OutputStream out) throws java.io.IOException;
	
	
	/** 
	* Write the message header to an output stream. The header will
	* be written to the stream followed by a canonical line break.
	*/
	
	public void writeHeader(OutputStream out) throws java.io.IOException;
	
	
	/** 
	* Convert message to string. Should the converting fail for some reason
	* this method should return null to mark error.
    */
	
	public String toString();
	
}