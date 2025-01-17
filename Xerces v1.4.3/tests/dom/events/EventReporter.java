/* $Id: EventReporter.java,v 1.1 2006/02/02 01:54:22 vauchers Exp $ */
/*
 * The Apache Software License, Version 1.1
 * 
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache\@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation, and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com .  For more information
 * on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package dom.events;

import org.w3c.dom.*;
import org.w3c.dom.events.*;

class EventReporter implements EventListener
{
    boolean silent=false; // Toggle this to mask reports you don't care about
    int count=0;
    String[] phasename={"?","BUBBLING","CAPTURING","AT_TARGET","?"};
    
    public void on()
    {
        System.out.println("\nEventReporter awakened:\n");
        silent=false;
    }
    public void off()
    {
        System.out.println("\nEventReporter muted\n");
        silent=true;
    }
    
    public void handleEvent(Event evt)
    {
        ++count;
        if(silent)
            return;
            
        System.out.print("EVT "+count+": '"+
            evt.getType()+
            "' listener '"+((Node)evt.getCurrentTarget()).getNodeName()+
            "' target '"+((Node)evt.getTarget()).getNodeName()+
            "' while "+phasename[evt.getEventPhase()] +
            "... ");
        if(evt.getBubbles()) System.out.print("will bubble");
        if(evt.getCancelable()) System.out.print("can cancel");
        System.out.print("\n");
        if(evt instanceof MutationEvent)
        {
            MutationEvent me=(MutationEvent)evt;
            System.out.print("\t");
            if(me.getRelatedNode()!=null)
                System.out.print(" relatedNode='"+me.getRelatedNode()+"'");
            if(me.getAttrName()!=null)
                System.out.print(" attrName='"+me.getAttrName()+"'");
            System.out.print("\n");
            if(me.getPrevValue()!=null)
                System.out.println("\t prevValue='"+me.getPrevValue()+"'");
            if(me.getNewValue()!=null)
                System.out.println("\t newValue='"+me.getNewValue()+"'");
        }
    }
}
