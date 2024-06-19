/*
*  Code: EmailAddress.java
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
import java.util.StringTokenizer;
import java.util.Arrays;

/**
* This class models an imutable email address object. The imutablility makes sure
* that once created, the address object cannot change. The modifiers in this
* class works as the modifiers in the String class - thay all return new objects
* that reflects the change - the original object is unchanged. This class also 
* provides a static method for email address verification and one for parsing
* an address from a string.<p>
*
* A valid name object according to the RFC 822 is a String that does not contain 
* the characters 0 - 31, 34 or 127 which is control characters, delete and qoute. 
* This class will ruthlessly accept ANY strings as valid names and it is up
* to the implementors and users of this class to make sure that the name string
* get encoded or left out before attempting to use it in a protocoll specific 
* manner.<p>
*
* A valid address string cannot  - like the name string - contain the characters 0 - 31, 
* 34 or 127. It also cannot contain spaces. An address must also  contain a "@" character 
* which cannot start or end the address string.<p>
*
* Upon creation of the address object an InvalidFormatException will be thrown if the 
* address does not conform to the above rules.
*
* @author Lars J. Nilsson
* @version 2.2 14/05/2002
*/

public final class EmailAddress implements Serializable, Cloneable {
    
    /**
    * RFC 822 specials
    */
    
    public static final char[] RFC822_SPECIALS = { '(', ')', '<', '>', '@', ',', ';', ':', '\\', '\"', '.', '[', ']' };
    
    
    /** Version id. */
    
    static final long serialVersionUID = -4475382869213986293L;
    
	
	/** Address holders name. */
	
	private final String address;
	
	
	/** Email address. */
	
	private final String name;
	
	
	/**
	* Construct a new email address object. An email address cannot contain the
	* characters 0 - 31, 34, 127 or any of the RFC822 special characters or spaces and must 
	* contain a "@" character, an InvalidFormatException will be throw if the address provided to this constrcutor
	* does not abide to those rules. 
	*/
	
	public EmailAddress(String address) throws InvalidFormatException { 
	   this("", address);
	}


	/**
    * Construct a new email address object. An email address cannot contain the
    * characters 0 - 31, 34, 127 or any of the RFC822 special characters or spaces and must 
    * contain a "@" character, an InvalidFormatException will be throw if the address provided to this constrcutor
    * does not abide to those rules. 
	*/
	
	public EmailAddress(String name, String address) throws InvalidFormatException { 
	   this.name = name;
	   if(isValid(address)) this.address = address;
	   else throw new InvalidFormatException("Invalid address to contructor: " + address);
	}

	
	/**
	* Set email address name. Since the email address object is imutable the set name
	* will not change the current object - it will return a new object with the name set.
	*/
	
	public EmailAddress setName(String name) { 
	    try {
	       return new EmailAddress(name, this.address);
	    } catch(InvalidFormatException e) { 
	       throw new IncompatibleClassChangeError("Address validity error: " + address);
	    }
	}	
	
	
	/**
	* Get the address name. This method will return an empty String if
	* there is no name provided.
	*/
	
    public String getName() { 
        return name;
    }	
    
    
    /**
    * Set email address. Since the email address object is imutable, this
    * method will return a new email address object. The format of the address 
    * will be checked (no characters - 31, 34, 127 or spaces, no RFC822 specials 
    * except full stop - and must contain a "@" character) and an InvalidFormatException will be thrown if needed.
    */
    
    public EmailAddress setAddress(String address) throws InvalidFormatException { 
        return new EmailAddress(this.name, address);
    }


    /**
    * Get the email address. This address is validaded and ready.
    */

	public String getAddress() {
	   return address;
	}

    
    /**
    * Clone this address. Since the address cannot be created without a validity
    * check we can supress the InvalidFormatException in this operation and
    * return a new object direct.
    */
    
    public Object clone() {
        try {
            return new EmailAddress(name, address);
        } catch(InvalidFormatException e) { 
	       throw new IncompatibleClassChangeError("Address validity error: " + address);
	    }
    }


    /**
    * Check the equality of the provided email address. This method will check
    * the address part of an email address only and is not case sensitive. Thus
    * "Kim &lt;kim@kim.com&gt;" is equal to "Kimmy &lt;Kim@Kím.com&gt;" but not 
    * to "Kimmmy &lt;kimmy@kim.com&gt;".
    */
	
	public boolean equals(Object o) { 
	    if(!(o instanceof EmailAddress)) return false;
	    if((this.address).equalsIgnoreCase(((EmailAddress)o).getAddress())) return true;
	    else return false;
	}
	
	
	/** Get hash code. */
	
	public int hashCode() {
	    return address.toLowerCase().hashCode();
    }


    /**
    * Check for email address validity. An email address must not contain the
    * characters 0 - 31, 34, 127 or spaces, and no RFC922 specials. It must however contain a "@" 
    * character which cannot be located at the start or the end of the address.
    */
	
	public static boolean isValid(String address) { 
        if(address == null || address.length() == 0) return false;
        else {
        
            // check for @ character
            int at = address.indexOf("@");
    
            if(at == -1) return false; // doesn't exist
            else if(at == 0 || at == (address.length() - 1)) return false; // wrong position
            else if(address.indexOf("@", at + 1) != -1) return false; // more than one
            else if(address.indexOf(" ") > -1) return false; // no spaces allowed
            else {
                
                // check characters, break loop if there is any and return    
                for(int i = 0; i < address.length(); i++) {
                    char ch = address.charAt(i);
                    if((ch >= 0 && ch <= 31) || ch == 34 || ch == 127 || isSpecial(ch, true, true)) {
                        return false;
                    }
                }
                return true;
            } 
        }
	}

    /**
    * This method tries to parse a string into an email address object, according to 
    * the RFC 822 address rules. If the parsing fails the method will throw an invalid 
    * format exception.<p>
    *
    * The parser will attempt to read a single address line wich may contain escape
    * characters and foldings but not encodings. Thus the following lines will all be
    * valid addresses:<br><br>
    *
    * - "Lars J. Nilsson" &lt;myaddress@address.com&gt;<br>
    * - Lars \"J.\" Nilsson myaddress@address.com<br>
    * - "Lars \"J.\" Nilsson" &lt;myaddress@address.com&gt;<p>
    *
    * The parsing mechanism attempts to unfold the given value if it contains
    * line breaks. However, this class is considered be within a mail protocoll
    * context (protocoll server space), meaning that it does not support linear 
    * white space within address domain tokens. That limitation also rules out 
    * folding within tokens. Thus the following string is regarded as valid...<br><br>
    *
    * - "Lars J. Nilsson" <br>
    * &nbsp;&nbsp;myaddress@address.com<br><br>
    *
    * ... while the following two examples will fail to parse correctly due to
    * the linear white space. They are considered invalid:<br><br>
    *
    * - "Lars J. Nilsson" myaddress @ address.com<br><br>
    * - "Lars J. Nilsson" myaddress@<br>
    * &nbsp;&nbsp;address.com<br><br>
    *
    * Most email constructors will honor the recomendation from RC 822 that
    * folding is limited to "higher-level syntactic breaks" and that no linear white
    * space shall occur between word tokens and their dots or "at"-signs when dealing
    * with a protocoll server. For addresses this means that they will very seldon 
    * be folded other than between addresses in a comma separated list. Still it is 
    * recomended that the unfolding of addresses is done by the mail reading 
    * mechanism during in- or output, and that the same mechanism also checks for 
    * linear white space.
    */

    public static EmailAddress parseAddress(String input) throws InvalidFormatException {
        StringTokenizer str = new StringTokenizer(input);
        StringBuffer name = new StringBuffer(); 
        String address = ""; 
        while(str.hasMoreTokens()) {
                
            String tmp = str.nextToken();
            if(tmp.indexOf("@") > -1) { // this is an address part

                if(tmp.length() > 0 && tmp.charAt(0) == '<') { // delete prececiding "<" and last ">" if they exist                    
                    tmp = tmp.substring(1);
                    if(tmp.length() > 0 && tmp.charAt(tmp.length() - 1) == '>') tmp = tmp.substring(0, tmp.length() - 1);
                }
            
                // if the address is valid we'll save it and break - if not, well
                // continue there might come an valid one after this
                    
                if(isValid(tmp)) {
                    address = tmp;
                    break;
                }
                 
            } else {
                
                // it's not an address - it's part of the name, roll through to 
                // check for escape characters check for 
                for(int i = 0; i < tmp.length(); i++) {
                    if((i + 1) < tmp.length() && tmp.charAt(i) == '\\') name.append(tmp.charAt(++i));
                    else name.append(tmp.charAt(i));
                }
            
                name.append(' '); // space between name atoms
            }
        }
    
        // oops... no address
        if(address.length() == 0) throw new InvalidFormatException("String \"" + input + "\" cannot be parsed into a valid email address");
        
        // check the name for end and start qoute characters
        // and delete them and trim end
        if(name.length() > 0 && name.charAt(0) == '\"') {
            name.deleteCharAt(0);
            while(name.length() > 0 && (name.charAt(name.length() - 1) == '\"' || name.charAt(name.length() - 1) == ' ')) name.deleteCharAt(name.length() - 1);
        }
        return new EmailAddress(name.toString().trim(), address.trim());
    }
    
    
    /** 
    * Convert address to String. The address will be presented
    * without quotes for the name and address delimiters only
    * if there is a name present, so this mehtod might return
    * either: "Lars J. Nilsson <webmster@larsan.net>" or
    * "webmaster@larsan.net".
    */
    
    public String toString() {
        StringBuffer answer = new StringBuffer();
        if(getName() != null && getName().length() > 0) {
            answer.append(getName()).append(" <");
            answer.append(getAddress()).append('>');
        } else answer.append(getAddress());
        return answer.toString();
    }
    
    
    /**
    * Check if character is one of the "rfc 822 special chars. Boolean argument makes it
    * possible to ignore the '@' and '.' characters
    */
    
    private static boolean isSpecial(char ch, boolean ignoreAt, boolean ignoreDot) {
        for(int i = 0; i < RFC822_SPECIALS.length; i++) {
            if(ignoreAt && ch == '@') continue;
            else if(ignoreDot && ch == '.') continue;
            else if(ch == RFC822_SPECIALS[i]) return true;
        }
        return false;
    }
}