/*
 * Boot.java
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import pcgen.util.AnyInputStream;

/**
 * Get PCGen into a known state.  When this class is loaded, system
 * XML files are loaded and parsed as well.  If there is a problem,
 * dump a diagnostic and @see System#exit(int) with 1.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 * @see java.util.Properties
 * @see org.w3c.dom.Document
 */
public class Boot
    extends pcgen.util.Boot
{
    /**
     * The XML version property.
     */
    public static final String XML_VERSION_PROPERTY
	= "pcgen.xml.version";
    /**
     * The XML version.
     */
    public static final String XML_VERSION
	= pcgen.util.Boot.properties.getProperty (XML_VERSION_PROPERTY);

    /**
     * The boot XML file property.
     */
    public static final String BOOT_FILE_PROPERTY
	= "pcgen.xml.startup";
    /**
     * The boot XML file.
     */
    public static final String BOOT_FILE
	= pcgen.util.Boot.properties.getProperty (BOOT_FILE_PROPERTY);

    /**
     * How to find resource documents property.
     */
    public static final String RESOURCE_DOCUMENTS_PROPERTY
	= "pcgen.xml.campaign.documents.resource";
    /**
     * How to find resource documents.
     */
    public static final String RESOURCE_DOCUMENTS
	= pcgen.util.Boot.properties.getProperty
	(RESOURCE_DOCUMENTS_PROPERTY);

    /**
     * How to find resource fragments property.
     */
    public static final String RESOURCE_FRAGMENTS_PROPERTY
	= "pcgen.xml.campaign.fragments.resource";
    /**
     * How to find resource fragments.
     */
    public static final String RESOURCE_FRAGMENTS
	= pcgen.util.Boot.properties.getProperty
	(RESOURCE_FRAGMENTS_PROPERTY);

    /**
     * The <code>DocumentBuilder</code> for parsing XML files.
     */
    private static DocumentBuilder documentBuilder = null;

    /**
     * The "master" XML document which represents the intersection of
     * all loaded documents.
     */
    public static Document masterDocument = null;

    /**
     * Hold all the document loading information in one place to make
     * it easy to write new loaders.  This holds the common code for
     * document and fragment handling.
     */
    private abstract static class Loader
    {
	/**
	 * Insert a node in the right place for a particular scope in
	 * an include tag.
	 *
	 * @param n The node to insert
	 * @param include The node for the include tag
	 * @param parent The node for the parent of include (an
	 *               optimization)
	 * @param owner The document containing include (an
	 *              optimization)
	 */
	abstract void insertNode (Node n, Node include,
				  Node parent, Document owner);

	/**
	 * Expand the sequence of include tag nodes in a document.
	 *
	 * @param ni The include tag nodes
	 * @param owner The document
	 *
	 * @exception javax.xml.transform.TransformerException
	 *            Something is seriously wrong with the XML
	 *            parser.
	 */
	void expandNodes (NodeIterator ni, Document owner)
	    throws TransformerException
	{
	    Element include = null;
	    while ((include = (Element) ni.nextNode ( )) != null) {
		Document document = documentFromHref
		    (include.getAttribute ("href"));

		if (document == null)
		    continue;

		NodeList nl = document.getDocumentElement ( )
		    .getChildNodes ( );
		Node parent = include.getParentNode ( );

		for (int i = 0, x = nl.getLength ( ); i < x; ++i) {
		    Node n = nl.item (i);

		    if (n.getNodeType ( ) == Node.ELEMENT_NODE)
			n = load ((Element) n);

		    insertNode (n, include, parent, owner);
		}

		parent.removeChild (include);
	    }
	}
    }

    private static class DocumentLoader
	extends Loader
    {
	public void insertNode (Node n, Node include,
				Node parent, Document owner)
	{
	    parent.insertBefore
		(owner.importNode (n, true), include);
	}
    }

    private static class FragmentLoader
	extends Loader
    {
	public void insertNode (Node n, Node include,
				Node parent, Document owner)
	{
	    parent.insertBefore
		(owner.importNode (n, true), include);
	}
    }

    /**
     * Global document loader helper for #load .
     */
    private static Loader documentLoader = null;
    /**
     * Global fragment loader helper for #load .
     */
    private static Loader fragmentLoader = null;

    static {
	try {
	    documentBuilder = createBuilder ( );
	    masterDocument = documentFromHref (BOOT_FILE);
	    Element root = masterDocument.getDocumentElement ( );

	    if (root == null)
		throw new Exception ("cannot create master document");

	    documentLoader = new DocumentLoader ( );
	    fragmentLoader = new FragmentLoader ( );
	    load (root);
	}

	catch (ParserConfigurationException pce) {
	    pcgen.util.Boot.reportBug ("bad XML parser settings", pce);

	    System.exit (1);
	}

	catch (TransformerException te) {
	    reportTransformerException (te);

	    System.exit (1);
	}

	catch (Exception e) {
	    pcgen.util.Boot.reportBug ("unspecified problem", e);

	    System.exit (1);
	}
    }

    /**
     * Create the default document builder for parsing, etc.
     *
     * @return The default document builder
     *
     * @exception javax.xml.parsers.ParserConfigurationException Not
     *            all settings in this parser work.
     */
    private static DocumentBuilder createBuilder ( )
	throws ParserConfigurationException
    {
	DocumentBuilderFactory factory
	    = DocumentBuilderFactory.newInstance ( );
	factory.setCoalescing (true);
	factory.setExpandEntityReferences (true);
	factory.setIgnoringComments (true);
	factory.setIgnoringElementContentWhitespace (true);
	// XXX
	factory.setNamespaceAware (false);
	factory.setValidating (false);

	return factory.newDocumentBuilder ( );
    }

    /**
     * Get the system read in from XML files.
     *
     * This is broken in that there is no external communication, so
     * you can't check that things failed, etc.  XXX
     *
     * @param element Scan the element and load any XML resource
     *                files recursively
     * @return an expanded element
     *
     * @exception javax.xml.transform.TransformerException Unable to
     *            build an XML transformer
     */
    private static Element load (Element element)
	throws TransformerException
    {
	if (element == null) {
	    pcgen.util.Boot.reportBug ("missing element");

	    return null;
	}

	Document owner = element.getOwnerDocument ( );
	Element root = owner.getDocumentElement ( );

	// This is broken for mixing documents with fragments, but it
	// is unclear what that means since document *replace* their
	// container, but fragments augment them.

	// First handle the complete documents.
    	NodeIterator ni = FindXML.selectNodeIterator
    	    (element, RESOURCE_DOCUMENTS);

	documentLoader.expandNodes (ni, owner);

//  	Element include = null;
//  	while ((include = (Element) ni.nextNode ( )) != null) {
//  	    Document document = documentFromHref
//  		(include.getAttribute ("href"));

//  	    if (document == null)
//  		continue;

//  	    NodeList nl = document.getDocumentElement ( )
//  		.getChildNodes ( );
//  	    Node parent = include.getParentNode ( );

//  	    for (int i = 0, x = nl.getLength ( ); i < x; ++i) {
//  		Node n = nl.item (i);

//  		if (n.getNodeType ( ) == Node.ELEMENT_NODE)
//  		    n = load ((Element) n);

//  		parent.insertBefore
//  		    (owner.importNode (n, true), include);
//  	    }

//  	    parent.removeChild (include);
//  	}

	// Next handle the fragments.
	ni = FindXML.selectNodeIterator (element, RESOURCE_FRAGMENTS);

	fragmentLoader.expandNodes (ni, owner);

//  	include = null;
//  	while ((include = (Element) ni.nextNode ( )) != null) {
//  	    Document document = documentFromHref
//  		(include.getAttribute ("href"));

//  	    if (document == null)
//  		continue;

//  	    NodeList nl = document.getDocumentElement ( )
//  		.getChildNodes ( );
//  	    Node parent = include.getParentNode ( );

//  	    for (int i = 0, x = nl.getLength ( ); i < x; ++i) {
//  		Node n = nl.item (i);

//  		if (n.getNodeType ( ) == Node.ELEMENT_NODE)
//  		    n = load ((Element) n);

//  		parent.insertBefore
//  		    (owner.importNode (n, true), include);
//  	    }

//  	    parent.removeChild (include);
//  	}

	return element;
    }

    /**
     * Create a <code>Document</code> from a location string (href).
     *
     * @param href The location
     *
     * @see pcgen.xml.FindXML#createLocation
     */
    private static Document documentFromHref (String href)
    {
	pcgen.util.AnyInputStream.Location location
	    = FindXML.createLocation (href, null);

	if (location == null) {
	    System.err.println
		("WARNING: could not locate " + href);

	    return null;
	}

	try {
	    Document document = documentBuilder.parse
		(location.inputStream);
	    NodeList nl = document.getDocumentElement ( )
		.getChildNodes ( );
	    String url = location.url.toString ( );

	    for (int i = 0, x = nl.getLength ( ); i < x; ++i) {
		Node n = nl.item (i);

		if (n.getNodeType ( ) == Node.ELEMENT_NODE)
		    ((Element) n).setAttribute ("source", url);
	    }

	    return document;
	}

	catch (SAXException saxe) {
	    reportSAXException (saxe, location.url.toString ( ));

	    return null;
	}

	catch (IOException ioe) {
	    System.err.println
		("I/O error for " + location.url.toString ( ));
	    ioe.printStackTrace ( );

	    return null;
	}
    }

    /**
     * Report <code>TransformerException</code>s.
     *
     * @param te The exception
     */
    public static void reportTransformerException
	(TransformerException te)
    {
	pcgen.util.Boot.reportBug ("whacked out XML parser", te);
    }

    /**
     * Report <code>SAXException</code>s.
     *
     * @param saxe The exception
     * @param where location
     */
    public static void reportSAXException (SAXException saxe,
					   String where)
    {
	if (saxe instanceof SAXParseException) {
	    SAXParseException saxpe = (SAXParseException) saxe;
	    System.err.println
		("Bad XML in " + where
		 + " at line " + saxpe.getLineNumber ( )
		 + ", column " + saxpe.getColumnNumber ( )
		 + ": " + saxpe.getMessage ( ));

	    return;
	}

	// App or configuration error
	Exception x = saxe.getException ( );

	if (x == null)
	    saxe.printStackTrace ( );
	else
	    x.printStackTrace ( );
    }

    /**
     * Save the @see #masterDocument.
     *
     * @param ps Typically <code>System.out</code>
     */
    public static void dumpMasterDocument (PrintStream ps)
    {
	try {
	    Transformer serializer = TransformerFactory
		.newInstance ( ).newTransformer ( );
	    serializer.transform
		(new DOMSource (masterDocument),
		 new StreamResult (ps));
	    // ps.println ( );
	}

	catch (TransformerException te) {
	    pcgen.util.Boot.reportBug ("whacked out XML parser", te);
	}
    }
}
