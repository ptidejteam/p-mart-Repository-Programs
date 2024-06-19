/*
*  Code: AddressField.java
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import net.larsan.email.encoder.Q_Encoder;
import net.larsan.email.encoder.Encoder;
import net.larsan.email.stream.LineBreak;

/**
* This is a dedicated header field which extends the EmailHeaderField
* for addresses including header field encoding possibilities. Encoding is
* performed by a Encoder object which can be specified upon creation of
* a new adress field object. Default encoding is the so called "q" encoding
* using the "ISO-8859-1" charcter set.<p>
*
* When written to an output stream multiple addresses will be separated by a ",", a canonical line break and a tab, 
* this behaviour is called "folding" and is presented in RFC 822.<p>
*
* This class is not synchronized.
*
* @author Lars J. Nilsson
* @version 1.3 31/08/2008
*/

public class AddressField extends EmailHeaderField {
    
    /** A List containing the email address objects of this field. */
    
	protected ArrayList addresses;
	
	
	/** Serial version. */
	
    static final long serialVersionUID = -6519390044345280757L;
	
	
	/**
	* Contruct a new address with a field name and the default 
	* encoder - the Q_Encoder using the "ISO-8859-1" character set.
	*/
	
	public AddressField(String name) {
	    this(name, new Q_Encoder("ISO-8859-1"));
    }

	
	/**
	* Contruct a new address field with a specified encoder and field name.
	*/

    public AddressField(String name, Encoder encoder) {
        super(name, "", encoder);
        addresses = new ArrayList(5);
    }
    
    
    /**
    * Clear the field by removing all addresses.
    */

    public void clearField() {
        addresses.clear();
    }


    /**
    * Add an address to this field.
    */

    public void addAddress(EmailAddress address) {
        addresses.add(address);
    }

    
    /**
    * Test if an address exist in this field.
    */

    public boolean containsAddress(EmailAddress address) {
        return addresses.contains(address);
    }

    
    /**
    * Remove an address from this field.
    */

    public void removeAddress(EmailAddress address) {
        addresses.remove(address);
    }

    
    /**
    * Get all addresses as an iteration of EmailAddress objects. Note that
    * changes made to this iterator will reflect on the field content.
    */

    public Iterator getAddresses() {
        return addresses.iterator();
    }
    
    
    /**
    * Get all addresses as an iteration of EmailAddress objects. Note that
    * changes made to this iterator will reflect on the field content.
    */
    
    public Iterator iterator() {
        return getAddresses();
    }


    /**
    * Get the number of addresses in this field.
    */
    
    public int length() {
        return addresses.size();
    }
    
    
    /** 
    * Equality check. Two lists are considered equal if they have the same label, length
    * and contains the same addresses. Note the the order of the addresses is not
    * considered.
    */
    
    public boolean equals(Object o) {
        if(!(o instanceof AddressField)) return false;
        AddressField other = (AddressField)o;
        if(length() != other.length()) return false;
        else if(!other.getName().equalsIgnoreCase(getName())) return false;
        else {
            for(Iterator i = iterator(); i.hasNext(); ) {
                EmailAddress addr = (EmailAddress)i.next();
                if(!other.containsAddress(addr)) return false;
            }
            return true;
        }
    }
    
    
    /** Hash code **/
    
    public int hashCode() {
        StringBuffer buff = new StringBuffer(getName().toLowerCase());
        for(Iterator i = iterator(); i.hasNext(); ) {
            EmailAddress addr = (EmailAddress)i.next();
            buff.append(addr.getAddress().toLowerCase());
        }
        return buff.toString().hashCode();
    }
    
    
    /** Get field value, ie: addresses separated by commas. */
    
    public String getValue() {
        StringBuffer answer = new StringBuffer();   
        for(Iterator it = addresses.iterator(); it.hasNext();) {
            EmailAddress tmp = (EmailAddress)it.next();
            if(tmp.getName().length() > 0) {
                answer.append(tmp.getName()).append(' ');
            }
            answer.append('<').append(tmp.getAddress()).append('>');
            if(it.hasNext()) answer.append(", ");
        }
        return answer.toString();
    }

    
    /**
    * Write this header field to an output stream.
    */

    public void write(OutputStream out) throws IOException { 
    
        OutputStreamWriter writer = new OutputStreamWriter(out, "US-ASCII");
        
        writer.write(getName());
        writer.write(": ");
        
        // next the stream might be passes to an encoder, so flush
        
        writer.flush();
        
        for(int i = 0; i < addresses.size(); i++) {
            
            EmailAddress address = (EmailAddress)addresses.get(i);
            
            if(address.getName().length() > 0) {
                
                getEncoder().encode(out, getEncoder().toByteArray(address.getName().toCharArray()));
                
                writer.write(' ');
                
            }
            
            writer.write('<');
            
            writer.write(address.getAddress());
            
            writer.write('>');
            
            if(i < addresses.size() - 1) { 
            
                // folding version: comma, white space, new line, tab
                
                writer.write(", ");
                
                writer.flush();
                
                out.write(LineBreak.CRLF);
                
                writer.write('\t');
                
            }
        
            writer.flush();
        }
    }
}