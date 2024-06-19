/*
*  Code: RecipientHandler.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2001 Lars J. Nilsson
*
*       This program is free software; you can redistribute it and/or
*       modify it under the terms of the GNU General Public License
*       as published by the Free Software Foundation; either version 2
*       of the License, or (at your option) any later version.
*
*       This program is distributed in the hope that it will be useful,
*       but WITHOUT ANY WARRANTY; without even the implied warranty of
*       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*       GNU General Public License for more details.
*
*       You should have received a copy of the GNU General Public License
*       along with this program; if not, write to the Free Software
*       Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package net.larsan.email;

/**
* This interface is responsible for the mapping of recipients in an email
* message object. This responsibility includes mapping of addresses from and
* to the header object.<p>
*
* A recipient is an imutable object which contains an email address and a reference
* to the type of the recipient. Different recipient handlers will want to create 
* the mapping between the header object and the message different, an email list
* application would for example not want all the recipients to be visible in the header
* while an ordinary client mail application certaily would.<p>
*
* Since instances of this interface are responsible for the presentation of the
* addresses in the header object implementors must ensure that their implementation
* conforms to protcoll rules and handles character sets other than the "US-ASCII".
*
* @author Lars J. Nilsson
* @version 1.1 28/03/2001
*/

public interface RecipientHandler extends java.io.Serializable {
    
    /** Add a single recipient to the message. */
    
    public void addRecipient(Recipient recipient);
    
    
    /** Add multiple recipients to the message. */
    
    public void addRecipients(Recipient[] recipients);
    
    
    /**
    * Set multiple recipients to the message. This operation
    * will remove all previous recipients from the message and
    * it's header object.
    */
    
    public void setRecipients(Recipient[] recipients);
    
    
    /** 
    * Get all recipients in in this message as an array. 
    */
    
    public Recipient[] getRecipients();
    
    
    /** 
    * Get recipients of a certain type in an array. See the
    * RecipientType interface for the different type identifiers.
    */
    
    public Recipient[] getRecipients(int type);
    
    
    /** Remove all recipients from the message. */
    
    public void clearRecipients();
    
    
    /** Remove a single recipient. */
    
    public void removeRecipient(Recipient recipient);
    
    
    /** 
    * Remove all recipients of a single type. See the
    * RecipientType interface for the different type identifiers.
    */
    
    public void removeRecipients(int type);
	
}
