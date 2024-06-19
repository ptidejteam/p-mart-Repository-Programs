/*****************************************************************************************************
 * $Id: ApplyXSL.java,v 1.1 2006/02/27 22:48:21 vauchers Exp $
 *
 * Copyright (c) 1998-1999 Lotus Corporation, Inc. All Rights Reserved.
 *				This software is provided without a warranty of any kind.
 *
 * $State: Exp $
 *****************************************************************************************************/

import java.io.*;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.xalan.xslt.Constants;
import org.apache.xalan.xslt.StylesheetRoot;
import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XString;
import org.apache.xalan.xpath.xml.XMLParserLiaison;

/*****************************************************************************************************
 *
 * ApplyXSL is an abstract class that can be extended to supply the basic
 * functions for transforming XML data using XSL stylesheets.
 *
 * @author Spencer Shepard (sshepard@us.ibm.com)
 * @author R. Adam King (rak@us.ibm.com)
 * @author Tom Rowe (trowe@us.ibm.com)
 *
 *****************************************************************************************************/

public abstract class ApplyXSL extends HttpServlet
{

  /**
   * Operational parameters for this class.
   * <p>Request-time values override init-time values which override class defaults.</p>
   * @see #init
   * @serial
   */
  protected ApplyXSLProperties ourDefaultParameters = null;

  /**
   * String representing the end of line characters for the System.
   */
  public final static String EOL = System.getProperty("line.separator");

  /**
   * String representing the file separator characters for the System.
   */
  public final static String FS = System.getProperty("file.separator");

   /**
   * String representing the current directory for properties files. See init().
   */
  public final static String ROOT = System.getProperty("server.root");
  public static String CURRENTDIR;

  /**
   * Initialize operational parameters from the configuration.
   * @param config Configuration
   * @exception ServletException Never thrown
   */
  public void init(ServletConfig config)
    throws ServletException
  {
    super.init(config);
    // If the server.root property --see above-- is null, use current working directory
    // as default location for media.properties.
    if (ROOT != null)
      CURRENTDIR= ROOT + FS + "servlets" + FS;
    else
      CURRENTDIR = System.getProperty("user.dir")+ FS;
    setDefaultParameters(config);
  }

  /**
   * Sets the default parameters for the servlet from the configuration.
   * @param config Configuration
   */
  protected void setDefaultParameters(ServletConfig config)
  {
    ourDefaultParameters = new ApplyXSLProperties(config);
  }

  /**
   * Implementers of this abstract method must return an XML XSLTInputSource DOM.
   * @param request  May contain or point to the XML XSLTInputSource
   * @param listener To record detailed parsing messages for possible return to requestor
   * @return XML XSLTInputSource DOM, or null if no XML XSLTInputSource can be created, found, or parsed
   * @see #process
   * @exception ApplyXSLException Thrown if exception occurs while handling request
   */
  protected abstract XSLTInputSource getDocument(XSLTProcessor processor,
                                                 HttpServletRequest request,
                                                 ApplyXSLListener listener)
    throws ApplyXSLException;

  /**
   * Implementers of this abstract method must return an XSL XSLTInputSource.
   * @param request May point to the XSLTInputSource
   * @param xmlSource  XML XSLTInputSource to be transformed
   * @param listener To record detailed parsing messages for possible return to requestor
   * @return XSL XSLTInputSource, or null if no stylesheet can be created, found, or parsed
   * @see #process
   * @exception ApplyXSLException Thrown if exception occurs while handling request
   */
  protected abstract XSLTInputSource getStylesheet(HttpServletRequest request,
                                                   XSLTInputSource xmlSource,
                                                   ApplyXSLListener listener)
    throws ApplyXSLException;

  /**
   * Returns the response content type specified by the media-type and encoding attributes of
   * the &lt;xsl:output> element(s) of the stylesheet.
   * @param xslSourceRoot XSL Stylesheet to be examined for &lt;xsl:output> elements.
   * @return The response content type (MIME type and charset) of the stylesheet output
   * @see #process
   */
  public String getContentType(StylesheetRoot xslSourceRoot)
  {
    String encoding = xslSourceRoot.getOutputEncoding(), media = xslSourceRoot.getOutputMediaType();
    if (encoding != null)
      return media + "; charset=" + encoding;
    return media;
  }

  /**
   * Defines and sets select top-level XSL stylesheet variables from the HTTP request, which
   * can be evaluated using &lt;xsl:param-variable&gt;.  The following variables will be
   * automatically set:
   * <dl>
   * <dt><i>ParameterName</i></dt>
   * <dd>Each non-reserved request parameter returned from request.getParameterNames().  If a
   *     parameter contains more than a single value, only the first value is available.</dd>
   * <dt>servlet-RemoteAddr</dt>
   * <dd>Contains String output from request.getRemoteAddr(), which is the IP address
   *     of the client machine.</dd>
   * <dt>servlet-RemoteHost</dt>
   * <dd>Contains String output from request.getRemoteHost(), which is the host name
   *     of the client machine.</dd>
   * <dt>servlet-RemoteUser</dt>
   * <dd>Contains String output from request.getRemoteUser(), which was the user name
   *     accepted by the server to grant access to this servlet.</dd>
   * <dt>servlet-Request</dt>
   * <dd>Contains the request object.</dd>
   * </dl>
   * @param xslprocessor Where to register parameters to be set
   * @param request Provides access to all meaningful parameters to set
   * @see #process
   */
  public void setStylesheetParams(XSLTProcessor xslprocessor, HttpServletRequest request)
  {
    XMLParserLiaison liaison = xslprocessor.getXMLProcessorLiaison();
    try
    {
      xslprocessor.setStylesheetParam("servlet-request",
                                      new XObject(request)); // Update ctor -sc
    }
    catch (Exception e)
    {
      return;
    } // Bail out if we can't do this simple set
    Enumeration paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements())
    {
      String paramName = (String) paramNames.nextElement();
      try
      {
        /*
        @@scott -- it's better to pass these in than not... they won't
        @@do any harm.
        if (paramName.equals("URL")                 ||
        paramName.equals("xslURL")              ||
        paramName.equals("debug")               ||
        paramName.equals("noConflictWarnings")  ||
        paramName.equals("catalog"))
        {
        // Reserved parameter names for this servlet
        }
        else
        */
        {
          String[] paramVals = request.getParameterValues(paramName);
          if (paramVals != null)
          {
            xslprocessor.setStylesheetParam(paramName,
                                            new XString(paramVals[0])); // Update ctor -sc
          }
        }
      }
      catch (Exception e)
      {
      }
    }
    try
    {
      xslprocessor.setStylesheetParam("servlet-RemoteAddr",
                                      new XString(request.getRemoteAddr())); // Update ctor -sc
    }
    catch (Exception e)
    {
    }
    try
    {
      xslprocessor.setStylesheetParam("servlet-RemoteHost",
                                      new XString(request.getRemoteHost())); // Update ctor -sc
    }
    catch (Exception e)
    {
    }
    try
    {
      xslprocessor.setStylesheetParam("servlet-RemoteUser",
                                      new XString(request.getRemoteUser())); // Update ctor -sc
    }
    catch (Exception e)
    {
    }
  }

  // doPost removed for security reasons due to the possibility of sending
  // unsecure XML and XSL XSLTInputSources through the request input stream

  /**
   * HTTP Get method passed on to process.
   * @param request The request
   * @param response The response
   * @see #process
   * @exception ServletException Never thrown
   * @exception IOException Never thrown
   */
  public void doGet (HttpServletRequest request,
                     HttpServletResponse response)
    throws ServletException, IOException
  {
    try
    {
      XSLTProcessor processor = org.apache.xalan.xslt.XSLTProcessorFactory.getProcessor();
      process(processor, request, response);
    }
    catch (Exception e)
    {
    }
  }

  /**
   * Coordinates applying an XSL stylesheet to XML data using operational parameters.
   * <p>If successfully applied, the result tree will be streamed to the response object
   * and the content type set according to the XSL stylesheet's &lt;xsl:output> element(s).</p>
   * <p>If there is a problem in parsing the XML/XSL or if there is a problem in applying
   * the XSL to the XML, an exception will be streamed to the response object.  The detail
   * of the information returned in the response object will depend on whether we're
   * running in debug mode or not.</p>
   * @param request  May contain information relevant to creating XML and XSL XSLTInputSource's
   * @param response Where to write the transformation result
   * @see #getDocument
   * @see #getStylesheet
   * @see #getContentType
   * @see #displayException
   * @see #setStylesheetParams
   * @exception ServletException Never thrown
   * @exception IOException Never thrown
   */
  public void process(XSLTProcessor processor, HttpServletRequest request,
                      HttpServletResponse response)
    throws ServletException, IOException
  {
    boolean debug = ourDefaultParameters.isDebug(request);

    long time = 0;
    if (debug)
      time = System.currentTimeMillis();

    // Listener to be used for all reporting
    ApplyXSLListener listener = new ApplyXSLListener();

    XSLTInputSource xmlSource = null, xslSource = null;
    // creating XML XSLTInputSource
    try
    {
      if ((xmlSource = getDocument(processor, request, listener)) == null)
        throw new ApplyXSLException("getDocument() returned null",
          new NullPointerException(),
          response.SC_NOT_FOUND);
    }
    catch (ApplyXSLException axe)
    {
      axe.appendMessage(EOL + "getDocument() resulted in ApplyXSLException" + EOL
                        + listener.getMessage());
      if (debug) writeLog(axe);
      displayException(response, axe, debug);
      xmlSource = null;
    }
    // creating XSL Stylesheet
    if (xmlSource != null)
    {
      try
      {
        if ((xslSource = getStylesheet(request, xmlSource, listener)) == null)
          throw new ApplyXSLException("getStylesheet() returned null",
            new NullPointerException(),
            response.SC_NOT_FOUND);
      }
      catch (ApplyXSLException axe)
      {
        axe.appendMessage(EOL + "getStylesheet() resulted in ApplyXSLException" + EOL
                          + listener.getMessage());
        if (debug) writeLog(axe);
        displayException(response, axe, debug);
        xslSource = null;
      }
    }
    // perform Transformation
    if ((xmlSource != null) && (xslSource != null))
    {
	  try
	  { // new try ... catch around ctor Update -sc
        XSLTProcessor xslprocessor = org.apache.xalan.xslt.XSLTProcessorFactory.getProcessor();
        {
          try
          {
            String contentType = null;
            if ((contentType = getContentType(xslprocessor.processStylesheet(xslSource))) != null)
              response.setContentType(contentType);
            xslprocessor.setQuietConflictWarnings(ourDefaultParameters.isNoCW(request));
            xslprocessor.setProblemListener(listener);
            if (debug)
            {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              XSLTResultTarget outBuffer = new XSLTResultTarget(baos);
              setStylesheetParams(xslprocessor, request);
              xslprocessor.process(xmlSource, xslSource, outBuffer);
              baos.writeTo(response.getOutputStream());
              writeLog(listener.getMessage(), response.SC_OK);
            }
            else
            {
              setStylesheetParams(xslprocessor, request);
              xslprocessor.process(xmlSource, xslSource,
                                   new XSLTResultTarget(response.getWriter()));
            }
          }
          catch (Exception exc)
          {
            ApplyXSLException axe = new ApplyXSLException("Exception occurred during Transformation:"
                                                          + EOL + listener.getMessage() + EOL
                                                          + exc.getMessage(), exc,
                                                                              response.SC_INTERNAL_SERVER_ERROR);
            if (debug) writeLog(axe);
            displayException(response, axe, debug);
          }
          finally
          {
            xslprocessor.reset();
          } // end of try ... catch ... finally
        } // end of blank block
	  }
      catch (org.xml.sax.SAXException saxExc)
      {
        ApplyXSLException axe = new ApplyXSLException("Exception occurred during ctor/Transformation:"
                                                      + EOL + listener.getMessage() + EOL
                                                      + saxExc.getMessage(), saxExc,
                                                                          response.SC_INTERNAL_SERVER_ERROR);
        if (debug) writeLog(axe);
        displayException(response, axe, debug);
      } // end of new try ... catch around ctor Update -sc
    } // end of if((xmlSource != null) ...
    if (debug)
    {
      time = System.currentTimeMillis() - time;
      writeLog("  No Conflict Warnings = " + ourDefaultParameters.isNoCW(request) +
               "  Transformation time: " + time + " ms", response.SC_OK);
    }
  }

  /**
   * Writes the following information to the servlet log:
   * <ol>
   * <li>HTTP status code</li>
   * <li>Message</li>
   * <li>Stack trace</li>
   * </ol>
   * @param axe Contains valid HTTP status code, message, and stack trace (optional)
   */
  protected void writeLog(ApplyXSLException axe)
  {
    writeLog(axe.getMessage(), axe.getStatusCode(), axe.getException());
  }

  /**
   * Writes the following information to the servlet log:
   * <ol>
   * <li>HTTP status code</li>
   * <li>Message</li>
   * <li>Stack trace</li>
   * </ol>
   * @param msg Message to be logged
   * @param statusCode Valid status code from javax.servlet.http.HttpServletResponse
   * @param t Used to generate stack trace (may be =null to suppress stack trace)
   */
  protected void writeLog(String msg, int statusCode, Throwable t)
  {
    if (t == null)
      writeLog(msg, statusCode);
    else
    {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(bytes, true);
      System.out.println("Exception is " + t.getClass().getName());
      t.printStackTrace(writer);
      log("HTTP Status Code: " + statusCode + " - " + msg + EOL + bytes.toString());
    }
  }

  /**
   * Writes the following information to the servlet log:
   * <ol>
   * <li>HTTP status code</li>
   * <li>Message</li>
   * </ol>
   * @param msg Message to be logged
   * @param statusCode Valid status code from javax.servlet.http.HttpServletResponse
   */
  protected void writeLog(String msg, int statusCode)
  {
    log("HTTP Status Code: " + statusCode + " - " + msg);
  }

  /**
   * Invokes response.sendError setting an HTTP status code and optionally an error message
   * as an HTML page.
   * <p>If running in debug mode, also try to return a stack trace of the exception and
   * and xml/xsl processor messages.</p>
   * @param response Where to stream the exception to
   * @param xse The wrapper which contains the exception and its HTTP status code
   * @param debug Indicates whether to include stack trace, etc.
   */
  protected void displayException(HttpServletResponse response, ApplyXSLException xse, boolean debug)
  {
    String mesg = xse.getMessage();
    if (mesg == null)
      mesg = "";
    else mesg = "<B>" + mesg + "</B>";
    StringTokenizer tokens = new StringTokenizer(mesg, EOL);
    StringBuffer strBuf = new StringBuffer();
    while (tokens.hasMoreTokens())
      strBuf.append(tokens.nextToken() + EOL + "<BR>");
    mesg = strBuf.toString();
    if (debug)
    {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(bytes, true);
      xse.getException().printStackTrace(writer);
      mesg += " <PRE> " + bytes.toString() + " </PRE> ";
    }
    response.setContentType("text/html");
    try
    {
      response.sendError(xse.getStatusCode(), mesg);
    }
    catch (IOException ioe)
    {
      System.err.println("IOException is occurring when sendError is called");
    }
  }
}