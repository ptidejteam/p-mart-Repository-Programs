// $Id: XmiWriterImpl.java,v 1.2 2006/03/02 05:07:48 vauchers Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
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

package org.argouml.model.uml;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.argouml.model.UmlException;
import org.argouml.model.XmiWriter;

import ru.novosoft.uml.model_management.MModel;
import ru.novosoft.uml.xmi.IncompleteXMIException;
import ru.novosoft.uml.xmi.XMIWriter;

/**
 * A wrapper around the genuine XmiReader that provides public access with no
 * knowledge of actual UML implementation.
 * 
 * @author Bob Tarling
 */
public class XmiWriterImpl implements XmiWriter {
    private static final Logger LOG = Logger.getLogger(XmiWriter.class);

    private XMIWriter xmiWriter;

    /**
     * Constructor for XMIReader.
     * 
     * @throws UmlException
     *             when there is a problem
     * @param model
     *            the UML model
     * @param writer
     *            the writer
     */
    public XmiWriterImpl(Object model, Writer writer) throws UmlException {
        try {
            xmiWriter = new XMIWriter((MModel) model, writer);
        } catch (IOException e) {
            throw new UmlException(e);
        }
    }

    /**
     * Write XMI to registered writer
     * 
     * @throws UmlException
     *             if it goes wrong
     */
    public void write() throws UmlException {
        try {
            xmiWriter.gen();
            if (!xmiWriter.getNotContainedElements().isEmpty()) {
                logNotContainedElements();
                throw new IncompleteXMIException();
            }
        } catch (Exception e) {
            LOG.error("Exception thrown by the NSUML XMIWriter", e);
            throw new UmlException(e);
        } finally {
            logNotContainedElements();
        }
    }

    private void logNotContainedElements() {
        if (xmiWriter != null) {
            Iterator it = xmiWriter.getNotContainedElements().iterator();
            while (it.hasNext()) {
                Object missingElement = it.next();
                LOG.error("Not contained in XMI: "
                        + missingElement.getClass().getName() + "["
                        + missingElement + "]");
            }
        }
    }
}
