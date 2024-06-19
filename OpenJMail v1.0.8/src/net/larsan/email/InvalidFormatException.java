/**
*  Code: InvalidFormatException.java
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
* This exception is thrown when a class that validates members
* after certain given syntax rules encounters an invalid format which it
* cannot recover from.
*
* @author Lars J. Nilsson
* @version 1.0 08/10/00
*/

public class InvalidFormatException extends EmailException {
	
	/**
	* Contruct a new exception without message.
	*/
	
	public InvalidFormatException() {
	   this(new String());
	}

    /**
    * Construct a new exception with a message.
    */

	public InvalidFormatException(String message) {
	   super(message);
	}
}
