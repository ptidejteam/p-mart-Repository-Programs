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
import org.apache.xalan.xpath.xml.QName;

import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.xml.sax.ext.*;
import org.apache.xalan.xslt.trace.*;
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;

/**
 * <meta name="usage" content="advanced"/>
 * The Xalan workhorse -- Collaborates with the XML parser liaison, the DOM,
 * and the XPath engine, to transform a source tree of nodes into a result tree
 * according to instructions and templates specified by a stylesheet tree.
 * We suggest you use one of the
 * static XSLTProcessorFactory getProcessor() methods to instantiate the processor
 * and return an interface that greatly simplifies the process of manipulating
 * XSLTEngineImpl.
 *
 * <p>The methods <code>process(...)</code> are the primary public entry points.
 * The best way to perform transformations is to use the
 * {@link XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)} method,
 * but you may use any of process methods defined in XSLTEngineImpl.</p>
 * 
 * <p>Please note that this class is not safe per instance over multiple 
 * threads.  If you are in a multithreaded environment, you should 
 * keep a pool of these objects, or create a new one each time.  In a 
 * multithreaded environment, the right way to do things is to create a 
 * StylesheetRoot via processStylesheet, and then reuse this object 
 * over multiple threads.</p>
 *
 * <p>If you reuse the processor instance, you should call reset() between transformations.</p>
 * @see XSLTProcessorFactory
 * @see XSLTProcessor
 */
public class XSLTEngineImpl implements XPathEnvSupport, XSLTProcessor
{
  //==========================================================
  // SECTION: Member variables
  //==========================================================

  /**
   * The root of a linked set of stylesheets.
   */
  StylesheetRoot m_stylesheetRoot = null;

  /**
   * The root document.
   */
  Node m_rootDoc = null;

  /**
   * The minimum version of XSLT supported.
   */
  static final double m_XSLTVerSupported = 1.0;

  /**
   * The namespace that we must match as a minumum for XSLT.
   */
  static final String m_XSLNameSpaceURL = "http://www.w3.org/1999/XSL/Transform";

  /**
   * Special lotus namespace for built-in extensions.
   */
  static final String m_XSLT4JNameSpaceURL = "http://xml.apache.org/xslt";

  /**
   * If this is set to true, do not warn about pattern
   * match conflicts.
   */
  boolean m_quietConflictWarnings = false;

  /*
  * If this is true, then the diag function will
  * be called.
  */
  private boolean m_traceTemplateChildren = false;

  /*
  * If this is true, then the simple tracing of templates
  * will be performed.
  */
  private boolean m_traceTemplates = false;

  /*
  * If this is true, then diagnostics of each select
  * will be performed.
  */
  boolean m_traceSelects = false;

  /*
  * A stream to print diagnostics to.
  */
  java.io.PrintWriter m_diagnosticsPrintWriter = null;

  /* For diagnostics */
  Hashtable m_durationsTable = new Hashtable();

  /**
   * The top of this stack should contain the currently processed
   * stylesheet SAX locator object.
   */
  Stack m_stylesheetLocatorStack = new Stack();

  /**
   * The pending element.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  protected String m_pendingElementName = null;

  /**
   * Flag to tell if a StartDocument event is pending.
   */
  protected boolean m_pendingStartDoc = false;

  /**
   * The pending attributes.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  MutableAttrListImpl m_pendingAttributes = new MutableAttrListImpl();

  /**
   * A stack to keep track of the result tree namespaces.
   */
  protected Stack m_resultNameSpaces = new Stack();

  /**
   * This is pushed on the m_resultNameSpaces stack 'till a
   * xmlns attribute is found.
   */
  static final ResultNameSpace m_emptyNamespace = new ResultNameSpace(null, null);

  /**
   * This is used whenever a unique namespace is needed.
   */
  int m_uniqueNSValue = 0;

  /**
   * Keyed on CSS2 property names, and holding dummy
   * values for quickly looking up if a result tree element
   * attribute is a CSS attribute or not.
   * @deprecated
   */
  // private static Hashtable m_cssKeys = null;

  /*
  * If this is true, translate CSS attributes on
  * the output to a "style" attribute.
  */
  boolean m_translateCSS = false;

  /**
   * Vector of stylesheet parameters pushed by the API via
   * setStylesheetParam.
   */
  Vector m_topLevelParams = new Vector();

  /**
   * The liason to the XML parser, so the XSL processor
   * can handle included files, and the like, and do the
   * initial parse of the XSL document.
   */
  XMLParserLiaison m_parserLiaison = null;

  /**
   * The listener for formatting events.  This should be
   * supplied by the Formatter object.
   */
  DocumentHandler m_flistener = null;

  /**
   * The current input element that is being processed.
   */
  protected Node m_currentNode;

  /**
   * This is set to true when the "ancestor" attribute of
   * the select element is encountered.
   */
  boolean m_needToCheckForInfiniteLoops = false;

  /**
   * Object to guard agains infinite recursion when
   * doing queries.
   */
  private StackGuard m_stackGuard = new StackGuard();

  /**
   * The stack of Variable stacks.  A VariableStack will be
   * pushed onto this stack for each template invocation.
   */
  private VariableStack m_variableStacks = new VariableStack();

  /**
   * Get the variable stack, which is in charge of variables and
   * parameters.
   */
  final VariableStack getVarStack() { return m_variableStacks; }

  /**
   * This is for use by multiple output documents, to determine
   * the base directory for the output document.  It needs to
   * be set by the caller.
   */
  private String m_outputFileName = null;

  /**
   * Stack of Booleans to keep track of if we should be outputting
   * cdata instead of escaped text.
   * Optimization: use array stack instead of object stack.
   */
  Stack m_cdataStack = new Stack();

  /**
   * Output handler to bottleneck SAX events.
   */
  ResultTreeHandler m_resultTreeHandler = new ResultTreeHandler();

  private static XSLMessages m_XSLMessages = new XSLMessages();

  /**
   * Static true for pushing on the stack and the like.
   */
  private static final Boolean TRUE = new Boolean(true);

  /**
   * Static false for pushing on the stack and the like.
   */
  private static final Boolean FALSE = new Boolean(false);

  /**
   * Table of tables of element keys.
   * @see KeyTable.
   */
  transient Vector m_key_tables = null;

  /**
   * Stack for the purposes of flagging infinite recursion with
   * attribute sets.
   */
  transient Stack m_attrSetStack = null;

  /**
   * The table of counters for xsl:number support.
   * @see ElemNumber
   */
  private transient CountersTable m_countersTable = null;

  /**
   * Is >0 when we're processing a for-each
   */
  BoolStack m_currentTemplateRuleIsNull = new BoolStack();
  
  /**
   * Used for infinite loop check. If the value is -1, do not
   * check for infinite loops. Anyone who wants to enable that 
   * check should change the value of this variable to be the
   * level of recursion that they want to check. Be careful setting 
   * this variable, if the number is too low, it may report an 
   * infinite loop situation, when there is none.
   * Post version 1.0.0, we'll make this a runtime feature.   
   */
  public static int m_recursionLimit = -1;

  /**
   * Get the table of counters, for optimized xsl:number support.
   */
  CountersTable getCountersTable()
  {
    if(null == m_countersTable)
      m_countersTable = new CountersTable();
    return m_countersTable;
  }

  /**
   * Construct an XSLT processor that uses the default DTM (Document Table Model) liaison
   * and XML parser. As a general rule, you should use XSLTProcessorFactory to create an
   * instance of this class and provide access to the instance via the XSLTProcessor interface.
   *
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  protected XSLTEngineImpl()
    throws org.xml.sax.SAXException
  {
    try
    {
      String parserLiaisonClassName = Constants.LIAISON_CLASS;
      Class parserLiaisonClass = Class.forName(parserLiaisonClassName);
      Constructor parserLiaisonCtor = parserLiaisonClass.getConstructor(null);
      m_parserLiaison
        = (XMLParserLiaison)parserLiaisonCtor.newInstance(null);
      m_parserLiaison.setEnvSupport(this);
    }
    catch(Exception e)
    {
      throw new XSLProcessorException(e);
    }
  }

  /**
   * Construct an XSLT processor that uses the the given parser liaison.
   * As a general rule, you should use XSLTProcessorFactory to create an
   * instance of this class and provide access to the instance via the XSLTProcessor interface.
   *
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  public XSLTEngineImpl(String parserLiaisonClassName)
    throws org.xml.sax.SAXException
  {
    try
    {
      Class parserLiaisonClass = Class.forName(parserLiaisonClassName);
      Constructor parserLiaisonCtor = parserLiaisonClass.getConstructor(null);
      m_parserLiaison
        = (XMLParserLiaison)parserLiaisonCtor.newInstance(null);
      m_parserLiaison.setEnvSupport(this);
    }
    catch(Exception e)
    {
      throw new XSLProcessorException(e);
    }
  }

  /**
   * Construct an XSL processor that uses the the given parser liaison.
   * As a general rule, you should use XSLTProcessorFactory to create an
   * instance of this class and provide access to the instance via the XSLTProcessor interface.
   *
   * @param XMLParserLiaison A liaison to an XML parser.
   *
   * @see org.apache.xalan.xpath.xml.XMLParserLiaison
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  public XSLTEngineImpl(XMLParserLiaison parserLiason)
  {
    setExecContext(parserLiason);
  }

  /**
   * Construct an XSLT processor that can call back to the XML parser, in order to handle
   * included files and the like.
   *
   * @param XMLParserLiaison A liaison to an XML parser.
   *
   * @see org.apache.xalan.xpath.xml.XMLParserLiaison
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  XSLTEngineImpl(XMLParserLiaison parserLiason, XPathFactory xpathFactory)
  {
    setExecContext(parserLiason);
  }

  /**
   * Reset the state.  This needs to be called after a process() call
   * is invoked, if the processor is to be used again.
   */
  public void reset()
  {
    m_stylesheetRoot = null;
    m_rootDoc = null;
    m_durationsTable.clear();
    // if(null != m_countersTable)
    //  System.out.println("Number counters made: "+m_countersTable.m_countersMade);
    m_countersTable = null;
    m_stylesheetLocatorStack.removeAllElements();
    m_stylesheetLocatorStack = new Stack();
    m_pendingElementName = null;
    m_pendingAttributes.clear();
    m_pendingAttributes = new MutableAttrListImpl();
    m_resultNameSpaces.removeAllElements();
    // m_resultNameSpaces = new Stack();
    m_cdataStack = new Stack();
    m_currentNode = null;
    m_needToCheckForInfiniteLoops = false;
    m_variableStacks = new VariableStack();
    m_stackGuard = new StackGuard();
    m_parserLiaison.reset();
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the object used to guard the stack from 
   * recursion.
   */
  StackGuard getStackGuard()
  {  
    return m_stackGuard;
  }  

  // Guard against being serialized by mistake
  private void writeObject(ObjectOutputStream stream)
    throws org.xml.sax.SAXException, IOException
  {
    // System.out.println("Writing XSLTEngineImpl");
    error(XSLTErrorResources.ER_CANNOT_SERIALIZE_XSLPROCESSOR); //,"Can not serialize an XSLTEngineImpl!");
  }

  /**
   * Switch the liaisons if needed according to the type of 
   * the source and result nodes.
   * @param sourceNode The node being transformed -- may be null.
   * @param resultNode The node to which result nodes will be added -- may be null.
   */
  void switchLiaisonsIfNeeded(Node sourceNode, Node resultNode)
    throws SAXException
  {
    // If the result node is a xerces node, try to be smart about which 
    // liaison is going to be used.
	
	// Here's the logic:

	// Source - Result - What we do
	// ==========================================
	// DTM - Xerces DOM - error, you can't mix types
	// DTM - DTM - Do nothing, We're OK
	// Xerces DOM - DTM - error, you can't have DTM as a result
	// Xerces DOM - Xerces DOM - switch to Xerces Liaison


    if((null != resultNode) && (resultNode instanceof org.apache.xerces.dom.NodeImpl) &&
       (m_parserLiaison instanceof org.apache.xalan.xpath.dtm.DTMLiaison))
    {
      if((null != sourceNode)
         && (!(sourceNode instanceof org.apache.xerces.dom.NodeImpl)))
      {
        throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_CANNOT_MIX_XERCESDOM, null)); //"Can not mix non Xerces-DOM input with Xerces-DOM output!");
      }
     
      XMLParserLiaison newLiaison = new org.apache.xalan.xpath.xdom.XercesLiaison();
      newLiaison.copyFromOtherLiaison((XMLParserLiaisonDefault)m_parserLiaison);
      setExecContext(newLiaison);
    }
    else if((null != sourceNode)&& (sourceNode instanceof org.apache.xerces.dom.NodeImpl) &&
       (m_parserLiaison instanceof org.apache.xalan.xpath.dtm.DTMLiaison))
    {
      if((null != resultNode) 
         && (!(resultNode instanceof org.apache.xerces.dom.NodeImpl)))
      {
        throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_CANNOT_MIX_XERCESDOM, null)); //"Can not mix Xerces-DOM input with non Xerces-DOM output!");
      }

      XMLParserLiaison newLiaison = new org.apache.xalan.xpath.xdom.XercesLiaison();
      newLiaison.copyFromOtherLiaison((XMLParserLiaisonDefault)m_parserLiaison);
      setExecContext(newLiaison);
    }
  }

  /**
   * Transform the source tree to the output in the given
   * result tree target. As a general rule, we recommend you use the
   * {@link XSLTProcessor#Process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)} method.
   * @param inputSource  The input source.
   * @param stylesheetSource  The stylesheet source.  May be null if source has a xml-stylesheet PI.
   * @param outputTarget The output source tree.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void process( XSLTInputSource inputSource,
                       XSLTInputSource stylesheetSource,
                       XSLTResultTarget outputTarget)
    throws SAXException
  {
    try
    {
      // If the result node is a xerces node, try to be smart about which 
      // liaison is going to be used.
      switchLiaisonsIfNeeded(inputSource.getNode(), outputTarget.getNode());
      
      String xslIdentifier = ((null == stylesheetSource) ||
                              (null == stylesheetSource.getSystemId()))
                             ? "Input XSL" : stylesheetSource.getSystemId();
      Boolean totalTimeID = new Boolean(true);
      pushTime(totalTimeID);
      Node sourceTree = null;

      if(null != inputSource)
        sourceTree = getSourceTreeFromInput(inputSource);

      if(null != stylesheetSource)
      {
        m_stylesheetRoot = processStylesheet(stylesheetSource);
      }
      else if(null != sourceTree)
      {
        String stylesheetURI = null;
        Stack hrefs = new Stack();
        for(Node child=sourceTree.getFirstChild(); null != child; child=child.getNextSibling())
        {
          if(Node.PROCESSING_INSTRUCTION_NODE == child.getNodeType())
          {
            ProcessingInstruction pi = (ProcessingInstruction)child;
            if(pi.getNodeName().equals("xml-stylesheet")
               || pi.getNodeName().equals("xml:stylesheet"))
            {
              boolean isOK = true;
              StringTokenizer tokenizer = new StringTokenizer(pi.getNodeValue(), " \t=");
              while(tokenizer.hasMoreTokens())
              {
                if(tokenizer.nextToken().equals("type"))
                {
                  String typeVal = tokenizer.nextToken();
                  typeVal = typeVal.substring(1, typeVal.length()-1);
                  if(!typeVal.equals("text/xsl"))
                  {
                    isOK = false;
                  }
                }
              }

              if(isOK)
              {
                tokenizer = new StringTokenizer(pi.getNodeValue(), " \t=");
                while(tokenizer.hasMoreTokens())
                {
                  if(tokenizer.nextToken().equals("href"))
                  {
                    stylesheetURI = tokenizer.nextToken();
                    stylesheetURI = stylesheetURI.substring(1, stylesheetURI.length()-1);
                    hrefs.push(stylesheetURI);
                  }
                }
                // break;
              }
            }
          }
        } // end for(int i = 0; i < nNodes; i++)
        boolean isRoot = true;
        Stylesheet prevStylesheet = null;
        while(!hrefs.isEmpty())
        {
          Stylesheet stylesheet = getStylesheetFromPIURL((String)hrefs.pop(), sourceTree,
                                                         (null != inputSource)
                                                         ? inputSource.getSystemId() : null,
                                                         isRoot);
          if(false == isRoot)
          {
            prevStylesheet.m_imports.addElement(stylesheet);
          }
          prevStylesheet = stylesheet;
          isRoot = false;
        }
      }
      else
      {
        error(XSLTErrorResources.ER_NO_INPUT_STYLESHEET); //"Stylesheet input was not specified!");
      }

      if(null == m_stylesheetRoot)
      {
        error(XSLTErrorResources.ER_FAILED_PROCESS_STYLESHEET); //"Failed to process stylesheet!");
      }

      if(null != sourceTree)
      {
        // System.out.println("Calling m_stylesheetRoot.process");
        m_stylesheetRoot.process(this, sourceTree, outputTarget);
        if(null != m_diagnosticsPrintWriter)
        {
          displayDuration("Total time", totalTimeID);
        }
      }
    }
    catch(MalformedURLException mue)
    {
      error(XSLTErrorResources.ERROR0000, new Object[] {mue.getMessage()}, mue);
      // throw se;
    }
    catch(FileNotFoundException fnfe)
    {
      error(XSLTErrorResources.ERROR0000, new Object[] {fnfe.getMessage()}, fnfe);
      // throw se;
    }
    catch(IOException ioe)
    {
      error(XSLTErrorResources.ERROR0000, new Object[] {ioe.getMessage()}, ioe);
      // throw se;
    }
    catch(SAXException se)
    {
      error(XSLTErrorResources.ER_SAX_EXCEPTION, se); //"SAX Exception", se);
      // throw se;
    }
  }

  /**
   * Bottleneck the creation of the stylesheet for derivation purposes.
   */
  public StylesheetRoot createStylesheetRoot(String baseIdentifier)
    throws MalformedURLException, FileNotFoundException,
           IOException, SAXException
  {
    return new StylesheetRoot(this, baseIdentifier);
  }

  /**
   * Given a URI to an XSL stylesheet,
   * Compile the stylesheet into an internal representation.
   * This calls reset() before processing if the stylesheet root has been set
   * to non-null.
   * @param xmldocURLString  The URL to the input XML document.
   * @return The compiled stylesheet object.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public StylesheetRoot processStylesheet(XSLTInputSource stylesheetSource)
    throws SAXException
  {
    if(null != m_stylesheetRoot)
      reset();

    String xslIdentifier = ((null == stylesheetSource) ||
                            (null == stylesheetSource.getSystemId()))
                           ? "Input XSL" : stylesheetSource.getSystemId();

    // In case we have a fragment identifier, go ahead and
    // try and parse the XML here.
    try
    {
      m_stylesheetRoot = createStylesheetRoot(stylesheetSource.getSystemId());
      addTraceListenersToStylesheet();
      StylesheetHandler stylesheetProcessor
        = new StylesheetHandler(this, m_stylesheetRoot);
      if(null != stylesheetSource.getNode())
      {
        if(stylesheetSource.getNode() instanceof StylesheetRoot)
        {
          m_stylesheetRoot = (StylesheetRoot)stylesheetSource.getNode();
        }
        else
        {
          TreeWalker tw = new TreeWalker(stylesheetProcessor);
          tw.traverse(stylesheetSource.getNode());
        }
      }
      else
      {
        diag("========= Parsing "+xslIdentifier+" ==========");
        pushTime(xslIdentifier);
        m_parserLiaison.setDocumentHandler(stylesheetProcessor);
        m_parserLiaison.parse(stylesheetSource);
        if(null != m_diagnosticsPrintWriter)
          displayDuration("Parse of "+xslIdentifier, xslIdentifier);
      }
    }
    catch(Exception e)
    {
      error(XSLTErrorResources.ER_COULDNT_PARSE_DOC, new Object[] {xslIdentifier}, e); //"Could not parse "+xslIdentifier+" document!", e);
    }
    return m_stylesheetRoot;
  }

  /**
   * Given a URI to an XSL stylesheet,
   * Compile the stylesheet into an internal representation.
   * This calls reset() before processing if the stylesheet root has been set
   * to non-null.
   * @param xmldocURLString  The URL to the input XML document.
   * @return The compiled stylesheet object.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public StylesheetRoot processStylesheet(String xsldocURLString)
    throws SAXException
  {
    try
    {
      XSLTInputSource input = new XSLTInputSource(getURLFromString(xsldocURLString, null).toString());
      return processStylesheet(input);
    }
    catch(SAXException se)
    {
      error(XSLTErrorResources.ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL, se); //"processStylesheet not succesfull!", se);
      return null; // shut up compiler
    }
  }

  /**
   * Set the stylesheet for this processor.  If this is set, then the
   * process calls that take only the input .xml will use
   * this instead of looking for a stylesheet PI.  Also,
   * setting the stylesheet is needed if you are going
   * to use the processor as a SAX DocumentHandler.
   */
  public void setStylesheet(StylesheetRoot stylesheetRoot)
  {
    m_stylesheetRoot = stylesheetRoot;
  }

  /**
   * Get the current stylesheet for this processor.
   */
  public StylesheetRoot getStylesheet()
  {
    return m_stylesheetRoot;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the filename of the output document, if it was set.
   * This is for use by multiple output documents, to determine
   * the base directory for the output document.  It needs to
   * be set by the caller.
   */
  public String getOutputFileName()
  {
    return m_outputFileName;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Set the filename of the output document.
   * This is for use by multiple output documents, to determine
   * the base directory for the output document.  It needs to
   * be set by the caller.
   */
  public void setOutputFileName(String filename)
  {
    m_outputFileName = filename;
  }

  //==========================================================
  // SECTION: XML Parsing Functions
  //==========================================================

  /**
   * Take a user string and try and parse XML, and also return
   * the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public URL getURLFromString(String urlString, String base)
    throws SAXException
  {
    return m_parserLiaison.getURLFromString(urlString, base);
  }

  /**
   * Given an input source, get the source tree.
   */
  public Node getSourceTreeFromInput(XSLTInputSource inputSource)
    throws org.xml.sax.SAXException
  {
    Node sourceTree;
    String xmlIdentifier = ((null == inputSource) ||
                            (null == inputSource.getSystemId()))
                           ? "Input XML" : inputSource.getSystemId();

    if(null != inputSource.getNode())
    {
      if(getXMLProcessorLiaison() instanceof org.apache.xalan.xpath.dtm.DTMLiaison)
        error(XSLTErrorResources.ER_CANT_USE_DTM_FOR_INPUT); //"Can't use a DTMLiaison for a input DOM node... pass a org.apache.xalan.xpath.xdom.XercesLiaison instead!");

      sourceTree = inputSource.getNode();
    }
    else
    {
      // In case we have a fragment identifier, go ahead and
      // try and parse the XML here.
      try
      {
        diag("========= Parsing "+xmlIdentifier+" ==========");
        pushTime(xmlIdentifier);
        m_parserLiaison.parse(inputSource);
        if(null != m_diagnosticsPrintWriter)
          displayDuration("Parse of "+xmlIdentifier, xmlIdentifier);
        sourceTree = m_parserLiaison.getDocument();
      }
      catch(Exception e)
      {
        // Unwrap exception
        if((e instanceof SAXException) && (null != ((SAXException)e).getException()))
        {
          // ((SAXException)e).getException().printStackTrace();
          e = ((SAXException)e).getException();
        }
        sourceTree = null; // shutup compiler
        error(XSLTErrorResources.ER_COULDNT_PARSE_DOC, new Object[] {xmlIdentifier}, e); //"Could not parse "+xmlIdentifier+" document!", e);
      }
    }

    return sourceTree;
  }

  /**
   * Read in the XML file, either producing a Document or calling SAX events, and register
   * the document in a table.  If the document has already been read in, it will not
   * be reparsed.
   * @param url The location of the XML.
   * @param docHandler The SAX event handler.
   * @param docToRegister If using a SAX event handler, the object to register in the source docs table.
   * @return lcom.ms.xml.om.Document object, which represents the parsed XML.
   * @exception SAXException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public Document parseXML(URL url,
                           DocumentHandler docHandler,
                           Document docToRegister)
    throws SAXException, IOException
  {
    Object docObj = getSourceDocsTable().get(url.toExternalForm());
    if(null != docObj)
    {
      return (Document)docObj;
    }

    Document doc;
    XSLTInputSource inputSource = new XSLTInputSource(url.toString());
    if(null != docHandler)
    {
      m_parserLiaison.setDocumentHandler(docHandler);
    }
    m_parserLiaison.parse(inputSource);
    if(null == docHandler)
    {
      doc = m_parserLiaison.getDocument();
    }
    else
    {
      doc = docToRegister;
    }
    return doc;
  }

  /**
   * Get the preferred InputSource specification that is associated with a
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches
   * the given criteria.   Does not yet handle the LINK REL="stylesheet" syntax.
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered stylesheet will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns An InputSource that can be passed to createStylesheet method.
   */
  public StylesheetSpec getAssociatedStylesheet(XSLTInputSource source,
                                                 String media,
                                                 String charset)
    throws SAXException
  {
    Vector sources = getAssociatedStylesheets(source, media, charset);
    if(null != sources)
      return (StylesheetSpec)sources.elementAt(0);
    else
      return null;
  }


  /**
   * Get the InputSource specifications that are associated with a
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches
   * the given criteria.   Does not yet handle the LINK REL="stylesheet" syntax.
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered stylesheet will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns A list of input sources that can be passed to createStylesheet method.
   */
  public Vector getAssociatedStylesheets(XSLTInputSource source,
                                         String mediaRequest,
                                         String charsetRequest)
    throws SAXException
  {
    Vector inputSources = null;
    Node sourceTree = getSourceTreeFromInput(source);

    for(Node child=sourceTree.getFirstChild(); null != child; child=child.getNextSibling())
    {
      if(Node.PROCESSING_INSTRUCTION_NODE == child.getNodeType())
      {
        ProcessingInstruction pi = (ProcessingInstruction)child;
        if(pi.getNodeName().equals("xml-stylesheet")
           || pi.getNodeName().equals("xml:stylesheet"))
        {
          String href = null; // CDATA #REQUIRED
          String type = null; // CDATA #REQUIRED
          String title = null; // CDATA #IMPLIED
          String media = null; // CDATA #IMPLIED
          String charset = null; // CDATA #IMPLIED
          boolean alternate = false; // (yes|no) "no"

          StringTokenizer tokenizer = new StringTokenizer(pi.getNodeValue(), " \t=");
          while(tokenizer.hasMoreTokens())
          {
            String name = tokenizer.nextToken();
            if(name.equals("type"))
            {
              String typeVal = tokenizer.nextToken();
              type = typeVal.substring(1, typeVal.length()-1);
            }
            else if(name.equals("href"))
            {
              href = tokenizer.nextToken();
              href = href.substring(1, href.length()-1);
            }
            else if(name.equals("title"))
            {
              title = tokenizer.nextToken();
              title = title.substring(1, title.length()-1);
            }
            else if(name.equals("media"))
            {
              media = tokenizer.nextToken();
              media = media.substring(1, media.length()-1);
            }
            else if(name.equals("charset"))
            {
              charset = tokenizer.nextToken();
              charset = charset.substring(1, charset.length()-1);
            }
            else if(name.equals("alternate"))
            {
              String alternateStr = tokenizer.nextToken();
              alternate = alternateStr.substring(1, alternateStr.length()-1).equals("yes");
            }
          }

          if((null != type) && type.equals("text/xsl") && (null != href))
          {

            if(null != mediaRequest)
            {
              if(null != media)
              {
                if(!media.equals(mediaRequest))
                  continue;
              }
              else
                continue;
            }
            if(null != charsetRequest)
            {
              if(null != charset)
              {
                if(!charset.equals(charsetRequest))
                  continue;
              }
              else
                continue;
            }
            if(null == inputSources)
              inputSources = new Vector();

            StylesheetSpec spec
              = new StylesheetSpec(href, type, title, media, alternate, charset);

            if(!alternate)
              inputSources.insertElementAt(spec, 0);
            else
              inputSources.addElement(spec);
          }
        }

      }
    } // end for(int i = 0; i < nNodes; i++)

    return inputSources;
  }

  /**
   * Reset the state of the XSL processor by reading in a new
   * XSL stylesheet.
   * @param xslURLString a valid URI to an XSL stylesheet.
   * @param outDiagnostics The print stream to write diagnostics (may be null).
   */
  Stylesheet getStylesheetFromPIURL(String xslURLString, Node fragBase,
                                    String xmlBaseIdent, boolean isRoot)
    throws SAXException,
    MalformedURLException,
    FileNotFoundException,
    IOException
  {
    Stylesheet stylesheet = null;
    String[] stringHolder =
    {
      null};
    xslURLString = xslURLString.trim();
    int fragIndex = xslURLString.indexOf('#');
    Document stylesheetDoc;
    if(fragIndex == 0)
    {
      diag("Locating stylesheet from fragment identifier...");
      String fragID = xslURLString.substring(1);
      PrefixResolver nsNode = getExecContext().getNamespaceContext();

      // Try a bunch of really ugly stuff to find the fragment.
      // What's the right way to do this?

      // Create a XPath parser.
      XPathProcessorImpl parser = new XPathProcessorImpl();

      // Create the XPath object.
      XPath xpath = new XPath();

      // Parse the xpath
      parser.initXPath(xpath, "id("+fragID+")", nsNode);
      XObject xobj = xpath.execute(getExecContext(), fragBase, nsNode);

      NodeList nl = xobj.nodeset();
      if(nl.getLength() == 0)
      {
        // xobj = Stylesheet.evalXPathStr(getExecContext(), "//*[@id='"+fragID+"']", fragBase, nsNode);
        // Create the XPath object.
        xpath = new XPath();

        // Parse the xpath
        parser.initXPath(xpath, "//*[@id='"+fragID+"']", nsNode);
        xobj = xpath.execute(getExecContext(), fragBase, nsNode);

        nl = xobj.nodeset();
        if(nl.getLength() == 0)
        {
          // xobj = Stylesheet.evalXPathStr(getExecContext(), "//*[@name='"+fragID+"']", fragBase, nsNode);
          // Create the XPath object.
          xpath = new XPath();

          // Parse the xpath
          parser.initXPath(xpath, "//*[@name='"+fragID+"']", nsNode);
          xobj = xpath.execute(getExecContext(), fragBase, nsNode);
          nl = xobj.nodeset();
          if(nl.getLength() == 0)
          {
            // Well, hell, maybe it's an XPath...
            // xobj = Stylesheet.evalXPathStr(getExecContext(), fragID, fragBase, nsNode);
            // Create the XPath object.
            xpath = new XPath();

            // Parse the xpath
            parser.initXPath(xpath, fragID, nsNode);
            xobj = xpath.execute(getExecContext(), fragBase, nsNode);
            nl = xobj.nodeset();
          }
        }
      }
      if(nl.getLength() == 0)
      {
        error(XSLTErrorResources.ER_COULDNT_FIND_FRAGMENT, new Object[] {fragID}); //"Could not find fragment: "+fragID);
      }
      Node frag = nl.item(0);

      if(Node.ELEMENT_NODE == frag.getNodeType())
      {
        pushTime(frag);
        if(isRoot)
        {
          m_stylesheetRoot = createStylesheetRoot(stringHolder[0]);
          stylesheet = m_stylesheetRoot;
        }
        else
        {
          stylesheet = new Stylesheet(m_stylesheetRoot, this, stringHolder[0]);
        }
        addTraceListenersToStylesheet();

        StylesheetHandler stylesheetProcessor
          = new StylesheetHandler(this, stylesheet);
        TreeWalker tw = new TreeWalker(stylesheetProcessor);
        tw.traverse(frag);

        displayDuration("Setup of "+xslURLString, frag);
      }
      else
      {
        stylesheetDoc = null;
        error(XSLTErrorResources.ER_NODE_NOT_ELEMENT, new Object[] {fragID}); //"Node pointed to by fragment identifier was not an element: "+fragID);
      }
    }
    else
    {
      // hmmm.. for now I'll rely on the XML parser to handle
      // fragment URLs.
      diag(XSLMessages.createMessage(XSLTErrorResources.WG_PARSING_AND_PREPARING, new Object[] {xslURLString})); //"========= Parsing and preparing "+xslURLString+" ==========");
      pushTime(xslURLString);

      if(isRoot)
      {
        m_stylesheetRoot = createStylesheetRoot(xslURLString);
        stylesheet = m_stylesheetRoot;
      }
      else
      {
        stylesheet = new Stylesheet(m_stylesheetRoot, this, xslURLString);
      }
      addTraceListenersToStylesheet();

      StylesheetHandler stylesheetProcessor
        = new StylesheetHandler(this, stylesheet);

      URL xslURL = getURLFromString(xslURLString, xmlBaseIdent);

      XSLTInputSource inputSource = new XSLTInputSource(xslURL.toString());
      m_parserLiaison.setDocumentHandler(stylesheetProcessor);
      m_parserLiaison.parse(inputSource);

      displayDuration("Parsing and init of "+xslURLString, xslURLString);
    }
    return stylesheet;
  }


  //==========================================================
  // SECTION: Source Tree Tables
  //==========================================================

  /**
   * Given a DOM Document, tell what URI was used to parse it.
   */
  public String findURIFromDoc(Document doc)
  {
    return m_parserLiaison.findURIFromDoc(doc);
  }

  /**
   * Get table of source tree documents.
   * Document objects are keyed by URL string.
   */
  public Hashtable getSourceDocsTable()
  {
    return m_parserLiaison.getSourceDocsTable();
  }

  /**
   * Set a source document. Every time a source document is requested
   * through either xsl:uri or in a process call,
   * the processor will first consult table of supplied
   * documents before trying to load and parse the corresponding
   * document from the Net.

   */
  public void setSourceDocument (String uri, Document doc)
  {
    m_parserLiaison.getSourceDocsTable().put(uri, doc);
  }

  // BEGIN SANJIVA CODE
  /**
   * Output an object to the result tree by doing the right conversions.
   * This is public for access by extensions.
   *
   * @param obj the Java object to output. If its of an X<something> type
   *        then that conversion is done first and then sent out.
   */
  public void outputToResultTree (Stylesheet stylesheetTree, Object obj)
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    XObject value;
    // Make the return object into an XObject because it
    // will be easier below.  One of the reasons to do this
    // is to keep all the conversion functionality in the
    // XObject classes.
    if(obj instanceof XObject)
    {
      value = (XObject)obj;
    }
    else if(obj instanceof String)
    {
      value = new XString((String)obj);
    }
    else if(obj instanceof Boolean)
    {
      value = new XBoolean(((Boolean)obj).booleanValue());
    }
    else if(obj instanceof Double)
    {
      value = new XNumber(((Double)obj).doubleValue());
    }
    else if(obj instanceof DocumentFragment)
    {
      value = new XRTreeFrag((DocumentFragment)obj);
    }
    else if(obj instanceof Node)
    {
      value = new XNodeSet((Node)obj);
    }
    else if(obj instanceof NodeList)
    {
      value = new XNodeSet((NodeList)obj);
    }
    else
    {
      value = new XString(obj.toString());
    }

    int type = value.getType();
    String s;
    switch(type)
    {
    case XObject.CLASS_BOOLEAN:        case XObject.CLASS_NUMBER:        case XObject.CLASS_STRING:
      s = value.str();
      m_resultTreeHandler.characters(s.toCharArray(), 0, s.length());
      break;
    case XObject.CLASS_NODESET:          // System.out.println(value);
      NodeList nl = value.nodeset();
      int nChildren = nl.getLength();
      for(int i = 0; i < nChildren; i++)
      {
        Node pos = (Node)nl.item(i);
        Node top = pos;
        while(null != pos)
        {
          flushPending();
          cloneToResultTree(stylesheetTree, pos, false, false,
                            true);
          Node nextNode = pos.getFirstChild();
          while(null == nextNode)
          {
            if(Node.ELEMENT_NODE == pos.getNodeType())
            {
              m_resultTreeHandler.endElement(pos.getNodeName());
            }
            if(top == pos)
              break;
            nextNode = pos.getNextSibling();
            if(null == nextNode)
            {
              pos = pos.getParentNode();
              if(top == pos)
              {
                if(Node.ELEMENT_NODE == pos.getNodeType())
                {
                  m_resultTreeHandler.endElement(pos.getNodeName());
                }
                nextNode = null;
                break;
              }
            }
          }
          pos = nextNode;
        }
      }
      break;

    case XObject.CLASS_RTREEFRAG:
      outputResultTreeFragment(value, m_parserLiaison);
      break;
    }
  }
  // END SANJIVA CODE

  /**
   * Execute the function-available() function.
   */
  public boolean functionAvailable(String namespace,
                                   String extensionName)
  {
    return m_parserLiaison.functionAvailable(namespace, extensionName);
  }

  /**
   * Execute the element-available() function.
   */
  public boolean elementAvailable(String namespace, 
                            String extensionName)
  {
    return m_parserLiaison.elementAvailable(namespace, extensionName);
  }


  /**
   * Handle an extension function.
   */
  public Object extFunction(String namespace, String extensionName,
                            Vector argVec, Object methodKey)
    throws org.xml.sax.SAXException
  {
    return m_parserLiaison.extFunction(namespace, extensionName, argVec, methodKey);
  }

  //==========================================================
  // SECTION: Diagnostic functions
  //==========================================================

  /**
   * List of listeners who are interested in tracing what's
   * being generated.
   */
  transient Vector m_traceListeners = null;

  /**
   * Add a trace listener for the purposes of debugging and diagnosis.
   * @param tl Trace listener to be added.
   */
  public void addTraceListener(TraceListener tl)
    throws TooManyListenersException
  {
    if(null == m_traceListeners)
      m_traceListeners = new Vector();
    m_traceListeners.addElement(tl);
    if(null != m_stylesheetRoot)
      m_stylesheetRoot.addTraceListener(tl);
  }

  /**
   * Add a trace listener for the purposes of debugging and diagnosis.
   * @param tl Trace listener to be added.
   */
  void addTraceListenersToStylesheet()
    throws SAXException
  {
    try
    {
      if(null != m_traceListeners)
      {
        int nListeners = m_traceListeners.size();
        for(int i = 0; i < nListeners; i++)
        {
          TraceListener tl = (TraceListener)m_traceListeners.elementAt(i);
          if(null != m_stylesheetRoot)
            m_stylesheetRoot.addTraceListener(tl);
        }
      }
    }
    catch(TooManyListenersException tmle)
    {
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_TOO_MANY_LISTENERS, null),tmle ); //"addTraceListenersToStylesheet - TooManyListenersException", tmle);
    }
  }

  /**
   * Remove a trace listener.
   * @param tl Trace listener to be removed.
   */
  public void removeTraceListener(TraceListener tl)
  {
    if(null != m_traceListeners)
    {
      m_traceListeners.removeElement(tl);
    }
    if(null != m_stylesheetRoot)
      m_stylesheetRoot.removeTraceListener(tl);
  }

  /**
   * Fire a generate event.
   */
  void fireGenerateEvent(GenerateEvent te)
  {
    if(null != m_traceListeners)
    {
      int nListeners = m_traceListeners.size();
      for(int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener)m_traceListeners.elementAt(i);
        tl.generated(te);
      }
    }
  }

  /**
   * Warn the user of an problem.
   * This is public for access by extensions.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void message(String msg)
    throws SAXException
  {
    message(null, null, msg);
  }



  /**
   * Warn the user of an problem.
   * This is public for access by extensions.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void message(Node styleNode, Node sourceNode, String msg)
    throws SAXException
  {
    boolean shouldThrow = getProblemListener().message(msg);
    if(shouldThrow)
    {
      throw new XSLProcessorException(msg);
    }
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(int msg)
    throws SAXException
  {
    warn(null, null, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(int msg, Object[] args)
    throws SAXException
  {
    warn(null, null, msg, args);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(Node styleNode, Node sourceNode, int msg)
    throws SAXException
  {
    warn(styleNode, sourceNode, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(Node styleNode, Node sourceNode, int msg, Object args[])
    throws SAXException
  {
    String formattedMsg = m_XSLMessages.createWarning(msg, args);
    Locator locator = m_stylesheetLocatorStack.isEmpty()
                      ? null :
                        ((Locator)m_stylesheetLocatorStack.peek());
    String id = (null == locator)
                ? null
                  : (null == locator.getSystemId())
                    ? locator.getPublicId()
                      : locator.getSystemId();

    boolean shouldThrow = getProblemListener().problem(ProblemListener.XSLPROCESSOR,
                                                    ProblemListener.WARNING,
                                                    styleNode, sourceNode, formattedMsg,
                                                    id,
                                                    (null == locator) ? 0: locator.getLineNumber(),
                                                    (null == locator) ? 0: locator.getColumnNumber());
    if(shouldThrow)
    {
      throw new XSLProcessorException(formattedMsg);
    }
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(String msg)
    throws SAXException
  {
    Locator locator = m_stylesheetLocatorStack.isEmpty()
                      ? null :
                        ((Locator)m_stylesheetLocatorStack.peek());
    String id = (null == locator)
                ? null
                  : (null == locator.getSystemId())
                    ? locator.getPublicId()
                      : locator.getSystemId();

    boolean shouldThrow = getProblemListener().problem(ProblemListener.XSLPROCESSOR,
                                                    ProblemListener.ERROR,
                                                    null, null, msg,
                                                    id,
                                                    (null == locator) ? 0: locator.getLineNumber(),
                                                    (null == locator) ? 0: locator.getColumnNumber());

    if(shouldThrow)
    {
      throw new XSLProcessorException("");
    }
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg)
    throws SAXException
  {
    error(null, null, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg, Object[] args)
    throws SAXException
  {
    error(null, null, msg, args);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg, Exception e)
    throws SAXException
  {
    error(msg, null, e);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg, Object args[], Exception e)
    throws SAXException
  {
    //msg  = (null == msg) ? XSLTErrorResources.ER_PROCESSOR_ERROR : msg;
    String formattedMsg = m_XSLMessages.createMessage(msg, args);
    Locator locator = m_stylesheetLocatorStack.isEmpty()
                      ? null :
                        ((Locator)m_stylesheetLocatorStack.peek());
    String id = (null == locator)
                ? null
                  : (null == locator.getSystemId())
                    ? locator.getPublicId()
                      : locator.getSystemId();

    boolean shouldThrow = getProblemListener().problem(ProblemListener.XSLPROCESSOR,
                                                    ProblemListener.ERROR,
                                                    null, null, formattedMsg,
                                                    id,
                                                    (null == locator) ? 0: locator.getLineNumber(),
                                                    (null == locator) ? 0: locator.getColumnNumber());

    if(shouldThrow)
    {
      if(e instanceof XSLProcessorException)
        throw ((XSLProcessorException)e);
      else
        throw new XSLProcessorException(formattedMsg, e);
    }
  }

  /**
    * <meta name="usage" content="internal"/>
  * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(Node styleNode, Node sourceNode, int msg)
    throws SAXException
  {
    error(styleNode, sourceNode, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(Node styleNode, Node sourceNode, int msg, Object args[])
    throws SAXException
  {
    String formattedMsg = m_XSLMessages.createMessage(msg, args);
    Locator locator = m_stylesheetLocatorStack.isEmpty()
                      ? null :
                        ((Locator)m_stylesheetLocatorStack.peek());
    String id = (null == locator)
                ? null
                  : (null == locator.getSystemId())
                    ? locator.getPublicId()
                      : locator.getSystemId();

    boolean shouldThrow = getProblemListener().problem(ProblemListener.XSLPROCESSOR,
                                                    ProblemListener.ERROR,
                                                    styleNode, sourceNode, formattedMsg,
                                                    id,
                                                    (null == locator) ? 0: locator.getLineNumber(),
                                                    (null == locator) ? 0: locator.getColumnNumber());

    if(shouldThrow)
    {
      throw new XSLProcessorException("");
    }
  }

  /**
   * Mark the time, so that displayDuration can later
   * display the elapse.
   */
  void pushTime(Object key)
  {
    if(null != key)
    {
      m_durationsTable.put(key, new Date());
    }
  }

  /**
   * Returns the duration since pushTime was called,
   * in milliseconds.
   */
  long popDuration(Object key)
  {
    long millisecondsDuration = 0;
    if(null != key)
    {
      Date date1 = (Date)m_durationsTable.get(key);
      Date date2 = new Date();
      millisecondsDuration = date2.getTime() - date1.getTime();
      m_durationsTable.remove(key);
    }
    return millisecondsDuration;
  }

  /**
   * Display the duration since pushTime was called.
   */
  protected void displayDuration(String info, Object key)
  {
    long millisecondsDuration = 0;
    if(null != key)
    {
      Date date1 = (Date)m_durationsTable.get(key);
      Date date2 = new Date();
      millisecondsDuration = date2.getTime() - date1.getTime();
      if(null != m_diagnosticsPrintWriter)
      {
        m_diagnosticsPrintWriter.println(info + " took " + millisecondsDuration + " milliseconds");
      }
      m_durationsTable.remove(key);
    }
  }

  /**
   * If this is set, diagnostics will be
   * written to the m_diagnosticsPrintWriter stream. If
   * the value is null, then diagnostics will be turned
   * off.
   */
  public void setDiagnosticsOutput(java.io.OutputStream out)
  {
    setDiagnosticsOutput(new PrintWriter(out));
  }

  /**
   * If this is set, diagnostics will be
   * written to the m_diagnosticsPrintWriter stream. If
   * the value is null, then diagnostics will be turned
   * off.
   */
  public void setDiagnosticsOutput(java.io.PrintWriter pw)
  {
    m_diagnosticsPrintWriter = pw;
    if(getProblemListener() instanceof ProblemListenerDefault)
    {
      ((ProblemListenerDefault)getProblemListener()).setDiagnosticsOutput(pw);
    }
  }

  /**
   * Bottleneck output of diagnostics.
   */
  protected void diag(String s)
  {
    if(null != m_diagnosticsPrintWriter)
    {
      m_diagnosticsPrintWriter.println(s);
    }
  }

  /**
   * If this is set to true, simple traces of
   * template calls are made.
   */
  public void setTraceTemplates(boolean b)
  {
    m_traceTemplates = b;
  }

  /**
   * If this is set to true, simple traces of
   * template calls are made.
   */
  public void setTraceSelect(boolean b)
  {
    m_traceSelects = b;
  }

  /**
   * If this is set to true, debug diagnostics about
   * template children as they are being constructed
   * will be written to the m_diagnosticsPrintWriter
   * stream.  diagnoseTemplateChildren is false by
   * default.
   */
  public void setTraceTemplateChildren(boolean b)
  {
    m_traceTemplateChildren = b;
  }

  /**
   * If the quietConflictWarnings property is set to
   * true, warnings about pattern conflicts won't be
   * printed to the diagnostics stream.
   * True by default.
   * @param b true if conflict warnings should be suppressed.
   */
  public void setQuietConflictWarnings(boolean b)
  {
    m_quietConflictWarnings = b;
  }

  /**
   * Print a trace of a selection being made.
   */
  protected void traceSelect(Element template, NodeList nl)
    throws SAXException
  {
    String msg = template.getNodeName()+": ";
    Attr attr = template.getAttributeNode(Constants.ATTRNAME_SELECT);
    if(null != attr)
    {
      msg += attr.getValue() + ", "+((null != nl) ? nl.getLength() : 0) + " selected";
    }
    else
    {
      msg += "*|text(), (default select), "+nl.getLength() + " selected";
    }

    attr = template.getAttributeNode(Constants.ATTRNAME_MODE);
    if(null != attr)
    {
      msg += ", mode = "+attr.getValue();
    }
    System.out.println(msg);
  }


  /**
   * Print a trace of a template that is being called, either by
   * a match, name, or as part of for-each.
   */
  protected void traceTemplate(Node templateNode)
    throws SAXException
  {
    if(Node.ELEMENT_NODE == templateNode.getNodeType())
    {
      Element template = (Element)templateNode;
      String msg;
      Attr attr = template.getAttributeNode(Constants.ATTRNAME_MATCH);
      if(null != attr)
      {
        msg = "Calling template for: " + attr.getValue();
      }
      else
      {
        attr = template.getAttributeNode(Constants.ATTRNAME_NAME);
        if(null != attr)
        {
          msg = "Calling named template, name = " + attr.getValue();
        }
        else
        {

          int xslToken = ((ElemTemplateElement)template).getXSLToken();

          if(Constants.ELEMNAME_FOREACH == xslToken)
          {
            attr = template.getAttributeNode(Constants.ATTRNAME_SELECT);
            if(null != attr)
            {
              msg = "Processing for-each, select = " + attr.getValue();
            }
            else
            {
              error(XSLTErrorResources.ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB); //"for-each must have either a match or name attribute");
              msg = null;
            }
          }
          else
          {
            error(XSLTErrorResources.ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB); //"templates must have either a match or name attribute");
            msg = null;
          }
        }
      }
      attr = template.getAttributeNode(Constants.ATTRNAME_MODE);
      if(null != attr)
      {
        msg += ", mode = "+attr.getValue();
      }
      System.out.println(msg);
    }
  }

  /**
   * Print some diagnostics about the current
   * template child.
   */
  protected void diagnoseTemplateChildren(
                                          Node templateChild,
                                          Node sourceNode)
  {
    if(m_traceTemplateChildren)
    {
      String templateChildTagName = templateChild.getNodeName();

      String xmlElemName = sourceNode.getNodeName();

      diag("source node: " + xmlElemName
           + ", template-node: "
           + templateChildTagName);
    }
  }

  //==========================================================
  // SECTION: Functions that create nodes in the result tree
  //==========================================================

  /**
   * Clone an element with or without children.
   * TODO: Fix or figure out node clone failure!
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  protected void cloneToResultTree(Stylesheet stylesheetTree, Node node,
                                   boolean shouldCloneWithChildren,
                                   boolean overrideStrip,
                                   boolean shouldCloneAttributes)
    throws SAXException,
           MalformedURLException,
           FileNotFoundException,
           IOException
  {
    boolean stripWhiteSpace = false;

    switch(node.getNodeType())
    {
    case Node.TEXT_NODE:
      {
        // If stripWhiteSpace is false, then take this as an override and
        // just preserve the space, otherwise use the XSL whitespace rules.
        if(!overrideStrip)
        {
          // stripWhiteSpace = isLiteral ? true : shouldStripSourceNode(node);
          stripWhiteSpace = false;
        }
        Text tx = (Text)node;
        String data = null;
        // System.out.println("stripWhiteSpace = "+stripWhiteSpace+", "+tx.getData());
        if(stripWhiteSpace)
        {
          if(!m_parserLiaison.isIgnorableWhitespace(tx))
          {
            data = getNormalizedText(tx);
            if((null != data) && isWhiteSpace( data ))
            {
              data = null;
            }
          }
        }
        else
        {
          Node parent = node.getParentNode();
          if(null != parent)
          {
            if( Node.DOCUMENT_NODE != parent.getNodeType())
            {
              data = getNormalizedText(tx);
              if((null != data) && (0 == data.length()))
              {
                data = null;
              }
            }
          }
          else
          {
            data = getNormalizedText(tx);
            if((null != data) && (0 == data.length()))
            {
              data = null;
            }
          }
        }

        if(null != data)
        {
          // TODO: Hack around the issue of comments next to literals.
          // This would be, when a comment is present, the whitespace
          // after the comment must be added to the literal.  The
          // parser should do this, but XML4J doesn't seem to.
          // <foo>some lit text
          //     <!-- comment -->
          //     </foo>
          // Loop through next siblings while they are comments, then,
          // if the node after that is a ignorable text node, append
          // it to the text node just added.
          if(m_parserLiaison.isIgnorableWhitespace(tx))
          {
            m_resultTreeHandler.ignorableWhitespace(data.toCharArray(), 0, data.length());
          }
          else
          {
            m_resultTreeHandler.characters(data.toCharArray(), 0, data.length());
          }
        }
      }
      break;
    case Node.DOCUMENT_NODE:
      // Can't clone a document, but refrain from throwing an error
      // so that copy-of will work
      break;
    case Node.ELEMENT_NODE:
      {
        AttributeList atts;
        if(shouldCloneAttributes)
        {
          copyAttributesToAttList( (Element)node, m_stylesheetRoot, m_pendingAttributes );
          copySourceNSAttrs(node, m_pendingAttributes);
        }
        m_resultTreeHandler.startElement (node.getNodeName());
      }
      break;
    case Node.CDATA_SECTION_NODE:
      {
        String data = ((CDATASection)node).getData();
        m_resultTreeHandler.cdata(data.toCharArray(), 0, data.length());
      }
      break;
    case Node.ATTRIBUTE_NODE:
      {
        addResultAttribute(m_pendingAttributes, ((Attr)node).getName(),
                           ((Attr)node).getValue());
      }
      break;
    case Node.COMMENT_NODE:
      {
        m_resultTreeHandler.comment(((Comment)node).getData());
      }
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      {
        error(null, node, XSLTErrorResources.ER_NO_CLONE_OF_DOCUMENT_FRAG); //"No clone of a document fragment!");
      }
      break;
    case Node.ENTITY_REFERENCE_NODE:
      {
        EntityReference er = (EntityReference)node;
        m_resultTreeHandler.entityReference(er.getNodeName());
      }
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      {
        ProcessingInstruction pi = (ProcessingInstruction)node;
        m_resultTreeHandler.processingInstruction(pi.getTarget(), pi.getData());
      }
      break;
    default:
      error(XSLTErrorResources.ER_CANT_CREATE_ITEM, new Object[] {node.getNodeName()}); //"Can not create item in result tree: "+node.getNodeName());
    }

  } // end cloneToResultTree function

  /**
   * Given a stylesheet element, create a result tree fragment from it's
   * contents.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   * @param stylesheetTree The stylesheet object that holds the fragment.
   * @param templateParent The template element that holds the fragment.
   * @param sourceTree The source tree document context.
   * @param sourceNode The current source context node.
   * @param mode The mode under which the template is operating.
   * @return An object that represents the result tree fragment.
   */
  DocumentFragment createResultTreeFrag(
                                        Stylesheet stylesheetTree,
                                        ElemTemplateElement templateParent,
                                        Node sourceTree,
                                        Node sourceNode, QName mode)
    throws SAXException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException
  {
    // flushPending();


    DocumentFragment resultFragment = createDocFrag();

    DocumentHandler savedFListener = this.m_flistener;
    boolean savedMustFlushStartDoc = this.m_mustFlushStartDoc;
    boolean savedPendingStartDoc = this.m_pendingStartDoc;
    String savedPendingName = this.m_pendingElementName;
    this.m_pendingElementName = null;
    MutableAttrListImpl savedPendingAttributes = this.m_pendingAttributes;
    this.m_pendingAttributes = new MutableAttrListImpl();

    m_flistener = new FormatterToDOM(getDOMFactory(), resultFragment);

    templateParent.executeChildren(this, sourceTree, sourceNode, mode);

    // flushPending();
    this.m_pendingElementName = savedPendingName;
    this.m_pendingAttributes = savedPendingAttributes;
    this.m_mustFlushStartDoc = savedMustFlushStartDoc;
    this.m_pendingStartDoc = savedPendingStartDoc;
    this.m_flistener = savedFListener;

    return resultFragment;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Write the children of a stylesheet element to the given listener.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   * @param stylesheetTree The stylesheet object that holds the fragment.
   * @param templateParent The template element that holds the fragment.
   * @param sourceTree The source tree document context.
   * @param sourceNode The current source context node.
   * @param mode The mode under which the template is operating.
   * @return An object that represents the result tree fragment.
   */
  public void writeChildren(
                            DocumentHandler flistener,
                            Stylesheet stylesheetTree,
                            ElemTemplateElement templateParent,
                            Node sourceTree,
                            Node sourceNode, QName mode)
    throws SAXException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException
  {
    flushPending();

    DocumentHandler savedFormatterListener = m_flistener;
    String savedPendingName = m_pendingElementName;

    m_pendingElementName = null;
    MutableAttrListImpl savedPendingAttributes = m_pendingAttributes;

    m_pendingAttributes = new MutableAttrListImpl();
    m_flistener = flistener;

    templateParent.executeChildren(this, sourceTree, sourceNode, mode);

    flushPending();
    m_flistener = savedFormatterListener;
    m_pendingElementName = savedPendingName;
    m_pendingAttributes = savedPendingAttributes;
  }

  /**
   * Given a result tree fragment, walk the tree and
   * output it to the result stream.
   */
  public void outputResultTreeFragment(XObject obj, XPathSupport support)
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    DocumentFragment docFrag = obj.rtree(support);
    NodeList nl = docFrag.getChildNodes();
    int nChildren = nl.getLength();
    TreeWalker tw = new TreeWalker(m_flistener);
    for(int i = 0; i < nChildren; i++)
    {
      flushPending(); // I think.
      tw.traverse(nl.item(i));
    }
  }

  //==========================================================
  // SECTION: Function to do with attribute handling
  //==========================================================

  /**
   * Tell if a given element name should output it's text
   * as cdata.
   * TODO: This is handling the cdata elems as strings instead
   * of qnames... this needs to be fixed.
   */
  boolean isCDataResultElem(String elementName)
  {
    boolean is = false;
    QName[] cdataElems = m_stylesheetRoot.m_cdataSectionElems;
    if(null != cdataElems)
    {
      String elemNS = null;
      String elemLocalName = null;
      int indexOfNSSep = elementName.indexOf(':');
      if(indexOfNSSep > 0)
      {
        String prefix = elementName.substring(0, indexOfNSSep);
        if(prefix.equals("xml"))
        {
          elemNS = QName.S_XMLNAMESPACEURI;
        }
        else
        {
          elemNS = getResultNamespaceForPrefix(prefix);
        }
        if(null == elemNS)
        {
          throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_PREFIX_MUST_RESOLVE, new Object[]{prefix}));//"Prefix must resolve to a namespace: "+prefix);
        }
      }
      elemLocalName = (indexOfNSSep < 0) ? elementName : elementName.substring(indexOfNSSep+1);
      int n = cdataElems.length;
      for(int i = 0; i < n; i++)
      {
        // This needs to be a qname!
        QName qname = cdataElems[i];
        is = qname.equals(elemNS, elemLocalName);
        if(is)
          break;
      }
    }
    return is;
  }

  /**
   * Tell if a qualified name equals the current result tree name.
   */
  boolean qnameEqualsResultElemName(QName qname, String elementName)
  {
    String elemNS = null;
    String elemLocalName = null;
    int indexOfNSSep = elementName.indexOf(':');
    if(indexOfNSSep > 0)
    {
      String prefix = elementName.substring(0, indexOfNSSep);
      if(prefix.equals("xml"))
      {
        elemNS = QName.S_XMLNAMESPACEURI;
      }
      else
      {
        elemNS = getResultNamespaceForPrefix(prefix);
      }
      if(null == elemNS)
      {
        throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_PREFIX_MUST_RESOLVE, new Object[]{prefix}));//"Prefix must resolve to a namespace: "+prefix);
      }
    }
    elemLocalName = (indexOfNSSep < 0) ? elementName : elementName.substring(indexOfNSSep+1);
    return qname.equals(elemNS, elemLocalName);
  }


  /**
   * Given a prefix, return the namespace,
   */
  String getResultNamespaceForPrefix(String prefix)
  {
    int type;
    String namespace = null;

    int nParents = m_resultNameSpaces.size();
    for(int i = (nParents - 1); i >= 0 && (null == namespace); i--)
    {
      ResultNameSpace rns = (ResultNameSpace)m_resultNameSpaces.elementAt(i);
      if (m_emptyNamespace != rns)
      {
        while(null != rns)
        {
          if(rns.m_prefix.equals(prefix))
          {
            namespace = rns.m_uri;
            break;
          }
          rns = rns.m_next;
        }
      }
    }
    return namespace;
  }

  /**
   * This should probably be in the XMLParserLiaison interface.
   */
  String getResultPrefixForNamespace(String namespace)
  {
    int type;
    String prefix = null;

    int nParents = m_resultNameSpaces.size();
    for(int i = (nParents - 1); i >= 0 && (null == prefix); i--)
    {
      ResultNameSpace rns = (ResultNameSpace)m_resultNameSpaces.elementAt(i);
      if (m_emptyNamespace != rns)
      {
        while(null != rns)
        {
          if(rns.m_uri.equals(namespace))
          {
            prefix = rns.m_prefix;
            break;
          }
          rns = rns.m_next;
        }
      }
    }
    return prefix;
  }

  /**
   * This should probably be in the XMLParserLiaison interface.
   */
  private String getPrefixForNamespace(String namespace, Element namespaceContext)
  {
    int type;
    Node parent = namespaceContext;
    String prefix = null;
    while ((null != parent) && (null == prefix)
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
            String namespaceOfPrefix = attr.getNodeValue();
            if((null != namespaceOfPrefix) && namespaceOfPrefix.equals(namespace))
            {
              prefix = isPrefix ? aname.substring(index+1) : "";
            }
          }
        }
      }

      parent = m_parserLiaison.getParentOfNode(parent);
    }
    return prefix;
  }

  /**
   * Copy <KBD>xmlns:</KBD> attributes in if not already in scope.
   */
  void copySourceNSAttrs(Node src, MutableAttrListImpl destination)
  {
    int type;
    // Vector nameValues = null;
    // Vector alreadyProcessedPrefixes = null;
    Node parent = src;
    while (parent != null
           && ((type = parent.getNodeType()) == Node.ELEMENT_NODE
               || (type == Node.ENTITY_REFERENCE_NODE)))
    {
      if (type == Node.ELEMENT_NODE)
      {
        NamedNodeMap nnm = parent.getAttributes();
        int nAttrs = nnm.getLength();
        for (int i = 0;  i < nAttrs;  i++)
        {
          Node attr = nnm.item(i);
          String aname = attr.getNodeName();
          boolean isPrefix = aname.startsWith("xmlns:");
          if (isPrefix || aname.equals("xmlns"))
          {
            String prefix = isPrefix ? aname.substring(6) : "";
            String desturi = getResultNamespaceForPrefix(prefix);
            String srcURI = attr.getNodeValue();
            if(!srcURI.equalsIgnoreCase(desturi))
            {
              addResultAttribute(m_pendingAttributes, aname, srcURI);
            }
          }
        }
      }
      parent = parent.getParentNode();
    }
  }

  /**
   * Given an element, return an attribute value in
   * the form of a string.
   * @param el The element from where to get the attribute.
   * @param key The name of the attribute.
   * @param contextNode The context to evaluate the
   * attribute value template.
   * @return Attribute value.
   */
  final static String getAttrVal(Element el, String key, Node contextNode)
  {
    Attr a = el.getAttributeNode(key);
    return (null == a) ? null : a.getValue();
  }

  /**
   * Given an element, return an attribute value in
   * the form of a string.
   */
  static final String getAttrVal(Element el, String key)
  {
    Attr a = el.getAttributeNode(key);
    return null == a ? null : a.getValue();
  }


  /**
   * Copy an attribute to the created output element, executing
   * attribute templates as need be, and processing the xsl:use
   * attribute.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  protected void copyAttributeToTarget( Attr attr,
                                        Node contextNode,
                                        Stylesheet stylesheetTree,
                                        MutableAttrListImpl attrList,
                                        Element namespaceContext
                                        )
    throws SAXException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException
  {
    String attrName = attr.getName().trim();

    String stringedValue = attr.getValue();

    // evaluateAttrVal might return a null value if the template expression
    // did not turn up a result, in which case I'm going to not add the
    // attribute.
    // TODO: Find out about empty attribute template expression handling.
    if(null != stringedValue)
    {
      if((attrName.equals("xmlns") || attrName.startsWith("xmlns:"))
         && stringedValue.startsWith("quote:"))
      {
        stringedValue = stringedValue.substring(6);
      }
      addResultAttribute(attrList, attrName, stringedValue);
    }
  } // end copyAttributeToTarget method

  /**
   * Copy the attributes from the XSL element to the created
   * output element, executing attribute templates and
   * processing the xsl:use attribute as need be.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  protected void copyAttributesToAttList(Element contextNode,
                                         Stylesheet stylesheetTree,
                                         MutableAttrListImpl attList)
    throws SAXException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException
  {
    NamedNodeMap attributes = contextNode.getAttributes();
    int nAttributes = (null != attributes) ? attributes.getLength() : 0;
    String attrSetUseVal = null;
    for(int i = 0; i < nAttributes; i++)
    {
      Attr attr = (Attr)attributes.item(i);
      if(m_parserLiaison.getExpandedAttributeName(attr).equals(m_stylesheetRoot.m_XSLNameSpaceURL+":use"))
      {
        attrSetUseVal = attr.getValue();
      }
      else
      {
        copyAttributeToTarget( attr, contextNode, stylesheetTree,
                               attList, contextNode);
      }
    } // end for(long i = 0; i < nAttributes; i++)

  } // end copyAttributesToTarget method


  //==========================================================
  // SECTION: Functions for controling whitespace
  //==========================================================

  /**
   * <meta name="usage" content="internal"/>
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
    if((null != m_stylesheetRoot) &&
       ((null != m_stylesheetRoot.m_whitespacePreservingElements) ||
       (null != m_stylesheetRoot.m_whitespaceStrippingElements))
       )
    {
    boolean strip = false; // return value
    int type = textNode.getNodeType();
    if((Node.TEXT_NODE == type) || (Node.CDATA_SECTION_NODE == type))
    {
      if(!m_parserLiaison.isIgnorableWhitespace((Text)textNode))
      {
        String data = ((Text)textNode).getData();
        if(null == data)
        {
          return true;
        }
        else if(!isWhiteSpace( data ))
        {
          return false;
        }
      }

      Node parent = m_parserLiaison.getParentOfNode(textNode);
      while(null != parent)
      {
        if(parent.getNodeType() == Node.ELEMENT_NODE)
        {
          Element parentElem = (Element)parent;

          /*
          Attr attr = parentElem.getAttributeNode("xml:space");
          if(null != attr)
          {
            String xmlSpaceVal = attr.getValue();

            if(xmlSpaceVal.equals("preserve"))
            {
              strip = false;
            }
            else if(xmlSpaceVal.equals("default"))
            {
              strip = true;
            }
            else
            {
              error(XSLTErrorResources.ER_XMLSPACE_ILLEGAL_VALUE, new Object[] {xmlSpaceVal}); //"xml:space in the source XML has an illegal value: "+xmlSpaceVal);
            }
            break;
          }
          */
          double highPreserveScore = XPath.MATCH_SCORE_NONE;
          double highStripScore = XPath.MATCH_SCORE_NONE;

          if(null != m_stylesheetRoot)
          {
            if(null != m_stylesheetRoot.m_whitespacePreservingElements)
            {
              int nTests = m_stylesheetRoot.m_whitespacePreservingElements.size();
              for(int i = 0; i < nTests; i++)
              {
                XPath matchPat = (XPath)m_stylesheetRoot.m_whitespacePreservingElements.elementAt(i);
                double score = matchPat.getMatchScore(getExecContext(), parent);
                if(score > highPreserveScore)
                  highPreserveScore = score;
              }
            }
            if(null != m_stylesheetRoot.m_whitespaceStrippingElements)
            {
              int nTests = m_stylesheetRoot.m_whitespaceStrippingElements.size();
              for(int i = 0; i < nTests; i++)
              {
                XPath matchPat = (XPath)m_stylesheetRoot.m_whitespaceStrippingElements.elementAt(i);
                double score = matchPat.getMatchScore(getExecContext(), parent);
                if(score > highStripScore)
                  highStripScore = score;
              }
            }
          }
          if((highPreserveScore > XPath.MATCH_SCORE_NONE) || (highStripScore > XPath.MATCH_SCORE_NONE))
          {
            if(highPreserveScore > highStripScore)
            {
              strip = false;
            }
            else if(highStripScore > highPreserveScore)
            {
              strip = true;
            }
            else
            {
              warn(XSLTErrorResources.WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE); //"Match conflict between xsl:strip-space and xsl:preserve-space");
            }
            break;
          }
        }
        parent = parent.getParentNode();
      }
    }
    return strip;
    }
    else return false;
  }

  /**
   * Returns whether the specified <var>ch</var> conforms to the XML 1.0 definition
   * of whitespace.  Refer to <A href="http://www.w3.org/TR/1998/REC-xml-19980210#NT-S">
   * the definition of <CODE>S</CODE></A> for details.
   * @param   ch      Character to check as XML whitespace.
   * @return          =true if <var>ch</var> is XML whitespace; otherwise =false.
   */
  static boolean isSpace(char ch)
  {
    return (ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA);
  }

  /**
   * Tell if the string is whitespace.
   * @param   string      String to be tested.
   * @return true if the string is all whitespace, false otherwise
   */
  boolean isWhiteSpace(String string)
  {
    final int len = string.length();

    for(int s = 0;  s < len;  s++)
    {
      if (!isSpace(string.charAt(s)))
	  {
		return false;
      }
    }
    return true;
  }

  /**
   * (Code stolen and modified from XML4J)
   * Conditionally trim all leading and trailing whitespace in the specified String.
   * All strings of white space are
   * replaced by a single space character (#x20), except spaces after punctuation which
   * receive double spaces if doublePunctuationSpaces is true.
   * This function may be useful to a formatter, but to get first class
   * results, the formatter should probably do it's own white space handling
   * based on the semantics of the formatting object.
   * @param   string      String to be trimmed.
   * @param   trimHead    Trim leading whitespace?
   * @param   trimTail    Trim trailing whitespace?
   * @param   doublePunctuationSpaces    Use double spaces for punctuation?
   * @return              The trimmed string.
   */
  String fixWhiteSpace(String string,
                              boolean trimHead,
                              boolean trimTail,
                              boolean doublePunctuationSpaces)
  {
    char[] buf = string.toCharArray();
    int len = buf.length;
    boolean edit = false;
    int s;
    for (s = 0;  s < len;  s++)
    {
      if (isSpace(buf[s]))
      {
        break;
      }
    }
    /* replace S to ' '. and ' '+ -> single ' '. */
    int d = s;
    boolean pres = false;
    for ( ;  s < len;  s ++)
    {
      char c = buf[s];
      if (isSpace(c))
      {
        if (!pres)
        {
          if (' ' != c)
          {
            edit = true;
          }
          buf[d++] = ' ';
          if(doublePunctuationSpaces && (s != 0))
          {
            char prevChar = buf[s-1];
            if(!((prevChar == '.') || (prevChar == '!') || (prevChar == '?')))
            {
              pres = true;
            }
          }
          else
          {
            pres = true;
          }
        }
        else
        {
          edit = true;
          pres = true;
        }
      }
      else
      {
        buf[d++] = c;
        pres = false;
      }
    }
    if (trimTail && 1 <= d && ' ' == buf[d-1])
    {
      edit = true;
      d --;
    }
    int start = 0;
    if (trimHead && 0 < d && ' ' == buf[0])
    {
      edit = true;
      start ++;
    }
    return edit ? new String(buf, start, d-start) : string;
  }

  /**
   * Trim all leading and trailing whitespace in the specified String.
   * @param   string      String to be trimmed.
   * @return              The trimmed string.
   */
  String trim(String string)
  {
    char[] buf = string.toCharArray();
    int len = buf.length;
    boolean edit = false;

    int s = 0, e = 0;
    if(len > 0)
    {
      for (s = 0;  s < len;  s++)
      {
        if (!isSpace(buf[s]))
        {
          break;
        }
      }
      for (e = len;  e > s;  e--)
      {
        if (!isSpace(buf[e-1]))
        {
          break;
        }
      }
      edit = (s > 0) || (e < len);
    }
    return edit ? new String(buf, s, e-s) : string;
  }

  /**
   * Tell how long the string will be if we trim the whitespace.
   * @param   string      String to be trimmed.
   * @return              The trimmed string.
   */
  int trimLen(String string)
  {
    char[] buf = string.toCharArray();
    int len = buf.length;

    int s, e;
    for(s = 0;  s < len;  s++)
    {
      if (!isSpace(buf[s]))
      {
        break;
      }
    }
    for (e = len; e > s; e--)
    {
      if (!isSpace(buf[e-1]))
      {
        break;
      }
    }
    return e-s;
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Normalize the linefeeds and/or carriage returns to
   * be consistently 0x0D 0x0A.  This should almost
   * certainly be done somewhere else... like in the
   * XML parser.
   */
  String getNormalizedText(Text tx)
  {
    // Hope the Text node implementation normalizes stuff.
    return tx.getData();
  }

  //==========================================================
  // SECTION: Support functions in fairly random order
  //==========================================================

  /**
   * Get the execution context for XPath.
   */
  public XPathSupport getExecContext()
  {
    return m_parserLiaison;
  }

  /**
   * Set the execution context for XPath.
   */
  public void setExecContext(XMLParserLiaison liaison)
  {
    m_parserLiaison = liaison;
    liaison.setEnvSupport(this);
  }

  /**
   * Get the XML Parser Liaison that this processor uses.
   */
  public XMLParserLiaison getXMLProcessorLiaison()
  {
    return m_parserLiaison;
  }

  /**
   * Convenience function to create an XString.
   * @param s A valid string.
   * @return An XString object.
   */
  public XString createXString(String s)
  {
    return new XString(s);
  }

  /**
   * Convenience function to create an XObject.
   * @param o Any java object.
   * @return An XObject object.
   */
  public XObject createXObject(Object o)
  {
    return new XObject(o);
  }

  /**
   * Convenience function to create an XNumber.
   * @param d Any double number.
   * @return An XNumber object.
   */
  public XNumber createXNumber(double d)
  {
    return new XNumber(d);
  }

  /**
   * Convenience function to create an XBoolean.
   * @param b boolean value.
   * @return An XBoolean object.
   */
  public XBoolean createXBoolean(boolean b)
  {
    return new XBoolean(b);
  }

  /**
   * Convenience function to create an XNodeSet.
   * @param nl A NodeList object.
   * @return An XNodeSet object.
   */
  public XNodeSet createXNodeSet(NodeList nl)
  {
    return new XNodeSet(nl);
  }

  /**
   * Convenience function to create an XNodeSet from a node.
   * @param n A DOM node.
   * @return An XNodeSet object.
   */
  public XNodeSet createXNodeSet(Node n)
  {
    return new XNodeSet(n);
  }

  /**
   * Convenience function to create an XNull.
   * @return An XNull object.
   */
  public XNull createXNull()
  {
    return new XNull();
  }

  /**
   * Given a valid element key, return the corresponding node list.
   */
  public NodeList getNodeSetByKey(Node doc, String name,
                                  String ref, PrefixResolver nscontext)
    throws org.xml.sax.SAXException
  {
    // Should this call the root or the current stylesheet?
    NodeList nl = m_stylesheetRoot.getNodeSetByKey(this, doc, name, ref, nscontext);
    if(null == nl)
      error(XSLTErrorResources.ER_NO_XSLKEY_DECLARATION, new Object[] {name}); //"There is no xsl:key declaration for '"+name+"'!");
    return nl;
  }

  /**
   * Given a valid element id, return the corresponding element.
   */
  Element getElementByID(String id, Document doc)
  {
    return m_parserLiaison.getElementByID(id, doc);
  }

  /**
   * Given a name, locate a variable in the current context, and return
   * the Object.
   */
  public XObject getVariable(QName qname)
    throws org.xml.sax.SAXException
  {
    Object obj = getVarStack().getVariable(qname);
    if((null != obj) && !(obj instanceof XObject))
    {
      obj = new XObject(obj);
    }
    return (XObject)obj;
  }

  /**
   * Get an XLocator provider keyed by node.  This get's
   * the association based on the root of the tree that the
   * node is parented by.
   */
  public XLocator getXLocatorFromNode(Node node)
  {
    return m_parserLiaison.getXLocatorFromNode(node);
  }

  /**
   * Associate an XLocator provider to a node.  This makes
   * the association based on the root of the tree that the
   * node is parented by.
   */
  public void associateXLocatorToNode(Node node, XLocator xlocator)
  {
    m_parserLiaison.associateXLocatorToNode(node, xlocator);
  }

  /**
   * Create a document fragment.  This function may return null.
   */
  DocumentFragment createDocFrag()
  {
    return new ResultTreeFrag(getDOMFactory(), m_parserLiaison);
  }

  /**
   * Given a document, get the default stylesheet URI from the
   * xsl:stylesheet PI.  However, this will only get you the
   * first URL, and there may be many.
   * @deprecated
   */
  public String getStyleSheetURIfromDoc(Node sourceTree)
  {
    String stylesheetURI = null;
    for(Node child=sourceTree.getFirstChild(); null != child; child=child.getNextSibling())
    {
      if(Node.PROCESSING_INSTRUCTION_NODE == child.getNodeType())
      {
        ProcessingInstruction pi = (ProcessingInstruction)child;
        if(pi.getNodeName().equals("xml-stylesheet")
           || pi.getNodeName().equals("xml:stylesheet"))
        {
          boolean isOK = true;
          StringTokenizer tokenizer = new StringTokenizer(pi.getNodeValue(), " \t=");
          while(tokenizer.hasMoreTokens())
          {
            if(tokenizer.nextToken().equals("type"))
            {
              String typeVal = tokenizer.nextToken();
              typeVal = typeVal.substring(1, typeVal.length()-1);
              if(!typeVal.equals("text/xsl"))
              {
                isOK = false;
              }
            }
          }

          if(isOK)
          {
            tokenizer = new StringTokenizer(pi.getNodeValue(), " \t=");
            while(tokenizer.hasMoreTokens())
            {
              if(tokenizer.nextToken().equals("href"))
              {
                stylesheetURI = tokenizer.nextToken();
                stylesheetURI = stylesheetURI.substring(1, stylesheetURI.length()-1);
              }
            }
            break;
          }
        }
      }
    } // end for(int i = 0; i < nNodes; i++)
    return stylesheetURI;
  }

  /**
   * Push a top-level stylesheet parameter.  This value can
   * be evaluated via xsl:param-variable.
   * @param key The name of the param.
   * @param expression An expression that will be evaluated.
   */
  public void setStylesheetParam(String key, String expression)
  {
    QName qname = new QName(key, m_parserLiaison.getNamespaceContext());
    m_topLevelParams.addElement(new Arg(qname, expression, true));
  }

  /**
   * Push a top-level stylesheet parameter.  This value can
   * be evaluated via xsl:param-variable.
   * @param key The name of the param.
   * @param value An XObject that will be used.
   */
  public void setStylesheetParam(String key, XObject value)
  {
    QName qname = new QName(key, m_parserLiaison.getNamespaceContext());
    m_topLevelParams.addElement(new Arg(qname, value));
  }

  /**
   * Resolve the params that were pushed by the caller.
   */
  void resolveTopLevelParams()
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException,
    SAXException
  {
    m_stylesheetRoot.pushTopLevelVariables(m_topLevelParams, this);
    getVarStack().markGlobalStackFrame();
  }

  /**
   * Returns the current input node that is being
   * processed.
   */
  public Node getSourceNode()
  {
    return m_currentNode;
  }

  /**
   * Returns the current input node that is being
   * processed.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public Node getSourceNode(String selectPattern, PrefixResolver namespaceContext)
    throws org.xml.sax.SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    XObject xobj = m_stylesheetRoot.evalXPathStr(getExecContext(), selectPattern, m_currentNode, namespaceContext);
    NodeList sourceNodes = xobj.nodeset();
    return ((null != sourceNodes) && (sourceNodes.getLength() > 0)) ?
           sourceNodes.item(0) : null;
  }

  /**
   * Reset the current element state
   */
  protected final void resetCurrentState(Node xmlNode)
  {
    m_currentNode = xmlNode;
  }


  /**
   * Given a tag name, an attribute name, and
   * an attribute value, do a very crude recursive
   * search and locate the first match.
   */
  protected Element findElementByAttribute(
                                           Node elem,
                                           String targetElementName,
                                           String targetAttributeName,
                                           String targetAttributeValue)
  {
    Element theFoundElement = null;
    String tagName = elem.getNodeName();

    if((null == targetElementName) || tagName.equals(targetElementName))
    {
      NamedNodeMap attributes = elem.getAttributes();
      try
      {
        int nAttributes = (null != attributes) ? attributes.getLength() : 0;
        for(int i = 0; i < nAttributes; i++)
        {
          Attr attr =
                     (Attr)attributes.item(i);
          String attrName = attr.getName();

          if((null != attrName) &&
             attrName.equals(targetAttributeName))
          {
            String attrVal = attr.getValue();
            if(attrVal.equals(targetAttributeValue))
            {
              theFoundElement = (Element)elem;
              break;
            }
          }
        }
      }
      catch(DOMException e)
      {
      }
    }
    if(null == theFoundElement)
    {
      for(Node childNode=elem.getFirstChild(); childNode != null; childNode=childNode.getNextSibling())
      {
        if (childNode.getNodeType() == Node.ELEMENT_NODE)
        {
          String childName = childNode.getNodeName();
          if(null != childName)
          {
            theFoundElement = findElementByAttribute(
                                                     childNode,
                                                     targetElementName,
                                                     targetAttributeName,
                                                     targetAttributeValue);

            if(null != theFoundElement)
            {
              break;
            }
          }
        }
      }
    }
    return theFoundElement;
  }




  /**
  * Do everything possible to get a good URL from a string.
  */
  /**
   * Take a user string and try and parse XML, and also return
   * the url.
   */
  URL getURLFromString(String urlString)
    throws MalformedURLException
  {
    URL url = null;
    try
    {
      url = new URL(urlString);
    }
    catch (MalformedURLException e)
    {
      try
      {
        String lastPart = new File (urlString).getAbsolutePath ();
        // Hack
        // if((lastPart.charAt(0) == '/') && (lastPart.charAt(2) == ':'))
        //  lastPart = lastPart.substring(1, lastPart.length() - 1);

        String fullpath = "file:" + lastPart;
        url = new URL(fullpath);
      }
      catch (MalformedURLException e2)
      {
        diag(XSLMessages.createMessage(XSLTErrorResources.ER_CANT_CREATE_URL, new Object[] {urlString})); //"Error! Cannot create url for: " + urlString);
        throw e2;
      }
    }
    return url;
  }

  /**
   * Get the current DocumentHandler.
   * This is the same object as the FormatterListener.
   */
  public DocumentHandler getDocumentHandler()
  {
    return m_flistener;
  }

  /**
   * Set the current DocumentHandler.  The object
   * set is the same object as the FormatterListener.
   */
  public void setDocumentHandler(DocumentHandler listener)
  {
    m_flistener = listener;
  }


  /**
   * Get the current formatter listener.
   */
  public DocumentHandler getFormatterListener()
  {
    return m_flistener;
  }

  /**
   * Set the current formatter listener.
   */
  public void setFormatterListener(DocumentHandler flistener)
  {
    m_flistener = flistener;
  }

  /**
   * Set a DOM document factory, primarily for creating result
   * tree fragments.
   */
  public void setDOMFactory(Document doc)
  {
    getXMLProcessorLiaison().setDOMFactory(doc);
  }


  /**
   * Get a DOM document, primarily for creating result
   * tree fragments.
   */
  public Document getDOMFactory()
  {
    return getXMLProcessorLiaison().getDOMFactory();
  }

  /**
   * Set the problem listener property.
   * The XSL class can have a single listener that can be informed
   * of errors and warnings, and can normally control if an exception
   * is thrown or not (or the problem listeners can throw their
   * own RuntimeExceptions).
   * @param l A ProblemListener interface.
   */
  public void setProblemListener(ProblemListener l)
  {
    m_parserLiaison.setProblemListener(l);
  }

  /**
   * Get the problem listener property.
   * The XSL class can have a single listener that can be informed
   * of errors and warnings, and can normally control if an exception
   * is thrown or not (or the problem listeners can throw their
   * own RuntimeExceptions).
   * @return A ProblemListener interface.
   */
  public ProblemListener getProblemListener()
  {
    return m_parserLiaison.getProblemListener();
  }

  private FormatterToDOM m_sourceTreeHandler = null;

  private java.io.OutputStream m_outputStream = null;

  /**
   * Set the output stream that will be used.
   * (This is only used, for the time being, when the processor is
   * being used as a SAX Document handler!)
   */
  public void setOutputStream(java.io.OutputStream os)
  {
    m_outputStream = os;
  }

  /**
   * Implement the setDocumentLocator event.
   */
  public void setDocumentLocator (Locator locator)
  {
  }

  /**
   * Implement the startDocument event.
   */
  public void startDocument ()
    throws SAXException
  {
    ErrorHandler errHandler = m_parserLiaison.getErrorHandler();
    
    // Force a XercesLiaison for right now, since we can't 
    // do the DTM with SAX right now.
    XMLParserLiaison liaison = new org.apache.xalan.xpath.xdom.XercesLiaison(errHandler);
    liaison.copyFromOtherLiaison((XMLParserLiaisonDefault)m_parserLiaison);
    setExecContext(liaison);
    
    Document sourceTree = m_parserLiaison.createDocument();
    m_sourceTreeHandler = new FormatterToDOM(sourceTree);
    resetCurrentState(sourceTree);
    m_rootDoc = sourceTree;
    // StylesheetRoot stylesheet = m_stylesheetRoot;
    // reset();
    try
    {
      getVarStack().pushContextMarker();
      resolveTopLevelParams();
    }
    catch(Exception e)
    {
      throw new SAXException(e.getMessage(), e);
    }
    // m_stylesheetRoot = stylesheet;
    m_sourceTreeHandler.startDocument();
  }

  /**
   * Implement the endDocument event.
   */
  public void endDocument ()
    throws SAXException
  {
    m_sourceTreeHandler.endDocument();

    try
    {
      XSLTResultTarget result = (null == m_outputStream) ?
                                new XSLTResultTarget(m_flistener) :
                                new XSLTResultTarget(m_outputStream);

      m_stylesheetRoot.process(this, m_sourceTreeHandler.getRootNode(),
                               result);
    }
    catch(Exception e)
    {
      if(!(e instanceof SAXException))
      {
        throw new SAXException(e);
      }
      else
      {
        throw (SAXException)e;
      }
    }
    m_sourceTreeHandler = null;
  }

  /**
   * Implement the startElement event.
   */
  public void startElement (String name, AttributeList atts)
    throws SAXException
  {
    m_sourceTreeHandler.startElement(name, atts);
  }

  /**
   * Implement the endElement event.
   */
  public void endElement (String name)
    throws SAXException
  {
    m_sourceTreeHandler.endElement(name);
  }
  
  private boolean m_isCData = false;
  
  /**
   * Report the start of a CDATA section.
   *
   * <p>The contents of the CDATA section will be reported through
   * the regular {@link org.xml.sax.ContentHandler#characters
   * characters} event.</p>
   *
   * @exception SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA ()
    throws SAXException
  {
    m_isCData = true;
  }
  
  /**
   * Report the end of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA ()
    throws SAXException
  {
    m_isCData = false;
  }

  /**
   * Implement the characters event.
   */
  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    if(m_isCData)
    {
      m_sourceTreeHandler.cdata(ch, start, length);
    }
    else
    {
      m_sourceTreeHandler.characters(ch, start, length);
    }
  }

  /**
   * Implement the characters event.
   */
  public void charactersRaw (char ch[], int start, int length)
    throws SAXException
  {
    m_sourceTreeHandler.charactersRaw(ch, start, length);
  }

  /**
   * Implement the ignorableWhitespace event.
   */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    m_sourceTreeHandler.charactersRaw(ch, start, length);
  }

  /**
   * Implement the processingInstruction event.
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    m_sourceTreeHandler.processingInstruction(target, data);
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * <p>This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).</p>
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception SAXException The application may raise an exception.
   */
  public void comment (char ch[], int start, int length)
    throws SAXException
  {
    m_sourceTreeHandler.comment(ch, start, length);
  }
      
  /**
   * Report the beginning of an entity.
   *
   * <p>The start and end of the document entity are not reported.
   * The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.</p>
   *
   * <p>Note that skipped entities will be reported through the
   * {@link org.xml.sax.ContentHandler#skippedEntity skippedEntity}
   * event, which is part of the ContentHandler interface.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @exception SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity (String name)
    throws SAXException
  {
    m_sourceTreeHandler.startEntity(name);
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity (String name)
    throws SAXException
  {
    m_sourceTreeHandler.endEntity(name);
  }
  
  /**
   * Report the start of DTD declarations, if any.
   *
   * <p>Any declarations are assumed to be in the internal subset
   * unless otherwise indicated by a {@link #startEntity startEntity}
   * event.</p>
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @exception SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD (String name, String publicId,
                                 String systemId)
    throws SAXException
  {
  }


  /**
   * Report the end of DTD declarations.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD ()
    throws SAXException
  {
  }
    
  /**
   * An exception for that occurs when a given stylesheet
   * goes into an infinite loop.
   */
  class XSLInfiniteLoopException extends Error
  {
    XSLInfiniteLoopException()
    {
      super();
    }

    public String getMessage()
    {
      return "Processing Terminated.";
    }
  } // end XSLInfiniteLoopException class definition

  /**
   * The StackGuard class guard against infinite loops.
   */
  class StackGuard
  {
    Node m_xslRule;
    Node m_sourceXML;

    java.util.Stack stack = new java.util.Stack();

    public StackGuard()
    {
    }

    public StackGuard(Node xslTemplate, Node sourceXML)
    {
      m_xslRule = xslTemplate;
      m_sourceXML = sourceXML;
    }

    public boolean equals(Object obj)
    {
      if(((StackGuard)obj).m_xslRule.equals(m_xslRule) &&
         ((StackGuard)obj).m_sourceXML.equals(m_sourceXML))
      {
        return true;
      }
      return false;
    }

    public void print(PrintWriter pw)
    {
      // for the moment, these diagnostics are really bad...
      if(m_sourceXML instanceof Text)
      {
        Text tx = (Text)m_sourceXML;
        pw.println(tx.getData());
      }
      else if(m_sourceXML instanceof Element)
      {
        Element elem = (Element)m_sourceXML;
        pw.println(elem.getNodeName());
      }
    }


    public void checkForInfinateLoop(StackGuard guard)
    {
      int nRules = stack.size();
      int loopCount = 0;
      for(int i = (nRules - 1); i >= 0; i--)
      {
        if(stack.elementAt(i).equals(guard))
        {
          loopCount++;
        }
        if(loopCount >= m_recursionLimit)
        {
          // Print out really bad diagnostics.
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          pw.println("Infinite loop diagnosed!  Stack trace:");
          int k;
          for(k = 0; k < nRules; k++)
          {
            pw.println("Source Elem #"+k+" ");
            StackGuard guardOnStack = (StackGuard)stack.elementAt(i);
            guardOnStack.print(pw);
          }
          pw.println("Source Elem #"+k+" ");
          guard.print(pw);
          pw.println("End of infinite loop diagnosis.");
          diag(sw.getBuffer().toString());
          throw new XSLInfiniteLoopException();
        }
      }
    }

    public void push(Node xslTemplate, Node sourceXML)
    {
      StackGuard guard = new StackGuard(xslTemplate, sourceXML);
      checkForInfinateLoop(guard);
      stack.push(guard);
    }

    public void pop()
    {
      stack.pop();
    }
  } // end StackGuard class


  /**
   * Bottleneck addition of result tree attributes, so I can keep
   * track of namespaces.
   */
  void addResultAttribute(MutableAttrListImpl attList, String aname, String value)
  {
    boolean isPrefix = aname.startsWith("xmlns:");
    if (aname.equals("xmlns") || isPrefix)
    {
      String p = isPrefix ? aname.substring(6) : "";
      ResultNameSpace ns = new ResultNameSpace(p, value);
      if(!m_resultNameSpaces.isEmpty())
      {
        ResultNameSpace nsOnStack = (ResultNameSpace)m_resultNameSpaces.peek();
        if(m_emptyNamespace == nsOnStack)
        {
          m_resultNameSpaces.setElementAt(ns,
                                          m_resultNameSpaces.size() - 1);
        }
        else
        {
          while(nsOnStack.m_next != null)
          {
            nsOnStack = nsOnStack.m_next;
          }
          nsOnStack.m_next = ns;
        }
      }
    }

    //attList.removeAttribute(aname);
    attList.addAttribute(aname, "CDATA", value);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell if the default namespace is listed in the pending
   * attributes.  *Only* for use to see if the HTML element should
   * be used to indicate a switch to the HTML formatter.
   */
  private boolean pendingHasDefaultNS()
  {
    if(null != m_pendingAttributes)
    {
      int n = m_pendingAttributes.getLength();
      for(int i = 0; i < n; i++)
      {
        if(m_pendingAttributes.getName(i).equals("xmlns"))
        {
          return true;
        }
      }
    }
    return false;
  }

  boolean m_mustFlushStartDoc = false;

  /**
   * Check to see if the output prefix should be excluded.
   */
  private String excludePrefix(String name)
  {
    if(null != m_stylesheetRoot) // Just extra defensive
    {
      int indexOfNSSep = name.indexOf(':');
      if(indexOfNSSep > 0)
      {
        String prefix = name.substring(0, indexOfNSSep);
        StringToStringTable erp = m_stylesheetRoot.getExcludeResultPrefixes();
        if((null != erp) && erp.contains(prefix))
        {
          name = name.substring(indexOfNSSep+1);
        }
      }
    }
    return name;
  }


  /**
   * Flush the pending element.
   */
  protected void flushPending()
    throws SAXException
  {
    if(m_pendingStartDoc && (null != m_pendingElementName))
    {
      if(!m_stylesheetRoot.isOutputMethodSet())
      {
        if(m_pendingElementName.equalsIgnoreCase("html") && !pendingHasDefaultNS())
        {
          FormatterToXML xmlListener;
          try
          {
            xmlListener = (FormatterToXML)m_flistener;

          }
          catch(ClassCastException cce)
          {
            xmlListener = null;
          }
          if(null != xmlListener)
          {
            // System.out.println("Setting the method automatically to HTML");
            m_stylesheetRoot.setOutputMethod("html");
            m_flistener = new FormatterToHTML(xmlListener);
          }
        }
      }
    }

    if(m_pendingStartDoc && m_mustFlushStartDoc)
    {
      m_pendingStartDoc = false;
      m_flistener.startDocument();
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_STARTDOCUMENT));
    }

    if((null != m_pendingElementName) && m_mustFlushStartDoc)
    {
      if(null != m_stylesheetRoot.m_cdataSectionElems)
      {
        if(isCDataResultElem(m_pendingElementName))
        {
          m_cdataStack.push(TRUE);
        }
        else
        {
          m_cdataStack.push(FALSE);
        }
      }
      m_flistener.startElement(m_pendingElementName, m_pendingAttributes);
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(this,
                                            GenerateEvent.EVENTTYPE_STARTELEMENT,
                                            m_pendingElementName, m_pendingAttributes));
      m_pendingAttributes.clear();
      m_pendingElementName = null;
    }
  }

  /**
   * Class to bottleneck the SAX output events for the
   * result tree.
   */
  class ResultTreeHandler implements DocumentHandler, RawCharacterHandler, LexicalHandler
  {
    /**
     * To fullfill the FormatterListener interface... not action
     * for the moment.
     */
    public void setDocumentLocator (Locator locator)
    {
    }

    /**
     * Bottleneck the startDocument event.
     */
    public void startDocument ()
      throws SAXException
    {
      m_uniqueNSValue = 0;
      m_pendingStartDoc = true;
      m_mustFlushStartDoc = false;
      // m_flistener.startDocument();
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_STARTDOCUMENT));
    }

    /**
     * Bottleneck the endDocument event.
     */
    public void endDocument ()
      throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      m_flistener.endDocument();
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_ENDDOCUMENT));
      m_variableStacks.popCurrentContext();
    }

    /**
     * Bottleneck the startElement event.
     */
    public void startElement (String name)
      throws SAXException
    {
      // name = excludePrefix(name);
      flushPending();
      m_resultNameSpaces.push(m_emptyNamespace);
      m_pendingElementName = name;
      m_mustFlushStartDoc = true;
    }

    /**
     * Bottleneck the startElement event.
     */
    public void startElement (String name, AttributeList atts)
      throws SAXException
    {
      // name = excludePrefix(name);
      flushPending();
      int nAtts = atts.getLength();
      m_pendingAttributes.clear();
      for(int i = 0; i < nAtts; i++)
      {
        m_pendingAttributes.addAttribute(atts.getName(i), atts.getType(i),
                                         atts.getValue(i));
      }
      m_resultNameSpaces.push(m_emptyNamespace);
      m_pendingElementName = name;
      m_mustFlushStartDoc = true;
    }

    /**
     * Bottleneck the endElement event.
     */
    public void endElement (String name)
      throws SAXException
    {
      // name = excludePrefix(name);
      flushPending();
      m_flistener.endElement(name);
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_ENDELEMENT,
                                            name, (AttributeList)null));
      m_resultNameSpaces.pop();
      if(null != m_stylesheetRoot.m_cdataSectionElems)
        m_cdataStack.pop();
    }

    /**
     * Bottleneck the characters event.
     */
    public void characters (char ch[], int start, int length)
      throws SAXException
    {
      if(!m_mustFlushStartDoc)
      {
        int n = ch.length;
        for(int i = 0; i < n; i++)
        {
          if(!Character.isSpaceChar(ch[i]))
          {
            m_mustFlushStartDoc = true;
            break;
          }
        }
      }
      if(m_mustFlushStartDoc)
      {
        flushPending();
        if((null != m_stylesheetRoot.m_cdataSectionElems) &&
           !m_cdataStack.isEmpty() && (m_cdataStack.peek() == TRUE))
        {
          boolean isLexHandler = (m_flistener instanceof LexicalHandler);
          if(isLexHandler)
            ((LexicalHandler)m_flistener).startCDATA();

          m_flistener.characters(ch, start, length);

          if(isLexHandler)
            ((LexicalHandler)m_flistener).endCDATA();

          if(null != m_traceListeners)
            fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                                GenerateEvent.EVENTTYPE_CDATA,
                                                ch, start, length));
        }
        else
        {
          m_flistener.characters(ch, start, length);
          if(null != m_traceListeners)
            fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                                GenerateEvent.EVENTTYPE_CHARACTERS,
                                                ch, start, length));
        }
      }
    }

    /**
     * Bottleneck the characters event.
     */
    public void charactersRaw (char ch[], int start, int length)
      throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      /*
      if(m_flistener instanceof org.apache.xml.serialize.BaseSerializer)
      {
        ((org.apache.xml.serialize.BaseSerializer)m_flistener).characters(new String( ch, start, length ), false, true);
      }
      else
      */
      if(m_flistener instanceof FormatterToXML)
      {
        ((FormatterToXML)m_flistener).charactersRaw(ch, start, length);
      }
      else if(m_flistener instanceof FormatterToDOM)
      {
        ((FormatterToDOM)m_flistener).charactersRaw(ch, start, length);
      }
      else
      {
        m_flistener.characters(ch, start, length);
      }
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_CHARACTERS,
                                            ch, start, length));
    }

    /**
     * Bottleneck the ignorableWhitespace event.
     */
    public void ignorableWhitespace (char ch[], int start, int length)
      throws SAXException
    {
      if(m_mustFlushStartDoc)
      {
        flushPending();
        m_flistener.ignorableWhitespace(ch, start, length);
        if(null != m_traceListeners)
          fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                              GenerateEvent.EVENTTYPE_IGNORABLEWHITESPACE,
                                              ch, start, length));
      }
    }

    /**
     * Bottleneck the processingInstruction event.
     */
    public void processingInstruction (String target, String data)
      throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      m_flistener.processingInstruction(target, data);
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_PI,
                                            target, data));
    }

    /**
     * Bottleneck the comment event.
     */
    public void comment(String data) throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).comment(data.toCharArray(), 0, data.length());
      }
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_COMMENT,
                                            data));
    }

    /**
     * Bottleneck the comment event.
     */
    public void comment(char ch[], int start, int length) throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).comment(ch, start, length);
      }
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_COMMENT,
                                            new String(ch, start, length)));
    }


    /**
     * Bottleneck the comment event.
     */
    public void entityReference(String name) throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).startEntity(name);
        ((LexicalHandler)m_flistener).endEntity(name);
      }
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_ENTITYREF,
                                            name));
    }

    /**
     * Start an entity.
     */
    public void startEntity(String name) throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).startEntity(name);
      }
    }

    /**
     * End an entity.
     */
    public void endEntity(String name) throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).endEntity(name);
      }
      if(null != m_traceListeners)
        fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                            GenerateEvent.EVENTTYPE_ENTITYREF,
                                            name));
    }

    /**
     * Start the DTD.
     */
    public void startDTD(String s1, String s2, String s3) throws SAXException
    {
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).startDTD(s1, s2, s3);
      }
    }

    /**
     * End the DTD.
     */
    public void endDTD() throws SAXException
    {
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).endDTD();
      }
    }

    /**
     * Start the CDATACharacters.
     */
    public void startCDATA() throws SAXException
    {
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).startCDATA();
      }
    }

    /**
     * End the CDATA characters.
     */
    public void endCDATA() throws SAXException
    {
      flushPending();
      if(m_flistener instanceof LexicalHandler)
      {
        ((LexicalHandler)m_flistener).endCDATA();
      }
    }

    /**
     * Bottleneck the cdata event.
     */
    public void cdata (char ch[], int start, int length)
      throws SAXException
    {
      m_mustFlushStartDoc = true;
      flushPending();
      if((null != m_stylesheetRoot.m_cdataSectionElems) &&
         !m_cdataStack.isEmpty() && (m_cdataStack.peek() == TRUE))
      {
        boolean isLexH = (m_flistener instanceof LexicalHandler);

        if(m_flistener instanceof LexicalHandler)
          ((LexicalHandler)m_flistener).startCDATA();

        m_flistener.characters(ch, start, length);

        if(m_flistener instanceof LexicalHandler)
          ((LexicalHandler)m_flistener).endCDATA();

        if(null != m_traceListeners)
          fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                              GenerateEvent.EVENTTYPE_CDATA,
                                              ch, start, length));
      }
      else
      {
        m_flistener.characters(ch, start, length);
        if(null != m_traceListeners)
          fireGenerateEvent(new GenerateEvent(XSLTEngineImpl.this,
                                              GenerateEvent.EVENTTYPE_CHARACTERS,
                                              ch, start, length));
      }

      /*
      if(m_flistener instanceof FormatterListener)
      {
      ((FormatterListener)m_flistener).cdata(ch, start, length);
      }
      else
      {
      // Bad but I think it's better than dropping it.
      m_flistener.characters(ch, start, length);
      }
      ((FormatterListener)m_flistener).cdata(ch, start, length);
      if(null != m_traceListeners)
      fireGenerateEvent(new GenerateEvent(this,
      GenerateEvent.EVENTTYPE_CDATA,
      ch, start, length));
      }
      */
    }
  }

} // end XSLTEngineImpl class
