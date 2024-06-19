/**
*  Code: SMTPException.java
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

package net.larsan.email.smtp;

/**
* This is the base exception class for the smtp sender. It is thrown
* on transport or protocoll related errors. It holds references to the remote
* server address and an eventual error code.
*
* @author Lars J. Nilsson
* @version 1.0 08/10/00
*/

public class SMTPException extends net.larsan.email.EmailException {
    
    private String host;
    private int errorCode;
	
	/**
	* Contruct a new exception with a server but without a message.
	*/
	
	public SMTPException(String host) {
	   this(host, "", -1);
	}

    /**
    * Construct a new exception with a message and a remote server.
    */

	public SMTPException(String host, String message) {
	   this(host, message, -1);
	}

    /**
    * Construct a new exception with a remote server, a message and an error code.
    */

	public SMTPException(String host, String message, int errorCode) {
	   super(message);
	   
	   this.host = host;
	   this.errorCode = errorCode;
	}

    /**
    * Get remote server address;
    */

    public String getServerAddress() {
        return host;
    }

    /**
    * Get error code. Returns -1 if no code is present.
    */

    public int getErrorCode() {
        return errorCode;
    }
}