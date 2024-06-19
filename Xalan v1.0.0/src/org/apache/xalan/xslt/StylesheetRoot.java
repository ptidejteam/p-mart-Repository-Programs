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
package org.apache.xalan.xslt;

import org.apache.xalan.xpath.xml.*;
import org.apache.xalan.xpath.*;
import org.w3c.dom.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xalan.xslt.trace.*;
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;
import org.apache.xml.serialize.*;

/**
 * <meta name="usage" content="general"/>
 * Binary representation of a stylesheet -- use the {@link org.apache.xalan.xslt.XSLTProcessor} ProcessStylesheet
 * method to create a StylesheetRoot and improve performance for a stylesheet performing multiple transformations.
 * Also required for XSLTProcessor to function as SAX DocumentHandler.
 */
public class StylesheetRoot extends Stylesheet implements java.io.Serializable
{

  /**
   * The URL that belongs to the result namespace.
   * @serial
   */
  String m_resultNameSpaceURL = null;

  /**
   * List of listeners who are interested in tracing what's going on.
   */
  transient Vector m_traceListeners = null;

  static final String DEFAULT_ENCODING = "UTF-8";

  String m_liaisonClassUsedToCreate = null;

  /**
   * <meta name="usage" content="internal"/>
   * Read the stylesheet root from a serialization stream.
   */
  private void readObject(ObjectInputStream stream)
    throws IOException, SAXException
  {
    // System.out.println("Reading Stylesheet");
    try
    {
      stream.defaultReadObject();
    }
    catch(ClassNotFoundException cnfe)
    {
      throw new XSLProcessorException(cnfe);
    }
    m_traceListeners = null;
    // System.out.println("Done reading Stylesheet");
  }

  /**
   * Add a trace listener for the purposes of debugging and diagnosis.
   */
  public void
                     addTraceListener(TraceListener tl)
    throws TooManyListenersException
  {
    if(null == m_traceListeners)
      m_traceListeners = new Vector();
    m_traceListeners.addElement(tl);
  }

  /**
   * Remove a trace listener.
   */
  public void
                        removeTraceListener(TraceListener tl)
  {
    if(null != m_traceListeners)
    {
      m_traceListeners.removeElement(tl);
    }
  }

  /**
   * Fire a trace event.
   */
  void fireTraceEvent(TracerEvent te)
  {
    if(null != m_traceListeners)
    {
      int nListeners = m_traceListeners.size();
      for(int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener)m_traceListeners.elementAt(i);
        tl.trace(te);
      }
    }
  }

  /**
   * Fire a trace event.
   */
  void fireSelectedEvent(SelectionEvent se)
    throws org.xml.sax.SAXException
  {
    if(null != m_traceListeners)
    {
      int nListeners = m_traceListeners.size();
      for(int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener)m_traceListeners.elementAt(i);
        tl.selected(se);
      }
    }
  }

  /**
   * Uses an XSL stylesheet document.
   * @param processor  The XSLTProcessor implementation.
   * @param baseIdentifier The file name or URL for the XSL stylesheet.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public StylesheetRoot(XSLTEngineImpl processor,
                        String baseIdentifier)
    throws XSLProcessorException,
           MalformedURLException,
           FileNotFoundException,
           IOException,
           SAXException
  {
    super(null, processor, baseIdentifier);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Initialize the stylesheet.
   */
  protected void init(XSLTEngineImpl processor)
    throws XSLProcessorException,
    MalformedURLException,
    FileNotFoundException,
    IOException,
    SAXException
  {
    m_liaisonClassUsedToCreate = ((Object)processor.getXMLProcessorLiaison()).getClass().getName();
    m_importStack = new Stack();
    // For some reason, the imports aren't working right if I
    // don't set the baseIdent to full url.  I think this is OK,
    // and probably safer and faster in general.
    // System.out.println("this.m_baseIdent: "+this.m_baseIdent);
    if(null == this.m_baseIdent)
      this.m_baseIdent = "";
    URL url = processor.getURLFromString(this.m_baseIdent, null);
    this.m_baseIdent = url.toExternalForm();
    m_importStack.push(processor.getURLFromString(this.m_baseIdent, null));
    m_stylesheetRoot = this;
    super.init(processor);
    // initDefaultRule();
  }

  /**
   * Get a new OutputFormat object according to the xsl:output attributes.
   */
  public OutputFormat getOutputFormat()
  {
    OutputFormat formatter = new OutputFormat(this.getOutputMethod(),
                                              this.getOutputEncoding(),
                                              this.getOutputIndent());
    formatter.setDoctype(this.getOutputDoctypePublic(), this.getOutputDoctypeSystem());
    formatter.setOmitXMLDeclaration(this.getOmitOutputXMLDecl());
    formatter.setStandalone(this.getOutputStandalone());
    formatter.setMediaType(this.getOutputMediaType());
    formatter.setVersion(this.getOutputVersion());
    // This is to get around differences between Xalan and Xerces.
    // Xalan uses -1 as default for no indenting, Xerces uses 0.
    // So we just bump up the indent value here because we will
    // subtract from it at output time (FormatterToXML.init());
    if (getOutputIndent())
      formatter.setIndent(formatter.getIndent()+1);

    // Don't pass the cdata elements to the formatter, 'cause we
    // handle it ourselves.
    /*
    QName[] qnames = this.getCDataSectionElems();
    int n = qnames.length;
    String[] strings = new String[n];
    for(int i = 0; i < n; i++)
      strings[i] = qnames[i].toString();
    formatter.setCDataElements(strings);
    */
    return formatter;
  }

  /**
   * Transform the XML source tree and place the output in the result tree target.
   * @param xmlSource  The XML input source tree.
   * @param outputTarget The output result tree.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   * @see org.apache.xalan.xslt.XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)
   */
  public void process( XSLTInputSource xmlSource,
                       XSLTResultTarget outputTarget)
    throws SAXException,
           MalformedURLException,
           FileNotFoundException,
           IOException
  {
    XSLTProcessor iprocessor =
                              (null != m_liaisonClassUsedToCreate) ?
                              new XSLTProcessorFactory().getProcessorUsingLiaisonName(m_liaisonClassUsedToCreate)
                              : new XSLTProcessorFactory().getProcessor();
    process(iprocessor, iprocessor.getSourceTreeFromInput(xmlSource), outputTarget);
    // System.out.println("Number counters made: "+
    //                   ((XSLTEngineImpl)iprocessor).getCountersTable().m_countersMade);
  }

  /**
   * Transform the XML source tree and place the output in the result tree target.
   * @param iprocessor  The processor that will track the running state.
   * @param xmlSource  The XML input source tree.
   * @param outputTarget The output result tree.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   * @see org.apache.xalan.xslt.XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)
   */
  public void process( XSLTProcessor iprocessor, XSLTInputSource xmlSource,
                       XSLTResultTarget outputTarget)
    throws SAXException,
           MalformedURLException,
           FileNotFoundException,
           IOException
  {
    process(iprocessor, iprocessor.getSourceTreeFromInput(xmlSource), outputTarget);
  }

  /**
   * Transform the XML source tree (a DOM Node) and place the output in the result tree target.
   * This is a convenience method. You can also use a DOM Node to instantiate an XSLTInputSource object,
   * and call {@link #process(XSLTProcessor, XSLTInputSource, XSLTResultTarget)} or
   * {@link org.apache.xalan.xslt.XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)}.
   * @param iprocessor  The processor that will track the running state.
   * @param sourceTree  The input source tree in the form of a DOM Node.
   * @param outputTarget The output result tree.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void process( XSLTProcessor iprocessor, Node sourceTree,
                       XSLTResultTarget outputTarget)
    throws SAXException,
           MalformedURLException,
           FileNotFoundException,
           IOException
  {
    XSLTEngineImpl processor = (XSLTEngineImpl)iprocessor; // TODO: Check for class cast exception
    synchronized(processor)
    {
      processor.switchLiaisonsIfNeeded(sourceTree, outputTarget.getNode());
      
      processor.m_stylesheetRoot = this;

      OutputStream ostream = null;

      try
      {
        // Double-check the node to make sure it matches the processor liaison.
        processor.getXMLProcessorLiaison().checkNode(sourceTree);

        // Needs work: We have to put the extension namespaces
        // into the liaison's table.  We wouldn't have to do this
        // if the stylesheet handled it's own extensions, which
        // I'll fix on a later date.
        Enumeration keys = m_extensionNamespaces.keys();
        while(keys.hasMoreElements())
        {
          Object key = keys.nextElement();
          // System.out.println("Putting ext namespace: "+key);
          processor.getExecContext().addExtensionNamespace ((String)key,
                                                            (ExtensionFunctionHandler)m_extensionNamespaces.get(key));
        }

        // Find the root pattern in the XSL.
        ElemTemplate rootRule = findTemplate(processor, sourceTree, sourceTree);

        if(null == rootRule)
        {
          rootRule = m_defaultRootRule;
        }

        DocumentHandler docHandler = outputTarget.getDocumentHandler();

        OutputFormat formatter = getOutputFormat();

        if(null != outputTarget.getEncoding())
          formatter.setEncoding(outputTarget.getEncoding());

        if(null != docHandler)
        {
          processor.m_flistener = docHandler;
        }
        else if(null != outputTarget.getByteStream())
        {
          if (!(processor.m_parserLiaison.getIndent() < 0))
          {
            // This is to get around differences between Xalan and Xerces.
            // Xalan uses -1 as default for no indenting, Xerces uses 0.
            // So we just bump up the indent value here because we will
            // subtract from it at output time (FormatterToXML.init());
            formatter.setIndent(processor.m_parserLiaison.getIndent() + 1);
          }
          processor.m_flistener = makeSAXSerializer(outputTarget.getByteStream(),
                                                               formatter);
        }
        else if(null != outputTarget.getCharacterStream())
        {
          if (!(processor.m_parserLiaison.getIndent() < 0))
          {
            formatter.setIndent(processor.m_parserLiaison.getIndent() + 1);
          }
          processor.m_flistener = makeSAXSerializer(outputTarget.getCharacterStream(),
                                                               formatter);
        }
        else if(null != outputTarget.getFileName())
        {
          if (!(processor.m_parserLiaison.getIndent() < 0))
          {
            formatter.setIndent(processor.m_parserLiaison.getIndent() + 1);
          }
          ostream = new FileOutputStream(outputTarget.getFileName());
          processor.m_flistener = makeSAXSerializer(ostream, formatter);
        }
        else if(null != outputTarget.getNode())
        {
          if(processor.getXMLProcessorLiaison() instanceof org.apache.xalan.xpath.dtm.DTMLiaison)
            processor.error(XSLTErrorResources.ER_CANT_USE_DTM_FOR_OUTPUT); //"Can't use a DTMLiaison for an output DOM node... pass a org.apache.xalan.xpath.xdom.XercesLiaison instead!");
          switch(outputTarget.getNode().getNodeType())
          {
          case Node.DOCUMENT_NODE:
            processor.m_flistener = new FormatterToDOM((Document)outputTarget.getNode());
            break;
          case Node.DOCUMENT_FRAGMENT_NODE:
            processor.m_flistener = new FormatterToDOM(processor.m_parserLiaison.createDocument(),
                                                       (DocumentFragment)outputTarget.getNode());
            break;
          case Node.ELEMENT_NODE:
            processor.m_flistener = new FormatterToDOM(processor.m_parserLiaison.createDocument(),
                                                       (Element)outputTarget.getNode());
            break;
          default:
            error(XSLTErrorResources.ER_CAN_ONLY_OUTPUT_TO_ELEMENT); //"Can only output to an Element, DocumentFragment, Document, or PrintWriter.");
          }
        }
        else
        {
          outputTarget.setNode(processor.m_parserLiaison.createDocument());
          processor.m_flistener = new FormatterToDOM((Document)outputTarget.getNode());
        }

        processor.resetCurrentState(sourceTree);
        processor.m_rootDoc = sourceTree;

        if(null != processor.m_diagnosticsPrintWriter)
        {
          processor.diag("=============================");
          processor.diag("Transforming...");
          processor.pushTime(sourceTree);
        }

        processor.getVarStack().pushContextMarker();
        try
        {
          processor.resolveTopLevelParams();
        }
        catch(Exception e)
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_PROCESS_ERROR, null), e); //"StylesheetRoot.process error", e);
        }

        processor.m_resultTreeHandler.startDocument();

        // Output the action of the found root rule.  All processing
        // occurs from here.  buildResultFromTemplate is highly recursive.
        rootRule.execute(processor, sourceTree, sourceTree, null);

        processor.m_resultTreeHandler.endDocument();

        // Reset the top-level params for the next round.
        processor.m_topLevelParams = new Vector();

        if(null != processor.m_diagnosticsPrintWriter)
        {
          processor.displayDuration("transform", sourceTree);
        }
      }
      finally
      {
        if (null != ostream)
        {
          ostream.close();
        }
      }
    }

  }

  /**
   * If this is set to true, the Xerces serializers will
   * be used, otherwise (false is the default) the Xalan serializers will be
   * used.  This is meant as a stop-gap until the Xerces
   * serializers come up to speed.
   */
  public boolean m_useXercesSerializers = false;
  
  /**
   * Creates a compatible SAX serializer for the specified writer
   * and output format. If the output format is missing, the default
   * is an XML format with UTF8 encoding.
   *
   * @param writer The writer
   * @param format The output format
   * @return A compatible SAX serializer
   */
  public DocumentHandler makeSAXSerializer( Writer writer, OutputFormat format )
  {
    DocumentHandler handler;
    if(m_useXercesSerializers)
    {
      handler = new XMLSerializer(writer, format);
      //handler = Serializer.makeSAXSerializer(writer, format);
    }
    else if ( format == null )
    {
      format = new OutputFormat( "xml", "UTF-8", false );
      FormatterToXML serializer = new FormatterToXML();
      serializer.init( writer, format );
      handler = serializer;
    }
    else if ( format.getMethod().equalsIgnoreCase( "html" ) )
    {
      FormatterToXML serializer = new FormatterToHTML();
      serializer.init( writer, format );
      handler = serializer;
    }
    else if ( format.getMethod().equalsIgnoreCase( "xml" ) )
    {
      FormatterToXML serializer = new FormatterToXML();
      serializer.init( writer, format );
      handler = serializer;
    }
    else if ( format.getMethod().equalsIgnoreCase( "text" ) )
    {
      handler = new FormatterToText(writer);
    }
    else if ( format.getMethod().equalsIgnoreCase( "xhtml" ) )
    {
      handler = new XMLSerializer(writer, format);
      //handler = Serializer.makeSAXSerializer(writer, format);
    }
    else
    {
      handler = new XMLSerializer(writer, format);
      //handler = Serializer.makeSAXSerializer(writer, format);
    }

    return handler;
  }

  /**
   * Creates a compatible SAX serializer for the specified output stream
   * and output format. If the output format is missing, the default
   * is an XML format with UTF8 encoding.
   *
   * @param ostream The output stream.
   * @return A compatible SAX serializer
   */
  public DocumentHandler getSAXSerializer( OutputStream ostream )
    throws UnsupportedEncodingException
  {
    return makeSAXSerializer(ostream, getOutputFormat());
  }

  /**
   * Creates a compatible SAX serializer for the specified output stream
   * and output format. If the output format is missing, the default
   * is an XML format with UTF8 encoding.
   *
   * @param ostream The output stream.
   * @param format The output format
   * @return A compatible SAX serializer
   */
  public DocumentHandler makeSAXSerializer( OutputStream ostream, OutputFormat format )
    throws UnsupportedEncodingException
  {
    DocumentHandler handler;
    if(m_useXercesSerializers)
    {
      handler = new XMLSerializer(ostream, format);
      //handler = Serializer.makeSAXSerializer(ostream, format);
    }
    else if ( format == null )
    {
      format = new OutputFormat( "xml", "UTF-8", false );
      FormatterToXML serializer = new FormatterToXML();
      serializer.init( ostream, format );
      handler = serializer;
    }
    else if ( format.getMethod().equalsIgnoreCase( "html" ) )
    {
      FormatterToXML serializer = new FormatterToHTML();
      serializer.init( ostream, format );
      handler = serializer;
    }
    else if ( format.getMethod().equalsIgnoreCase( "xml" ) )
    {
      FormatterToXML serializer = new FormatterToXML();
      serializer.init( ostream, format );
      handler = serializer;
    }
    else if ( format.getMethod().equalsIgnoreCase( "text" ) )
    {
      String encoding = format.getEncoding();
      if(null == encoding)
      {
        try
        {
          encoding = System.getProperty("file.encoding");
          encoding = (null != encoding) ?
                     FormatterToXML.convertJava2MimeEncoding( encoding ) : "ASCII";
          if(null == encoding)
          {
            encoding = "ASCII";
          }
        }
        catch(SecurityException se)
        {
          encoding = "ASCII";
        }
      }

      this.m_encoding =   encoding;

      String javaEncoding = FormatterToXML.convertMime2JavaEncoding(encoding);

      Writer w = new OutputStreamWriter( ostream, javaEncoding );
      handler = new FormatterToText(w);
    }
    else if ( format.getMethod().equalsIgnoreCase( "xhtml" ) )
    {
      handler = new XMLSerializer(ostream, format);
      //handler = Serializer.makeSAXSerializer(ostream, format);
    }
    else
    {
      handler = new XMLSerializer(ostream, format);
      //handler = Serializer.makeSAXSerializer(ostream, format);
    }

    return handler;
  }


  /**
   * The output method as specified in xsl:output.
   */
  String m_outputmethod = null;

  /**
   * Return the output method that was specified in the stylesheet.
   * The returned value is one of Formatter.OUTPUT_METH_XML,
   * Formatter.OUTPUT_METH_HTML, or Formatter.OUTPUT_METH_TEXT.
   */
  public String getOutputMethod() { return (null == m_outputmethod) ? "xml" : m_outputmethod; }

  /**
   * Return the output method that was specified in the stylesheet.
   * The returned value is one of Formatter.OUTPUT_METH_XML,
   * Formatter.OUTPUT_METH_HTML, or Formatter.OUTPUT_METH_TEXT.
   */
  public boolean isOutputMethodSet() { return (null != m_outputmethod); }


  /**
   * Set the output method.
   */
  public void setOutputMethod(String om)
  {
    m_outputmethod = om;
  }

  /**
   * The version tells the version of XML to be used for outputting the result tree,
   * as specified in xsl:output.
   */
  String m_version = null;

  /** Get the version string that was specified in the stylesheet. */
  public String getOutputVersion() { return m_version; }

  /**
   * indent-result is by default no, which means an XSL processor must not
   * change the whitespace on output.
   * @serial
   */
  boolean m_indentResult = false;

  /** Get the media-type string that was specified in the stylesheet. */
  public boolean getOutputIndent() { return m_indentResult; }

  /**
   * The encoding attribute specifies the preferred encoding to use
   * for outputting the result tree.
   */
  String m_encoding = null;

  /** Get the encoding string that was specified in the stylesheet. */
  public String getOutputEncoding()
  {
    return m_encoding;
  }

  /**
   * The media-type attribute is applicable for the xml output method.
   * The default value for the media-type attribute is text/xml.
   */
  String m_mediatype = null;

  /** Get the media-type string that was specified in the stylesheet. */
  public String getOutputMediaType() { return m_mediatype; }

  /**
   * If the doctype-system-id attribute is specified, the xml output method should
   * output a document type declaration immediately before the first element.
   * The name following <!DOCTYPE should be the name of the first element.
   */
  String m_doctypeSystem = null;

  /** Get the doctype-system-id string that was specified in the stylesheet. */
  public String getOutputDoctypeSystem() { return m_doctypeSystem; }

  /**
   * If doctype-public-id attribute is also specified, then the xml output
   * method should output PUBLIC followed by the public identifier and then
   * the system identifier; otherwise, it should output SYSTEM followed by
   * the system identifier. The internal subset should be empty. The
   * doctype-public-id attribute should be ignored unless the doctype-system-id
   * attribute is specified.
   */
  String m_doctypePublic = null;

  /** Get the doctype-public-id string that was specified in the stylesheet. */
  public String getOutputDoctypePublic() { return m_doctypePublic; }

  /**
   * Tells whether or not to output an XML declaration.
   */
  boolean m_omitxmlDecl = false;

  /** Get the XML Declaration that was specified in the stylesheet. */
  public boolean getOmitOutputXMLDecl() { return m_omitxmlDecl; }

  /**
   * Tells what the xmldecl should specify for the standalone value.
   */
  boolean m_standalone = false;

  /** Get the standalone string that was specified in the stylesheet. */
  public boolean getOutputStandalone() { return m_standalone; }

  /**
   * List of qnames that specifies elements that should be formatted
   * as CDATA.
   */
  QName[] m_cdataSectionElems = null;

  /**
   * Get list of qnames that specifies elements that should be formatted
   * as CDATA.
   */
  public QName[] getCDataSectionElems()
  {
    return m_cdataSectionElems;
  }

  /**
   * Process the xsl:output element.
   */
  void processOutputSpec(String name, AttributeList atts)
    throws SAXException
  {
    int nAttrs = atts.getLength();
    boolean didSpecifyIndent = false;
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_OUTPUT_METHOD))
      {
        m_outputmethod = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_VERSION))
      {
        m_version = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_INDENT))
      {
        m_indentResult = getYesOrNo(aname, atts.getValue(i));
        didSpecifyIndent = true;
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_ENCODING))
      {
        m_encoding = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_MEDIATYPE))
      {
        m_mediatype = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_DOCTYPE_SYSTEM))
      {
        m_doctypeSystem = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_DOCTYPE_PUBLIC))
      {
        m_doctypePublic = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_OMITXMLDECL))
      {
        m_omitxmlDecl = getYesOrNo(aname, atts.getValue(i));
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_STANDALONE))
      {
        m_standalone = getYesOrNo(aname, atts.getValue(i));
      }
      else if(aname.equals(Constants.ATTRNAME_OUTPUT_CDATA_SECTION_ELEMENTS))
      {
        StringTokenizer tokenizer = new StringTokenizer(atts.getValue(i));
        int nElems = tokenizer.countTokens();
        int cdataindex;
        if(null == m_cdataSectionElems)
        {
          m_cdataSectionElems = new QName[nElems];
          cdataindex = 0;
        }
        else
        {
          QName[] cdataSectionElems = m_cdataSectionElems;
          int n = cdataSectionElems.length;
          m_cdataSectionElems = new QName[nElems+n];
          for(int k = 0; k < n; k++)
          {
            m_cdataSectionElems[k] = cdataSectionElems[k];
          }
          cdataindex = n;
        }
        while(tokenizer.hasMoreTokens())
        {
          String token = tokenizer.nextToken();
          QName qname = new QName(token, m_namespaces);
          m_cdataSectionElems[cdataindex] = qname;
          cdataindex++;
        }
      }
      else
      {
        // If the attribute is test, or foo:test and foo is not a
        // known namespace prefix, the attribute is invalid.
        // if foo is a known prefix, then the attribute is valid.
        if (!isAttrOK(aname, atts, i))
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}));
      }
    }
    if((null != m_outputmethod) && (m_outputmethod.equals("html")) &&
       (false == didSpecifyIndent))
    {
      m_indentResult = true;
    }
  }

  /**
   * Tell if this is the root of the stylesheet tree.
   */
  boolean isRoot()
  {
    return true;
  }

  /**
   * A stack of who's importing who is needed in order to support
   * "It is an error if a stylesheet directly or indirectly imports
   * itself. Apart from this, the case where a stylesheet with a
   * particular URI is imported in multiple places is not treated
   * specially."
   */
  transient Stack m_importStack;


  /**
   * <meta name="usage" content="advanced"/>
   * The default template to use for text nodes if we don't find
   * anything else.  This is initialized in initDefaultRule().
   * @serial
   */
  public ElemTemplate m_defaultTextRule;

  /**
   * <meta name="usage" content="advanced"/>
   * The default template to use if we don't find anything
   * else.  This is initialized in initDefaultRule().
   * @serial
   */
  public ElemTemplate m_defaultRule;

  /**
   * <meta name="usage" content="advanced"/>
   * The default template to use for the root if we don't find
   * anything else.  This is initialized in initDefaultRule().
   * We kind of need this because the defaultRule isn't good
   * enough because it doesn't supply a document context.
   * For now, I default the root document element to "HTML".
   * Don't know if this is really a good idea or not.
   * I suspect it is not.
   * @serial
   */
  public ElemTemplate m_defaultRootRule;

  /**
   * Create the default rule if needed.
   */
  void initDefaultRule()
    throws XSLProcessorException, SAXException
  {
    int lineNumber = 0;
    int columnNumber = 0;
    // Then manufacture a default
    MutableAttrListImpl attrs = new MutableAttrListImpl();
    attrs.addAttribute(Constants.ATTRNAME_MATCH, "CDATA", "*");
    m_defaultRule = new ElemTemplate(null, this,
                                     "xsl:"+Constants.ELEMNAME_TEMPLATE_STRING,
                                     attrs, lineNumber, columnNumber);
    attrs.clear();
    ElemApplyTemplates childrenElement
      = new ElemApplyTemplates(null, this,
                               "xsl:"+Constants.ELEMNAME_APPLY_TEMPLATES_STRING,
                               attrs, lineNumber, columnNumber);
    childrenElement.m_isDefaultTemplate = true;
    m_defaultRule.appendChild(childrenElement);

    // -----------------------------

    attrs.clear();
    attrs.addAttribute(Constants.ATTRNAME_MATCH, "CDATA", "text() | @*");
    m_defaultTextRule = new ElemTemplate(null, this,
                                         "xsl:"+Constants.ELEMNAME_TEMPLATE_STRING,
                                         attrs, lineNumber, columnNumber);
    attrs.clear();
    attrs.addAttribute(Constants.ATTRNAME_SELECT, "CDATA", ".");
    ElemValueOf elemValueOf
      = new ElemValueOf(null, this,
                        "xsl:"+Constants.ELEMNAME_VALUEOF_STRING,
                        attrs, lineNumber, columnNumber);
    m_defaultTextRule.appendChild(elemValueOf);

    //--------------------------------

    attrs.clear();
    attrs.addAttribute(Constants.ATTRNAME_MATCH, "CDATA", "/");
    m_defaultRootRule = new ElemTemplate(null, this,
                                         "xsl:"+Constants.ELEMNAME_TEMPLATE_STRING,
                                         attrs, lineNumber, columnNumber);

    attrs.clear();
    childrenElement
      = new ElemApplyTemplates(null, this,
                               "xsl:"+Constants.ELEMNAME_APPLY_TEMPLATES_STRING,
                               attrs, lineNumber, columnNumber);
    childrenElement.m_isDefaultTemplate = true;
    m_defaultRootRule.appendChild(childrenElement);
  }

}
