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
package org.apache.xalan.xpath;
 
import org.w3c.dom.*;
import java.util.*;
import org.apache.xalan.xpath.xml.PrefixResolver;
import org.apache.xalan.xpath.xml.QName;
import java.io.*;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;
import org.apache.xalan.xpath.xml.ProblemListener;
 
/** 
 * <meta name="usage" content="general"/>
 * The XPath class represents the semantic parse tree of the XPath pattern.
 * It is the representation of the grammar which filters out
 * the choice for replacement order of the production rules.
 * In order to conserve memory and reduce object creation, the 
 * tree is represented as an array of integers:
 *    [op code][length][...]
 * where strings are represented within the array as 
 * indexes into the token tree.
 */
public class XPath implements Serializable
{    
  /**
   * <meta name="usage" content="internal"/>
   * A true boolean object so we don't have to keep creating them.
   */
  static XBoolean m_true = new XBooleanStatic(true);
  
  /**
   * <meta name="usage" content="internal"/>
   * A true boolean object so we don't have to keep creating them.
   */
  static XBoolean m_false = new XBooleanStatic(false);
  
  /**
   * The current pattern string, for diagnostics purposes
   */
  String m_currentPattern;
  
  /**
   * Get the pattern string.
   */
  public String getPatternString()
  {
    return m_currentPattern;
  }
  
  /**
   * The max size that the token queue can grow to.
   */
  static final int MAXTOKENQUEUESIZE = 500;

  /**
   *  TokenStack is the queue of used tokens. The current token is the token at the 
   * end of the m_tokenQueue. The idea is that the queue can be marked and a sequence
   * of tokens can be reused.
   */
  Object[] m_tokenQueue = new Object[MAXTOKENQUEUESIZE];
  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the XPath as a list of tokens.
   */
  public Object[] getTokenQueue()
  {
    return m_tokenQueue;
  }

  /**
   * The current size of the token queue.
   */
  int m_tokenQueueSize = 0;

  /**
   * <meta name="usage" content="advanced"/>
   * Get size of the token queue.
   */
  public int getTokenQueueSize()
  {
    return m_tokenQueueSize;
  }

  /**
   * An operations map is used instead of a proper parse tree.  It contains 
   * operations codes and indexes into the m_tokenQueue.
   * I use an array instead of a full parse tree in order to cut down 
   * on the number of objects created.
   */
  int m_opMap[] = null;
  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the opcode list that describes the XPath operations.  It contains 
   * operations codes and indexes into the m_tokenQueue.
   * I use an array instead of a full parse tree in order to cut down 
   * on the number of objects created.
   */
  public int[] getOpMap()
  {
    return m_opMap;
  }
  
  // Position indexes
  
  /**
   * <meta name="usage" content="advanced"/>
   * The length is always the opcode position + 1.
   * Length is always expressed as the opcode+length bytes, 
   * so it is always 2 or greater.
   */
  public static final int MAPINDEX_LENGTH = 1;
  
  /**
   * If this is true, extra programmer error checks will be made.
   */
  static final boolean m_debug = false;
  
  /**
   * This class can have a single listener that can be informed 
   * of errors and warnings, and can normally control if an exception
   * is thrown or not (or the problem listeners can throw their 
   * own RuntimeExceptions).
   */
  transient private org.apache.xalan.xpath.xml.ProblemListener m_problemListener = null;
  
  /**
   * If m_trace is set to true, trace strings will be written 
   * out to System.out.
   */
  static final boolean m_trace = false;
 
  /**
   * Construct a XPath, passing in a problem listener.  The object must 
   * be initialized by the XPathProcessorImpl.initXPath method.
   * @param problemListener An interface whereby the caller 
   * can listen for errors and warnings.
   */
  public XPath(ProblemListener problemListener)
  {
    m_problemListener = problemListener;
  }

  /**
   * Construct an XPath object.  The object must be initialized by the 
   * XPathProcessorImpl.initXPath method.
   */
  public XPath()
  {
    m_problemListener = new org.apache.xalan.xpath.xml.ProblemListenerDefault();
  }

  /**
   * Set the problem listener property.
   * This class can have a single listener that can be informed 
   * of errors and warnings, and can normally control if an exception
   * is thrown or not (or the problem listeners can throw their 
   * own RuntimeExceptions).
   * @param l A ProblemListener interface.
   */
  public void setProblemListener(ProblemListener l)
  {
    m_problemListener = l;
  }

  /**
   * Get the problem listener property.
   * This class can have a single listener that can be informed 
   * of errors and warnings, and can normally control if an exception
   * is thrown or not (or the problem listeners can throw their 
   * own RuntimeExceptions).
   * @return A ProblemListener interface.
   */
  public ProblemListener getProblemListener()
  {
    return m_problemListener;
  }
  
  /**
   * getXLocatorHandler.
   */
  private XLocator createXLocatorHandler(XPathSupport callbacks)
  {
    return callbacks.createXLocatorHandler();
  }

  /**
   * Read the object from an input stream.
   */
  private void readObject(ObjectInputStream stream)
    throws IOException
  {
    try
    {
      stream.defaultReadObject();
    }
    catch(ClassNotFoundException cnfe)
    {
      throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_XPATH_READOBJECT, new Object[]{cnfe.getMessage()})); //"In XPath.readObject: "+cnfe.getMessage());
    }
  }
  
  /**
   * Given an expression and a context, evaluate the XPath 
   * and return the result.
   * @param execContext The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the 
   * XPath are supposed to be expanded.
   * @exception SAXException thrown if the active ProblemListener decides 
   * the error condition is severe enough to halt processing.
   */
  public XObject execute(XPathSupport execContext, Node contextNode, 
                        PrefixResolver namespaceContext)
    throws org.xml.sax.SAXException
  {
    return execute(execContext, contextNode, namespaceContext, null, null, false);
  }

  /**
   * <meta name="usage" content="experimental"/>
   * Given an expression and a context, evaluate the XPath 
   * and call the callback as nodes are found.  Only some simple 
   * types of expresions right now can call back, so if this 
   * method returns null, then the callbacks have been called, otherwise
   * a valid XObject will be returned.
   * @param execContext The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the 
   * XPath are supposed to be expanded.
   * @exception SAXException thrown if the active ProblemListener decides 
   * the error condition is severe enough to halt processing.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @param stopAtFirst True if the search should stop once the first node in document 
   * order is found.
   * @return The result of the XPath or null if callbacks are used.
   * @exception SAXException thrown if 
   * the error condition is severe enough to halt processing.
   */
  public XObject execute(XPathSupport execContext, Node contextNode, 
                         PrefixResolver namespaceContext, 
                         NodeCallback callback, Object callbackInfo, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {    
    PrefixResolver savedPrefixResolver = execContext.getNamespaceContext();
    execContext.setNamespaceContext(namespaceContext);
    execContext.setCurrentNode(contextNode);
    XObject xobj = null;
    try
    {
      if(null != callbackInfo)
        execContext.pushXPathContext(this, execContext, contextNode, namespaceContext);
      xobj = execute(execContext, contextNode, 0, callback, callbackInfo, stopAtFirst);
      if(null != callbackInfo)
        execContext.popXPathContext();
    }
    finally
    {
      execContext.setNamespaceContext(savedPrefixResolver);
      execContext.setCurrentNode(null); // I think this is probably fine
    }
    return xobj;
  }
  
  /**
   * Get the match score of the given node.
   * @param context The current source tree context node.
   * @returns score, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  public double getMatchScore(XPathSupport execContext, Node context) 
    throws org.xml.sax.SAXException
  {
    double score = MATCH_SCORE_NONE;
    int opPos = 0;
    if(m_opMap[opPos] == OP_MATCHPATTERN)
    {
      opPos = getFirstChildPos(opPos);
      
      XLocator locator = execContext.getXLocatorFromNode(context);
      
      if(null == locator)
        locator = execContext.createXLocatorHandler();

      while(m_opMap[opPos] == OP_LOCATIONPATHPATTERN)
      {
        int nextOpPos = getNextOpPos(opPos);
        
        // opPos = getFirstChildPos(opPos);        
        score = locator.locationPathPattern(this, execContext, context, opPos);

        if(score != MATCH_SCORE_NONE)
          break;
        opPos = nextOpPos;
      }
      
    }
    else
    {
      error(context, XPATHErrorResources.ER_EXPECTED_MATCH_PATTERN); //"Expected match pattern in getMatchScore!");
    }
    
    return score;
  }

    
  /**
   * Get the position in the current context node list.
   */
  int getCountOfContextNodeList(XPath path, XPathSupport execContext, Node context)
    throws org.xml.sax.SAXException
  {
    //    assert(null != m_contextNodeList, "m_contextNodeList must be non-null");
    
    if(execContext.getThrowFoundIndex())
      throw new FoundIndex();
    
    NodeList cnl = execContext.getContextNodeList();
    
    if(null == cnl)
    {
      XObject xobject = execContext.reExecuteXPathContext(path, execContext, context);
      if((null != xobject) && (xobject.getType() == XObject.CLASS_NODESET))
        cnl = xobject.nodeset();
      else if(execContext.getContextNodePosition() > 0)
        throw new FoundIndex(); // Tell 'em to try again!
      else
        return 0; // I give up...
    }
    return cnl.getLength();
  }

  /**
   * Get the position in the current context node list.
   */
  int getPositionInContextNodeList(Node context, XPathSupport execContext)
  {
    // assert(null != m_contextNodeList, "m_contextNodeList must be non-null");
    
    if(execContext.getThrowFoundIndex())
      throw new FoundIndex();
    
    int pos = execContext.getContextNodePosition();
    if(pos >= 0)
      return pos;
    
    pos = -1;

    if(null != execContext.getContextNodeList())
    {
      int nNodes = execContext.getContextNodeList().getLength();
      
      for(int i = 0; i < nNodes; i++)
      {
        Node item = execContext.getContextNodeList().item(i);
        if((null != item) && item.equals( context ))
        {
          pos = i+1; // for 1-based XSL count.
          break;
        }
      }
    }
    return pos;
  }
  
  /**
   * Replace the large arrays 
   * with a small array.
   */
  void shrink()
  {
    int map[] = m_opMap;
    int n = m_opMap[MAPINDEX_LENGTH];;
    m_opMap = new int[n+4];
    int i;
    for(i = 0; i < n; i++)
    {
      m_opMap[i] = map[i];
    }
    m_opMap[i] = 0;
    m_opMap[i+1] = 0;
    m_opMap[i+2] = 0;
        
    Object[] tokens = m_tokenQueue;
    n = m_tokenQueueSize;
    m_tokenQueue = new Object[n+4];
    for(i = 0; i < n; i++)
    {
      m_tokenQueue[i] = tokens[i];
    }
    m_tokenQueue[i] = null;
    m_tokenQueue[i+1] = null;
    m_tokenQueue[i+2] = null;
  }
  
  /**
   * Install a built-in function.
   * @param name The unqualified name of the function.
   * @param funcIndex The index of the function in the table.
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public void installFunction (String name, int funcIndex, Function func)
  {            
    m_functions[funcIndex] = func;    
  }
  
  /**
   * Install a built-in function.
   * @param name The unqualified name of the function.
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public static int installFunction(String name, Function func)
  {
    int funcIndex;
    Object funcIndexObj = XPathProcessorImpl.m_functions.get(name);
    if(null != funcIndexObj)
    {
      funcIndex = ((Integer)funcIndexObj).intValue();
    }
    else
    {
      funcIndex = m_funcNextFreeIndex;
      m_funcNextFreeIndex++;
      XPathProcessorImpl.m_functions.put(name, new Integer(funcIndex));
    }
    m_functions[funcIndex] = func;
    return funcIndex;
  }

  /**
   * Execute from the beginning of the xpath.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns The result of the expression.
   */
  protected XObject xpath(XPathSupport execContext, Node context, int opPos)
    throws org.xml.sax.SAXException
  {
    return execute(execContext, context, opPos+2);
  }

  /**
   * OR two expressions and return the boolean result.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the one of the two arguments are true.
   */
  protected XBoolean or(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    XBoolean result;
    XObject expr1 = execute(execContext, context, opPos);
    if(!expr1.bool())
    {
      XObject expr2 = execute(execContext, context, expr2Pos);
      if(!expr2.bool())
      {
        result = new XBoolean(false);
      }
      else
      {
        result = new XBoolean(true);
      }
    }
    else
    {
      result = new XBoolean(true);
    }
    return result;
  }

  /**
   * OR two expressions and return the boolean result.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are both true.
   */
  protected XBoolean and(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    XObject expr1 = execute(execContext, context, opPos);
    if(expr1.bool())
    {
      XObject expr2 = execute(execContext, context, expr2Pos);
      return expr2.bool() ? m_true : m_false;
    }
    else
      return m_false;
  }

  /**
   * Tell if two expressions are functionally not equal.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are not equal.
   */
  protected XBoolean notequals(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return (expr1.notEquals(expr2)) ? m_true : m_false;
  }


  /**
   * Tell if two expressions are functionally equal.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are equal.
   */
  protected XBoolean equals(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos, null, null, true);
    XObject expr2 = execute(execContext, context, expr2Pos, null, null, true);
    
    return expr1.equals(expr2) ? m_true : m_false;
  }
    
  /**
   * Tell if one argument is less than or equal to the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is less than or equal to arg 2.
   */
  protected XBoolean lte(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return expr1.lessThanOrEqual(expr2) ? m_true : m_false;
  }

  /**
   * Tell if one argument is less than the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is less than arg 2.
   */
  protected XBoolean lt(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return expr1.lessThan(expr2) ? m_true : m_false;
  }

  /**
   * Tell if one argument is greater than or equal to the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is greater than or equal to arg 2.
   */
  protected XBoolean gte(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return expr1.greaterThanOrEqual(expr2) ? m_true : m_false;
  }


  /**
   * Tell if one argument is greater than the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is greater than arg 2.
   */
  protected XBoolean gt(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return expr1.greaterThan(expr2) ? m_true : m_false;
  }

  /**
   * Give the sum of two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns sum of arg1 and arg2.
   */
  protected XNumber plus(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return new XNumber(expr1.num() +  expr2.num());
  }

  /**
   * Give the difference of two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns difference of arg1 and arg2.
   */
  protected XNumber minus(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return new XNumber(expr1.num() -  expr2.num());
  }

  /**
   * Multiply two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 * arg2.
   */
  protected XNumber mult(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return new XNumber(expr1.num() *  expr2.num());
  }

  /**
   * Divide a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 / arg2.
   */
  protected XNumber div(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return new XNumber(expr1.num() / expr2.num());
  }

  /**
   * Return the remainder from a truncating division.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 mod arg2.
   */
  protected XNumber mod(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return new XNumber(expr1.num() %  expr2.num());
  }

  /**
   * Return the remainder from a truncating division.
   * (Quo is no longer supported by xpath).
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 mod arg2.
   */
  protected XNumber quo(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    // Actually, this is no longer supported by xpath...
    warn(XPATHErrorResources.WG_QUO_NO_LONGER_DEFINED); //"Old syntax: quo(...) is no longer defined in XPath.");
    
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(execContext, context, opPos);
    XObject expr2 = execute(execContext, context, expr2Pos);
    
    return new XNumber((int)(expr1.num() /  expr2.num()));
  }

  /**
   * Return the negation of a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns -arg.
   */
  protected XNumber neg(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(execContext, context, opPos+2);
    
    return new XNumber(-expr1.num());
  }

  /**
   * Cast an expression to a string.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a string.
   */
  protected XString string(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(execContext, context, opPos+2);
    
    return new XString(expr1.str());
  }

  /**
   * Cast an expression to a boolean.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a boolean.
   */
  protected XBoolean bool(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(execContext, context, opPos+2);
    
    return expr1.bool() ? m_true : m_false;
  }
 
  /**
   * Cast an expression to a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a number.
   */
  protected XNumber number(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(execContext, context, opPos+2);
    
    return new XNumber(expr1.num());
  }
  
  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns the union of node-set operands.
   */
  protected XNodeSet union(XPathSupport execContext, 
                           Node context, int opPos, 
                           NodeCallback callback, Object callbackInfo) 
    throws org.xml.sax.SAXException
  {
    XLocator xlocator = execContext.getXLocatorFromNode(context);
    
    if(null == xlocator)
        xlocator = execContext.createXLocatorHandler();
      
    XNodeSet results = xlocator.union(this, execContext, 
                                      context, opPos, callback, callbackInfo);
    
    return results;
  }

  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XString object.
   */
  protected XString literal(XPathSupport execContext, Node context, int opPos) 
  {
    opPos = getFirstChildPos(opPos);
    
    // TODO: It's too expensive to create an object every time...
    return (XString)m_tokenQueue[m_opMap[opPos]];
  }
  
  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XObject object.
   */
  protected XObject variable(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    String varName = (String)m_tokenQueue[m_opMap[opPos]];
    // System.out.println("variable name: "+varName);
    // TODO: I don't this will be parsed right in the first place...
    QName qname = new QName(varName, execContext.getNamespaceContext());
    XObject result;
    try
    {
      result = execContext.getVariable(qname);
    }
    catch(Exception e)
    {
      error(XPATHErrorResources.ER_COULDNOT_GET_VAR_NAMED, new Object[] {varName}); //"Could not get variable named "+varName);
      result = null;
    }

    if(null == result)
    {
      error(context, XPATHErrorResources.ER_ILLEGAL_VARIABLE_REFERENCE, new Object[] {varName}); //"VariableReference given for variable out "+
                    //"of context or without definition!  Name = " + varName);
    }

    return result;
  }


  /**
   * Execute an expression as a group.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg.
   */
  protected XObject group(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    return execute(execContext, context, opPos+2);
  }


  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XString object.
   */
  protected XNumber numberlit(XPathSupport execContext, Node context, int opPos) 
  {
    opPos = getFirstChildPos(opPos);
    
    return (XNumber)m_tokenQueue[m_opMap[opPos]];
  }
  
  /**
   * Execute a function argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the result of the argument expression.
   */
  protected XObject arg(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    return execute(execContext, context, opPos+2);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Execute a location path.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns a node-set.
   */
  public XNodeSet locationPath(XPathSupport execContext, 
                               Node context, int opPos, 
                               NodeCallback callback, Object callbackInfo, 
                               boolean stopAtFirst) 
    throws org.xml.sax.SAXException
  {    
    XLocator xlocator = execContext.getXLocatorFromNode(context);
    
    if(null == xlocator)
        xlocator = execContext.createXLocatorHandler();
      
    XNodeSet results = xlocator.locationPath(this, execContext, 
                                             context, opPos, callback, callbackInfo, 
                                             stopAtFirst);
    
    return results;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Evaluate a predicate.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns either a boolean or a number.
   */
  public XObject predicate(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(execContext, context, opPos+2, null, null, true);
    int objType = expr1.getType();
    if((XObject.CLASS_NUMBER != objType) && (XObject.CLASS_BOOLEAN != objType))
    {
      expr1 = expr1.bool() ? m_true : m_false;
    }
    
    return expr1;
  }
  
  /**
   * Execute a step in a location path.  This must be implemented 
   * by a derived class of XPath (or don't call at all 
   * from the derived implementation of locationPath()).
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns a node-set.
   */
  protected MutableNodeList step(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    warn(context,XPATHErrorResources.WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST); //"XPath needs a derived object to implement step!");
    return null;
  }

  /**
   * Handle an extension function.
   */
  protected XObject extfunction(XPathSupport execContext, Node context, int opPos, 
                                String namespace, String extensionName, 
                                Vector argVec, Object methodKey) 
    throws org.xml.sax.SAXException
  {
    XObject result;
    Object val = execContext.extFunction(namespace, extensionName, 
                                         argVec, methodKey);
    if(null != val)
    {
      if(val instanceof XObject)
      {
        result = (XObject)val;
      }
      else if(val instanceof XLocator)
      {
        XLocator locator = (XLocator)val;
        opPos = getNextOpPos(opPos+1);
        result = locator.connectToNodes(this, execContext, context, opPos, argVec);  
        // System.out.println("nodeset len: "+result.nodeset().getLength());
      }
      else if(val instanceof String)
      {
        result = new XString((String)val);
      }
      else if(val instanceof Boolean)
      {
        result = ((Boolean)val).booleanValue() ? m_true : m_false;
      }
      else if(val instanceof Double)
      {
        result = new XNumber(((Double)val).doubleValue());
      }
      else if(val instanceof DocumentFragment)
      {
        result = new XRTreeFrag((DocumentFragment)val);
      }
      else if(val instanceof Node)
      {
        // First, see if we need to follow-up with a location path.
        opPos = getNextOpPos(opPos);
        XNodeSet mnl = null;
        if((opPos < m_opMap[MAPINDEX_LENGTH]) &&
           (OP_LOCATIONPATH == (m_opMap[opPos] & XPath.LOCATIONPATHEX_MASK)))
        {
          mnl = locationPath(execContext, (Node)val, opPos, null, null, false);
        }
        result = (null == mnl) ? new XNodeSet((Node)val)
                                 : mnl;
      }
      else if(val instanceof NodeList)
      {
        // First, see if we need to follow-up with a location path.
        opPos = getNextOpPos(opPos);
        XNodeSet mnl = null;
        if((opPos < m_opMap[MAPINDEX_LENGTH]) &&
           (OP_LOCATIONPATH == (m_opMap[opPos] & XPath.LOCATIONPATHEX_MASK)))
        {
          NodeList nl = (NodeList)val;
          int nNodes = nl.getLength();
          for(int i = 0; i < nNodes; i++)
          {
            XNodeSet xnl = locationPath(execContext, nl.item(i), opPos, null, null, false);
            if(null == xnl)
              mnl = xnl;
            else
              mnl.mutableNodeset().addNodes(xnl.nodeset());
          }
        }
        result = (null == mnl) ? new XNodeSet((NodeList)val)
                                 : mnl;
      }
      else
      {
        result = new XObject(val);
      }
    }
    else
    {
      result = new XNull();
    }
    return result;
  }


  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the match score in the form of an XObject.
   */
  protected XObject matchPattern(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject score = null;

    while(m_opMap[opPos] == OP_LOCATIONPATHPATTERN)
    {
      int nextOpPos = getNextOpPos(opPos);
      score = execute(execContext, context, opPos);
      if(score.num() != MATCH_SCORE_NONE)
        break;
      opPos = nextOpPos;
    }
    if(null == score)
    {
      score = new XNumber(MATCH_SCORE_NONE);
    }
    
    return score;
  }

  /**
   * Execute a location path.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns score in an XNumber, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  protected XNumber locationPathPattern(XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    // opPos = getFirstChildPos(opPos);
    XLocator locator = execContext.getXLocatorFromNode(context);
    
    if(null == locator)
        locator = execContext.createXLocatorHandler();
      
    double results = locator.locationPathPattern(this, execContext, context, opPos);
    
    return new XNumber(results);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building indexes of match patterns for fast lookup.
   * This allows a caller to get the name or type of a node, and quickly 
   * find the likely candidates that may match.
   */
  public Vector getTargetElementStrings()
  {
    Vector targetStrings = new Vector();

    int opPos = 2;

    while(m_opMap[opPos] == OP_LOCATIONPATHPATTERN)
    {
      int nextOpPos = getNextOpPos(opPos);
      opPos = getFirstChildPos(opPos);
      
      while( m_opMap[opPos] != ENDOP )
      {
        int nextStepPos = getNextOpPos(opPos);
        int nextOp = m_opMap[nextStepPos];
        if((nextOp == OP_PREDICATE) || (nextOp == ENDOP))
        {
          int stepType = m_opMap[opPos];
          opPos += 3;
          switch(stepType)
          {
          case OP_FUNCTION:
            targetStrings.addElement(PSEUDONAME_ANY);
            break;
          case FROM_ROOT:
            targetStrings.addElement(PSEUDONAME_ROOT);
            break;
          case MATCH_ATTRIBUTE:
          case MATCH_ANY_ANCESTOR:
          case MATCH_IMMEDIATE_ANCESTOR:
            int tok = m_opMap[opPos];
            opPos++;
            switch(tok)
            {
            case NODETYPE_COMMENT:
              targetStrings.addElement(PSEUDONAME_COMMENT);
              break;
            case NODETYPE_TEXT:
              targetStrings.addElement(PSEUDONAME_TEXT);
              break;
            case NODETYPE_NODE:
              targetStrings.addElement(PSEUDONAME_ANY);
              break;
            case NODETYPE_ROOT:
              targetStrings.addElement(PSEUDONAME_ROOT);
              break;
            case NODETYPE_ANYELEMENT:
              targetStrings.addElement(PSEUDONAME_ANY);
              break;
            case NODETYPE_PI:
              targetStrings.addElement(PSEUDONAME_ANY);
              break;
            case NODENAME:
              // Skip the namespace
              int tokenIndex = m_opMap[opPos+1];
              if(tokenIndex >= 0)
              {
                String targetName = (String)m_tokenQueue[tokenIndex];
                if(targetName.equals("*"))
                {
                  targetStrings.addElement(PSEUDONAME_ANY);
                }
                else
                {
                  targetStrings.addElement(targetName);
                }
              }
              else
              {
                targetStrings.addElement(PSEUDONAME_ANY);
              }
              break;
            default:
              targetStrings.addElement(PSEUDONAME_ANY);
              break;
            }
            break;
          }
        }
        opPos = nextStepPos;
      }
      
      opPos = nextOpPos;
    }
    return targetStrings;
  }
    
  /**
   * Execute an extension function from an op code.
   */
  private XObject executeExtension(XPathSupport execContext, Node context, int opPos)
    throws org.xml.sax.SAXException
  {
    int endExtFunc = opPos+m_opMap[opPos+1]-1;
    opPos = getFirstChildPos(opPos);
    String ns = (String)m_tokenQueue[m_opMap[opPos]];
    opPos++;
    String funcName = (String)m_tokenQueue[m_opMap[opPos]];
    opPos++;
    Vector args = new Vector();
    while(opPos < endExtFunc)
    {
      int nextOpPos = getNextOpPos(opPos);
      args.addElement( execute(execContext, context, opPos) );
      opPos = nextOpPos;
    }
    return extfunction(execContext, context, opPos, ns, funcName, args, 
                       // Create a method key, for faster lookup.
      String.valueOf(m_opMap[opPos])+String.valueOf(((Object)this).hashCode()));
  }

  /**
   * Execute a function from an op code.
   */
  XObject executeFunction(XPathSupport execContext, Node context, int opPos)
    throws org.xml.sax.SAXException
  {
    int endFunc = opPos+m_opMap[opPos+1]-1;
    opPos = getFirstChildPos(opPos);
    int funcID = m_opMap[opPos];
    opPos++;
    if(-1 != funcID)
    {
      return m_functions[funcID].execute(this, execContext, context, opPos, funcID, endFunc);
    }
    else
    {
      warn(XPATHErrorResources.WG_FUNCTION_TOKEN_NOT_FOUND); //"function token not found.");
      return null;
    }
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Execute the XPath object from a given opcode position.
   * @param execContext The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @return The result of the XPath.
   */
  public XObject execute(XPathSupport execContext, 
                         Node context, int opPos)
    throws org.xml.sax.SAXException
  {
    int op = m_opMap[opPos];
    switch(op)
    {
    case OP_XPATH: return execute(execContext, context, opPos+2);
    case OP_OR: return or(execContext, context, opPos);
    case OP_AND: return and(execContext, context, opPos);
    case OP_NOTEQUALS: return notequals(execContext, context, opPos);
    case OP_EQUALS: return equals(execContext, context, opPos);
    case OP_LTE: return lte(execContext, context, opPos);
    case OP_LT: return lt(execContext, context, opPos);
    case OP_GTE: return gte(execContext, context, opPos);
    case OP_GT: return gt(execContext, context, opPos);
    case OP_PLUS: return plus(execContext, context, opPos);
    case OP_MINUS: return minus(execContext, context, opPos);
    case OP_MULT: return mult(execContext, context, opPos);
    case OP_DIV: return div(execContext, context, opPos);
    case OP_MOD: return mod(execContext, context, opPos);
    case OP_QUO: return quo(execContext, context, opPos);
    case OP_NEG: return neg(execContext, context, opPos);
    case OP_STRING: return string(execContext, context, opPos);
    case OP_BOOL: return bool(execContext, context, opPos);
    case OP_NUMBER: return number(execContext, context, opPos);
    case OP_UNION: return union(execContext, context, opPos, null, null);
    case OP_LITERAL: return literal(execContext, context, opPos);
    case OP_VARIABLE: return variable(execContext, context, opPos);
    case OP_GROUP: return group(execContext, context, opPos);
    case OP_NUMBERLIT: return numberlit(execContext, context, opPos);
    case OP_ARGUMENT: return arg(execContext, context, opPos);
    case OP_EXTFUNCTION: return executeExtension(execContext, context, opPos);
    case OP_FUNCTION: return executeFunction(execContext, context, opPos);
    case OP_LOCATIONPATH: return locationPath(execContext, context, opPos, null, null, false);
    case OP_PREDICATE: return null; // should never hit this here.
    case OP_MATCHPATTERN: return matchPattern(execContext, context, opPos+2);
    case OP_LOCATIONPATHPATTERN: return locationPathPattern(execContext, context, opPos);
    default: if(op == OP_LOCATIONPATH_EX) return locationPath(execContext, context, opPos, null, null, false);
             else error(context, XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(m_opMap[opPos])}); //"ERROR! Unknown op code: "+m_opMap[opPos]);
    }
    return null;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Execute the XPath object from a given opcode position, calling back 
   * to a NodeCallback interface as the nodes are found.  This will return 
   * null if the path is simple enough for callbacks to be used, otherwise 
   * it will return an XObject.
   * @param execContext The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @return The result of the XPath or null if callbacks are used.
   */
  public XObject execute(XPathSupport execContext, Node context, int opPos, 
                         NodeCallback callback, Object callbackInfo, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int op = m_opMap[opPos];
    switch(op)
    {
    case OP_XPATH: return execute(execContext, context, opPos+2, callback, callbackInfo, stopAtFirst);
    case OP_OR: return or(execContext, context, opPos);
    case OP_AND: return and(execContext, context, opPos);
    case OP_NOTEQUALS: return notequals(execContext, context, opPos);
    case OP_EQUALS: return equals(execContext, context, opPos);
    case OP_LTE: return lte(execContext, context, opPos);
    case OP_LT: return lt(execContext, context, opPos);
    case OP_GTE: return gte(execContext, context, opPos);
    case OP_GT: return gt(execContext, context, opPos);
    case OP_PLUS: return plus(execContext, context, opPos);
    case OP_MINUS: return minus(execContext, context, opPos);
    case OP_MULT: return mult(execContext, context, opPos);
    case OP_DIV: return div(execContext, context, opPos);
    case OP_MOD: return mod(execContext, context, opPos);
    case OP_QUO: return quo(execContext, context, opPos);
    case OP_NEG: return neg(execContext, context, opPos);
    case OP_STRING: return string(execContext, context, opPos);
    case OP_BOOL: return bool(execContext, context, opPos);
    case OP_NUMBER: return number(execContext, context, opPos);
    case OP_UNION: return union(execContext, context, opPos, callback, callbackInfo);
    case OP_LITERAL: return literal(execContext, context, opPos);
    case OP_VARIABLE: return variable(execContext, context, opPos);
    case OP_GROUP: return group(execContext, context, opPos);
    case OP_NUMBERLIT: return numberlit(execContext, context, opPos);
    case OP_ARGUMENT: return arg(execContext, context, opPos);
    case OP_EXTFUNCTION: return executeExtension(execContext, context, opPos);
    case OP_FUNCTION: return executeFunction(execContext, context, opPos);
    case OP_LOCATIONPATH: return locationPath(execContext, context, opPos, callback, callbackInfo, stopAtFirst);
    case OP_PREDICATE: return null; // should never hit this here.
    case OP_MATCHPATTERN: return matchPattern(execContext, context, opPos+2);
    case OP_LOCATIONPATHPATTERN: return locationPathPattern(execContext, context, opPos);
    default: if(op == OP_LOCATIONPATH_EX) return locationPath(execContext, context, opPos, callback, callbackInfo, stopAtFirst);
             else error(context, XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(m_opMap[opPos])}); //"ERROR! Unknown op code: "+m_opMap[opPos]);
    }
    
    return null;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Given an operation position, return the current op.
   * @return position of next operation in m_opMap.
   */
  public int getOp(int opPos)
  {
    return m_opMap[opPos];
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Given an operation position, return the end position, i.e. the 
   * beginning of the next operation.
   * @return position of next operation in m_opMap.
   */
  public int getNextOpPos(int opPos)
  {
    return opPos+m_opMap[opPos+1];
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Given an operation position, return the end position, i.e. the 
   * beginning of the next operation.
   * @return position of next operation in m_opMap.
   */
  public static int getNextOpPos(int[] opMap, int opPos)
  {
    return opPos+opMap[opPos+1];
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Go to the first child of a given operation.
   */
  public static int getFirstChildPos(int opPos)
  {
    return opPos+2;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Go to the first child of a given operation.
   */
  public int getArgLengthOfStep(int opPos)
  {
    return m_opMap[opPos+MAPINDEX_LENGTH+1]-3;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Go to the first child of a given operation.
   */
  public static int getFirstChildPosOfStep(int opPos)
  {
    return opPos+3;
  }

  /**
   * Simple trace function.
   */
  private final void trace(String s)
  {
    System.out.println(s);
  }

  /**
   * Warn the user of an problem.
   */
  void warn(int msg)
    throws org.xml.sax.SAXException
  {
    warn(null, msg, null);
  }
  
  /**
   * Warn the user of an problem.
   */
  void warn(int msg, Object[]args)
    throws org.xml.sax.SAXException
  {
    warn(null, msg, args);
  }

  /**
   * Warn the user of an problem.
   */
  void warn(Node sourceNode, int msg)
    throws org.xml.sax.SAXException
  {
	  warn(sourceNode, msg, null);
  }	   

  /**
   * Warn the user of an problem.
   */
  void warn(Node sourceNode, int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    String fmsg = XSLMessages.createXPATHWarning(msg, args); 
    boolean shouldThrow = this.m_problemListener.problem(ProblemListener.XPATHPROCESSOR, 
                                              ProblemListener.WARNING,
                                              (Object)null, // should be the namespace context 
                                              sourceNode, fmsg, null, 0, 0);
    if(shouldThrow)
    {
      throw new XPathException(fmsg);
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
  void error(int msg)
    throws org.xml.sax.SAXException
  {
    error(null, msg, null);
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  void error(int msg, Object[]args )
    throws org.xml.sax.SAXException
  {
    error(null, msg, args);
  }
  
  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  void error(Node sourceNode, int msg)
    throws org.xml.sax.SAXException
  {
	  error(sourceNode, msg, null);
  } 	  

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  void error(Node sourceNode, int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    String fMsg = XSLMessages.createXPATHMessage(msg, args); 
    String emsg = ((null != m_currentPattern) 
                   ? ("pattern = '"+m_currentPattern+"'\n") : "") +
                  fMsg;

    boolean shouldThrow = this.m_problemListener.problem(ProblemListener.XPATHPROCESSOR, 
                                                         ProblemListener.ERROR,
                                                         null, // Should be the namespace context 
                                                         sourceNode, emsg, null, 0, 0);
    if(shouldThrow)
    {
      throw new XPathException(emsg);
    }
  }
    
  /**
   * <meta name="usage" content="advanced"/>
   * The match score if no match is made.
   */
  public static final double MATCH_SCORE_NONE = Double.NEGATIVE_INFINITY;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern has the form 
   * of a QName optionally preceded by an @ character.
   */
  public static final double MATCH_SCORE_QNAME = 0.0;
  
  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern pattern has the form NCName:*.
   */
  public static final double MATCH_SCORE_NSWILD = -0.25;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of just a NodeTest.
   */
  public static final double MATCH_SCORE_NODETEST = -0.5;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of something 
   * other than just a NodeTest or just a qname.
   */
  public static final double MATCH_SCORE_OTHER = 0.5;

  // List of operations codes.
  //
  // Code for the descriptions of the operations codes:
  // [UPPER CASE] indicates a literal value,
  // [lower case] is a description of a value,
  //      ([length] always indicates the length of the operation,
  //       including the operations code and the length integer.)
  // {UPPER CASE} indicates the given production,
  // {description} is the description of a new production,
  //      (For instance, {boolean expression} means some expression 
  //       that should be resolved to a boolean.)
  //  * means that it occurs zero or more times,
  //  + means that it occurs one or more times,
  //  ? means that it is optional.
  //
  // returns: indicates what the production should return.

  /**
   * <meta name="usage" content="advanced"/>
   * [ENDOP]
   * Some operators may like to have a terminator.
   */
  public static final int ENDOP = -1;
  
  /**
   * [EMPTY]
   * Empty slot to indicate NULL.
   */
  public static final int EMPTY = -2;

  /**
   * <meta name="usage" content="advanced"/>
   * [ELEMWILDCARD]
   * Means ELEMWILDCARD ("*"), used instead 
   * of string index in some places.
   */
  public static final int ELEMWILDCARD = -3;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_XPATH]
   * [length]
   *  {expression}
   * 
   * returns: 
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_XPATH = 1;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_OR]
   * [length]
   *  {boolean expression}
   *  {boolean expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_OR = 2;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_AND]
   * [length]
   *  {boolean expression}
   *  {boolean expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_AND = 3;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NOTEQUALS]
   * [length]
   *  {expression}
   *  {expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_NOTEQUALS = 4;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_EQUALS]
   * [length]
   *  {expression}
   *  {expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_EQUALS = 5;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LTE] (less-than-or-equals)
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_LTE = 6;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LT] (less-than)
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_LT = 7;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_GTE] (greater-than-or-equals)
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_GTE = 8;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_GT] (greater-than)
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_GT = 9;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_PLUS]
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XNumber
   */
  public static final int OP_PLUS = 10;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_MINUS]
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XNumber
   */
  public static final int OP_MINUS = 11;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_MULT]
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XNumber
   */
  public static final int OP_MULT = 12;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_DIV]
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XNumber
   */
  public static final int OP_DIV = 13;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_MOD]
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XNumber
   */
  public static final int OP_MOD = 14;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_QUO]
   * [length]
   *  {number expression}
   *  {number expression}
   * 
   * returns: 
   *  XNumber
   */
  public static final int OP_QUO = 15;


  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NEG]
   * [length]
   *  {number expression}
   * 
   * returns: 
   *  XNumber
   */
  public static final int OP_NEG = 16;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_STRING] (cast operation)
   * [length]
   *  {expression}
   * 
   * returns: 
   *  XString
   */
  public static final int OP_STRING = 17;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_BOOL] (cast operation)
   * [length]
   *  {expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_BOOL = 18;


  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NUMBER] (cast operation)
   * [length]
   *  {expression}
   * 
   * returns: 
   *  XBoolean
   */
  public static final int OP_NUMBER = 19;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_UNION]
   * [length]
   *  {PathExpr}+
   * 
   * returns: 
   *  XNodeSet
   */
  public static final int OP_UNION = 20;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LITERAL]
   * [3]
   * [index to token]
   * 
   * returns: 
   *  XString
   */
  public static final int OP_LITERAL = 21;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_VARIABLE]
   * [3]
   * [index to token]
   * 
   * returns: 
   *  XString
   */
  public static final int OP_VARIABLE = 22;
    
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_GROUP]
   * [length]
   *  {expression}
   * 
   * returns: 
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_GROUP = 23;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NUMBERLIT] (Number literal.)
   * [3]
   * [index to token]
   * 
   * returns: 
   *  XString
   */
  public static final int OP_NUMBERLIT = 24;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_ARGUMENT] (Function argument.)
   * [length]
   *  {expression}
   * 
   * returns: 
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_ARGUMENT = 25;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_EXTFUNCTION] (Extension function.)
   * [length]
   * [index to namespace token]
   * [index to function name token]
   *  {OP_ARGUMENT}*
   * 
   * returns: 
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_EXTFUNCTION = 26;
  
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_FUNCTION]
   * [length]
   * [FUNC_name]
   *  {OP_ARGUMENT}*
   * [ENDOP]
   * 
   * returns: 
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_FUNCTION = 27;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LOCATIONPATH]
   * [length]
   *   {FROM_stepType}
   * | {function}{predicate}*
   * [ENDOP]
   * 
   * (Note that element and attribute namespaces and 
   * names can be wildcarded '*'.)
   * 
   * returns: 
   *  XNodeSet
   */
  public static final int OP_LOCATIONPATH = 28;
  public static final int LOCATIONPATHEX_MASK = 0x0000FFFF;
  public static final int LOCATIONPATHEX_ISSIMPLE = 0x00010000;
  public static final int OP_LOCATIONPATH_EX = (28 | 0x00010000);
    

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_PREDICATE]
   * [length]
   *  {expression}
   * [ENDOP] (For safety)
   * 
   * returns: 
   *  XBoolean or XNumber
   */
  public static final int OP_PREDICATE = 29;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_UNION]
   * [length]
   *  {PathExpr}+
   * 
   * returns: 
   *  XNodeSet
   */
  public static final int OP_MATCHPATTERN = 30;
  
  
  /**
   * <meta name="usage" content="advanced"/>
   * [OP_UNION]
   * [length]
   *  {PathExpr}+
   * 
   * returns: 
   *  XNodeSet
   */
  public static final int OP_LOCATIONPATHPATTERN = 31;

  
  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_COMMENT]
   * No size or arguments.
   * Note: must not overlap function OP number!
   * 
   * returns: 
   *  XBoolean
   */
  public static final int NODETYPE_COMMENT = 1030;
    
  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_TEXT]
   * No size or arguments.
   * Note: must not overlap function OP number!
   * 
   * returns: 
   *  XBoolean
   */
  public static final int NODETYPE_TEXT = 1031;
    
  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_PI]
   * [index to token]
   * Note: must not overlap function OP number!
   * 
   * returns: 
   *  XBoolean
   */
  public static final int NODETYPE_PI = 1032;
    
  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_NODE]
   * No size or arguments.
   * Note: must not overlap function OP number!
   * 
   * returns: 
   *  XBoolean
   */
  public static final int NODETYPE_NODE = 1033;
    
  /**
   * <meta name="usage" content="advanced"/>
   * [NODENAME]
   * [index to ns token or EMPTY]
   * [index to name token]
   * 
   * returns: 
   *  XBoolean
   */
  public static final int NODENAME = 34;
    
  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_ROOT]
   * No size or arguments.
   * 
   * returns: 
   *  XBoolean
   */
  public static final int NODETYPE_ROOT = 35;
    
  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_ANY]
   * No size or arguments.
   * 
   * returns: 
   *  XBoolean
   */
  public static final int NODETYPE_ANYELEMENT = 36;
  
  /**
   * <meta name="usage" content="advanced"/>
   * [FROM_stepType]
   * [length, including predicates]
   * [length of just the step, without the predicates]
   * {node test}
   * {predicates}?
   * 
   * returns: 
   *  XBoolean
   */
  public static final int FROM_ANCESTORS = 37;
  public static final int FROM_ANCESTORS_OR_SELF = 38;
  public static final int FROM_ATTRIBUTES = 39;
  public static final int FROM_CHILDREN = 40;
  public static final int FROM_DESCENDANTS = 41;
  public static final int FROM_DESCENDANTS_OR_SELF = 42;
  public static final int FROM_FOLLOWING = 43;
  public static final int FROM_FOLLOWING_SIBLINGS = 44; 
  public static final int FROM_PARENT = 45;
  public static final int FROM_PRECEDING = 46;
  public static final int FROM_PRECEDING_SIBLINGS = 47;
  public static final int FROM_SELF = 48;
  public static final int FROM_NAMESPACE = 49;  
  public static final int FROM_ROOT = 55;  
  // public static final int FROM_ATTRIBUTE = 50;
  // public static final int FROM_DOC = 51;
  // public static final int FROM_DOCREF = 52;
  // public static final int FROM_ID = 53;
  // public static final int FROM_IDREF = 54;
  
  public static final int FUNC_CURRENT = 0;
  public static final int FUNC_LAST = 1;
  public static final int FUNC_POSITION = 2;
  public static final int FUNC_COUNT = 3;
  public static final int FUNC_ID = 4;
  public static final int FUNC_KEY = 5;
  // public static final int FUNC_DOC = 6;
  public static final int FUNC_LOCAL_PART = 7;
  public static final int FUNC_NAMESPACE = 8;
  public static final int FUNC_QNAME = 9;
  public static final int FUNC_GENERATE_ID = 10;
  public static final int FUNC_NOT = 11;
  public static final int FUNC_TRUE = 12;
  public static final int FUNC_FALSE = 13;
  public static final int FUNC_BOOLEAN = 14;
  public static final int FUNC_NUMBER = 15;
  public static final int FUNC_FLOOR = 16;
  public static final int FUNC_CEILING = 17;
  public static final int FUNC_ROUND = 18;
  public static final int FUNC_SUM = 19;
  public static final int FUNC_STRING = 20;
  public static final int FUNC_STARTS_WITH = 21;
  public static final int FUNC_CONTAINS = 22;
  public static final int FUNC_SUBSTRING_BEFORE = 23;
  public static final int FUNC_SUBSTRING_AFTER = 24;
  public static final int FUNC_NORMALIZE_SPACE = 25;
  public static final int FUNC_TRANSLATE = 26;
  public static final int FUNC_CONCAT = 27;
 // public static final int FUNC_FORMAT_NUMBER = 28;
  public static final int FUNC_SUBSTRING = 29;
  public static final int FUNC_STRING_LENGTH = 30;
  public static final int FUNC_SYSTEM_PROPERTY = 31;
  public static final int FUNC_LANG = 32;
  public static final int FUNC_EXT_FUNCTION_AVAILABLE = 33;
  public static final int FUNC_EXT_ELEM_AVAILABLE = 34;
    
  // Proprietary
  public static final int FUNC_DOCLOCATION = 35;

  public static final int FUNC_UNPARSED_ENTITY_URI = 36;

  /**
   * Number of built in functions.  Be sure to update this as 
   * built-in functions are added.
   */
  private static final int NUM_BUILT_IN_FUNCS = 37;

  /**
   * Number of built-in functions that may be added.
   */
  private static final int NUM_ALLOWABLE_ADDINS = 30;

  /**
   * The function table.
   */
  static private Function m_functions[];
  
  /**
   * The index to the next free function index.
   */
  static private int m_funcNextFreeIndex = NUM_BUILT_IN_FUNCS;
  
  static
  {
    m_functions = new Function[NUM_BUILT_IN_FUNCS+NUM_ALLOWABLE_ADDINS];
    m_functions[FUNC_CURRENT] = new FuncLoader("FuncCurrent", FUNC_CURRENT);
    m_functions[FUNC_LAST] = new FuncLoader("FuncLast", FUNC_LAST);
    m_functions[FUNC_POSITION] = new FuncLoader("FuncPosition", FUNC_POSITION);
    m_functions[FUNC_COUNT] = new FuncLoader("FuncCount", FUNC_COUNT);
    m_functions[FUNC_ID] = new FuncLoader("FuncId", FUNC_ID);
    m_functions[FUNC_KEY] = new FuncLoader("FuncKey", FUNC_KEY);
    // m_functions[FUNC_DOC] = new FuncDoc();
    m_functions[FUNC_LOCAL_PART] = new FuncLoader("FuncLocalPart", FUNC_LOCAL_PART);
    m_functions[FUNC_NAMESPACE] = new FuncLoader("FuncNamespace", FUNC_NAMESPACE);
    m_functions[FUNC_QNAME] = new FuncLoader("FuncQname", FUNC_QNAME);
    m_functions[FUNC_GENERATE_ID] = new FuncLoader("FuncGenerateId", FUNC_GENERATE_ID);
    m_functions[FUNC_NOT] = new FuncLoader("FuncNot", FUNC_NOT);
    m_functions[FUNC_TRUE] = new FuncLoader("FuncTrue", FUNC_TRUE);
    m_functions[FUNC_FALSE] = new FuncLoader("FuncFalse", FUNC_FALSE);
    m_functions[FUNC_BOOLEAN] = new FuncLoader("FuncBoolean", FUNC_BOOLEAN);
    m_functions[FUNC_LANG] = new FuncLoader("FuncLang", FUNC_LANG);
    m_functions[FUNC_NUMBER] = new FuncLoader("FuncNumber", FUNC_NUMBER);
    m_functions[FUNC_FLOOR] = new FuncLoader("FuncFloor", FUNC_FLOOR);
    m_functions[FUNC_CEILING] = new FuncLoader("FuncCeiling", FUNC_CEILING);
    m_functions[FUNC_ROUND] = new FuncLoader("FuncRound", FUNC_ROUND);
    m_functions[FUNC_SUM] = new FuncLoader("FuncSum", FUNC_SUM);
    m_functions[FUNC_STRING] = new FuncLoader("FuncString", FUNC_STRING);
    m_functions[FUNC_STARTS_WITH] = new FuncLoader("FuncStartsWith", FUNC_STARTS_WITH);
    m_functions[FUNC_CONTAINS] = new FuncLoader("FuncContains", FUNC_CONTAINS);
    m_functions[FUNC_SUBSTRING_BEFORE] = new FuncLoader("FuncSubstringBefore", FUNC_SUBSTRING_BEFORE);
    m_functions[FUNC_SUBSTRING_AFTER] = new FuncLoader("FuncSubstringAfter", FUNC_SUBSTRING_AFTER);
    m_functions[FUNC_NORMALIZE_SPACE] = new FuncLoader("FuncNormalizeSpace", FUNC_NORMALIZE_SPACE);
    m_functions[FUNC_TRANSLATE] = new FuncLoader("FuncTranslate", FUNC_TRANSLATE);
    m_functions[FUNC_CONCAT] = new FuncLoader("FuncConcat", FUNC_CONCAT);
    //m_functions[FUNC_FORMAT_NUMBER] = new FuncFormatNumber();
    m_functions[FUNC_SYSTEM_PROPERTY] = new FuncLoader("FuncSystemProperty", FUNC_SYSTEM_PROPERTY);
    m_functions[FUNC_EXT_FUNCTION_AVAILABLE] = new FuncLoader("FuncExtFunctionAvailable", FUNC_EXT_FUNCTION_AVAILABLE);
    m_functions[FUNC_EXT_ELEM_AVAILABLE] = new FuncLoader("FuncExtElementAvailable", FUNC_EXT_ELEM_AVAILABLE);
    m_functions[FUNC_SUBSTRING] = new FuncLoader("FuncSubstring", FUNC_SUBSTRING);
    m_functions[FUNC_STRING_LENGTH] = new FuncLoader("FuncStringLength", FUNC_STRING_LENGTH);
    m_functions[FUNC_DOCLOCATION] = new FuncLoader("FuncDoclocation", FUNC_DOCLOCATION);
    m_functions[FUNC_UNPARSED_ENTITY_URI] = new FuncLoader("FuncUnparsedEntityURI", FUNC_UNPARSED_ENTITY_URI);
    
  }
      
  /**
   * <meta name="usage" content="advanced"/>
   * For match patterns.
   */
  public static final int MATCH_ATTRIBUTE = 94;  
      
  /**
   * <meta name="usage" content="advanced"/>
   * For match patterns.
   */
  public static final int MATCH_ANY_ANCESTOR = 95;  
      
  /**
   * <meta name="usage" content="advanced"/>
   * For match patterns.
   */
  public static final int MATCH_IMMEDIATE_ANCESTOR = 96; 
  
  /**
   * <meta name="usage" content="advanced"/>
   * used mainly for keys in the pattern lookup table,
   * for those nodes that don't have unique lookup values.
   */
  public static final String PSEUDONAME_ANY = "*";
  
  /**
   * <meta name="usage" content="advanced"/>
   * used mainly for keys in the pattern lookup table,
   * for those nodes that don't have unique lookup values.
   */
  public static final String PSEUDONAME_ROOT = "/";
  
  /**
   * <meta name="usage" content="advanced"/>
   * used mainly for keys in the pattern lookup table,
   * for those nodes that don't have unique lookup values.
   */
  public static final String PSEUDONAME_TEXT = "#text";
  
  /**
   * <meta name="usage" content="advanced"/>
   * used mainly for keys in the pattern lookup table,
   * for those nodes that don't have unique lookup values.
   */
  public static final String PSEUDONAME_COMMENT = "#comment";
  
  /**
   * <meta name="usage" content="advanced"/>
   * used mainly for keys in the pattern lookup table,
   * for those nodes that don't have unique lookup values.
   */
  public static final String PSEUDONAME_PI = "#pi";
  
  /**
   * <meta name="usage" content="advanced"/>
   * used mainly for keys in the pattern lookup table,
   * for those nodes that don't have unique lookup values.
   */
  public static final String PSEUDONAME_OTHER = "*";
  
  private static final int NEXT_FREE_ID = 99;   
}
