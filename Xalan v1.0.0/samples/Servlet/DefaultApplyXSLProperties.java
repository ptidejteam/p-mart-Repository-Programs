/*****************************************************************************************************
 * $Id: DefaultApplyXSLProperties.java,v 1.1 2006/02/27 22:48:21 vauchers Exp $
 * 
 * Copyright (c) 1998-1999 Lotus Corporation, Inc. All Rights Reserved.
 *				This software is provided without a warranty of any kind.
 * 
 * $State: Exp $
 *****************************************************************************************************/

import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

/*****************************************************************************************************
 * 
 * DefaultApplyXSLProperties contains operational parameters for DefaultApplyXSL based 
 * on program defaults and configuration.  
 * <p>This class is also used to return values for request-time parameters.</p>
 *
 * @author Spencer Shepard (sshepard@us.ibm.com)
 * @author R. Adam King (rak@us.ibm.com)
 * @author Tom Rowe (trowe@us.ibm.com)
 *
 *****************************************************************************************************/

public class DefaultApplyXSLProperties extends ApplyXSLProperties {
    
    /**
      * Program default for parameter "catalog".
      * @see #getCatalog
      */
    private final String DEFAULT_catalog;

    /**
      * Host used for local context comparisons.
      * @see #getLocalHost
      * @see #setLocalHost
      */
    protected transient String localHost = null;

    /**
      * Constructor to use program defaults.
      */
    public DefaultApplyXSLProperties()
    {
	super();
	DEFAULT_catalog = null;
	setLocalHost();
    }

    /**
      * Constructor to use to override program defaults.
      * @param config Servlet configuration
      * @see #setLocalHost
      */
    public DefaultApplyXSLProperties(ServletConfig config)
    {
	super(config);
	String cat = config.getInitParameter("catalog");
	if (cat != null) DEFAULT_catalog = cat;
	else DEFAULT_catalog = null;
	setLocalHost();
    }

    /**
      * Sets the name of the local IP host name; this value will be used to constrain untrusted 
      * XML document and XSL stylesheet URLs to this trusted host.
      * @see #getLocalHost
      */
    protected void setLocalHost()
    {
	try { 
	    localHost = InetAddress.getLocalHost().getHostName();
	} catch (Exception uhe) {
	    localHost = null;
	}
    }

    /**
      * Returns the name of trusted IP host.
      * @return Name of trusted host
      * @see #setLocalHost
      */
    public String getLocalHost()
    {
	return localHost;
    }

    /**
      * Returns a URL which is constrained to a trusted IP host.
      * @param xURL URL or file path to be made safe 
      * @return Safe URL
      * @exception MalformedURLException Thrown when xURL is not a valid URL
      * @see #setLocalHost
      * @see #getLocalHost
      */
    public URL toSafeURL(String xURL)
    throws MalformedURLException
    {
	if (xURL == null)
	    return null;

	if (xURL.startsWith("/")) {
	    try {
		return new URL("http", localHost, xURL);
	    } catch (MalformedURLException mue) {
	    throw new MalformedURLException("toSafeURL(): " + xURL + 
					    " did not map to local");
	    }
	}
	URL tempURL = null;
	try { 
	    tempURL = new URL(xURL);
	} catch (MalformedURLException mue) {
	    throw new MalformedURLException("toSafeURL(): " + xURL + 
					    " not a valid URL");
	}
	try { 
	    return new URL(tempURL.getProtocol(), localHost, 
			   tempURL.getPort(), tempURL.getFile());
	} catch (MalformedURLException mue) {
	    throw new MalformedURLException("toSafeURL(): " + xURL + 
					    " could not be converted to local host");
	}
    }

    /**
      *	Returns a string representing the constrained URL for the XML document.
      * If there is no request parameter for the XML document, return the configured default.
      * @param request May contain an XML document URL parameter
      * @return String form of XML URL
      * @exception MalformedURLException Thrown when request URL is not a valid URL or path
      * @see #toSafeURL
      */
    public String getXMLurl(HttpServletRequest request)
    throws MalformedURLException
    {
	URL url = toSafeURL(getRequestParmString(request, "URL"));
	if (url == null)
	    return super.getXMLurl(null);
	return url.toExternalForm();
    }

    /**
      * Returns a string representing the constrained URL for the XSL stylesheet 
      * from the request.
      * @param request May contain an XSL stylesheet URL parameter
      * @return String form of request XSL URL, or null if request contains no xslURL parameter
      * @exception MalformedURLException Thrown when request URL is not a valid URL or path
      * @see #toSafeURL
      */
    public String getXSLRequestURL(HttpServletRequest request)
    throws MalformedURLException
    {
	URL url = toSafeURL(getRequestParmString(request, "xslURL"));
	if (url == null)
	    return null;
	return url.toExternalForm();
    }

    /**
      * Returns a string representing the constrained request URL for the XSL stylesheet.
      * If there is no request parameter for the XSL stylesheet, return the configured default.
      * @param request May contain an XSL stylesheet URL parameter
      * @return String form of XSL URL
      * @exception MalformedURLException Thrown when request URL is not a valid URL or path
      * @see #toSafeURL
      */
    public String getXSLurl(HttpServletRequest request)
    throws MalformedURLException
    {
	String reqURL = getXSLRequestURL(request);
	if (reqURL != null)
	    return reqURL;
	return super.getXSLurl(null);
    }

    /**
      * Returns URLs for all <a href="http://www.ccil.org/~cowan/XML/XCatalog.html">XCatalogs</a> 
      * that are to be used to process the request.  Catalogs are used to resolve XML public identifiers
      * into system identifiers.
      * <p>A single XCatalog can be configured as a default,
      * but multiple XCatalogs can be specified at request time to augment the configured default.
      * @param request May contain one or more XCatalog parameters
      * @return Array of strings for all catalog URLs
      */
    public String[] getCatalog(HttpServletRequest request)
    {
	String temp[] = request.getParameterValues("catalog");
	if (DEFAULT_catalog == null)
	    return temp;
	if (temp == null) {
	    String defaultArray[] = new String [1];
	    defaultArray[0] = DEFAULT_catalog;
	    return defaultArray;
	}
	int i, len = temp.length + 1;
	String newCatalogs[] = new String[len];
	newCatalogs[0] = DEFAULT_catalog;
	for (i=1; i < len; i++) {
	    newCatalogs[i] = temp[i-1];
	}
	return newCatalogs;
    }
}
