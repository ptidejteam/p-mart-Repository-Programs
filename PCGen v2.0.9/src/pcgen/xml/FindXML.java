package pcgen.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import jformatter.text.FormatException;
import jformatter.text.Parameters;
import jformatter.text.Printf;

import pcgen.util.AnyInputStream;

/**
 * <code>FindXML</code> locates a matching XML file to a given
 * string, performing any necessary file operations to retrieven an
 * <code>InputStream</code>.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 * @see org.apache.xpath.XPathAPI
 * @see <a
 * href="http://sourceforge.net/projects/jformatter">JFormatter</a>
 */
public class FindXML
{
    /**
     * Where to search for XML files?
     */
    public static final String SEARCH_XML_PATHS_PROPERTY
	= "pcgen.xml.paths";

    /**
     * Where to search for XML files.
     */
    private static Collection searchXMLPaths = null;

    /**
     * How to find specific XML resource nodes.
     */
    public static final String RESOURCE_NODE_PATTERN_PROPERTY
	= "pcgen.xml.campaign.nodes.matching";

    /**
     * For caching XPath stuff in @see #selectNodeList.
     */
    private static Map xpathCache = new HashMap ( );

    static {
	Collection rawSearchXMLPaths = pcgen.util.Boot.getPropertyStrings
	    (SEARCH_XML_PATHS_PROPERTY);
	searchXMLPaths = (Collection) new Vector ( );

	Iterator i = rawSearchXMLPaths.iterator ( );

	while (i.hasNext ( )) {
	    String prefix = (String) i.next ( );

	    if (prefix.equals ("."))
		searchXMLPaths.add ("");
	    else
		searchXMLPaths.add (prefix + "/");
	}
    }

    /**
     * Try to find any possible XML file.
     *
     * @param path Basename of XML file
     *
     * @return The stream or <code>null</code> if none
     */
    public static InputStream createInputStream (String path)
    {
	Iterator i = searchXMLPaths.iterator ( );

	while (i.hasNext ( )) {
	    pcgen.util.AnyInputStream.Location l = null;

	    try {
		l = pcgen.util.AnyInputStream.createLocationFromJar
		    ((String) i.next ( ) + path);

		if (pcgen.util.Boot.SHOW_FILES_AS_FOUND)
		    System.err.println ("Found " + l.url);

		return l.inputStream;
	    }

	    catch (IOException ioe) {
		continue;
	    }
	}

	return null;
    }

    /**
     * Try to find any possible XML location.  Look for files, then
     * check for jars.
     *
     * @param path Basename of XML file
     * @param hint Parent of XML file
     *
     * @return The location or <code>null</code> if none
     */
    public static pcgen.util.AnyInputStream.Location createLocation (String path,
							  String hint)
    {
	Iterator i = searchXMLPaths.iterator ( );

	while (i.hasNext ( )) {
	    pcgen.util.AnyInputStream.Location l = null;
	    InputStream is = null;

	    try {
		String file = (String) i.next ( ) + path;

		try {
		    l = pcgen.util.AnyInputStream.createLocation (file);
		}

		catch (IOException ioe) {
		    l = pcgen.util.AnyInputStream.createLocationFromJar (file);
		}

		if (pcgen.util.Boot.SHOW_FILES_AS_FOUND)
		    System.err.println ("Found " + l.url);

		return l;
	    }

	    catch (IOException ioe) {
		continue;
	    }
	}

	return null;
    }

    /**
     * Like @see org.apache.xpath.XPathAPI#selectNodeList and @see
     * org.apache.xpath.XPathAPI#eval, but caches results for better
     * performance.
     *
     * @param node Where to search
     * @param xpath A valid XPath string
     *
     * @return A NodeIterator, should never be <code>null</code>
     *
     * @exception javax.xml.transform.TransformerException Some
     *            oddball internal problem with XPathAPI
     */
    public static NodeIterator selectNodeIterator (Node node,
						   String xpath)
	throws TransformerException
    {
	// Look at implementation in XPathAPI

	PrefixResolverDefault prefixResolver
	    = new PrefixResolverDefault
		((node.getNodeType ( ) == Node.DOCUMENT_NODE)
		 ? ((Document) node).getDocumentElement ( )
		 : node);
//  	PrefixResolver prefixResolver
//  	    = new PrefixResolverDefault (document);

	XPath compiledXPath = null;

	// Instead of caching the result, just cacne the XPath so that
	// you can change the master document but reuse the XPath.
	if (xpathCache.containsKey (xpath))
	    compiledXPath = (XPath) xpathCache.get (xpath);

	else {
	    // Create the XPath object.
	    compiledXPath = new XPath
		(xpath, null, prefixResolver, XPath.SELECT, null);

	    xpathCache.put (xpath, compiledXPath);
	}

	XPathContext xpathSupport = new XPathContext ( );
	XObject list = compiledXPath.execute
	    (xpathSupport, node, prefixResolver);
  	NodeSet ns = list.mutableNodeset ( );
	ns.setShouldCacheNodes (true);

	return ns;
    }

    /**
     * Try to find all possible XPaths.
     *
     * @param xpath The XPath
     *
     * @return The matching nodes.  If there are none, an empty
     *         iterator.
     */
    public static NodeIterator findAllNodes (String xpath)
    {
	NodeSet ns = new NodeSet ( );

	try {
	    NodeIterator ni = selectNodeIterator
		(Boot.masterDocument, xpath);
	    ns.addNodes (ni);
	}

	catch (TransformerException te) {
	    Boot.reportTransformerException (te);
	}

	return (NodeIterator) ns;
    }

    /**
     * Find all FOO nodes.
     *
     * @param resource The nodes to locate (e.g., "race")
     *
     * @return The matching nodes.  If there are none, an empty
     * iterator.
     *
     * @see <a
     * href="http://sourceforge.net/projects/jformatter">JFormatter</a>
     */
    public static NodeIterator findResourceNodes (String resource)
    {
	try {
	    return findAllNodes (Printf.print
				 (Boot.properties.getProperty
				  (RESOURCE_NODE_PATTERN_PROPERTY),
				  new Parameters ( )
				      .addObj (resource)));
	}

	catch (FormatException fe) {
	    fe.printStackTrace ( );

	    return new NodeSet ( );
	}
    }

    /**
     * Find all class skill nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findClassSkillNodes ( )
    {
	return findResourceNodes ("class-skill");
    }

    /**
     * Find all class-spell nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findClassSpellNodes ( )
    {
	return findResourceNodes ("class-spell");
    }

    /**
     * Find all class nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findClassNodes ( )
    {
	return findResourceNodes ("class");
    }

    /**
     * Find all deity nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findDeityNodes ( )
    {
	return findResourceNodes ("deity");
    }

    /**
     * Find all domain nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findDomainNodes ( )
    {
	return findResourceNodes ("domain");
    }

    /**
     * Find all equipment nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findEquipmentNodes ( )
    {
	return findResourceNodes ("equipment");
    }

    /**
     * Find all feat nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findFeatNodes ( )
    {
	return findResourceNodes ("feat");
    }

    /**
     * Find all race nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findRaceNodes ( )
    {
	return findResourceNodes ("race");
    }

    /**
     * Find all skill nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findSkillNodes ( )
    {
	return findResourceNodes ("skill");
    }

    /**
     * Find all spell nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findSpellNodes ( )
    {
	return findResourceNodes ("spell");
    }

    /**
     * Find all weapons nodes.
     *
     * @return The matching nodes.  If there are none (how weird), an
     * empty iterator.
     */
    public static NodeIterator findWeaponsNodes ( )
    {
	return findResourceNodes ("weapons");
    }
}
