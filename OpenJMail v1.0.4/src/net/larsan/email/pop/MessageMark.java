/**
*  Code: MessageMark.java
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
* This class is a simple utility used by the POPRetriever and hold number and size of
* pop server messages. It can either be returned by the POPRetriever as total number and size
* of maildrop as an answer on the "status" command or as the number and size of a specific
* message in the maildrop after a "list" command.
*
* @author Lars J. Nilsson
* @version 1.0 04/12/00
*/

public class MessageMark {
    
    /** Message number or number of messages. */
    
    public final int number;
    
    /** Message size in bytes or maildrop size in bytes. */
    
    public final int size;
    
    /**
    * Contruct a new MessageMark.
    */
    
    public MessageMark(int number, int size) {
        this.number = number;
        this.size = size;
    }
}