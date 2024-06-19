/**
*  Code: CoderTable.java
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

package net.larsan.email.encoder;

import java.util.*;

/**
* This class is used by the message parser and other clients as a collection
* of CoderPair objects. The CoderTable is not sychronized.
*
* @author Lars J. Nilsson
* @version 1.1 28/03/2001
*/

public class CoderTable {
    
    // CoderPairs
    
    private ArrayList coders;
    
    
    /** Construct a new CoderTable. */
    
    public CoderTable() {
        coders = new ArrayList();
    }

    
    /** Add a CoderPair to the table. */

    public void addCoderPair(CoderPair cp) {
        coders.add(cp);
    }

    
    /** Remove a CoderPair from the table. */

    public void removeCoderPair(String name) {
        
        for(Iterator i = coders.iterator(); i.hasNext();) {
            
            CoderPair cp = (CoderPair)i.next();
            
            if(name.equalsIgnoreCase(cp.getName())) {
                coders.remove(cp);
                break;
            }
        }
    }

    
    /** Check if a CoderPair exists in the table. */

    public boolean exists(String name) {
        
        boolean answer = false;
        
        for(Iterator i = coders.iterator(); i.hasNext();) {
            
            CoderPair cp = (CoderPair)i.next();
            
            if(name.equalsIgnoreCase(cp.getName())) {
                answer = true;
                break;
            }
        }
        
        return answer;
    }

    
    /**
    * Get the Decoder object specified by the parameter name. This method returns
    * null if the CoderTable does not contain a Decoder mapped to the parameter name.
    */

    public Decoder getDecoder(String name) {
        
        Decoder answer = null;
        
        for(Iterator i = coders.iterator(); i.hasNext();) {
            
            CoderPair cp = (CoderPair)i.next();
            
            if(name.equalsIgnoreCase(cp.getName())) {
                answer = cp.getDecoder();
                break;
            }
        }
        
        return answer;
    }

    /**
    * Get the Encoder object specified by the parameter name. This method returns
    * null if the CoderTable does not contain a Encoder mapped to the parameter name.
    */

    public Encoder getEncoder(String name) {
        
        Encoder answer = null;
        
        for(Iterator i = coders.iterator(); i.hasNext();) {
            
            CoderPair cp = (CoderPair)i.next();
            
            if(name.equalsIgnoreCase(cp.getName())) {
                answer = cp.getEncoder();
                break;
            }
        }
        
        return answer;
    }
}