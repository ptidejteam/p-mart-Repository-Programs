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
package org.apache.xalan.xslt.client;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.Constructor;
import org.apache.xalan.xslt.*;
import org.apache.xalan.xpath.xml.*;
import org.apache.xalan.xslt.res.XSLTErrorResources;
// import org.apache.xalan.xpath.xml.xml4j.*;

import org.w3c.dom.*;

/**
 * <meta name="usage" content="general"/>
 * Provides applet host for the XSLT processor. To perform transformations on an HTML client:
 * <ol>
 * <li>Use an &lt;applet&gt; tag to embed this applet in the HTML client.</li>
 * <li>Use the DocumentURL and StyleURL PARAM tags or the {@link #setDocumentURL} and
 * {@link #setStyleURL} methods to specify the XML source document and XSL stylesheet.</li>
 * <li>Call the {@link #transformToHTML} method to perform the transformation and return
 * the result as a String.</li>
 * </ol>
 */
public class XSLTProcessorApplet extends Applet
{

  /**
   * The XML Parser Liaison.
   */
  transient XMLParserLiaison m_liaison = null;
  XSLTProcessor m_processor = null;

  /**
   * @serial
   */
  private String m_styleURL;

  /**
   * @serial
   */
  private String m_documentURL;

  // Parameter names.  To change a name of a parameter, you need only make
  // a single change.  Simply modify the value of the parameter string below.
  //--------------------------------------------------------------------------
  /**
   * @serial
   */
  private final String PARAM_styleURL = "styleURL";

  /**
   * @serial
   */
  private final String PARAM_documentURL = "documentURL";

  /**
   * @serial
   */
  private final String PARAM_parser = "parser";

  /**
   * @serial
   */
  private String whichParser = null;

  // We'll keep the DOM trees around, so tell which trees
  // are cached.
  /**
   * @serial
   */
  private String m_styleURLOfCached = null;

  /**
   * @serial
   */
  private String m_documentURLOfCached = null;

  /**
   * I save this for use on the worker thread, but i don't think I
   * need to do this.
   * @serial
   */
  private URL m_codeBase = null;
  private URL m_documentBase = null;

  private Document m_docTree = null;
  private Document m_styleTree = null;

  /**
   * Thread stuff for the trusted worker thread.
   */
  transient private Thread m_callThread = null;

  /**
   */
  transient private TrustedAgent m_trustedAgent = null;

  /**
   */
  transient private Thread m_trustedWorker = null;

  /**
   * Where the worker thread puts the HTML text.
   */
  transient private String m_htmlText = null;

  /**
   * Stylesheet attribute name and value that the caller can set.
   */
  transient private String m_nameOfIDAttrOfElemToModify = null;

  /**
   */
  transient private String m_elemIdToModify = null;

  /**
   */
  transient private String m_attrNameToSet = null;

  /**
   */
  transient private String m_attrValueToSet = null;

  /**
   * The XSLTProcessorApplet constructor takes no arguments.
   */
  public XSLTProcessorApplet()
  {
  }

  /**
   * Get basic information about the applet
   * @return A String with the applet name and author.
   */
  public String getAppletInfo()
  {
    return "Name: XSLTProcessorApplet\r\n" +
      "Author: Scott Boag";
  }

  /**
   * Get descriptions of the applet parameters.
   * @return A two-dimensional array of Strings with Name, Type, and Description
   * for each parameter.
   */
  public String[][] getParameterInfo()
  {
    String[][] info =
    {
      { PARAM_styleURL, "String", "URL to a XSL style sheet" },
      { PARAM_documentURL, "String", "URL to a XML document" },
      { PARAM_parser, "String", "Which parser to use: XML4J or ANY" },
    };
    return info;
  }

  /**
   * Standard applet initialization.
   */
  public void init()
  {
    // PARAMETER SUPPORT
    //		The following code retrieves the value of each parameter
    // specified with the <PARAM> tag and stores it in a member
    // variable.
    //----------------------------------------------------------------------
    String param;
    param = getParameter(PARAM_parser);
    whichParser = (param != null) ? param : "ANY";

    // styleURL: Parameter description
    //----------------------------------------------------------------------
    param = getParameter(PARAM_styleURL);
    if (param != null)
      setStyleURL(param);
    // documentURL: Parameter description
    //----------------------------------------------------------------------
    param = getParameter(PARAM_documentURL);
    if (param != null)
      setDocumentURL(param);
    m_codeBase = this.getCodeBase();
    m_documentBase = this.getDocumentBase();

    // If you use a ResourceWizard-generated "control creator" class to
    // arrange controls in your applet, you may want to call its
    // CreateControls() method from within this method. Remove the following
    // call to resize() before adding the call to CreateControls();
    // CreateControls() does its own resizing.
    //----------------------------------------------------------------------
    resize(320, 240);

    initLiaison();

  }

  /**
   * Try to init the XML liaison object: currently not implemented.
   */
  protected void initLiaison()
  {
    //String parserName = (whichParser.equals("ANY")) ? Constants.LIAISON_CLASS : whichParser ;
    String parserName =  Constants.LIAISON_CLASS;
    try
    {
      Class parserLiaisonClass = Class.forName(parserName);

      Constructor parserLiaisonCtor = parserLiaisonClass.getConstructor(null);
      m_liaison = (XMLParserLiaison)parserLiaisonCtor.newInstance(null);
    }
    catch(Exception e)
    {
      e.printStackTrace();
        //System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_COULD_NOT_CREATE_XML_PROC_LIAISON, new Object[] {parserLiaisonClassName})); //"Could not create XML Processor Liaison: "+parserLiaisonClassName);
        return;
    }
    try
    {
      m_processor = XSLTProcessorFactory.getProcessor();
      m_processor.setDiagnosticsOutput(System.out);

    }
    catch(org.xml.sax.SAXException se)
    {
      se.printStackTrace();
      throw new RuntimeException(se.getMessage());
    }

  }

  /**
   * Cleanup; called when applet is terminated and unloaded.
   */
  public void destroy()
  {
    if(null != m_trustedWorker)
    {
      m_trustedWorker.stop();
      // m_trustedWorker.destroy();
      m_trustedWorker = null;
    }
    m_styleURLOfCached = null;
    m_documentURLOfCached = null;
  }

  /**
   * Do not call; this applet contains no UI or visual components.
   */

  public void paint(Graphics g)
  {
  }

  /**
   *  Automatically called when the HTML client containing the applet loads.
   *  This method starts execution of the applet thread.
   */
  public void start()
  {
    m_trustedAgent = new TrustedAgent();
    Thread currentThread = Thread.currentThread();
    m_trustedWorker = new Thread(currentThread.getThreadGroup(), m_trustedAgent);
    m_trustedWorker.start();
    try
    {
      this.showStatus("Causing Xalan and XML4J to Load and JIT...");
      // Prime the pump so that subsequent transforms don't look so slow.
      StringReader xmlbuf = new StringReader("<?xml version='1.0'?><foo/>");
      StringReader xslbuf = new StringReader("<?xml version='1.0'?><xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'><xsl:template match='foo'><out/></xsl:template></xsl:stylesheet>");
      PrintWriter pw = new PrintWriter(new StringWriter());
      synchronized(m_processor)
      {

        m_processor.process(new XSLTInputSource(xmlbuf),
                            new XSLTInputSource(xslbuf),
                            new XSLTResultTarget(pw));
      this.showStatus("PRIMED the pump!");
      }
      // System.out.println("Primed the pump!");
      this.showStatus("Ready to click!");
    }
    catch(Exception e)
    {
      this.showStatus("Could not prime the pump!");
      System.out.println("Could not prime the pump!");
      e.printStackTrace();
    }
  }

  /**
   * Automatically called when the HTML page containing the applet is no longer
   * on the screen. Stops execution of the applet thread.
   */
  public void stop()
  {
    if(null != m_trustedWorker)
    {
      m_trustedWorker.stop();
      // m_trustedWorker.destroy();
      m_trustedWorker = null;
    }
    m_styleURLOfCached = null;
    m_documentURLOfCached = null;
  }

  /**
   * Set the URL to the XSL stylesheet that will be used
   * to transform the input XML.  No processing is done yet.
   * @param valid URL string.
   */
  public void setStyleURL(String urlString)
  {
    m_styleURL =urlString;
  }

  /**
   * Set the URL to the XML document that will be transformed
   * with the XSL stylesheet.  No processing is done yet.
   * @param valid URL string.
   */
  public void setDocumentURL(String urlString)
  {
    m_documentURL = urlString;
  }

  /**
   * The processor keeps a cache of the source and
   * style trees, so call this method if they have changed
   * or you want to do garbage collection.
   */
  public void freeCache()
  {
    m_styleURLOfCached = null;
    m_documentURLOfCached = null;
  }

  /**
   * Set an attribute in the stylesheet, which gives the ability
   * to have some dynamic selection control.
   * @param nameOfIDAttrOfElemToModify The name of an attribute to search for a unique id.
   * @param elemId The unique ID to look for.
   * @param attrName Once the element is found, the name of the attribute to set.
   * @param value The value to set the attribute to.
   */
  public void setStyleSheetAttribute(String nameOfIDAttrOfElemToModify,
                                     String elemId,
                                     String attrName,
                                     String value)
  {
    m_nameOfIDAttrOfElemToModify = nameOfIDAttrOfElemToModify;
    m_elemIdToModify = elemId;
    m_attrNameToSet = attrName;
    m_attrValueToSet = value;
  }

  transient String m_key;
  transient String m_expression;

  /**
   * Submit a stylesheet parameter.
   * @param expr The parameter expression to be submitted.
   * @see org.apache.xalan.xslt.XSLTProcessor#setStylesheetParam(String, String)
   */
  public void setStylesheetParam(String key, String expr)
  {
    m_key = key;
    m_expression = expr;
  }

  /**
   * Given a String containing markup, escape the markup so it
   * can be displayed in the browser.
   */
  public String escapeString(String s)
  {
    StringBuffer sb = new StringBuffer();
    int length = s.length();

    for (int i = 0;  i < length;  i ++)
    {
      char ch = s.charAt(i);
      if ('<' == ch)
      {
        sb.append("&lt;");
      }
      else if ('>' == ch)
      {
        sb.append("&gt;");
      }
      else if ('&' == ch)
      {
        sb.append("&amp;");
      }
      else if (0xd800 <= ch && ch < 0xdc00)
      {
        // UTF-16 surrogate
        int next;
        if (i+1 >= length)
        {
          throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString(ch)}));//"Invalid UTF-16 surrogate detected: "
            //+Integer.toHexString(ch)+ " ?");
        }
        else
        {
          next = s.charAt(++i);
          if (!(0xdc00 <= next && next < 0xe000))
            throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString(ch)+" "+Integer.toHexString(next)}));//"Invalid UTF-16 surrogate detected: "
              //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
          next = ((ch-0xd800)<<10)+next-0xdc00+0x00010000;
        }
        sb.append("&#x");
        sb.append(Integer.toHexString(next));
        sb.append(";");
      }
      else
      {
        sb.append(ch);
      }
    }
    return sb.toString();
  }


  /**
   * Assuming the stylesheet URL and the input XML URL have been set,
   * perform the transformation and return the result as a String.
   */
  public String getHtmlText()
  {
    m_trustedAgent.m_getData = true;
    m_callThread = Thread.currentThread();
    try
    {
      synchronized(m_callThread)
      {
        m_callThread.wait();
      }
    }
    catch(InterruptedException ie)
    {
      System.out.println(ie.getMessage());
    }
    return m_htmlText;
  }

  /**
   * Get a DOM tree as escaped text, suitable for display
   * in the browser.
   */
  public String getTreeAsText(String treeURL)
    throws IOException
  {
    String text = "";
    byte[] buffer = new byte[50000];

    try
		{
      URL docURL = new URL(m_documentBase, treeURL);
      InputStream in = docURL.openStream();

			int nun_chars;
			while ( ( nun_chars = in.read( buffer, 0, buffer.length ) ) != -1 )
			{
				text = text + new String( buffer, 0, nun_chars );
			}
			in.close();
		}
		catch ( Exception any_error )
    {any_error.printStackTrace();}
    return text;
  }

  /**
   * Get the XML source Tree as a text string suitable
   * for display in a browser.  Note that this is for display of the
   * XML itself, not for rendering of HTML by the browser.
   * @exception Exception thrown if tree can not be converted.
   */
  public String getSourceTreeAsText()
    throws Exception
  {
    return getTreeAsText(m_documentURL);
  }

  /**
   * Get the XSL style Tree as a text string suitable
   * for display in a browser.  Note that this is for display of the
   * XML itself, not for rendering of HTML by the browser.
   * @exception Exception thrown if tree can not be converted.
   */
  public String getStyleTreeAsText()
    throws Exception
  {
    return getTreeAsText(m_styleURL);
  }

  /**
   * Get the HTML result Tree as a text string suitable
   * for display in a browser.  Note that this is for display of the
   * XML itself, not for rendering of HTML by the browser.
   * @exception Exception thrown if tree can not be converted.
   */
  public String getResultTreeAsText()
    throws Exception
  {
    return escapeString(getHtmlText());
  }

  /**
   * Process a document and a stylesheet and return
   * the transformation result.  If one of these is null, the
   * existing value (of a previous transformation) is not affected.
   */
  public String transformToHtml(String doc, String style)
  {
    if(null != doc)
    {
      m_documentURL = doc;
    }
    if(null != style)
    {
      m_styleURL = style;
    }
    return getHtmlText();
  }

  /**
   * Process a document and a stylesheet and return
   * the transformation result. Use the xsl:stylesheet PI to find the
   * document, if one exists.
   */
  public String transformToHtml(String doc)
  {
    if(null != doc)
    {
      m_documentURL = doc;
    }
    m_styleURL = null;
    return getHtmlText();
  }

  /**
   * Do the real transformation after the right XML processor
   * liason has been found.
   */
  private String doTransformation(XMLParserLiaison xmlProcessorLiaison,
                                  XSLTProcessor processor)
    throws XSLProcessorException, MalformedURLException,
           FileNotFoundException, IOException, org.xml.sax.SAXException
  {
    URL documentURL = null;
    URL styleURL = null;
    StringWriter osw = new StringWriter();
    PrintWriter pw = new PrintWriter(osw, false);

    // xmlProcessorLiaison.SaveXMLToFile(m_resultTree, pw);

    if(null != m_key)
    {
      processor.setStylesheetParam(m_key, m_expression);
    }

    this.showStatus("Begin Transformation...");
    try
    {
      documentURL = new URL(m_codeBase, m_documentURL);
      XSLTInputSource xmlSource = new XSLTInputSource(documentURL.toString());
      Node xmlNode = m_processor.getSourceTreeFromInput(xmlSource);

      styleURL = new URL(m_codeBase, m_styleURL);
      XSLTInputSource xslSource = new XSLTInputSource(styleURL.toString());
      Node xslNode =  m_processor.getSourceTreeFromInput(xslSource);

      if (xmlNode==null) System.out.println("XML Source Node is null");

      // Node type should always be  Node.DOCUMENT_NODE (9)
      if (xmlNode.getNodeType()== Node.DOCUMENT_NODE)
        m_docTree = (Document)xmlNode;
      else
        System.out.println("XML source document node type is " + xmlNode.getNodeType());
      if (xslNode.getNodeType()== Node.DOCUMENT_NODE)
        m_styleTree = (Document)xslNode;
      else
        System.out.println("Stylesheet source document node type is " + xslNode.getNodeType());

      processor.process(xmlSource, xslSource, new XSLTResultTarget(pw));
    }
    catch(MalformedURLException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
    this.showStatus("Transformation Done!");

    String htmlData = osw.toString();

    return htmlData;
  }

  /**
   * Process the transformation.
   */
  private String processTransformation()
    throws XSLProcessorException, MalformedURLException,
           IOException, FileNotFoundException,
           org.xml.sax.SAXException
  {
    String htmlData = null;
    try
    {
      if(whichParser.trim().equals("XML4J") || whichParser.trim().equals("ANY"))
      {
        this.showStatus("Waiting for Xalan and XML4J to finish loading and JITing...");
        synchronized(m_processor)
        {
          // XSLTEngineImpl processor = new XSLProcessor(m_liaison);
          m_processor.reset();
          htmlData = doTransformation(m_liaison, m_processor);
        }
      }
      else
      {
          System.out.println("XSLTProcessorApplet only works with XML4J at the moment!");
      }
    }
    catch(NoClassDefFoundError e)
    {
      System.out.println("Can not find "+whichParser+" XML Processor!!");
    }
    return htmlData;
  }

  /**
   * This class maintains a worker thread that that is
   * trusted and can do things like access data.  You need
   * this because the thread that is called by the browser
   * is not trusted and can't access data from the URLs.
   */
  class TrustedAgent implements Runnable
  {
    public boolean m_getData = false;
    public void run()
    {
      while(true)
      {
        m_trustedWorker.yield();
        if(m_getData)
        {
          try
          {
            m_getData = false;
            m_htmlText = null;
            m_htmlText = processTransformation();
          }
          catch(Exception e)
          {
            e.printStackTrace();
          }
          finally
          {
            synchronized(m_callThread)
            {
              m_callThread.notify();
            }

          }
        }
        else
        {
          try
          {
            m_trustedWorker.sleep(50);
          }
          catch (InterruptedException ie)
          {
            ie.printStackTrace();
          }
        }
      }
    }
  }
}

