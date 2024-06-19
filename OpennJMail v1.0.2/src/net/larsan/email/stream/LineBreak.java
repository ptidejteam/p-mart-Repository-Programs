/*
*  Code: LineBreak.java
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

package net.larsan.email.stream;

/**
* This simple interface holds different line breaks as final static byte arrays. It
* should be noted that only the so called "CRLF" (carriage return / line feed) is 
* allowed in most email protocolls. This is called a canonical line break. However, you 
* will frequently find so called "bare line feeds" in current email applications - meaning
* that there is a singe "CR" or "LF" in a message. We all try to be as patient as we 
* can with this...
*
* @author Lars J. Nilsson
* @version 1.0 29/03/2001
*/

public interface LineBreak {
    
    /** A canonical line break. Ie: a '\r' followed by a '\n'. */ 
    
    public static final byte[] CRLF = { (byte)'\r', (byte)'\n' };
    
    /** A carriage return. Ie: a '\r'. */
    
    public static final byte[] CR = { (byte)'\r' };
    
    /** A line feed. Ie: a '\n'. */
    
    public static final byte[] LF = { (byte)'\n' };
	
}