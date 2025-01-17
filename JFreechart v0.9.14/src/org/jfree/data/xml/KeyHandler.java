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
 * ---------------
 * KeyHandler.java
 * ---------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyHandler.java,v 1.1 2007/10/10 19:19:11 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Jan-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX handler for reading a key.
 *
 * @author David Gilbert
 */
public class KeyHandler extends DefaultHandler implements DatasetTags {

    /** The root handler. */
    private RootHandler rootHandler;

    /** The item handler. */
    private ItemHandler itemHandler;

    /** Storage for the current CDATA */
    private StringBuffer currentText;

    /** The key. */
    //private Comparable key;

    /**
     * Creates a new handler.
     *
     * @param rootHandler  the root handler.
     * @param itemHandler  the item handler.
     */
    public KeyHandler(RootHandler rootHandler, ItemHandler itemHandler) {
        this.rootHandler = rootHandler;
        this.itemHandler = itemHandler;
        this.currentText = new StringBuffer();
        //this.key = null;
    }

    /**
     * The start of an element.
     *
     * @param namespaceURI  the namespace.
     * @param localName  the element name.
     * @param qName  the element name.
     * @param atts  the attributes.
     *
     * @throws SAXException for errors.
     */
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts) throws SAXException {

        if (qName.equals(KEY_TAG)) {
            clearCurrentText();
        }
        else {
            throw new SAXException("Expecting <Key> but found " + qName);
        }

    }

    /**
     * The end of an element.
     *
     * @param namespaceURI  the namespace.
     * @param localName  the element name.
     * @param qName  the element name.
     *
     * @throws SAXException for errors.
     */
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {

        if (qName.equals(KEY_TAG)) {
            this.itemHandler.setKey(getCurrentText());
            this.rootHandler.popSubHandler();
            this.rootHandler.pushSubHandler(new ValueHandler(this.rootHandler, this.itemHandler));
        }
        else {
            throw new SAXException("Expecting </Key> but found " + qName);
        }

    }

    /**
     * Receives some (or all) of the text in the current element.
     *
     * @param ch  character buffer.
     * @param start  the start index.
     * @param length  the length of the valid character data.
     */
    public void characters(char[] ch, int start, int length) {
        if (this.currentText != null) {
            this.currentText.append(String.copyValueOf(ch, start, length));
        }
    }

    /**
     * Returns the current text of the textbuffer.
     *
     * @return the current text.
     */
    protected String getCurrentText() {
        return this.currentText.toString();
    }

    /**
     * Removes all text from the textbuffer at the end of a CDATA section.
     */
    protected void clearCurrentText() {
        this.currentText.delete(0, currentText.length());
    }

}
