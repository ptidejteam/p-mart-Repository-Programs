/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ----------------
 * RootHandler.java
 * ----------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: RootHandler.java,v 1.1 2007/10/10 19:22:01 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Jan-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.xml;

import java.util.Stack;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX handler that delegates work to sub-handlers.
 *
 * @author David Gilbert
 */
public class RootHandler extends DefaultHandler implements DatasetTags {

    /** The sub-handlers. */
    private Stack subHandlers;

    /**
     * Creates a new handler.
     */
    public RootHandler() {
        this.subHandlers = new Stack();
    }

    /**
     * Returns the stack of sub handlers.
     *
     * @return The sub-handler stack.
     */
    public Stack getSubHandlers() {
        return this.subHandlers;
    }

    /**
     * Receives some (or all) of the text in the current element.
     *
     * @param ch  character buffer.
     * @param start  the start index.
     * @param length  the length of the valid character data.
     *
     * @throws SAXException for errors.
     */
    public void characters(char[] ch, int start, int length) throws SAXException {

        DefaultHandler handler = getCurrentHandler();
        if (handler != this) {
            handler.characters(ch, start, length);
        }

    }

    /**
     * Returns the handler at the top of the stack.
     *
     * @return the handler.
     */
    public DefaultHandler getCurrentHandler() {

        DefaultHandler result = this;

        if (this.subHandlers != null) {
            if (this.subHandlers.size() > 0) {
                Object top = this.subHandlers.peek();
                if (top != null) {
                    result = (DefaultHandler) top;
                }
            }
        }

        return result;

    }

    /**
     * Pushes a sub-handler onto the stack.
     *
     * @param subhandler  the sub-handler.
     */
    public void pushSubHandler(DefaultHandler subhandler) {
        this.subHandlers.push(subhandler);
    }

    /**
     * Pops a sub-handler from the stack.
     *
     * @return The sub-handler.
     */
    public DefaultHandler popSubHandler() {
        return (DefaultHandler) this.subHandlers.pop();
    }

}
