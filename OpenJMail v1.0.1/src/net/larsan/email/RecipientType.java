/**
*  Code: RecipientType.java
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
* A simple class that holds different recipients types.
*
* @author Lars J. Nilsson
* @version 1.1 30/03/2001
*/

public class RecipientType {

 
    /** Recipient type to. */

    public static final int TO = 1;

 
    /** Recipient type cc. */

    public static final int CC = 2;

 
    /** Recipient type bcc. */

    public static final int BCC = 3;
    
    
    /** Recipient type string identifiers. */
    
    private static String[] names;
    
    static {
        
        names = new String[3];
        
        names[0] = "To";
        names[1] = "Cc";
        names[2] = "Bcc";
        
    }

    /** Get this identifier as a string abbrevation. */
    
    public static String translateDecimal(int decimal) {
        if(decimal < 4 && decimal > 0) return names[decimal - 1];
        else return new String();
    }  
    
    
    /** Get this string abbrevation as an integer identifier. */
    
    public static int translateName(String name) {
        
        if(name.equalsIgnoreCase("To")) return 1;
        else if(name.equalsIgnoreCase("Cc")) return 2;
        else if(name.equalsIgnoreCase("Bcc")) return 3;
        else return 0;
        
    }
}