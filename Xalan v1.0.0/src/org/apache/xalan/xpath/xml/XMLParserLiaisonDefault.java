/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>. 
 */
package org.apache.xalan.xpath.xml;
 
import org.apache.xalan.xpath.*;
import java.net.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xalan.xpath.xml.XSLMessages;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
//import org.apache.xml.serialize.Serializer;

/**
 * <meta name="usage" content="general"/>
 * This class is the default XMLParserLiaison for the XSL Processor.
 * It can be used alone, for programmatically built DOMs (I expect 
 * this use will be very rare), or it can be derived from, in order 
 * to make parser specific implementations.
 */
public class XMLParserLiaisonDefault extends XPathSupportDefault
  implements XMLParserLiaison
{  
  /**
   * The problem listener for handling problem reports.
   */
  private static ProblemListener m_problemListener = null;  
  
  /**
   * Set the current problem listener.
   */
  public void setProblemListener(ProblemListener listener)
  {
    m_problemListener = listener;   
  }

  /**
   * Set the current problem listener.
   */
  public ProblemListener getProblemListener()
  {
    return m_problemListener;
  }

  /**
   * Construct an instance.
   */
  public XMLParserLiaisonDefault(ProblemListener problemListener)
  {
    m_problemListener = problemListener;
    if(null == m_problemListener)
      m_problemListener = new ProblemListenerDefault();    
  }

  /**
   * Construct an instance.
   */
  public XMLParserLiaisonDefault()
  {
    m_problemListener = new ProblemListenerDefault();   
  }

  /**
   * Reset for new run.
   */
  public void reset()
  {
    m_NSInfos = new Hashtable();
    m_candidateNoAncestorXMLNS =  new Vector();
    m_extensionFunctionNamespaces = new Hashtable();
    m_sourceDocs = new Hashtable();
    m_formatterListener = null;
    m_dataProviders = new Vector(1);
  }

  /**
   * Table of XLocator objects, keyed by node.  TODO: The nasty problem with 
   * this is with multiple reuse of the liaison with multiple documents, 
   * especially after disk serialization.
   */
  protected Vector m_dataProviders = new Vector(1);
  
  /**
   * The amount to indent when indent-result="yes".
   */
  protected int m_indent = -1;
  
  /**
   * By default expand all entity references in the 
   * source and style trees.
   */
  protected boolean m_shouldExpandEntityRefs = true;
  
  /**
   * If set to true, validation will be performed.  Validation is off by default.
   */
  protected boolean m_use_validation = false;

  
  /**
   * These are characters that will be escaped in the output.
   */
  public String m_attrSpecialChars = "<>&\"\'\r\n";
  // m_attrSpecialChars should be public since Liaison implementations 
  // may need additional access to this SCurcuru 02-Mar-00
  
  /**
   * The associated formatter listener.
   */
  protected DocumentHandler m_formatterListener = null;
    
  /**
   * XPath environment support, which we aggregate back to 
   * in order to implement the XPathEnvSupport interface.
   */
  protected XPathEnvSupport m_envSupport = null;

  /**
   * <meta name="usage" content="internal"/>
   * Table of extensions that may be called from the expression language 
   * via the call(name, ...) function.  Objects are keyed on the call 
   * name.
   * @see extensions.html.
   */
  public Hashtable m_extensionFunctionNamespaces = new Hashtable();
  
  /**
  * The table of extension namespaces.
  * @serial
  */
  // public Hashtable m_extensionNamespaces = new Hashtable();

  /**
   * Table of input documents.
   * Document objects are keyed by URL string.
   */
  Hashtable m_sourceDocs = new Hashtable();
    
  /**
   * If true (the default) the liaison will attempt to expand namespaces.
   */
  protected boolean m_processNamespaces = true;
  
  // The language variant that should be used, passed from servlet HTTP header.
  protected String m_acceptLanguage = null;
      
  /**
   * Locale to use for errors and warnings.
   */
  protected Locale m_locale = null;
  
  /**
   * A custom entity resolver to be used with parsing.
   */
  protected EntityResolver m_entityResolver = null;
  
  /**
   * The DTD event handler to be used with parsing.
   */
  protected DTDHandler m_DTDHandler = null;
  
  /**
   * The document handler that will be used.  This will be 
   * nulled out in the parse call.
   */
  protected DocumentHandler m_docHandler = null;
  
  /**
   * The document just parsed.  Short lived only!
   */
  protected Document m_document;

  /**
   * The error event handler that will be used for the parse.
   */
  protected ErrorHandler m_errorHandler = null;
  
  /**
   * Callback that may be executed when a node is found, if the 
   * XPath query can be done in document order.
   * The callback will be set to null after the next LocationPath or 
   * Union is processed.
   */
  private NodeCallback m_callback = null;
 
  /**
   * Object that will be passed to the processLocatedNode method.
   * The object will be set to null after the next LocationPath or 
   * Union is processed.
   */
  private Object m_callbackInfo = null;    

  /**
   * <meta name="usage" content="experimental"/>
   * Set a callback that may be called by XPath as nodes are located.
   * The callback will only be called if the XLocator determines that 
   * the location path can process the nodes in document order.
   * If the callback is called, the nodes will not be put into the 
   * node list, and the LocationPath will return an empty node list.
   * The callback will be set to null after the next LocationPath or 
   * Union is processed.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   */
  public void setCallback(NodeCallback callback, Object callbackInfo)
  {
    m_callback = callback;
    m_callbackInfo = callbackInfo;
  }
  
  /**
   * <meta name="usage" content="experimental"/>
   * Get the callback that may be called by XPath as nodes are located.
   * @return the current callback method.
   */
  public NodeCallback getCallback()
  {
    return m_callback;
  }

  /**
   * Get the object that will be passed to the processLocatedNode method.
   * @return object that will be passed to the processLocatedNode method.
   */
  public Object getCallbackInfo()
  {
    return m_callbackInfo;
  }

  
  //==========================================================
  // SECTION: Parsing Support
  //==========================================================

  /** 
   * Returns the document just parsed. 
   */
  public Document getDocument() 
  {
    return m_document;
  }
  
  
  /**
   * Check node to see if it matches this liaison.
   */
  public void checkNode(Node node)
    throws SAXException
  {
    // Implemented by derived classes, or else there's no checking.
  }
  
  /**
   * Copy attributes from another liaison.
   */
  public void copyFromOtherLiaison(XMLParserLiaisonDefault from)
    throws SAXException
  {
    if(null != from) // defensive
    {
      this.setAcceptLanguage(from.getAcceptLanguage());
      this.setErrorHandler(from.getErrorHandler());
      this.setProblemListener(from.getProblemListener());
      this.setUseValidation(from.getUseValidation());
      this.setEntityResolver(from.m_entityResolver);
      this.setLocale(from.m_locale);
      this.setDTDHandler(from.m_DTDHandler);
      this.m_indent = from.m_indent;
      this.m_shouldExpandEntityRefs = from.m_shouldExpandEntityRefs;
      this.m_use_validation = from.m_use_validation;
      this.m_attrSpecialChars = from.m_attrSpecialChars;
    }
 }
  
  /**
    * Allow an application to request a locale for errors and warnings.
    *
    * <p>SAX parsers are not required to provide localisation for errors
    * and warnings; if they cannot support the requested locale,
    * however, they must throw a SAX exception.  Applications may
    * not request a locale change in the middle of a parse.</p>
    *
    * @param locale A Java Locale object.
    * @exception org.xml.sax.SAXException Throws an exception
    *            (using the previous or default locale) if the 
    *            requested locale is not supported.
    * @see org.xml.sax.SAXException
    * @see org.xml.sax.SAXParseException
    */
  public void setLocale (Locale locale)
    throws SAXException
  {
    m_locale = locale;
  }


  /**
    * Allow an application to register a custom entity resolver.
    *
    * <p>If the application does not register an entity resolver, the
    * SAX parser will resolve system identifiers and open connections
    * to entities itself (this is the default behaviour implemented in
    * HandlerBase).</p>
    *
    * <p>Applications may register a new or different entity resolver
    * in the middle of a parse, and the SAX parser must begin using
    * the new resolver immediately.</p>
    *
    * @param resolver The object for resolving entities.
    * @see EntityResolver
    * @see HandlerBase
    */
  public void setEntityResolver (EntityResolver resolver)
  {
    m_entityResolver = resolver;
  }


  /**
    * Allow an application to register a DTD event handler.
    *
    * <p>If the application does not register a DTD handler, all DTD
    * events reported by the SAX parser will be silently
    * ignored (this is the default behaviour implemented by
    * HandlerBase).</p>
    *
    * <p>Applications may register a new or different
    * handler in the middle of a parse, and the SAX parser must
    * begin using the new handler immediately.</p>
    *
    * @param handler The DTD handler.
    * @see DTDHandler
    * @see HandlerBase
    */
  public void setDTDHandler (DTDHandler handler)
  {
    m_DTDHandler = handler;
  }


  /**
    * Allow an application to register a document event handler.
    *
    * <p>If the application does not register a document handler, all
    * document events reported by the SAX parser will be silently
    * ignored (this is the default behaviour implemented by
    * HandlerBase).</p>
    *
    * <p>Applications may register a new or different handler in the
    * middle of a parse, and the SAX parser must begin using the new
    * handler immediately.</p>
    *
    * @param handler The document handler.
    * @see DocumentHandler
    * @see HandlerBase
    */
  public void setDocumentHandler (DocumentHandler handler)
  {
    m_docHandler = handler;
  }


  /**
    * Allow an application to register an error event handler.
    *
    * <p>If the application does not register an error event handler,
    * all error events reported by the SAX parser will be silently
    * ignored, except for fatalError, which will throw a SAXException
    * (this is the default behaviour implemented by HandlerBase).</p>
    *
    * <p>Applications may register a new or different handler in the
    * middle of a parse, and the SAX parser must begin using the new
    * handler immediately.</p>
    *
    * @param handler The error handler.
    * @see ErrorHandler
    * @see SAXException
    * @see HandlerBase
    */
  public void setErrorHandler (ErrorHandler handler)
  {
    m_errorHandler = handler;
  }


  /**
    * Parse an XML document.
    *
    * <p>The application can use this method to instruct the SAX parser
    * to begin parsing an XML document from any valid input
    * source (a character stream, a byte stream, or a URI).</p>
    *
    * <p>Applications may not invoke this method while a parse is in
    * progress (they should create a new Parser instead for each
    * additional XML document).  Once a parse is complete, an
    * application may reuse the same Parser object, possibly with a
    * different input source.</p>
    * 
    * <p>This method needs to be overridden by a derived class.</p>
    *
    * @param source The input source for the top-level of the
    *        XML document.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @exception java.io.IOException An IO exception from the parser,
    *            possibly from a byte stream or character stream
    *            supplied by the application.
    * @see org.xml.sax.InputSource
    * @see #parse(java.lang.String)
    * @see #setEntityResolver
    * @see #setDTDHandler
    * @see #setDocumentHandler
    * @see #setErrorHandler
    */
  public void parse (InputSource source)
    throws SAXException, IOException
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_PARSE_NOT_SUPPORTED, new Object[]{source.getSystemId()})); //"parse (InputSource source) not supported in XMLParserLiaisonDefault! "+
      //"Can not open "+source.getSystemId());
  }

  /**
    * Parse an XML document from a system identifier (URI).
    *
    * <p>This method is a shortcut for the common case of reading a
    * document from a system identifier.  It is the exact
    * equivalent of the following:</p>
    *
    * <pre>
    * parse(new InputSource(systemId));
    * </pre>
    *
    * <p>If the system identifier is a URL, it must be fully resolved
    * by the application before it is passed to the parser.</p>
    *
    * @param systemId The system identifier (URI).
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @exception java.io.IOException An IO exception from the parser,
    *            possibly from a byte stream or character stream
    *            supplied by the application.
    * @see #parse(org.xml.sax.InputSource)
    */
  public void parse (String systemId)
    throws SAXException, IOException
  {
    InputSource source = new InputSource(systemId);
    parse(source);
    try 
    {
      Reader reader = source.getCharacterStream();
      if (reader != null) 
      {
        reader.close();
      }
      else 
      {
        InputStream is = source.getByteStream();
        if (is != null) 
        {
          is.close();
        }
      }
    }
    catch (IOException e) 
    {
      // ignore
    }
  }
  
  /**
   * Returns true if the liaison supports the SAX DocumentHandler 
   * interface.  The default is that the parser does not support 
   * the SAX interface.
   */
  public boolean supportsSAX()
  {
    return false;
  }
    
  /**
   * Get a factory to create XPaths.
   */
  public XPathFactory getDefaultXPathFactory()
  {
    return SimpleNodeLocator.factory();
  }


  /**
   * Set whether or not the liaison attempts to expand namespaces.  Used 
   * for optimization.  No longer supported.
   * @deprecated
   */
  public void setProcessNamespaces(boolean processNamespaces)
  {
    m_processNamespaces = processNamespaces;
  }
  
  /**
   * Tells if namespaces should be supported.  For optimization purposes.
   * @deprecated
   */
  public boolean getProcessNamespaces()
  {
    return m_processNamespaces;
  }
  
 /**
   * <meta name="usage" content="advanced"/>
   * Tells, through the combination of the default-space attribute 
   * on xsl:stylesheet, xsl:strip-space, xsl:preserve-space, and the
   * xml:space attribute, whether or not extra whitespace should be stripped 
   * from the node.  Literal elements from template elements should 
   * <em>not</em> be tested with this function.
   * @param textNode A text node from the source tree.
   * @return true if the text node should be stripped of extra whitespace.
   */
  public boolean shouldStripSourceNode(Node textNode)
    throws org.xml.sax.SAXException
  {
    return (null == m_envSupport) ? false : m_envSupport.shouldStripSourceNode(textNode);
  }
    
  /**
   * Return a string suitible for telling the user what parser is being used.
   */
  public String getParserDescription()
  {
    return "(No parser - generic DOM)";
  }
  
  /**
   * Get the amount to indent when indent-result="yes".
   */
  public int getIndent()
  {
    return m_indent;
  }
  
  /**
   * Set the amount to indent when indent-result="yes".
   */
  public void setIndent(int i)
  {
    m_indent = i;
  }
  
  /**
   * Set language variant that should be used, passed from servlet HTTP header.
   */
  public void setAcceptLanguage(String acceptLanguage)
  {
    m_acceptLanguage = acceptLanguage;
  }

  /**
   * Get language variant that should be used, passed from servlet HTTP header.
   */
  public String getAcceptLanguage()
  {
    return m_acceptLanguage;
  }
    
  /**
   * get whether or not to expand all entity references in the 
   * source and style trees.
   */
  public boolean getShouldExpandEntityRefs()
  {
    return m_shouldExpandEntityRefs;
  }
  
  /**
   * Set whether or not to expand all entity references in the 
   * source and style trees.
   * Not supported in DTM. Entities are expanded by default.
   */
  public void setShouldExpandEntityRefs(boolean b)
  {
    m_shouldExpandEntityRefs = b;
  }
 
  /**
   * If set to true, validation will be performed.  Validation is off by default.
   */
  public boolean getUseValidation()
  {
    return m_use_validation;
  }
  
  /**
   * If set to true, validation will be performed.  Validation is off by default.
   */
  public void setUseValidation(boolean b)
  {
    m_use_validation = b;
  }
  
  /**
   * Set special characters for attributes that will be escaped.
   * @deprecated
   */
  public void setSpecialCharacters(String str)
  {
    m_attrSpecialChars = str;
  }
  
  /**
   * Get special characters for attributes that will be escaped.
   * @deprecated
   */
  public String getSpecialCharacters()
  {
    return m_attrSpecialChars;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * XPath environment support, which the liaison may aggregate back to 
   * in order to implement the XPathEnvSupport interface.
   */
  public void setEnvSupport(XPathEnvSupport envSupport)
  {
    m_envSupport = envSupport;
  }
  
  /**
   * Take a user string and try and parse XML, and also return 
   * the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide 
   * the error condition is severe enough to halt processing.
   */
  public URL getURLFromString(String urlString, String base)
    throws SAXException 
  {
    String origURLString = urlString;
    String origBase = base;
    
    // System.out.println("getURLFromString - urlString: "+urlString+", base: "+base);
    Object doc;
    URL url = null;
    int fileStartType = 0;
    try
    {
      
      if(null != base)
      {
        if(base.toLowerCase().startsWith("file:/"))
        {
          fileStartType = 1;
        }
        else if(base.toLowerCase().startsWith("file:"))
        {
          fileStartType = 2;
        }
      }
      
      boolean isAbsoluteURL;
      
      // From http://www.ics.uci.edu/pub/ietf/uri/rfc1630.txt
      // A partial form can be distinguished from an absolute form in that the
      // latter must have a colon and that colon must occur before any slash
      // characters. Systems not requiring partial forms should not use any
      // unencoded slashes in their naming schemes.  If they do, absolute URIs
      // will still work, but confusion may result.
      int indexOfColon = urlString.indexOf(':');
      int indexOfSlash = urlString.indexOf('/');
      if((indexOfColon != -1) && (indexOfSlash != -1) && (indexOfColon < indexOfSlash))
      {
        // The url (or filename, for that matter) is absolute.
        isAbsoluteURL = true;
      }
      else
      {
        isAbsoluteURL = false;
      }
      
      if(isAbsoluteURL || (null == base) || (base.length() == 0))
      {
        try 
        {
          url = new URL(urlString);
        }
        catch (MalformedURLException e) {}
      }
      // The Java URL handling doesn't seem to handle relative file names.
      else if(!((urlString.charAt(0) == '.') || (fileStartType > 0)))
      {
        try 
        {
          URL baseUrl = new URL(base);
          url = new URL(baseUrl, urlString);
        }
        catch (MalformedURLException e) 
        {
        }
      }
      
      if(null == url)
      {
        // Then we're going to try and make a file URL below, so strip 
        // off the protocol header.
        if(urlString.toLowerCase().startsWith("file:/"))
        {
          urlString = urlString.substring(6);
        }
        else if(urlString.toLowerCase().startsWith("file:"))
        {
          urlString = urlString.substring(5);
        }
      }
      
      if((null == url) && ((null == base) || (fileStartType > 0)))
      {
        if(1 == fileStartType)
        {
          if(null != base)
            base = base.substring(6);
          fileStartType = 1;
        }
        else if(2 == fileStartType)
        {
          if(null != base)
            base = base.substring(5);
          fileStartType = 2;
        }
        
        File f = new File(urlString);
        
        if(!f.isAbsolute() && (null != base))
        {
          // String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
          // System.out.println("prebuiltUrlString (1): "+base);
          StringTokenizer tokenizer = new StringTokenizer(base, "\\/");
          String fixedBase = null;
          while(tokenizer.hasMoreTokens())
          {
            String token = tokenizer.nextToken();
            if (null == fixedBase) 
            {
              // Thanks to Rick Maddy for the bug fix for UNIX here.
              if (base.charAt(0) == '\\' || base.charAt(0) == '/') 
              {
                fixedBase = File.separator + token;
              }
              else 
              {
                fixedBase = token;
              }
            }
            else 
            {
              fixedBase+= File.separator + token;
            }
          }
          // System.out.println("rebuiltUrlString (1): "+fixedBase);
          f = new File(fixedBase);
          String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
          // System.out.println("dir: "+dir);
          // System.out.println("urlString: "+urlString);
          // f = new File(dir, urlString);
          // System.out.println("f (1): "+f.toString());
          // urlString = f.getAbsolutePath();
          f = new File(urlString); 
          boolean isAbsolute =  f.isAbsolute() 
                                || (urlString.charAt( 0 ) == '\\')
                                || (urlString.charAt( 0 ) == '/');
          if(!isAbsolute)
          {
            // Getting more and more ugly...
            if(dir.charAt( dir.length()-1 ) != File.separator.charAt(0) && 
               urlString.charAt( 0 ) != File.separator.charAt(0))
            {
              urlString = dir + File.separator + urlString;
            }
            else
            {
              urlString = dir + urlString;
            }

            // System.out.println("prebuiltUrlString (2): "+urlString);
            tokenizer = new StringTokenizer(urlString, "\\/");
            String rebuiltUrlString = null;
            while(tokenizer.hasMoreTokens())
            {
              String token = tokenizer.nextToken();
              if (null == rebuiltUrlString) 
              {
                // Thanks to Rick Maddy for the bug fix for UNIX here.
                if (urlString.charAt(0) == '\\' || urlString.charAt(0) == '/') 
                {
                  rebuiltUrlString = File.separator + token;
                }
                else 
                {
                  rebuiltUrlString = token;
                }
              }
              else 
              {
                rebuiltUrlString+= File.separator + token;
              }
            }
            // System.out.println("rebuiltUrlString (2): "+rebuiltUrlString);
            if(null != rebuiltUrlString)
              urlString = rebuiltUrlString;
          }
          // System.out.println("fileStartType: "+fileStartType);
          if(1 == fileStartType)
          {
            if (urlString.charAt(0) == '/') 
            {
              urlString = "file://"+urlString;
            }
            else
            {
              urlString = "file:/"+urlString;
            }
          }
          else if(2 == fileStartType)
          {
            urlString = "file:"+urlString;
          }
          try 
          {
            // System.out.println("Final before try: "+urlString);
            url = new URL(urlString);
          }
          catch (MalformedURLException e) 
          {
            // System.out.println("Error trying to make URL from "+urlString);
          }
        }
      }
      if(null == url)
      {
        // The sun java VM doesn't do this correctly, but I'll 
        // try it here as a second-to-last resort.
        if((null != origBase) && (origBase.length() > 0))
        {
          try 
          {
            URL baseURL = new URL(origBase);
            // System.out.println("Trying to make URL from "+origBase+" and "+origURLString);
            url = new URL(baseURL, origURLString);
            // System.out.println("Success! New URL is: "+url.toString());
          }
          catch (MalformedURLException e) 
          {
            // System.out.println("Error trying to make URL from "+origBase+" and "+origURLString);
          }
        }
        
        if(null == url)
        {
          try 
          {
            String lastPart;
            if(null != origBase)
            {
              File baseFile = new File(origBase);
              if(baseFile.isDirectory())
              {
                lastPart = new File(baseFile, urlString).getAbsolutePath ();
              }
              else
              {
                String parentDir = baseFile.getParent();
                lastPart = new File(parentDir, urlString).getAbsolutePath ();
              }
            }
            else
            {
              lastPart = new File (urlString).getAbsolutePath ();
            }
            // Hack
            // if((lastPart.charAt(0) == '/') && (lastPart.charAt(2) == ':'))
            //   lastPart = lastPart.substring(1, lastPart.length() - 1);
            
            String fullpath;
            if (lastPart.charAt(0) == '\\' || lastPart.charAt(0) == '/') 
            {
              fullpath = "file://" + lastPart;
            }
            else
            {
              fullpath = "file:" + lastPart;
            }
            url = new URL(fullpath);
          }
          catch (MalformedURLException e2)
          {
            throw new SAXException( XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_CREATE_URL, new Object[]{urlString}),e2); //"Cannot create url for: " + urlString, e2 );
          }
        }
      }
    }
    catch(SecurityException se)
    {
      try
      {
        url = new URL("http://xml.apache.org/xslt/"+java.lang.Math.random()); // dummy
      }
      catch (MalformedURLException e2)
      {
        // I give up
      }
    }
    // System.out.println("url: "+url.toString());
    return url;
  }
  
  /**
   * Create an empty DOM Document.  Mainly used for creating an 
   * output document.  Implementation of XMLParserLiaison
   * interface method.
   * Default handling: Not supported - need parser-specific implementation.
   */
  public Document createDocument()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CREATEDOCUMENT_NOT_SUPPORTED, null));//"createDocument() not supported in XMLParserLiaisonDefault!");
    // return null;
  }
  
  //==========================================================
  // SECTION: Namespace resolution
  //==========================================================
  
  /**
   * Given a prefix and a namespace context, return the expanded namespace.
   * Default handling: 
   */
  public String getNamespaceForPrefix(String prefix, Element namespaceContext)
  {
    int type;
    Node parent = namespaceContext;
    String namespace = null;
    if(prefix.equals("xml"))
    {
      namespace = QName.S_XMLNAMESPACEURI;
    }
    else
    {
      while ((null != parent) && (null == namespace)
             && (((type = parent.getNodeType()) == Node.ELEMENT_NODE)
                 || (type == Node.ENTITY_REFERENCE_NODE))) 
      {
        if (type == Node.ELEMENT_NODE) 
        {
          NamedNodeMap nnm = parent.getAttributes();
          for (int i = 0;  i < nnm.getLength();  i ++) 
          {
            Node attr = nnm.item(i);
            String aname = attr.getNodeName();
            boolean isPrefix = aname.startsWith("xmlns:");
            if (isPrefix || aname.equals("xmlns")) 
            {
              int index = aname.indexOf(':');
              String p = isPrefix ? aname.substring(index+1) : "";
              if (p.equals(prefix)) 
              {
                namespace = attr.getNodeValue();
                break;
              }
            }
          }
        }
        parent = getParentOfNode(parent);
      }
    }
    return namespace;
  }
    
  /**
   * An experiment for the moment.
   */
  Hashtable m_NSInfos = new Hashtable();
  
  protected static final NSInfo m_NSInfoUnProcWithXMLNS = new NSInfo(false, true);
  protected static final NSInfo m_NSInfoUnProcWithoutXMLNS = new NSInfo(false, false);
  protected static final NSInfo m_NSInfoUnProcNoAncestorXMLNS = new NSInfo(false, false, NSInfo.ANCESTORNOXMLNS);
  protected static final NSInfo m_NSInfoNullWithXMLNS = new NSInfo(true, true);
  protected static final NSInfo m_NSInfoNullWithoutXMLNS = new NSInfo(true, false);
  protected static final NSInfo m_NSInfoNullNoAncestorXMLNS = new NSInfo(true, false, NSInfo.ANCESTORNOXMLNS);
    
  protected Vector m_candidateNoAncestorXMLNS =  new Vector();
  
  /**
   * Returns the namespace of the given node.
   */
  public String getNamespaceOfNode(Node n)
  {
    String namespaceOfPrefix;
    boolean hasProcessedNS;
    NSInfo nsInfo;
    int ntype = n.getNodeType();
    if(Node.ATTRIBUTE_NODE != ntype)
    {
      Object nsObj = m_NSInfos.get(n); // return value
      nsInfo = (nsObj == null) ? null : (NSInfo)nsObj;
      hasProcessedNS = (nsInfo == null) ? false : nsInfo.m_hasProcessedNS;
    }
    else
    {
      hasProcessedNS = false;
      nsInfo = null;
    }
    if(hasProcessedNS)
    {
      namespaceOfPrefix = nsInfo.m_namespace;
    }
    else
    {
      namespaceOfPrefix = null;
      String nodeName = n.getNodeName();
      int indexOfNSSep = nodeName.indexOf(':');
      String prefix;

      if(Node.ATTRIBUTE_NODE == ntype)
      {
        if(indexOfNSSep > 0)
        {
          prefix = nodeName.substring(0, indexOfNSSep);
        }
        else
        {
          // Attributes don't use the default namespace, so if 
          // there isn't a prefix, we're done.
          return namespaceOfPrefix;
        }
      }
      else
      {
        prefix = (indexOfNSSep >= 0) ? nodeName.substring(0, indexOfNSSep) : "";
      }
      boolean ancestorsHaveXMLNS = false;
      boolean nHasXMLNS = false;
      if(prefix.equals("xml"))
      {
        namespaceOfPrefix = QName.S_XMLNAMESPACEURI;
      }
      else
      {
        int parentType;
        Node parent = n;
        while ((null != parent) && (null == namespaceOfPrefix)) 
        {
          if((null != nsInfo) 
             && (nsInfo.m_ancestorHasXMLNSAttrs == nsInfo.ANCESTORNOXMLNS))
          {
            break;
          }
          parentType = parent.getNodeType();
          if((null == nsInfo) || nsInfo.m_hasXMLNSAttrs)
          {
            boolean elementHasXMLNS = false;
            if (parentType == Node.ELEMENT_NODE) 
            {
              NamedNodeMap nnm = parent.getAttributes();
              for (int i = 0;  i < nnm.getLength();  i ++) 
              {
                Node attr = nnm.item(i);
                String aname = attr.getNodeName();
                if(aname.charAt(0) == 'x')
                {
                  boolean isPrefix = aname.startsWith("xmlns:");
                  if (aname.equals("xmlns") || isPrefix) 
                  {
                    if(n == parent)
                      nHasXMLNS = true;
                    elementHasXMLNS = true;
                    ancestorsHaveXMLNS = true;
                    String p = isPrefix ? aname.substring(6) : "";
                    if (p.equals(prefix)) 
                    {
                      namespaceOfPrefix = attr.getNodeValue();
                      break;
                    }
                  }
                }
              }
            }
            if((Node.ATTRIBUTE_NODE != parentType) && (null == nsInfo) && (n != parent))
            {
              nsInfo = elementHasXMLNS ? m_NSInfoUnProcWithXMLNS : m_NSInfoUnProcWithoutXMLNS;
              m_NSInfos.put(parent, nsInfo);
            }
          }
          if(Node.ATTRIBUTE_NODE == parentType)
          {
            parent = getParentOfNode(parent);
          }
          else
          {
            m_candidateNoAncestorXMLNS.addElement(parent);
            m_candidateNoAncestorXMLNS.addElement(nsInfo);
            parent = parent.getParentNode();
          }
          if(null != parent)
          {
            Object nsObj = m_NSInfos.get(parent); // return value
            nsInfo = (nsObj == null) ? null : (NSInfo)nsObj;
          }
       }
        int nCandidates = m_candidateNoAncestorXMLNS.size();
        if(nCandidates > 0)
        {
          if((false == ancestorsHaveXMLNS) && (null == parent))
          {
            for(int i = 0; i < nCandidates; i+=2)
            {
              Object candidateInfo = m_candidateNoAncestorXMLNS.elementAt(i+1);
              if(candidateInfo == m_NSInfoUnProcWithoutXMLNS)
              {
                m_NSInfos.put(m_candidateNoAncestorXMLNS.elementAt(i), 
                              m_NSInfoUnProcNoAncestorXMLNS );
              }
              else if(candidateInfo == m_NSInfoNullWithoutXMLNS)
              {
                m_NSInfos.put(m_candidateNoAncestorXMLNS.elementAt(i), 
                              m_NSInfoNullNoAncestorXMLNS );
              }
            }
          }
          m_candidateNoAncestorXMLNS.removeAllElements();
        }
      }
      
      if(Node.ATTRIBUTE_NODE != ntype)
      {
        if(null == namespaceOfPrefix)
        {
          if(ancestorsHaveXMLNS)
          {
            if(nHasXMLNS)
              m_NSInfos.put(n, m_NSInfoNullWithXMLNS );
            else
              m_NSInfos.put(n, m_NSInfoNullWithoutXMLNS );
          }
          else
          {
            m_NSInfos.put(n, m_NSInfoNullNoAncestorXMLNS );
          }
        }
        else
        {
          m_NSInfos.put(n,  new NSInfo(namespaceOfPrefix, nHasXMLNS));
        }
      }
    }
    return namespaceOfPrefix;
  }

  /**
   * Returns the local name of the given node.
   */
  public String getLocalNameOfNode(Node n)
  {
    String qname = n.getNodeName();
    int index = qname.indexOf(':');
    return (index < 0) ? qname : qname.substring(index+1);
  }

  /**
   * Returns the element name with the namespace expanded.
   */
  public String getExpandedElementName(Element elem)
  {
    String namespace = getNamespaceOfNode(elem);
    return (null != namespace) ? namespace+":"+ getLocalNameOfNode(elem) 
                                 : getLocalNameOfNode(elem);
  }

  /**
   * Returns the attribute name with the namespace expanded.
   */
  public String getExpandedAttributeName(Attr attr)
  {
    String namespace = getNamespaceOfNode(attr);
    return (null != namespace) ? namespace+":"+ getLocalNameOfNode(attr) 
                                 : getLocalNameOfNode(attr);
  }

  //==========================================================
  // SECTION: DOM Helper Functions
  //==========================================================

  /** 
   * Tell if the node is ignorable whitespace.
   * @deprecated
   */
  public boolean isIgnorableWhitespace(Text node)
  {
    boolean isIgnorable = false; // return value
    // TODO: I can probably do something to figure out if this 
    // space is ignorable from just the information in
    // the DOM tree.
    return isIgnorable;
  }
  
  /**
   * I have to write this silly, and expensive function, 
   * because the DOM WG decided that attributes don't 
   * have parents.  If Xalan is used with a DOM implementation
   * that reuses attribute nodes, this will not work correctly.
   */
  public Node getParentOfNode(Node node)
    throws RuntimeException
  {
    Node parent;
    int nodeType = node.getNodeType();
    if(Node.ATTRIBUTE_NODE == nodeType)
    {
      Document doc = node.getOwnerDocument();
      
      if(null == doc)
      {
        throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CHILD_HAS_NO_OWNER_DOCUMENT, null));//"Attribute child does not have an owner document!");
      }
      
      Element rootElem = doc.getDocumentElement();
      
      if(null == rootElem)
      {
        throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT, null));//"Attribute child does not have an owner document element!");
      }
      
      parent = locateAttrParent(rootElem, node);
    }
    else
    {
      parent = node.getParentNode();
      // if((Node.DOCUMENT_NODE != nodeType) && (null == parent))
      // {
      //   throw new RuntimeException("Child does not have parent!");
      // }
    }
    return parent;
  }
  
  /**
   * Given an ID, return the element.
   */
  public Element getElementByID(String id, Document doc)
  {
    return null;
  }
  
  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed 
   * entity with the specified name in the same document as the context 
   * node (see [3.3 Unparsed Entities]). It returns the empty string if 
   * there is no such entity.
   * (Should this go here or in the XLocator??)
   * Since it states in the DOM draft: "An XML processor may choose to 
   * completely expand entities before the structure model is passed 
   * to the DOM; in this case, there will be no EntityReferences in the DOM tree."
   * So I'm not sure how well this is going to work.
   */
  public String getUnparsedEntityURI(String name, Document doc)
  {
    String url = ""; 
    DocumentType doctype = doc.getDoctype(); 
    if(null != doctype)
    {
      NamedNodeMap entities = doctype.getEntities();
      Entity entity = (Entity)entities.getNamedItem(name);
      String notationName = entity.getNotationName();
      if(null != notationName) // then it's unparsed
      {
        // The draft says: "The XSLT processor may use the public 
        // identifier to generate a URI for the entity instead of the URI 
        // specified in the system identifier. If the XSLT processor does 
        // not use the public identifier to generate the URI, it must use 
        // the system identifier; if the system identifier is a relative 
        // URI, it must be resolved into an absolute URI using the URI of 
        // the resource containing the entity declaration as the base 
        // URI [RFC2396]."
        // So I'm falling a bit short here.
        url = entity.getSystemId();
        if(null == url)
        {
          url = entity.getPublicId();
        }
        else
        {
          // This should be resolved to an absolute URL, but that's hard 
          // to do from here.
        }
      }
    }
    return url;
  }  
  /**
   * Support for getParentOfNode.
   */
  private Node locateAttrParent(Element elem, Node attr)
  {
    Node parent = null;
    NamedNodeMap attrs = elem.getAttributes();
    if(null != attrs)
    {
      int nAttrs = attrs.getLength();
      for(int i = 0; i < nAttrs; i++)
      {
        if(attr == attrs.item(i))
        {
          parent = elem;
          break;
        }
      }
    }
    if(null == parent)
    {
      for(Node node = elem.getFirstChild(); null != node; node = node.getNextSibling())
      {
        if(Node.ELEMENT_NODE == node.getNodeType())
        {
          parent = locateAttrParent((Element)node, attr);
          if(null != parent) break;
        }
      }
    }
    return parent;
  }
  
  /**
   * Get the first unparented node in the ancestor chain.
   */
  public Node getRoot(Node node)
  {
    Node root = null;
    while(node != null)
    {
      root = node;
      node = getParentOfNode(node);
    }
    return root;
  }
  
  /**
   * The factory object used for creating nodes 
   * in the result tree.
   */
  protected Document m_DOMFactory = null;
  
  /**
   * Get the factory object required to create DOM nodes 
   * in the result tree.
   */
  public void setDOMFactory(Document domFactory)
  {
    this.m_DOMFactory = domFactory;
  }

      
  /**
   * Get the factory object required to create DOM nodes 
   * in the result tree.
   */
  public Document getDOMFactory()
  {
    if(null == this.m_DOMFactory)
    {
      this.m_DOMFactory = createDocument();
    }
    
    return this.m_DOMFactory;
  }
  
  /**
   * Get the textual contents of the node. If the node 
   * is an element, apply whitespace stripping rules, 
   * though I'm not sure if this is right (I'll fix 
   * or declare victory when I review the entire 
   * whitespace handling).
   */
  public static String getNodeData(Node node)
  {
    StringBuffer buf = new StringBuffer();
    getNodeData(node, buf);
    return (buf.length() > 0) ? buf.toString() : "";
  }
      
  /**
   * Get the textual contents of the node. If the node 
   * is an element, apply whitespace stripping rules, 
   * though I'm not sure if this is right (I'll fix 
   * or declare victory when I review the entire 
   * whitespace handling).
   */
  public static void getNodeData(Node node, StringBuffer buf)
  {
    String data = null;

    switch(node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE:
      {
        NodeList mnl = node.getChildNodes();
        int n = mnl.getLength();
        for(int i = 0; i < n; i++)
          getNodeData(mnl.item(i), buf);
      }	  
      break;
    case Node.DOCUMENT_NODE:
    case Node.ELEMENT_NODE:
      {
        for(Node child = node.getFirstChild(); null != child; child = child.getNextSibling())
          getNodeData(child, buf);
      }
      break;
    case Node.TEXT_NODE:
    case Node.CDATA_SECTION_NODE:
        buf.append( ((Text)node).getData() );
      break;
    case Node.ATTRIBUTE_NODE:
      buf.append( node.getNodeValue() );
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:      
      // warning(XPATHErrorResources.WG_PARSING_AND_PREPARING);        
      break;
    default:
      // ignore
      break;
    }
  }
    
  /**
   * Print a DOM tree.
   * @deprecated
   */
  public void toMarkup(Document doc, Writer writer, 
                       String method,
                       String version,
                       boolean doIndent, 
                       int indent,
                       String encoding, 
                       String mediaType,
                       String doctypeSystem,
                       String doctypePublic,
                       boolean omitXMLDeclaration,
                       boolean standalone, 
                       String[] cdataSectionElems)
    throws Exception
  {
    OutputFormat formatter = new OutputFormat(doc, encoding, doIndent);
    formatter.setMethod(method);
    formatter.setIndent(indent);
    formatter.setIndenting(doIndent);
    formatter.setVersion(version);
    formatter.setMediaType(mediaType);
    formatter.setDoctype(doctypePublic, doctypeSystem);
    formatter.setOmitXMLDeclaration(omitXMLDeclaration);
    formatter.setStandalone(standalone);
    formatter.setCDataElements(cdataSectionElems);
    
    XMLSerializer serializer = new XMLSerializer(writer, formatter);
    serializer.serialize(doc);
    // serializer.serialize(doc, writer, formatter);
  } 
   
  /**
   * Print a DOM tree.
   * @deprecated
   */
  public void toMarkup(Document doc, OutputStream ostream, 
                       String method,
                       String version,
                       boolean doIndent, 
                       int indent,
                       String encoding, 
                       String mediaType,
                       String doctypeSystem,
                       String doctypePublic,
                       boolean omitXMLDeclaration,
                       boolean standalone, 
                       String[] cdataSectionElems)
    throws Exception
  {
    OutputFormat formatter = new OutputFormat(doc, encoding, doIndent);
    formatter.setMethod(method);
    formatter.setIndent(indent);
    formatter.setIndenting(doIndent);
    formatter.setVersion(version);
    formatter.setMediaType(mediaType);
    formatter.setDoctype(doctypePublic, doctypeSystem);
    formatter.setOmitXMLDeclaration(omitXMLDeclaration);
    formatter.setStandalone(standalone);
    formatter.setCDataElements(cdataSectionElems);
    
    XMLSerializer serializer = new XMLSerializer(ostream, formatter);
    serializer.serialize(doc);
    // serializer.serialize(doc);
    // serializer.serialize(doc, ostream, formatter);
  } 

  //==========================================================
  // SECTION: Diagnostics
  //==========================================================

  /**
   * Function that is called when a problem event occurs.
   * 
   * @param   where             Either and XMLPARSER, XSLPROCESSOR, or QUERYENGINE.
   * @param   classification    Either ERROR or WARNING.
   * @param   styleNode         The style tree node where the problem
   *                            occurred.  May be null.
   * @param   sourceNode        The source tree node where the problem
   *                            occurred.  May be null.
   * @param   msg               A string message explaining the problem.
   * @param   lineNo            The line number where the problem occurred,  
   *                            if it is known. May be zero.
   * @param   charOffset        The character offset where the problem,  
   *                            occurred if it is known. May be zero.
   * 
   * @return  true if the return is an ERROR, in which case
   *          exception will be thrown.  Otherwise the processor will 
   *          continue to process.
   */
  public boolean problem(short where, short classification, 
                         Node styleNode, Node sourceNode,
                         String msg, int lineNo, int charOffset)
  {
    if(this.ERROR != classification)
    {
      m_problemListener.problem(where, classification,
                                (Object)styleNode, sourceNode, msg, null, 
                                lineNo, charOffset);
      return false;
    }
    else
    {
      return true;
    }
  }
  
  /**
   * Warn the user of an problem.
   */
  public static void warning(int msg)
  {
    String fmsg = XSLMessages.createXPATHWarning(msg, null);
    m_problemListener.problem(XPathSupport.XPATHPROCESSOR, 
                                              XPathSupport.WARNING,
                                              null, 
                                              null, fmsg, null, 0, 0);
     
  }
  
  /**
   * Warn the user of an problem.
   */
  public void warn(int msg)
  {
    warn(null, msg, null);
  }

  /**
   * Warn the user of an problem.
   */
  public void warn(Node sourceNode, int msg)
  {
	  warn(sourceNode, msg, null);
  }	

  /**
   * Warn the user of an problem.
   */
  public void warn(Node sourceNode, int msg, Object[] args)
  {
	String fmsg = XSLMessages.createXPATHWarning(msg, args);
    boolean shouldThrow = problem(XPathSupport.XPATHPROCESSOR, 
                                              XPathSupport.WARNING,
                                              null, 
                                              sourceNode, fmsg, 0, 0);
    if(shouldThrow)
    {
      throw new RuntimeException(fmsg);
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an 
   * exception.
   */
  private void assert(boolean b, String msg)
    throws org.xml.sax.SAXException
  {
    if(!b)
      error(null, XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION, new Object[] {msg}); //"Programmer assertion is incorrect! - "+msg);
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  public void error(int msg)
    throws org.xml.sax.SAXException
  {
    error(null, msg, null);
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  public void error(Node sourceNode, int msg)
    throws org.xml.sax.SAXException
  {
    error(sourceNode, msg, null);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   */
  public void error(Node sourceNode, int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
	String fmsg = XSLMessages.createXPATHMessage(msg, args);
    boolean shouldThrow = problem(XPathSupport.XPATHPROCESSOR, 
                                              XPathSupport.ERROR,
                                              null, 
                                              sourceNode, fmsg, 0, 0);
    if(shouldThrow)
    {
      throw new XPathException(fmsg);
    }
  }
  
  //==========================================================
  // SECTION: Execution context state tracking
  //==========================================================
    
  
  /**
   * Given a valid element key, return the corresponding node list.
   */
  public NodeList getNodeSetByKey(Node doc, String name, 
                           String ref, 
                           org.apache.xalan.xpath.xml.PrefixResolver nscontext)
    throws org.xml.sax.SAXException
  {
    if(null == m_envSupport)
    {
      return null;
    }
    else
    {
      return m_envSupport.getNodeSetByKey(doc, name, ref, nscontext);
    }
  }
  
  /**
   * Given a name, locate a variable in the current context, and return
   * the Object.
   */
  public XObject getVariable(QName name)
    throws org.xml.sax.SAXException
  {
    if(null == m_envSupport)
    {
      System.out.println("No env support to get variable named: "+name.m_localpart);
      return null;
    }
    else
    {
      return m_envSupport.getVariable(name);
    }
  }
      
  /**
   * <meta name="usage" content="advanced"/>
   * Register an extension namespace handler. This handler provides
   * functions for testing whether a function is known within the 
   * namespace and also for invoking the functions.
   *
   * @param uri the URI for the extension.
   * @param extNS the extension handler.
   */
  public void addExtensionNamespace (String uri,
         ExtensionFunctionHandler extNS) {
    m_extensionFunctionNamespaces.put (uri, extNS);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Register an element extension namespace handler. This handler provides
   * functions for testing whether a function is known within the 
   * namespace and also for invoking the functions.
   *
   * @param uri the URI for the extension.
   * @param extNS the extension handler.
   */
  public void addExtensionElementNamespace (String uri,
         ExtensionFunctionHandler extNS) {
    m_extensionFunctionNamespaces.put (uri, extNS);
  }

  /**
   * Execute the function-available() function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being tested
   *
   * @return whether the given function is available or not.
   */
  public boolean functionAvailable (String ns, String funcName) 
  {
    boolean isAvailable = false;
    if (null != ns) 
    {
      ExtensionFunctionHandler extNS = 
                                      (ExtensionFunctionHandler) m_extensionFunctionNamespaces.get (ns);
      if (extNS != null) 
      {
        isAvailable = extNS.isFunctionAvailable (funcName);
      }
    }
    // System.err.println (">>> functionAvailable (ns=" + ns + 
    //                    ", func=" + funcName + ") = " + isAvailable);
    return isAvailable;
  }
    
  /**
   * Execute the element-available() function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being tested
   *
   * @return whether the given function is available or not.
   */
  public boolean elementAvailable (String ns, String funcName) 
  {
    boolean isAvailable = false;
    if (null != ns) 
    {
      ExtensionFunctionHandler extNS = 
                                      (ExtensionFunctionHandler) m_extensionFunctionNamespaces.get (ns);
      if (extNS != null) 
      {
        isAvailable = extNS.isElementAvailable (funcName);
      }
    }
    // System.err.println (">>> elementAvailable (ns=" + ns + 
    //                    ", func=" + funcName + ") = " + isAvailable);
    return isAvailable;
  }


  /**
   * Handle an extension function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being called
   * @param argVec   arguments to the function in a vector
   *
   * @return result of executing the function
   */
  public Object extFunction (String ns, String funcName, Vector argVec, 
                             Object methodKey)
    throws org.xml.sax.SAXException
  {
    if(null == m_extensionFunctionNamespaces.get ("http://xml.apache.org/xslt/java"))
    {
      // register the java namespace as being implemented by the 
      // xslt-javaclass engine. Note that there's no real code
      // per se for this extension as the functions carry the 
      // object on which to call etc. and all the logic of breaking
      // that up is in the xslt-javaclass engine.
      String uri = "http://xml.apache.org/xslt/java";
      ExtensionFunctionHandler fh = new ExtensionFunctionHandler (uri, null, "xslt-javaclass", null, null);
      
      addExtensionNamespace (uri, fh);   
    }
    if(null == m_extensionFunctionNamespaces.get ("http://xsl.lotus.com/java"))
    {
      // register the java namespace as being implemented by the 
      // xslt-javaclass engine. Note that there's no real code
      // per se for this extension as the functions carry the 
      // object on which to call etc. and all the logic of breaking
      // that up is in the xslt-javaclass engine.
      String uri = "http://xsl.lotus.com/java";
      ExtensionFunctionHandler fh = new ExtensionFunctionHandler (uri, null, "xslt-javaclass", null, null);
      
      addExtensionNamespace (uri, fh);   
    }

    Object result = null;
    if (null != ns)
    {
      ExtensionFunctionHandler extNS = (ExtensionFunctionHandler)
                                       m_extensionFunctionNamespaces.get (ns);

      // if not found try to auto declare this extension namespace:
      // try treaing the URI of the extension as a fully qualified
      // class name; if it works then go with treating this an extension
      // implemented in "javaclass" for with that class being the srcURL.
      // forget about setting functions in that case - so if u do
      // extension-function-available then u get false, but that's ok.
      if (extNS == null) 
      {
        try 
        {
          // Scott: I don't think this is doing anything for us.
          // String cname = ns.startsWith ("class:") ? ns.substring (6) : ns;
          // Class.forName (cname); // does it load?
          extNS = new ExtensionFunctionHandler (ns, null, "javaclass",
                                                ns, null);
          addExtensionNamespace (ns, extNS);
        }
        catch (Exception e) 
        {
          // oops, it failed .. ok, so this path ain't gonna pan out. shucks.
        }
      }

      if (extNS != null)
      {
        try
        {
          result = extNS.callFunction (funcName, argVec, methodKey, null);
        }
        catch (Exception e)
        {
          // e.printStackTrace();
          // throw new XPathProcessorException ("Extension function '" + ns +
          //  ":" + funcName +
          //  "', threw exception: " + e, e);
          String msg = e.getMessage();
          if(null != msg)
          {
            if(msg.startsWith("Stopping after fatal error:"))
            {
              msg = msg.substring("Stopping after fatal error:".length());
            }
            System.out.println("Call to extension function failed: "+msg);
            result = new XNull();
          }
        }
      }
      else 
      {
        throw new XPathProcessorException ("Extension function '" + ns +
          ":" + funcName + "' is unknown");
      }
    }
    return result;
  }

  public Hashtable getSourceDocsTable()
  {
    return m_sourceDocs;
  }
  
  /**
   * Given a document, find the URL associated with that document.
   * @param owner Document that was previously processed by this liaison.
   */
  public String findURIFromDoc(Document owner)
  {
    String uri = null;
    Enumeration values = m_sourceDocs.elements();
    Enumeration keys = m_sourceDocs.keys();
    while(values.hasMoreElements())
    {
      Document docval = (Document)values.nextElement();
      String key = (String)keys.nextElement();
      if(docval == owner)
      {
        uri = key;
        break;
      }
    }
    return uri;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Associate an XLocator provider to a node.  This makes
   * the association based on the root of the tree that the 
   * node is parented by.
   */
  public void associateXLocatorToNode(Node node, XLocator xlocator)
  {
    Node root = getRoot(node);
    XLocator found = null;
    int n = m_dataProviders.size();
    for(int i = 0; i < n; i++)
    {
      DataProviderAssociation ass = (DataProviderAssociation)m_dataProviders.elementAt(i);
      if(root == ass.m_key)
      {
        found = ass.m_locator;
        break;
      }
    }
    if(null == found)
    {
      m_dataProviders.addElement(new DataProviderAssociation(root, xlocator));
    }
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * getXLocatorHandler.
   */
  public XLocator createXLocatorHandler()
  {
    return new SimpleNodeLocator();
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get an XLocator provider keyed by node.  This get's
   * the association based on the root of the tree that the 
   * node is parented by.
   */
  public XLocator getXLocatorFromNode(Node node)
  {
    Node root = getRoot(node);
    XLocator xlocator = null;
    int n = m_dataProviders.size();
    for(int i = 0; i < n; i++)
    {
      DataProviderAssociation ass = (DataProviderAssociation)m_dataProviders.elementAt(i);
      if(root == ass.m_key)
      {
        xlocator = ass.m_locator;
        break;
      }
    }
    return xlocator;
  }
  
  /**
   * Tells if FoundIndex should be thrown if index is found.
   * This is an optimization for match patterns.
   */
  private boolean m_throwFoundIndex = false;
  
  
  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown 
   * if index is found.
   * This is an optimization for match patterns, and 
   * is used internally by the XPath engine.
   */
  public boolean getThrowFoundIndex()
  {
    return m_throwFoundIndex;
  }

  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown 
   * if index is found.
   * This is an optimization for match patterns, and 
   * is used internally by the XPath engine.
   */
  public void setThrowFoundIndex(boolean b)
  {
    m_throwFoundIndex = b;
  }
  
  /**
   * Get the current error handler, if there is one.
   */
  public org.xml.sax.ErrorHandler getErrorHandler()
  {
    return m_errorHandler;
  }

}

