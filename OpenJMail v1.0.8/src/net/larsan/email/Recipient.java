/**
*  Code: Recipient.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2000 Lars J. Nilsson
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

/**
* This is a wrapper class for a simple recipient. It holds the recipient
* type and email address and is imutable. The recipient can be checked for equality 
* using the <code>equals</code> method. This method checks if the two recipients
* is of the same type and in that case - if the also have the
* same email address.<p>
*
* The different recipient types is stated in the RecipientType class.
* To create a carbon copy recipient to address none@none.com would look like 
* this:
*
* <pre>
*   try {
*    
*       EmailAddress address = new EmailAddress("none@none.com");
*       Recipient recipient = new Recipient(RecipientType.CC, address);
*
*   } catch(InvalidFormatException e) {
*       // handle the malformed email address here
*   }
* </pre>
*
* @author Lars J. Nilsson
* @version 1.0.1 21/10/2001
*/

public final class Recipient implements java.io.Serializable {
    
    
    /** Version id. */
    
    static final long serialVersionUID = 1051064094354407013L;
    
    
    /** Recipient type. See RecipientType class for identifiers. */
    
	private final int type;
	
	
	/** Recipient address. */
	
	private final EmailAddress address;
		
    /**
    * Create a new recipient. The different recipient types are
    * located as static members of the RecipientType class, and 
    * they are rather simple:
    *
    * <pre>
    *   RecipientType.TO
    *   RecipientType.CC
    *   RecipientType.BCC
    * </pre>
    */
    
	public Recipient(int type, EmailAddress address) { 
	   this.type = type;
	   this.address = address;
	}

    /**
    * Get the recipient type. The different recipient types are
    * located as static members of the RecipientType class:
    *
    * <pre>
    *   RecipientType.TO
    *   RecipientType.CC
    *   RecipientType.BCC
    * </pre>
    */

    public int getType() { 
        return type;
    }

    /**
    * Get the recipients email address
    */

	public EmailAddress getAddress() { 
	    return address;
	}

    /**
    * Check this recipient for equality. Two recipients are
    * equal if they is of the same recipient type and their
    * email addresses are equal.
    */

	public boolean equals(Object o) { 
	    if(!(o instanceof Recipient)) return false;
	    Recipient recipient = (Recipient)o;
	    if(type == recipient.getType() && address.equals(recipient.getAddress())) return true;
	    else return false;
	}  
	
	
	/** Get hash code. */
	
	public int hashCode() {
	    return (getType() + ":" + address.getAddress()).toLowerCase().hashCode();  
	}
	
	/** To string, 'type' + : + address */
	
	public String toString() {
	    return RecipientType.translateDecimal(getType()) + ": " + address.toString();
	} 
}
