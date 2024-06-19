/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ---------------------------
 * CategoryDatasetHandler.java
 * ---------------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryDatasetHandler.java,v 1.1 2007/10/10 20:00:10 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Jan-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.data.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;

/**
 * A SAX handler for reading a {@link CategoryDataset} from an XML file.
 *
 * @author David Gilbert
 */
public class CategoryDatasetHandler extends RootHandler implements DatasetTags {

    /** The dataset under construction. */
    private DefaultCategoryDataset dataset;

    /**
     * Creates a new handler.
     */
    public CategoryDatasetHandler() {
        this.dataset = null;
    }

    /**
     * Returns the dataset.
     * 
     * @return The dataset.
     */
    public CategoryDataset getDataset() {
        return this.dataset;
    }

    /**
     * Adds an item to the dataset.
     * 
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param value  the value.
     */
    public void addItem(Comparable rowKey, Comparable columnKey, Number value) {
        this.dataset.addValue(value, rowKey, columnKey);
    }

    /**
     * The start of an element.
     * 
     * @param namespaceURI  the namespace.
     * @param localName  the element name.
     * @param qName  the element name.
     * @param atts  the element attributes.
     * 
     * @throws SAXException for errors.
     */
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts) throws SAXException {

        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.startElement(namespaceURI, localName, qName, atts);
        }
        else if (qName.equals(CATEGORYDATASET_TAG)) {
            this.dataset = new DefaultCategoryDataset();
        }
        else if (qName.equals(SERIES_TAG)) {
            CategorySeriesHandler subhandler = new CategorySeriesHandler(this);
            getSubHandlers().push(subhandler);
            subhandler.startElement(namespaceURI, localName, qName, atts);
        }
        else {
            throw new SAXException("Element not recognised: " + qName);
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

        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.endElement(namespaceURI, localName, qName);
        }

    }

}
