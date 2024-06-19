/**
*  Code: POPException.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2000 Lars J. Nilsson
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

package net.larsan.email.pop;

/**
* This is the base exception class for the POPRetriever. It is thrown
* on transport or protocoll related errors. It holds references to the remote
* server address.
*
* @author Lars J. Nilsson
* @version 1.0 08/10/00
*/

public class POPException extends net.larsan.email.EmailException {
    
    private String host;
	
	/**
	* Contruct a new exception with a server but without a message.
	*/
	
	public POPException(String host) {
	   this(host, "");
	}


    /**
    * Construct a new exception with a remote server and a message.
    */

	public POPException(String host, String message) {
	   super(message);
	   
	   this.host = host;

	}

    /**
    * Get the pop server address.
    */

    public String getServerAddress() {
        return host;
    }
}