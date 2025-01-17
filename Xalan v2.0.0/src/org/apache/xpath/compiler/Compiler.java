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
package org.apache.xpath.compiler;

import org.apache.xpath.operations.And;
import org.apache.xpath.operations.Bool;
import org.apache.xpath.operations.Div;
import org.apache.xpath.operations.Equals;
import org.apache.xpath.operations.Gt;
import org.apache.xpath.operations.Gte;
import org.apache.xpath.operations.Lt;
import org.apache.xpath.operations.Lte;
import org.apache.xpath.operations.Minus;
import org.apache.xpath.operations.Mod;
import org.apache.xpath.operations.Mult;
import org.apache.xpath.operations.Neg;
import org.apache.xpath.operations.NotEquals;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Or;
import org.apache.xpath.operations.Plus;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.objects.*;
import org.apache.xpath.axes.*;
import org.apache.xpath.patterns.*;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.*;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.PrefixResolver;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.SourceLocator;
import org.apache.xml.utils.SAXSourceLocator;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.w3c.dom.traversal.NodeFilter;

/**
 * <meta name="usage" content="advanced"/>
 * An instance of this class compiles an XPath string expression into 
 * a Expression object.  This class compiles the string into a sequence 
 * of operation codes (op map) and then builds from that into an Expression 
 * tree.
 */
public class Compiler extends OpMap
{

  /**
   * Construct a Compiler object with a specific ErrorListener and 
   * SourceLocator where the expression is located.
   *
   * @param errorHandler Error listener where messages will be sent, or null 
   *                     if messages should be sent to System err.
   * @param locator The location object where the expression lives, which 
   *                may be null.
   */
  public Compiler(ErrorListener errorHandler, SourceLocator locator)
  {
    m_errorHandler = errorHandler;
    if(null != locator)
    {
      SAXSourceLocator ssl = new SAXSourceLocator();
      ssl.setColumnNumber(locator.getColumnNumber());
      ssl.setLineNumber(locator.getLineNumber());
      ssl.setPublicId(locator.getPublicId());
      ssl.setSystemId(locator.getSystemId());
      m_locator = ssl;
    }
  }

  /**
   * Construct a Compiler instance that has a null error listener and a 
   * null source locator.
   */
  public Compiler()
  {
    m_errorHandler = null;
    m_locator = null;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Execute the XPath object from a given opcode position.
   * @param xctxt The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @return The result of the XPath.
   *
   * @throws TransformerException if there is a syntax or other error.
   */
  public Expression compile(int opPos) throws TransformerException
  {

    int op = m_opMap[opPos];

    Expression expr = null;
    // System.out.println(getPatternString()+"op: "+op);
    switch (op)
    {
    case OpCodes.OP_XPATH :
      expr = compile(opPos + 2); break;
    case OpCodes.OP_OR :
      expr = or(opPos); break;
    case OpCodes.OP_AND :
      expr = and(opPos); break;
    case OpCodes.OP_NOTEQUALS :
      expr = notequals(opPos); break;
    case OpCodes.OP_EQUALS :
      expr = equals(opPos); break;
    case OpCodes.OP_LTE :
      expr = lte(opPos); break;
    case OpCodes.OP_LT :
      expr = lt(opPos); break;
    case OpCodes.OP_GTE :
      expr = gte(opPos); break;
    case OpCodes.OP_GT :
      expr = gt(opPos); break;
    case OpCodes.OP_PLUS :
      expr = plus(opPos); break;
    case OpCodes.OP_MINUS :
      expr = minus(opPos); break;
    case OpCodes.OP_MULT :
      expr = mult(opPos); break;
    case OpCodes.OP_DIV :
      expr = div(opPos); break;
    case OpCodes.OP_MOD :
      expr = mod(opPos); break;
//    case OpCodes.OP_QUO :
//      expr = quo(opPos); break;
    case OpCodes.OP_NEG :
      expr = neg(opPos); break;
    case OpCodes.OP_STRING :
      expr = string(opPos); break;
    case OpCodes.OP_BOOL :
      expr = bool(opPos); break;
    case OpCodes.OP_NUMBER :
      expr = number(opPos); break;
    case OpCodes.OP_UNION :
      expr = union(opPos); break;
    case OpCodes.OP_LITERAL :
      expr = literal(opPos); break;
    case OpCodes.OP_VARIABLE :
      expr = variable(opPos); break;
    case OpCodes.OP_GROUP :
      expr = group(opPos); break;
    case OpCodes.OP_NUMBERLIT :
      expr = numberlit(opPos); break;
    case OpCodes.OP_ARGUMENT :
      expr = arg(opPos); break;
    case OpCodes.OP_EXTFUNCTION :
      expr = compileExtension(opPos); break;
    case OpCodes.OP_FUNCTION :
      expr = compileFunction(opPos); break;
    case OpCodes.OP_LOCATIONPATH :
      expr = locationPath(opPos); break;
    case OpCodes.OP_PREDICATE :
      expr = null; break;  // should never hit this here.
    case OpCodes.OP_MATCHPATTERN :
      expr = matchPattern(opPos + 2); break;
    case OpCodes.OP_LOCATIONPATHPATTERN :
      expr = locationPathPattern(opPos); break;
    default :
      error(XPATHErrorResources.ER_UNKNOWN_OPCODE,
            new Object[]{ Integer.toString(m_opMap[opPos]) });  //"ERROR! Unknown op code: "+m_opMap[opPos]);
    }
    if(null != expr)
      expr.setSourceLocator(m_locator);

    return expr;
  }

  /**
   * Bottle-neck compilation of an operation with left and right operands.
   *
   * @param operation non-null reference to parent operation.
   * @param opPos The op map position of the parent operation.
   *
   * @return reference to {@link org.apache.xpath.operations.Operation} instance.
   *
   * @throws TransformerException if there is a syntax or other error.
   */
  private Expression compileOperation(Operation operation, int opPos)
          throws TransformerException
  {

    int leftPos = getFirstChildPos(opPos);
    int rightPos = getNextOpPos(leftPos);

    operation.setLeftRight(compile(leftPos), compile(rightPos));

    return operation;
  }

  /**
   * Bottle-neck compilation of a unary operation.
   *
   * @param unary The parent unary operation.
   * @param opPos The position in the op map of the parent operation.
   *
   * @return The unary argument.
   *
   * @throws TransformerException if syntax or other error occurs.
   */
  private Expression compileUnary(UnaryOperation unary, int opPos)
          throws TransformerException
  {

    int rightPos = getFirstChildPos(opPos);

    unary.setRight(compile(rightPos));

    return unary;
  }

  /**
   * Compile an 'or' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Or} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression or(int opPos) throws TransformerException
  {
    return compileOperation(new Or(), opPos);
  }

  /**
   * Compile an 'and' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.And} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression and(int opPos) throws TransformerException
  {
    return compileOperation(new And(), opPos);
  }

  /**
   * Compile a '!=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.NotEquals} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression notequals(int opPos) throws TransformerException
  {
    return compileOperation(new NotEquals(), opPos);
  }

  /**
   * Compile a '=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Equals} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression equals(int opPos) throws TransformerException
  {
    return compileOperation(new Equals(), opPos);
  }

  /**
   * Compile a '<=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Lte} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression lte(int opPos) throws TransformerException
  {
    return compileOperation(new Lte(), opPos);
  }

  /**
   * Compile a '<' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Lt} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression lt(int opPos) throws TransformerException
  {
    return compileOperation(new Lt(), opPos);
  }

  /**
   * Compile a '>=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Gte} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression gte(int opPos) throws TransformerException
  {
    return compileOperation(new Gte(), opPos);
  }

  /**
   * Compile a '>' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Gt} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression gt(int opPos) throws TransformerException
  {
    return compileOperation(new Gt(), opPos);
  }

  /**
   * Compile a '+' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Plus} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression plus(int opPos) throws TransformerException
  {
    return compileOperation(new Plus(), opPos);
  }

  /**
   * Compile a '-' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Minus} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression minus(int opPos) throws TransformerException
  {
    return compileOperation(new Minus(), opPos);
  }

  /**
   * Compile a '*' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Mult} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression mult(int opPos) throws TransformerException
  {
    return compileOperation(new Mult(), opPos);
  }

  /**
   * Compile a 'div' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Div} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression div(int opPos) throws TransformerException
  {
    return compileOperation(new Div(), opPos);
  }

  /**
   * Compile a 'mod' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Mod} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression mod(int opPos) throws TransformerException
  {
    return compileOperation(new Mod(), opPos);
  }

  /*
   * Compile a 'quo' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Quo} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
//  protected Expression quo(int opPos) throws TransformerException
//  {
//    return compileOperation(new Quo(), opPos);
//  }

  /**
   * Compile a unary '-' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Neg} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression neg(int opPos) throws TransformerException
  {
    return compileUnary(new Neg(), opPos);
  }

  /**
   * Compile a 'string(...)' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.String} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression string(int opPos) throws TransformerException
  {
    return compileUnary(new org.apache.xpath.operations.String(), opPos);
  }

  /**
   * Compile a 'boolean(...)' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Bool} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression bool(int opPos) throws TransformerException
  {
    return compileUnary(new org.apache.xpath.operations.Bool(), opPos);
  }

  /**
   * Compile a 'number(...)' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Number} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression number(int opPos) throws TransformerException
  {
    return compileUnary(new org.apache.xpath.operations.Number(), opPos);
  }

  /**
   * Compile a literal string value.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.objects.XString} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression literal(int opPos)
  {

    opPos = getFirstChildPos(opPos);

    return (XString) m_tokenQueue[m_opMap[opPos]];
  }

  /**
   * Compile a literal number value.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.objects.XNumber} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression numberlit(int opPos)
  {

    opPos = getFirstChildPos(opPos);

    return (XNumber) m_tokenQueue[m_opMap[opPos]];
  }

  /**
   * Compile a variable reference.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Variable} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression variable(int opPos) throws TransformerException
  {

    Variable var = new Variable();

    opPos = getFirstChildPos(opPos);

    int nsPos = m_opMap[opPos];
    java.lang.String namespace 
      = (OpCodes.EMPTY == nsPos) ? null 
                                   : (java.lang.String) m_tokenQueue[nsPos];
    java.lang.String localname 
      = (java.lang.String) m_tokenQueue[m_opMap[opPos+1]];
    QName qname = new QName(namespace, localname);

    var.setQName(qname);

    return var;
  }

  /**
   * Compile an expression group.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to the contained expression.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression group(int opPos) throws TransformerException
  {

    // no-op
    return compile(opPos + 2);
  }

  /**
   * Compile a function argument.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to the argument expression.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression arg(int opPos) throws TransformerException
  {

    // no-op
    return compile(opPos + 2);
  }

  /**
   * Compile a location path union. The UnionPathIterator itself may create
   * {@link org.apache.xpath.axes.LocPathIterator} children.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.axes.UnionPathIterator} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression union(int opPos) throws TransformerException
  {
    locPathDepth++;
    try
    {
      return new UnionPathIterator(this, opPos);
    }
    finally
    {
      locPathDepth--;
    }
  }
  
  private int locPathDepth = -1;
  
  /**
   * Get the level of the location path or union being constructed.  
   * @return 0 if it is a top-level path.
   */
  public int getLocationPathDepth()
  {
    return locPathDepth;
  }

  /**
   * Compile a location path.  The LocPathIterator itself may create
   * {@link org.apache.xpath.axes.AxesWalker} children.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.axes.LocPathIterator} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression locationPath(int opPos) throws TransformerException
  {
    locPathDepth++;
    try
    {
      LocPathIterator iter = WalkerFactory.newLocPathIterator(this, opPos);
      if(locPathDepth == 0)
        iter.setIsTopLevel(true);
      return iter;
    }
    finally
    {
      locPathDepth--;
    }
  }

  /**
   * Compile a location step predicate expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the contained predicate expression.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression predicate(int opPos) throws TransformerException
  {
    return compile(opPos + 2);
  }

  /**
   * Compile an entire match pattern expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.patterns.UnionPattern} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression matchPattern(int opPos) throws TransformerException
  {

    // First, count...
    int nextOpPos = opPos;
    int i;

    for (i = 0; m_opMap[nextOpPos] == OpCodes.OP_LOCATIONPATHPATTERN; i++)
    {
      nextOpPos = getNextOpPos(nextOpPos);
    }

    if (i == 1)
      return compile(opPos);

    UnionPattern up = new UnionPattern();
    StepPattern[] patterns = new StepPattern[i];

    for (i = 0; m_opMap[opPos] == OpCodes.OP_LOCATIONPATHPATTERN; i++)
    {
      nextOpPos = getNextOpPos(opPos);
      patterns[i] = (StepPattern) compile(opPos);
      opPos = nextOpPos;
    }

    up.setPatterns(patterns);

    return up;
  }

  /**
   * Compile a location match pattern unit expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.patterns.StepPattern} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression locationPathPattern(int opPos)
          throws TransformerException
  {

    opPos = getFirstChildPos(opPos);

    return stepPattern(opPos, 0, null);
  }

  /**
   * Get a {@link org.w3c.dom.traversal.NodeFilter} bit set that tells what 
   * to show for a given node test.
   *
   * @param opPos the op map position for the location step.
   *
   * @return {@link org.w3c.dom.traversal.NodeFilter} bit set that tells what 
   *         to show for a given node test.
   */
  public int getWhatToShow(int opPos)
  {

    int axesType = getOp(opPos);
    int testType = getOp(opPos + 3);

    // System.out.println("testType: "+testType);
    switch (testType)
    {
    case OpCodes.NODETYPE_COMMENT :
      return NodeFilter.SHOW_COMMENT;
    case OpCodes.NODETYPE_TEXT :
//      return NodeFilter.SHOW_TEXT | NodeFilter.SHOW_COMMENT;
      return NodeFilter.SHOW_TEXT | NodeFilter.SHOW_CDATA_SECTION ;
    case OpCodes.NODETYPE_PI :
      return NodeFilter.SHOW_PROCESSING_INSTRUCTION;
    case OpCodes.NODETYPE_NODE :
//      return NodeFilter.SHOW_ALL;
      switch (axesType)
      {
      case OpCodes.FROM_NAMESPACE:
        return NodeFilter.SHOW_ATTRIBUTE | NodeTest.SHOW_NAMESPACE;
      case OpCodes.FROM_ATTRIBUTES :
      case OpCodes.MATCH_ATTRIBUTE :
        return NodeFilter.SHOW_ATTRIBUTE;
      case OpCodes.FROM_SELF:
      case OpCodes.FROM_ANCESTORS_OR_SELF:
      case OpCodes.FROM_DESCENDANTS_OR_SELF:
        return NodeFilter.SHOW_ALL;
      default:
        if (getOp(0) == OpCodes.OP_MATCHPATTERN)
          return ~NodeFilter.SHOW_ATTRIBUTE
                  & ~NodeFilter.SHOW_DOCUMENT
                  & ~NodeFilter.SHOW_DOCUMENT_FRAGMENT;
        else
          return ~NodeFilter.SHOW_ATTRIBUTE;
      }
    case OpCodes.NODETYPE_ROOT :
      return NodeFilter.SHOW_DOCUMENT | NodeFilter.SHOW_DOCUMENT_FRAGMENT;
    case OpCodes.NODETYPE_FUNCTEST :
      return NodeTest.SHOW_BYFUNCTION;
    case OpCodes.NODENAME :
      switch (axesType)
      {
      case OpCodes.FROM_NAMESPACE :
        return NodeFilter.SHOW_ATTRIBUTE | NodeTest.SHOW_NAMESPACE;
      case OpCodes.FROM_ATTRIBUTES :
      case OpCodes.MATCH_ATTRIBUTE :
        return NodeFilter.SHOW_ATTRIBUTE;

      // break;
      case OpCodes.MATCH_ANY_ANCESTOR :
      case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
        return NodeFilter.SHOW_ELEMENT;

      // break;
      default :
        return NodeFilter.SHOW_ELEMENT;
      }
    default :
      // System.err.println("We should never reach here.");
      return NodeFilter.SHOW_ALL;
    }
  }

  /**
   * Compile a step pattern unit expression, used for both location paths 
   * and match patterns.
   * 
   * @param opPos The current position in the m_opMap array.
   * @param stepCount The number of steps to expect.
   * @param ancestorPattern The owning StepPattern, which may be null.
   *
   * @return reference to {@link org.apache.xpath.patterns.StepPattern} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected StepPattern stepPattern(
          int opPos, int stepCount, StepPattern ancestorPattern)
            throws TransformerException
  {

    int startOpPos = opPos;
    int stepType = getOpMap()[opPos];

    if (OpCodes.ENDOP == stepType)
      return null;

    int endStep = getNextOpPos(opPos);

    // int nextStepType = getOpMap()[endStep];
    StepPattern pattern;

    // boolean isSimple = ((OpCodes.ENDOP == nextStepType) && (stepCount == 0));
    int argLen;

    switch (stepType)
    {
    case OpCodes.OP_FUNCTION :
      argLen = m_opMap[opPos + OpMap.MAPINDEX_LENGTH];
      pattern = new FunctionPattern(compileFunction(opPos));
      break;
    case OpCodes.FROM_ROOT :
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(NodeFilter.SHOW_DOCUMENT | NodeFilter.SHOW_DOCUMENT_FRAGMENT);
      break;
    case OpCodes.MATCH_ATTRIBUTE :
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(NodeFilter.SHOW_ATTRIBUTE,
                                getStepNS(startOpPos),
                                getStepLocalName(startOpPos));
      break;
    case OpCodes.MATCH_ANY_ANCESTOR :
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new AncestorStepPattern(getWhatToShow(startOpPos),
                                        getStepNS(startOpPos),
                                        getStepLocalName(startOpPos));
      break;
    case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(getWhatToShow(startOpPos),
                                getStepNS(startOpPos),
                                getStepLocalName(startOpPos));
      break;
    default :
      error(XPATHErrorResources.ER_UNKNOWN_MATCH_OPERATION, null);  //"unknown match operation!");

      return null;
    }

    pattern.setPredicates(getCompiledPredicates(opPos + argLen));
    pattern.setRelativePathPattern(ancestorPattern);

    StepPattern relativePathPattern = stepPattern(endStep, stepCount + 1,
                                        pattern);

    return (null != relativePathPattern) ? relativePathPattern : pattern;
  }

  /**
   * Compile a zero or more predicates for a given match pattern.
   * 
   * @param opPos The position of the first predicate the m_opMap array.
   *
   * @return reference to array of {@link org.apache.xpath.Expression} instances.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression[] getCompiledPredicates(int opPos)
          throws TransformerException
  {

    int count = countPredicates(opPos);

    if (count > 0)
    {
      Expression[] predicates = new Expression[count];

      compilePredicates(opPos, predicates);

      return predicates;
    }

    return null;
  }

  /**
   * Count the number of predicates in the step.
   *
   * @param opPos The position of the first predicate the m_opMap array.
   *
   * @return The number of predicates for this step.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public int countPredicates(int opPos) throws TransformerException
  {

    int count = 0;

    while (OpCodes.OP_PREDICATE == getOp(opPos))
    {
      count++;

      opPos = getNextOpPos(opPos);
    }

    return count;
  }

  /**
   * Compiles predicates in the step.
   *
   * @param opPos The position of the first predicate the m_opMap array.
   * @param predicates An empty pre-determined array of 
   *            {@link org.apache.xpath.Expression}s, that will be filled in.
   *
   * @throws TransformerException
   */
  private void compilePredicates(int opPos, Expression[] predicates)
          throws TransformerException
  {

    for (int i = 0; OpCodes.OP_PREDICATE == getOp(opPos); i++)
    {
      predicates[i] = predicate(opPos);
      opPos = getNextOpPos(opPos);
    }
  }

  /**
   * Compile a built-in XPath function.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.functions.Function} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression compileFunction(int opPos) throws TransformerException
  {

    int endFunc = opPos + m_opMap[opPos + 1] - 1;

    opPos = getFirstChildPos(opPos);

    int funcID = m_opMap[opPos];

    opPos++;

    if (-1 != funcID)
    {
      Function func = FunctionTable.getFunction(funcID);

      try
      {
        int i = 0;

        for (int p = opPos; p < endFunc; p = getNextOpPos(p), i++)
        {

          // System.out.println("argPos: "+ p);
          // System.out.println("argCode: "+ m_opMap[p]);
          func.setArg(compile(p), i);
        }

        func.checkNumberArgs(i);
      }
      catch (WrongNumberArgsException wnae)
      {
        java.lang.String name = FunctionTable.m_functions[funcID].getName();

        m_errorHandler.fatalError( new TransformerException(name + " only allows " + wnae.getMessage()
                               + " arguments", m_locator));
      }

      return func;
    }
    else
    {
      error(XPATHErrorResources.ER_FUNCTION_TOKEN_NOT_FOUND, null);  //"function token not found.");

      return null;
    }
  }

  /**
   * Compile an extension function.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.functions.FuncExtFunction} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  private Expression compileExtension(int opPos)
          throws TransformerException
  {

    int endExtFunc = opPos + m_opMap[opPos + 1] - 1;

    opPos = getFirstChildPos(opPos);

    java.lang.String ns = (java.lang.String) m_tokenQueue[m_opMap[opPos]];

    opPos++;

    java.lang.String funcName =
      (java.lang.String) m_tokenQueue[m_opMap[opPos]];

    opPos++;

    Function extension = new FuncExtFunction(ns, funcName,

    // Create a method key, for faster lookup.
    String.valueOf(opPos) + String.valueOf(hashCode()));

    try
    {
      int i = 0;

      while (opPos < endExtFunc)
      {
        int nextOpPos = getNextOpPos(opPos);

        extension.setArg(this.compile(opPos), i);

        opPos = nextOpPos;

        i++;
      }
    }
    catch (WrongNumberArgsException wnae)
    {
      ;  // should never happen
    }

    return extension;
  }

  /**
   * Warn the user of an problem.
   *
   * @param msg An error number that corresponds to one of the numbers found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void warn(int msg, Object[] args) throws TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHWarning(msg, args);

    if (null != m_errorHandler)
    {
      m_errorHandler.warning(new TransformerException(fmsg, m_locator));
    }
    else
    {
      System.out.println(fmsg
                          +"; file "+m_locator.getSystemId()
                          +"; line "+m_locator.getLineNumber()
                          +"; column "+m_locator.getColumnNumber());
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * @param b  If false, a runtime exception will be thrown.
   * @param msg The assertion message, which should be informative.
   * 
   * @throws RuntimeException if the b argument is false.
   */
  public void assert(boolean b, java.lang.String msg)
  {

    if (!b)
    {
      java.lang.String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ msg });

      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg An error number that corresponds to one of the numbers found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void error(int msg, Object[] args) throws TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHMessage(msg, args);
    

    if (null != m_errorHandler)
    {
      m_errorHandler.fatalError(new TransformerException(fmsg, m_locator));
    }
    else
    {

      // System.out.println(te.getMessage()
      //                    +"; file "+te.getSystemId()
      //                    +"; line "+te.getLineNumber()
      //                    +"; column "+te.getColumnNumber());
      throw new TransformerException(fmsg, (SAXSourceLocator)m_locator);
    }
  }

  /**
   * The current prefixResolver for the execution context.
   */
  private PrefixResolver m_currentPrefixResolver = null;

  /**
   * Get the current namespace context for the xpath.
   *
   * @return The current prefix resolver, *may* be null, though hopefully not.
   */
  public PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Set the current namespace context for the xpath.
   *
   * @param pr The resolver for prefixes in the XPath expression.
   */
  public void setNamespaceContext(PrefixResolver pr)
  {
    m_currentPrefixResolver = pr;
  }

  /** The error listener where errors will be sent.  If this is null, errors 
   *  and warnings will be sent to System.err.  May be null.    */
  ErrorListener m_errorHandler;

  /** The source locator for the expression being compiled.  May be null. */
  SourceLocator m_locator;
}
