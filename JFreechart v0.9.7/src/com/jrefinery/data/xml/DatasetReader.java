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
 * ------------------
 * DatasetReader.java
 * ------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DatasetReader.java,v 1.1 2007/10/10 20:00:10 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Nov-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.data.xml;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.CategoryDataset;

/**
 * A utility class for reading datasets from XML.
 *
 * @author David Gilbert
 */
public class DatasetReader {

    /**
     * Reads a {@link PieDataset} from an XML file.
     *
     * @param file  the file.
     * 
     * @return A dataset.
     *
     * @throws IOException if there is a problem reading the file.
     */
    public static PieDataset readPieDatasetFromXML(File file) throws IOException {

        InputStream in = new FileInputStream(file);
        return readPieDatasetFromXML(in);

    }

    /**
     * Reads a {@link PieDataset} from a stream.
     *
     * @param in  the input stream.
     *
     * @return A dataset.
     * 
     * @throws IOException if there is an I/O error.
     */
    public static PieDataset readPieDatasetFromXML(InputStream in) throws IOException {

        PieDataset result = null;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            PieDatasetHandler handler = new PieDatasetHandler();
            parser.parse(in, handler);
            result = handler.getDataset();
        }
        catch (SAXException e) {
            System.out.println(e.getMessage());
        }
        catch (ParserConfigurationException e2) {
            System.out.println(e2.getMessage());
        }

        return result;

    }

    /**
     * Reads a {@link CategoryDataset} from a file.
     *
     * @param file  the file.
     * 
     * @return A dataset.
     *
     * @throws IOException if there is a problem reading the file.
     */
    public static CategoryDataset readCategoryDatasetFromXML(File file) throws IOException {

        InputStream in = new FileInputStream(file);
        return readCategoryDatasetFromXML(in);

    }

    /**
     * Reads a {@link CategoryDataset} from a stream.
     *
     * @param in  the stream.
     * 
     * @return A dataset.
     *
     * @throws IOException if there is a problem reading the file.
     */
    public static CategoryDataset readCategoryDatasetFromXML(InputStream in) throws IOException {

        CategoryDataset result = null;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            CategoryDatasetHandler handler = new CategoryDatasetHandler();
            parser.parse(in, handler);
            result = handler.getDataset();
        }
        catch (SAXException e) {
            System.out.println(e.getMessage());
        }
        catch (ParserConfigurationException e2) {
            System.out.println(e2.getMessage());
        }
        return result;

    }

}
