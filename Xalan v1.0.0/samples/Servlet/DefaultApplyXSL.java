/*****************************************************************************************************
 * $Id: DefaultApplyXSL.java,v 1.1 2006/02/27 22:48:21 vauchers Exp $
 *
 * Copyright (c) 1998-1999 Lotus Corporation, Inc. All Rights Reserved.
 *				This software is provided without a warranty of any kind.
 *
 * $State: Exp $
 *****************************************************************************************************/

import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.xerces.parsers.DOMParser;
// import org.apache.xerces.framework.Catalog;
// import com.ibm.xml.internal.XCatalog;

import org.apache.xalan.xslt.XSLTInputSource;

/*****************************************************************************************************
 * DefaultApplyXSL extends the ApplyXSL abstract class.  It's primary function is to provide a
 * baseline servlet to let you apply XSL stylesheets to XML data at the server.  Why do you
 * want to apply XSL stylesheets to XML data at the server?  Because you can maximize the sharability
 * of your data without regard to the XML/XSL processing capabilities of your clients.
 *
 * <p>Attempts will be made to create an XML XSLTInputSource DOM from the following sources:</p>
 * <ol>
 * <li>A relative URL specified in the HTTP request's path information. This capability is intended
 * for use by <b>servlet engines that map</b> some or all XML data to be processed at the server.</li>
 * <li>A URL specified in the HTTP request's <code>URL=</code> parameter.  This capability
 * is intended for <b>clients wishing to selectively process</b> XML data at the server.  For
 * security reasons, this URL will be forced to the local IP host.</li>
 * <li>The HTTP request's XML input stream. This capability is intended for use by chained servlets.</li>
 * </ol>
 *
 * <p>Attempts will be made to create an XSL XSLTInputSource from the following sources:</p>
 * <ol>
 * <li>A URL specified in the HTTP request's <code>xslURL=</code> parameter.  This capability
 * is intended for clients wishing to selectively override the server algorithm for applying XSL
 * stylesheets.  For security reasons, this URL will be forced to the local IP host.</li>
 * <li>XML association.  XML XSLTInputSources may contain references to one or more stylesheets using
 * <a HREF="http://www.w3.org/TR/1999/PR-xml-stylesheet-19990114">this</a> W3C proposed recommendation.
 * If the XML XSLTInputSource does contain such references, a best match will be chosen based on the browser
 * type making the request and the default association.  This capability enables relationships to be
 * defined between client capabilities and stylesheets capable of acting on these capabilities.</li>
 * <li>A <a NAME="globalxsl">configured default stylesheet URL</a></li>
 * </ol>
 *
 * <hr>
 *
 * <p >This servlet <a NAME="prereqs">requires</a> the following software:</p>
 * <ul>
 * <li>A servlet engine which implements a 2.x-level servlet API, such as
 * <a HREF="http://www.software.ibm.com/webservers/appserv/">IBM WebSphere Application Server</a>
 * or <a HREF="http://www.lotus.com/home.nsf/welcome/domino">Lotus Domino R5</a>.</li>
 * <li>Version 2.x of <a HREF="http://www.alphaworks.ibm.com/formula/xml">IBM's XML for Java</a>.
 * LotusXSL will run with either a 1.x- or 2.x-level XML4J, but this servlet requires XML4J 2.x.
 * XML4J is already embedded in the IBM and Lotus software listed above.</li>
 * </ul>
 *
 * <hr>
 *
 * <p>If you are running WebSphere Application Server v2.03 or later, this servlet is already
 * installed and default configured.  Otherwise, <a NAME="install">install</a> this servlet by:</p>
 * <ol>
 * <li>Ensure that your servlet engine meets the <a HREF="#prereqs">system requirements</a>.</li>
 * <li>Install the xml4j.jar and lotusxsl.jar files in your servlet engine's class path.  Unfortunately,
 * the servlet specification does not yet define a standard way to install servlets.  Please refer to
 * your servlet engine's operational instructions; for IBM WebSphere Application Server, the instructions
 * are <a HREF="http://www.software.ibm.com/webservers/appserv/library.html">here</a>.</li>
 * <li><a NAME="#configprops">Copy</a> the lotusxsl/examples/servlet/media.properties file
 * to a servlet-accessible location in your directory tree.  This file provides configuration
 * information to this servlet.</li>
 * <li>Copy the lotusxsl/examples/servlet/*.xsl files to a servlet-accessible location in your
 * directory tree.  These files provide default XSL stylesheets to be used when no others are
 * available.  Default.xsl provides a Microsoft IE5-like default display using JavaScript to expand
 * and contract nodes; default2.xsl provides a similar display without the use of JavaScript.</li>
 * <li>Configure this servlet to your servlet engine.  Once again, the servlet specification defines
 * no standard servlet configuration procedure, so you must consult the operational instructions for
 * your servlet engine.
 * <table ID="config1" BORDER="1" CELLPADDING="2" CELLSPACING="0">
 * <tr>
 *    <td COLSPAN="5" BGCOLOR="#000000"><font COLOR="#ffffff">
 *    <b>Default Apply XSL Servlet Base Configuration</b></font></td>
 * </tr>
 * <tr>
 *    <td><b>Parm Name</b></td>
 *    <td><b>Parm Value</b></td>
 *    <td><b>Required?</b></td>
 * </tr>
 * <tr>
 *    <td>Servlet Name</td>
 *    <td>DefaultApplyXSL</td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>Description</td>
 *    <td>Apply XSL to XML</td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>Servlet Class</td>
 *    <td>com.lotus.xsl.server.DefaultApplyXSL</td>
 *    <td>Yes</td>
 * </tr>
 * </table>
 * <br>
 * <table ID="config2" BORDER="1" CELLPADDING="2" CELLSPACING="0">
 * <tr>
 *    <td COLSPAN="5" BGCOLOR="#000000"><font COLOR="#ffffff">
 *    <b>Default Apply XSL Servlet Initialization Properties</b></font></td>
 * </tr>
 * <tr>
 *    <td><b>Description</b></td>
 *    <td><b>Parm Name</b></td>
 *    <td><b>Parm Value</b></td>
 *    <td><b>Required?</b></td>
 *    <td><b>Default Value</b></td>
 * </tr>
 * <tr>
 *    <td><a HREF="#configprops">Location</a> of user-Agent/media mapping rules file</td>
 *    <td>mediaURL</td>
 *    <td>a full URL, or path relative to the System's server.root /servlets directory</td>
 *    <td>No</td>
 *    <td>None</td>
 * </tr>
 * <tr>
 *    <td><a HREF="#globalxsl">XSL stylesheet URL</a></td>
 *    <td><b>xslURL</b></td>
 *    <td>http://<i>localhost/localpath/yourDefault.xsl</i></td>
 *    <td>No</td>
 *    <td>None</td>
 * </tr>
 * <tr>
 *    <td>Run in debug mode?</td>
 *    <td><b>debug</b></td>
 *    <td>true or false</td>
 *    <td>No</td>
 *    <td>false</td>
 * </tr>
 * <tr>
 *    <td>Generate warning messages from XSL processor</td>
 *    <td><b>noConflictWarnings</b></td>
 *    <td>true or false</td>
 *    <td>No</td>
 *    <td>false</td>
 * </tr>
 * <tr>
 *    <td><a HREF="#xcatalog">Catalog</a> for public identifiers</td>
 *    <td><b>catalog</b></td>
 *    <td>a full URL</td>
 *    <td>No</td>
 *    <td>none</td>
 * </table>
 * </li>
 * <li>If you wish to configure your servlet engine to automatically process some or all XML data
 * at the server, you should follow your servlet engine's operational instructions on how to
 * route these files to the default apply XSL servlet.  Usually, this entails defining a servlet
 * filter(s) or alias(es).</li>
 * <li>Restart your servlet engine.</li>
 * <li>Access an XML XSLTInputSource using one of the techniques outlined in the
 * <a HREF="#examples">examples</a></li>
 * </ol>
 *
 * <hr>
 *
 * <p>The following <a NAME="examples">examples</a> serve to illustrate the use of this servlet:</p>
 * <dl>
 * <dt>http://localhost/servlet/DefaultApplyXSL?URL=/data.xml&xslURL=/style.xsl</dt>
 * <dd>...will apply the style.xsl stylesheet to the data.xml data.  Both files will be
 * served from the Web server's HTTP XSLTInputSource root.</dd>
 * <dt>http://localhost/servlet/DefaultApplyXSL?URL=/data.xml&xslURL=/style.xsl&debug=true</dt>
 * <dd>...will ensure that XML and XSL processor messages will be returned in the event of problems
 * applying style.xsl to data.xml</dd>
 * <dt>http://localhost/servlet/DefaultApplyXSL/data.xml?xslURL=/style.xsl</dt>
 * <dd>...will apply the style.xsl stylesheet to the data.xml data, just like the first example.
 * This is an alternative way of specifying the XML XSLTInputSource by utilizing the HTTP request's path
 * information.
 * <dt>http://localhost/servlet/DefaultApplyXSL/data.xml</dt>
 * <dd>...will examine data.xml for an <a HREF="http://www.w3.org/TR/1999/PR-xml-stylesheet-19990114">associated</a>
 * XSL stylesheet.  If multiple XSLs are associated with the data, the stylesheet whose media
 * attribute <a HREF="#media">maps</a> to your browser type will be chosen.  If no mapping is
 * successful, the primary associated stylesheet will be used.
 * <dt>http://localhost/servlet/data.xml</dt>
 * <dd>...will provide the exact same function as the previous example, but this example assumes
 * that /servlet/data.xml has been mapped to be executed by this servlet.  The servlet engine may be configured
 * to map all or some *.xml files to this servlet through the use of servlet aliases or filters.
 * <dt>http://localhost/servlet/data.xml?catalog=http://www.xml.org/dtds/oag.xml</dt>
 * <dd>...will supplement any servlet-configured <a HREF="#xcatalog">XCatalog</a>
 * with a catalog of supply chain DTDs residing at the XML.ORG DTD repository.
 * </dl>
 *
 * <hr>
 *
 * <p>The following <a NAME="usage">usage notes</a> may prove...well...useful:</p>
 * <ul>
 * <li>All <b>bold</b> servlet initialization parameters listed in the
 * <a HREF="#config2">configuration table</a> may be overridden on any HTTP request.  For example,
 * when the servlet is initialized to not run in debug mode, this setting may be overridden on a
 * request by specifying a <code>debug=true</code> HTTP request parameter.
 * <p>All request-time parameters may be specified once with the exception of the "catalog"
 * paramater, which may be specified multiple times in order to load multiple
 * <a HREF="#xcatalog">XCatalogs</a>.</p></li>
 * <li>A number of HTTP request variables are automatically set as XSL stylesheet
 * top-level parameters.  A complete list of these variables is available
 * <a HREF="com.lotus.xsl.server.ApplyXSL.html#setStylesheetParams">here</a>
 * These parameters are directly accessible to your stylesheet by doing something like:
 * <pre>
 *     &lt;?xml version="1.0"?&gt;
 *     &lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0"&gt;
 *     &lt;xsl:param name="servlet-RemoteAddr" select="'defaultAddr'"/&gt;
 *     &nbsp;
 *     &lt;xsl:template match="documentElement"&gt;
 *       Client's address is &lt;xsl:value-of select="$servlet-RemoteAddr"/&gt;
 *     &lt;/xsl:template&gt;
 *     &nbsp;
 *     &lt;/xsl:stylesheet&gt;
 * </pre></li>
 * <li>For performance reasons, this servlet will not validate XML data or XSL stylesheet documents.</li>
 * <li>For security reasons, this servlet will only process HTTP/GET requests.</li>
 * <li>The <a NAME="media">media.properties</a> file provides mapping rules between a value contained
 * in the HTTP request's user-Agent field and a value to be scanned for in XSL stylesheet(s) associated
 * with the XML data. This mapping enables relationships to be defined between client capabilities and
 * stylesheets capable of acting on these capabilities.  For example, mapping rules of...
 * <pre>
 *     MSIE=explorer
 *     MSPIE=pocketexplorer
 * </pre>
 * ...and XML data that contains XSL stylesheet associations of...
 * <pre>
 *     &lt;?xml-stylesheet                 media="explorer"       href="alldata.xsl"  type="text/xsl"?>
 *     &lt;?xml-stylesheet alternate="yes" media="pocketexplorer" href="somedata.xsl" type="text/xsl"?>
 * </pre>
 * ...and an HTTP request from Microsoft's Pocket Internet Explorer (that contains a user-Agent value
 * of <code>foo MSPIE bar</code>) will apply the XSL stylesheet somedata.xsl.  If you wish to define
 * additional browser types, or define relationships that exploit specific presentation capabilities
 * of browsers, simply define additional mapping rules in media.properties.</li>
 * <li><a NAME="xcatalog">XML Catalogs</a> are Web resources which contain mappings from public
 * identifiers to system identifiers. Public identifiers are often used to abstract embedded file
 * locations.  For example, an XML file that contains...
 * <pre>
 *     &lt;!ENTITY % CarPartNumbers PUBLIC "FordProbePartNumbers" "http://www.ford.com/parts/probe.ent">
 *     %CarPartNumbers;
 * </pre>
 * ...is relying on a catalog to map "FordProbePartNumbers" to a meaningful local file.  Failing to map
 * to a local file will result in remote retrieval from http://www.ford.com/parts/probe.ent
 * <p>The underlying XML processor for this servlet (XML4J) supports multiple catalog formats.  However,
 * this servlet assumes <a HREF="http://www.ccil.org/~cowan/XML/XCatalog.html">XCatalog</a> format.</li>
 * <li>Programmers wishing to dynamically construct XML or XSL data should consider subclassing
 * the getDocument or getStylesheet methods.</li>
 * </ul>
 *
 * @author Spencer Shepard (sshepard@us.ibm.com)
 * @author R. Adam King (rak@us.ibm.com)
 * @author Tom Rowe (trowe@us.ibm.com)
 *
 *****************************************************************************************************/

import org.apache.xalan.xslt.XSLTProcessor;

public class DefaultApplyXSL extends ApplyXSL
{
  /**
   * Mapping of HTTP request's user-Agent values to stylesheet media= values.
   * <p>This mapping is defined by a file pointed to by the operational parameter "mediaURL" which can
   *  either contain a full URL or a path relative to the System's server.root /servlets directory.</p>
   * @see #setMediaProps
   * @see #getMedia
   * @serial
   */
  protected OrderedProps ourMediaProps = null;

  /**
   * Initialize operational parameters from the configuration.
   * @param config Configuration
   * @exception ServletException Never thrown
   */
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    setMediaProps(config.getInitParameter("mediaURL"));
  }

  /**
   * Overrides method in ApplyXSL in order to use DefaultApply Properties.
   * @param config Configuration
   */
  protected void setDefaultParameters(ServletConfig config)
  {
    ourDefaultParameters = new DefaultApplyXSLProperties(config);
  }

  /**
   * Returns a connection which respects the Accept-Language header of the HTTP request.  This
   * is useful when XSL files are internationalized for use with Web servers which respect this
   * header.
   * <p>For example, Apache 1.3.6 may be configured for multiviews.  Under this configuration,
   * requests for http://myhost/index.html would return http://myhost/index.html.fr to French browsers
   * and http://myhost/index.html.en to English browsers.</p>
   * @param url Location to connect to
   * @param request Could contain an Accept-Language header
   * @return An Accept-Language-enabled URL connection
   * @see #getStylesheet
   */
  protected URLConnection toAcceptLanguageConnection(URL url, HttpServletRequest request)
    throws Exception
  {
    URLConnection tempConnection = url.openConnection();
    tempConnection.setRequestProperty("Accept-Language", request.getHeader("Accept-Language"));
    return tempConnection;
  }

  /**
   *	Loads the media properties file specified by the given string.
   * @param mediaURLstring Location of the media properties file.  Can be either a full URL or a path relative
   * to the System's server.root /servlets directory.  If this parameter is null,
   * server.root/servlets/media.properties will be used.
   * @see ApplyXSL#CURRENTDIR
   */
  protected void setMediaProps(String mediaURLstring)
  {
    if (mediaURLstring != null)
    {
      URL url = null;
      try
      {
        url = new URL(mediaURLstring);
      }
      catch (MalformedURLException mue1)
      {
        try
        {
          url = new URL("file", "", CURRENTDIR + mediaURLstring);
        }
        catch (MalformedURLException mue2)
        {
          writeLog("Unable to find the media properties file based on parameter 'mediaURL' = "
                   + mediaURLstring, HttpServletResponse.SC_ACCEPTED, mue2);
          url = null;
        }
      }
      if (url != null)
      {
        try
        {
          ourMediaProps = new OrderedProps(url.openStream());
        }
        catch (IOException ioe1)
        {
          writeLog("Exception occurred while opening media properties file: " + mediaURLstring +
                   ".  Media table may be invalid.", HttpServletResponse.SC_ACCEPTED, ioe1);
        }
      }
    }
    else
    {
      String defaultProp = CURRENTDIR + "media.properties";
      try
      {
        ourMediaProps = new OrderedProps(new FileInputStream(defaultProp));
      }
      catch (IOException ioe2)
      {
        writeLog("Default media properties file " + defaultProp + " not found.",
                 HttpServletResponse.SC_ACCEPTED, ioe2);
      }
    }
  }

  /**
   * Returns a DOM from the specified input stream.
   * @param request Could contain the URL(s) for catalog(s)
   * @param ins Input stream to parse
   * @param listener To record detailed parsing messages for a possible return to requestor
   * @return The DOM
   * @exception IOException Thrown when input stream cannot be opened for parsing
   * @exception SAXException Thrown by parser if stream not XSLTInputSource compliant
   * @exception com.lotus.xsl.XSLProcessorException Thrown when stream cannot be parsed
   * @exception Exception Only the above Exceptions should be thrown
   */
  public XSLTInputSource makeDocument(XSLTProcessor processor,
                                      HttpServletRequest request,
                                      InputStream ins,
                                      ErrorHandler listener)
    throws Exception
  {
    /*
    DOMParser parser = new DOMParser();
    //parser.setNodeExpansion(NonValidatingDOMParser.DEFERRED);
    parser.setNodeExpansion(NonValidatingDOMParser.FULL);
    parser.setExpandEntityReferences(true);
    parser.setErrorHandler(listener);
    String catalogURL[] = ((DefaultApplyXSLProperties) ourDefaultParameters).getCatalog(request);
    if (catalogURL != null)
    {
      XCatalog catalog = new XCatalog(parser.getParserState());
      parser.getEntityHandler().setEntityResolver(catalog);
      int i, len = catalogURL.length;
      for (i = 0; i < len; i++)
      {
        parser.loadCatalog(new InputSource(catalogURL[i]));
      }
    }
    parser.parse(new InputSource(ins));
    return new XSLTInputSource(parser.getDocument());
    */

    XSLTInputSource source = new XSLTInputSource(ins);

    // Not sure what to do about the catalog business here.  If this is needed,
    // we should have the liaison do it, if possible.

    return source;
  }

  /**
   * Returns a media name mapped from the specified request's user-Agent header.
   * This mapping enables relationships to be defined between client capabilities and stylesheets
   * capable of acting on these capabilities.
   * <p>Refer to the media.properties file for details.</p>
   * @param request Contains the user-Agent header
   * @return The media name that corresponds to the user-Agent, or "unknown"
   * @see #getStylesheet
   * @see #HEADER_NAME
   */
  public String getMedia(HttpServletRequest request)
  {
    return ourMediaProps.getValue(request.getHeader(HEADER_NAME));
  }

  /**
   * Returns the XSL stylesheet URL associated with the specified XML document.  If multiple XSL
   * stylesheets are associated with the XML document, preference will be given to the stylesheet
   * which contains an attribute name/value pair that corresponds to the specified attributeName
   * and attributeValue.
   * @param xmlSource XML XSLTInputSource to be searched for associated XSL stylesheets
   * @param attributeName  Attribute name to provide preferential matching
   * @param attributeValue Attribute value to provide preferential matching
   * @return The preferred XSL stylesheet URL, or null if no XSL stylesheet association is found
   * @see #getStylesheet
   */
  public static String getXSLURLfromDoc(XSLTInputSource xmlSource, String attributeName, String attributeValue)
  {
    String tempURL = null, returnURL = null;
    NodeList children = xmlSource.getNode().getChildNodes();
    int nNodes = children.getLength(), i;
    for(i = 0; i < nNodes; i++)
    {
      Node child = children.item(i);
      if(Node.PROCESSING_INSTRUCTION_NODE == child.getNodeType())
      {
        ProcessingInstruction pi = (ProcessingInstruction)child;
        if(pi.getNodeName().equals("xml-stylesheet"))
        {
          PIA pia = new PIA(pi);
          if("text/xsl".equals(pia.getAttribute("type")))
          {
            tempURL = pia.getAttribute("href");
            String attribute = pia.getAttribute(attributeName);
            if ((attribute != null) && (attribute.indexOf(attributeValue) > -1))
              return tempURL;
            if (!"yes".equals(pia.getAttribute("alternate")))
              returnURL = tempURL;
          }
        }
      }
    }
    return returnURL;
  }

  /**
   * Returns an XSL XSLTInputSource.  Attempts will be make to create the Stylesheet from the following
   * sources:
   * <ol>
   * <li>A URL specified in the HTTP request's <code>xslURL=</code> parameter.  This capability
   * is intended for clients wishing to selectively override the server algorithm for applying XSL
   * stylesheets.  For security reasons, this URL will be forced to the local IP host.</li>
   * <li>XML association.  XML documents may contain references to one or more stylesheets using
   * <a HREF="http://www.w3.org/TR/1999/PR-xml-stylesheet-19990114">this</a> W3C proposed recommendation.
   * If the XML document does contain such references, a best match will be chosen based on the browser
   * type making the request and the default association.  This capability enables relationships to be
   * defined between client capabilities and stylesheets capable of acting on these capabilities.</li>
   * <li>A configured default stylesheet URL</li>
   * </ol>
   * @param request May contain or point to the XSL XSLTInputSource
   * @param xmlSource  May point to the XSL XSLTInputSource
   * @param listener To record detailed parsing messages for possible return to requestor
   * @return XSL XSLTInputSource, or null if the request could not be parsed
   * @see #makeDocument
   * @see #getMedia
   * @see #STYLESHEET_ATTRIBUTE
   * @see #getXSLURLfromDoc
   * @see #toAcceptLanguageConnection
   * @exception ApplyXSLException Thrown if exception occurs while handling request
   */
  protected XSLTInputSource getStylesheet(HttpServletRequest request,
                                          XSLTInputSource xmlSource,
                                          ApplyXSLListener listener)
    throws ApplyXSLException
  {
    try
    {
      //stylesheet URL from request
      String xslURL = ((DefaultApplyXSLProperties) ourDefaultParameters).getXSLRequestURL(request);
      if (xslURL != null)
        listener.out.println("Parsing XSL Stylesheet Document from request parameter: "
                             + xslURL);
      else
      {
        // find stylesheet from XML Document, Media tag preference
        if (xmlSource != null)
          xslURL = getXSLURLfromDoc(xmlSource, STYLESHEET_ATTRIBUTE, getMedia(request));
        if (xslURL != null)
          listener.out.println("Parsing XSL Stylesheet Document from XML Document tag: " + xslURL);
        else
          // Configuration Default
          if ((xslURL = ourDefaultParameters.getXSLurl(null)) != null)
            listener.out.println("Parsing XSL Stylesheet Document from configuration: " + xslURL);
      }
      return new XSLTInputSource(toAcceptLanguageConnection(new URL(xslURL),
                                                            request).getURL().toString());
    }
    catch (IOException ioe)
    {
      throw new ApplyXSLException(ioe, HttpServletResponse.SC_NOT_FOUND);
    }
    catch (Exception e)
    {
      throw new ApplyXSLException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns an XML XSLTInputSource DOM.  Attempts will be make to create the DOM from the following
   * sources:
   * <ol>
   * <li>A relative URL specified in the HTTP request's path information. This capability is intended
   * for use by <b>servlet engines that map</b> some or all XML data to be processed at the server.</li>
   * <li>A URL specified in the HTTP request's <code>URL=</code> parameter.  This capability
   * is intended for <b>clients wishing to selectively process</b> XML data at the server.  For
   * security reasons, this URL will be forced to the local IP host.</li>
   * <li>The HTTP request's XML input stream. This capability is intended for use by chained servlets.</li>
   * </ol>
   * @param request May contain or point to the XML XSLTInputSource
   * @param listener To record detailed parsing messages for possible return to requestor
   * @return XML XSLTInputSource DOM, or null if the XSLTInputSource could not be parsed
   * @exception ApplyXSLException Thrown if exception occurs while handling request
   */
  protected XSLTInputSource getDocument(XSLTProcessor processor,
                                        HttpServletRequest request,
                                        ApplyXSLListener listener)
    throws ApplyXSLException
  {
    try
    {
      String xmlURL = null;
      // document from PathInfo
      if ((xmlURL = request.getPathInfo()) != null)
      {
        listener.out.println("Parsing XML Document from PathInfo: " + xmlURL);
        return makeDocument(processor,
                            request, new URL("http", ((DefaultApplyXSLProperties)
                                                       ourDefaultParameters).getLocalHost(),
                                             xmlURL.replace('\\', '/')).openStream(), listener);
      }
      // document from Request parameter
      if ((xmlURL = ourDefaultParameters.getXMLurl(request)) != null)
      {
        listener.out.println("Parsing XML Document from request parameter: " + xmlURL);
        return makeDocument(processor, request, new URL(xmlURL).openStream(), listener);
      }
      // document from chain
      String contentType = request.getContentType();
      if ((contentType != null) && contentType.startsWith("text/xml"))
      {
        listener.out.println("Parsing XML Document from request chain");
        return makeDocument(processor, request, request.getInputStream(), listener);
      }
    }
    catch (IOException ioe)
    {
      throw new ApplyXSLException(ioe, HttpServletResponse.SC_NOT_FOUND);
    }
    catch (Exception e)
    {
      throw new ApplyXSLException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return null;
  }

  /**
   * The attribute name in the <?xml-stylesheet> tag used in stylesheet selection.
   */
  protected static final String STYLESHEET_ATTRIBUTE = "media";

  /**
   *	The HTTP Header used for matching the Stylesheet attribute via the
   * media properties file selected.
   */
  protected static final String HEADER_NAME = "user-Agent";
}

/**
 *  Stores the keys and values from a file (similar to a properties file) and
 *  can return the first value which has a key contained in its string.
 *  File can have comment lines starting with '#" and for each line the entries are
 *  separated by tabs and '=' char.
 */
class OrderedProps
{

  /**
   * Stores the Key and Values as an array of Strings
   */
  private Vector attVec = new Vector(15);

  /**
   * Constructor.
   * @param inputStream Stream containing the properties file.
   * @exception IOException Thrown if unable to read from stream
   */
  OrderedProps(InputStream inputStream)
    throws IOException
  {
    BufferedReader input  = new BufferedReader(new InputStreamReader(inputStream));
    String currentLine, Key = null;
    StringTokenizer currentTokens;
    while ((currentLine = input.readLine()) != null)
    {
      currentTokens = new StringTokenizer(currentLine, "=\t\r\n");
      if (currentTokens.hasMoreTokens()) Key = currentTokens.nextToken().trim();
      if ((Key != null) && !Key.startsWith("#") && currentTokens.hasMoreTokens())
      {
        String temp[] = new String[2];
        temp[0] = Key; temp[1] = currentTokens.nextToken().trim();
        attVec.addElement(temp);
      }
    }
  }

  /**
   * Iterates through the Key list and returns the first value for whose
   * key the given string contains.  Returns "unknown" if no key is contained
   * in the string.
   * @param s String being searched for a key.
   * @return Value for key found in string, otherwise "unknown"
   */
  String getValue(String s)
  {
    int i, j = attVec.size();
    for (i = 0; i < j; i++)
    {
      String temp[] = (String[]) attVec.elementAt(i);
      if (s.indexOf(temp[0]) > -1)
        return temp[1];
    }
    return "unknown";
  }
}



/**
 * Parses a processing instruction's (PI) attributes for easy retrieval.
 */
class PIA
{

  private Hashtable piAttributes = null;

  /**
   * Constructor.
   * @param pi The processing instruction whose attributes are to be parsed
   */
  PIA(ProcessingInstruction pi)
  {
    piAttributes = new Hashtable();
    StringTokenizer tokenizer = new StringTokenizer(pi.getNodeValue(), "=\"");
    while(tokenizer.hasMoreTokens())
    {
      piAttributes.put(tokenizer.nextToken().trim(), tokenizer.nextToken().trim());
    }
  }

  /**
   * Returns value of specified attribute.
   *  @param name Attribute name
   *  @return Attribute value, or null if the attribute name does not exist
   */
  String getAttribute(String name)
  {
    return (String) piAttributes.get(name);
  }

}
