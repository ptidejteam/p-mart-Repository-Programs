// $Id: XmiReferenceProviderImpl.java,v 1.2 2006/03/02 05:07:41 vauchers Exp $
// Copyright (c) 2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.model.mdr;

import javax.jmi.reflect.RefObject;

import org.argouml.model.Model;
import org.argouml.model.UUIDManager;
import org.netbeans.api.xmi.XMIReferenceProvider;

/**
 * Custom reference provider for MDR XMI Writer.
 * 
 * Returns our internal ID to be used when writing the xmi.id field
 * 
 * @author Tom Morris
 * 
 */
public class XmiReferenceProviderImpl implements XMIReferenceProvider {
    
    private final String systemId = null;

    /**
     * @see org.netbeans.api.xmi.XMIReferenceProvider#getReference(javax.jmi.reflect.RefObject)
     */
    public XMIReferenceProvider.XMIReference getReference(RefObject object) {
        String uuid = Model.getFacade().getUUID(object);

        if (uuid == null) {
            uuid = UUIDManager.getInstance().getNewUUID();
        }
        return new XMIReferenceProvider.XMIReference(systemId, uuid);
    }
}