/*
 * $Id: Resolver.java,v 1.1 2006/04/25 22:57:26 vauchers Exp $
 *
 * The Apache Software License, Version 1.1
 *
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
 * 4. The names "Crimson" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Sun Microsystems, Inc., 
 * http://www.sun.com.  For more information on the Apache Software 
 * Foundation, please see <http://www.apache.org/>.
 */


package org.apache.xerces.tree;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.Hashtable;

import org.xml.sax.*;


/**
 * This entity resolver class provides a number of utilities which can help
 * managment of external parsed entities in XML.  These are commonly used
 * to hold markup declarations that are to be used as part of a Document
 * Type Declaration (DTD), or to hold text marked up with XML.
 *
 * <P> Features include: <UL>
 *
 * <LI> Static factory methods are provided for constructing SAX InputSource
 * objects from Files, URLs, or MIME objects.  This eliminates a class of
 * error-prone coding in applications.
 *
 * <LI> Character encodings for XML documents are correctly supported: <UL>
 *
 *	<LI> The encodings defined in the RFCs for MIME content types
 *	(2046 for general MIME, and 2376 for XML in particular), are
 *	supported, handling <em>charset=...</em> attributes and accepting
 *	content types which are known to be safe for use with XML;
 *
 *	<LI> The character encoding autodetection algorithm identified
 *	in the XML specification is used, and leverages all of
 *	the JDK 1.1 (and later) character encoding support.
 *
 *	<LI> The use of MIME typing may optionally be disabled, forcing the
 *	use of autodetection, to support web servers which don't correctly
 *	report MIME types for XML.  For example, they may report text that
 *	is encoded in EUC-JP as being US-ASCII text, leading to fatal
 *	errors during parsing.
 *
 *	<LI> The InputSource objects returned by this class always
 *	have a <code>java.io.Reader</code> available as the "character
 *	stream" property.
 *
 *	</UL>
 *
 * <LI> Catalog entries can map public identifiers to Java resources or
 * to local URLs.  These are used to reduce network dependencies and loads,
 * and will often be used for external DTD components.  For example, packages
 * shipping DTD files as resources in JAR files can eliminate network traffic
 * when accessing them, and sites may provide local caches of common DTDs.
 * Note that no particular catalog syntax is supported by this class, only
 * the notion of a set of entries.
 *
 * </UL>
 *
 * <P> Subclasses can perform tasks such as supporting new URI schemes for
 * URIs which are not URLs, such as URNs (see RFC 2396) or for accessing
 * MIME entities which are part of a <em>multipart/related</em> group
 * (see RFC 2387).  They may also be used to support particular catalog
 * syntaxes, such as the <a href="http://www.oasis-open.org/html/a401.htm">
 * SGML/Open Catalog (SOCAT)</a> which supports the SGML notion of "Formal
 * Public Identifiers (FPIs).
 *
 * @author David Brownell
 * @author Rajiv Mordani
 * @version $Revision: 1.1 $
 */
public class Resolver implements EntityResolver
{
    private boolean		ignoringMIME;

    // table mapping public IDs to (local) URIs
    private Hashtable		id2uri;

    // tables mapping public IDs to resources and classloaders
    private Hashtable		id2resource;
    private Hashtable		id2loader;

    //
    // table of MIME content types (less attributes!) known
    // to be mostly "OK" to use with XML MIME entities.  the
    // idea is to rule out obvious braindamage ("image/jpg")
    // not the subtle stuff ("text/html") that might actually
    // be (or become) safe.
    //
    private static final String types [] = {
	"application/xml",
	"text/xml",
	"text/plain",
	"text/html",			// commonly mis-inferred
	"application/x-netcdf",		// this is often illegal XML
	"content/unknown"
    };

    /** Constructs a resolver. */
    public			Resolver () { }
    
    /**
     * Returns an input source, using the MIME type information and URL
     * scheme to statically determine the correct character encoding if
     * possible and otherwise autodetecting it.  MIME carefully specifies
     * the character encoding defaults, and how attributes of the content
     * type can change it.  XML further specifies two mandatory encodings
     * (UTF-8 and UTF-16), and includes an XML declaration which can be
     * used to internally label most documents encoded using US-ASCII
     * supersets (such as Shift_JIS, EUC-JP, ISO-2022-*, ISO-8859-*, and
     * more).
     *
     * <P> This method can be used to access XML documents which do not
     * have URIs (such as servlet input streams, or most JavaMail message
     * entities) and to support access methods such as HTTP POST or PUT.
     * (URLs normally return content using the GET method.)
     *
     * <P> <em> The caller should set the system ID in order for relative URIs
     * found in this document to be interpreted correctly.</em> In some cases,
     * a custom resolver will need to be used; for example, documents
     * may be grouped in a single MIME "multipart/related" bundle, and
     * relative URLs would refer to other documents in that bundle.
     *
     * @param contentType The MIME content type for the source for which
     *	an InputSource is desired, such as <em>text/xml;charset=utf-8</em>.
     * @param stream The input byte stream for the input source.
     * @param checkType If true, this verifies that the content type is known
     *	to support XML documents, such as <em>application/xml</em>.
     * @param scheme Unless this is "file", unspecified MIME types
     *	default to US-ASCII.  Files are always autodetected since most
     *	file systems discard character encoding information.
     */
    public static InputSource createInputSource (
	String		contentType,
	InputStream	stream,
	boolean		checkType,
	String		scheme
    ) throws IOException
    {
        InputSource     retval;
	String		charset = null;
	    
	if (contentType != null) {
	    int		index;

	    contentType = contentType.toLowerCase ();
	    index = contentType.indexOf (';');
	    if (index != -1) {
		String	attributes;

		attributes = contentType.substring (index + 1);
		contentType = contentType.substring (0, index);

		// use "charset=..." if it's available
		index = attributes.indexOf ("charset");
		if (index != -1) {
		    attributes = attributes.substring (index + 7);
		    // strip out subsequent attributes
		    if ((index = attributes.indexOf (';')) != -1)
			attributes = attributes.substring (0, index);
		    // find start of value
		    if ((index = attributes.indexOf ('=')) != -1) {
			attributes = attributes.substring (index + 1);
			// strip out rfc822 comments
			if ((index = attributes.indexOf ('(')) != -1)
			    attributes = attributes.substring (0, index);
			// double quotes are optional
			if ((index = attributes.indexOf ('"')) != -1) {
			    attributes = attributes.substring (index + 1);
			    attributes = attributes.substring (0,
				    attributes.indexOf ('"'));
			}
			charset = attributes.trim ();
			// XXX "\;", "\)" etc were mishandled above
		    }
		}
	    }

	    //
	    // Check MIME type.
	    //
	    if (checkType) {
		boolean isOK = false;
		for (int i = 0; i < types.length; i++)
		    if (types [i].equals (contentType)) {
			isOK = true;
			break;
		    }
		if (!isOK)
		    throw new IOException ("Not XML: " + contentType);
	    }

	    //
	    // "text/*" MIME types have hard-wired character set
	    // defaults, as specified in the RFCs.  For XML, we
	    // ignore the system "file.encoding" property since
	    // autodetection is more correct.
	    //
	    if (charset == null) {
		contentType = contentType.trim ();
		if (contentType.startsWith ("text/")) {
		    if (!"file".equalsIgnoreCase (scheme))
			charset = "US-ASCII";
		}
		// "application/*" has no default
	    }
	}

	retval = new InputSource (XmlReader.createReader (stream, charset));
	retval.setByteStream (stream);
	retval.setEncoding (charset);
	return retval;
    }


    /**
     * Creates an input source from a given URI.
     *
     * @param uri the URI (system ID) for the entity
     * @param checkType if true, the MIME content type for the entity
     *	is checked for document type and character set encoding.
     */
    static public InputSource createInputSource (URL uri, boolean checkType)
    throws IOException
    {
	URLConnection	conn = uri.openConnection ();
	if (conn instanceof HttpURLConnection) {
	    int status = ((HttpURLConnection)conn).getResponseCode ();
	    if ((status >= 400 && status <= 417) ||
		(status >=500 && status <=505))
	    {
	   	throw new IOException ("Error in opening uri " + uri + 
					"status code=" + status); 
	    }
	}
	InputSource	retval;
	
	if (checkType) {
	    String	contentType = conn.getContentType ();
	    retval = createInputSource (contentType, conn.getInputStream (),
		    false, uri.getProtocol ());
	} else {
	    retval = new InputSource (
		    XmlReader.createReader (conn.getInputStream ()));
	}
	retval.setSystemId (conn.getURL ().toString ());
	return retval;
    }


    /**
     * Creates an input source from a given file, autodetecting
     * the character encoding.
     *
     * @param uri the URI (system ID) for the entity
     */
    static public InputSource createInputSource (File file)
    throws IOException
    {
	InputSource	retval;
	String		path;
	
	retval = new InputSource (
		    XmlReader.createReader (new FileInputStream (file)));

	// On JDK 1.2 and later, simplify this:
	//	"path = file.toURL ().toString ()".
	path = file.getAbsolutePath ();
	if (File.separatorChar != '/')
	    path = path.replace (File.separatorChar, '/');
	if (!path.startsWith ("/"))
	    path = "/" + path;
	if (!path.endsWith ("/") && file.isDirectory ())
	    path = path + "/";

	retval.setSystemId ("file:" + path);
	return retval;
    }


    /**
     * <b>SAX:</b>
     * Resolve the given entity into an input source.  If the name can't
     * be mapped to a preferred form of the entity, the URI is used.  To
     * resolve the entity, first a local catalog mapping names to URIs is
     * consulted.  If no mapping is found there, a catalog mapping names
     * to java resources is consulted.  Finally, if neither mapping found
     * a copy of the entity, the specified URI is used.
     *
     * <P> When a URI is used, <a href="#createInputSource">
     * createInputSource</a> is used to correctly deduce the character
     * encoding used by this entity.  No MIME type checking is done.
     *
     * @param name Used to find alternate copies of the entity, when
     *	this value is non-null; this is the XML "public ID".
     * @param uri Used when no alternate copy of the entity is found;
     *	this is the XML "system ID", normally a URI.
     */
    public InputSource 	resolveEntity (String name, String uri)
    throws IOException, SAXException
    {    	    
	InputSource     retval;        
	String		mappedURI = name2uri (name);
	InputStream	stream;

	// prefer explicit URI mappings, then bundled resources...
	if (mappedURI == null && (stream = mapResource (name)) != null) {
	    uri = "java:resource:" + (String) id2resource.get (name);
	    retval = new InputSource (XmlReader.createReader (stream));

	// ...and treat all URIs the same (as URLs for now). 
	} else {
	    URL			url;
	    URLConnection	conn;
	    
	    if (mappedURI != null)
		uri = mappedURI;
	    else if (uri == null)
		return null;

	    url = new URL (uri);
	    conn = url.openConnection ();
	    uri = conn.getURL ().toString ();
	    // System.out.println ("++ URI: " + url);
	    if (ignoringMIME)
		retval = new InputSource (
			XmlReader.createReader (conn.getInputStream ()));
	    else {
		String		contentType = conn.getContentType ();
		retval = createInputSource (contentType,
			conn.getInputStream (),
			false, url.getProtocol ());
	    }
	}
	retval.setSystemId (uri);
	retval.setPublicId (name);
	return retval;
    }


    /**
     * Returns true if this resolver is ignoring MIME types in the documents
     * it returns, to work around bugs in how servers have reported the
     * documents' MIME types.
     */
    public boolean isIgnoringMIME ()
	{ return ignoringMIME; }

    /**
     * Tells the resolver whether to ignore MIME types in the documents it
     * retrieves.  Many web servers incorrectly assign text documents a
     * default character encoding, even when that is incorrect.  For example,
     * all HTTP text documents default to use ISO-8859-1 (used for Western
     * European languages), and other MIME sources default text documents
     * to use US-ASCII (a seven bit encoding).  For XML documents which
     * include text encoding declarations (as most should do), these server
     * bugs can be worked around by ignoring the MIME type entirely.
     */
    public void setIgnoringMIME (boolean value)
	{ ignoringMIME = value; }


    // maps the public ID to an alternate URI, if one is registered
    private String name2uri (String publicId)
    {
	if (publicId == null || id2uri == null)
	    return null;
	return (String) id2uri.get (publicId);
    }


    /**
     * Registers the given public ID as corresponding to a particular 
     * URI, typically a local copy.  This URI will be used in preference
     * to ones provided as system IDs in XML entity declarations.  This
     * mechanism would most typically be used for Document Type Definitions
     * (DTDs), where the public IDs are formally managed and versioned.
     *
     * @param publicId The managed public ID being mapped
     * @param uri The URI of the preferred copy of that entity
     */
    public void registerCatalogEntry (
	String		publicId,
	String		uri
    )
    {
	if (id2uri == null)
	    id2uri = new Hashtable (17);
	id2uri.put (publicId, uri);
    }


    // return the resource as a stream
    private InputStream mapResource (String publicId)
    {
	// System.out.println ("++ PUBLIC: " + publicId);
	if (publicId == null || id2resource == null)
	    return null;

	String		resourceName = (String) id2resource.get (publicId);
	ClassLoader	loader = null;

	if (resourceName == null)
	    return null;
	// System.out.println ("++ Resource: " + resourceName);
	
	if (id2loader != null)
	    loader = (ClassLoader) id2loader.get (publicId);
	// System.out.println ("++ Loader: " + loader);
	if (loader == null)
	    return ClassLoader.getSystemResourceAsStream (resourceName);
	return loader.getResourceAsStream (resourceName);
    }

    /**
     * Registers a given public ID as corresponding to a particular Java
     * resource in a given class loader, typically distributed with a
     * software package.  This resource will be preferred over system IDs
     * included in XML documents.  This mechanism should most typically be
     * used for Document Type Definitions (DTDs), where the public IDs are
     * formally managed and versioned.
     *
     * <P> If a mapping to a URI has been provided, that mapping takes
     * precedence over this one.
     *
     * @param publicId The managed public ID being mapped
     * @param resourceName The name of the Java resource
     * @param loader The class loader holding the resource, or null if
     *	it is a system resource.
     */
    public void registerCatalogEntry (
	String		publicId,
	String		resourceName,
	ClassLoader	loader
    )
    {
	if (id2resource == null)
	    id2resource = new Hashtable (17);
	id2resource.put (publicId, resourceName);

	if (loader != null) {
	    if (id2loader == null)
		id2loader = new Hashtable (17);
	    id2loader.put (publicId, loader);
	}
    }
}
