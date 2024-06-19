/**
*  Code: CoderPair.java
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

package net.larsan.email.encoder;

/**
* This is a simple wrapper class that pairs Encoder and Decoder objects
* together under a specified name. This class can be used in contexts where
* Decoders and Encoders should be grouped togheter in the context, for
* example in an email client applications which needs to boh encode and
* decode content.
*
* @author Lars J. Nilsson
* @version 1.0 08/10/00
*/

public class CoderPair {
    
    // instance data
    private Encoder encoder;
    private Decoder decoder;
    private String name;
    
    /**
    * Contruct a new Encoder / Decoder pair.
    */
    
    public CoderPair(String name, Encoder encoder, Decoder decoder) {
        this.name = name;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    /** Get the Encoder object. */

    public Encoder getEncoder() {
        return encoder;
    }

    /** Get the Decoder object. */

    public Decoder getDecoder() {
        return decoder;
    }

    /** Get the CoderPair name. */

    public String getName() {
        return name;
    }
}