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

import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xalan.xpath.*;
import java.util.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import org.apache.xalan.xpath.xml.QName;
import org.apache.xalan.xslt.res.*;
// import org.apache.xalan.xpath.dtm.*;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:number.
 */
public class ElemNumber extends ElemTemplateElement
{
  /**
   * Only nodes are counted that match this pattern.
   */
  public XPath m_countMatchPattern = null;
  
  /**
   * Specifies where to count from.
   * For level="single" or level="multiple":
   * Only ancestors that are searched are 
   * those that are descendants of the nearest ancestor that matches 
   * the from pattern.
   * For level="any:
   * Only nodes after the first node before the 
   * current node that match the from pattern are considered.
   */
  public XPath m_fromMatchPattern = null;
  
  /**
   * When level="single", it goes up to the first node in the ancestor-or-self axis 
   * that matches the count pattern, and constructs a list of length one containing 
   * one plus the number of preceding siblings of that ancestor that match the count 
   * pattern. If there is no such ancestor, it constructs an empty list. If the from 
   * attribute is specified, then the only ancestors that are searched are those 
   * that are descendants of the nearest ancestor that matches the from pattern. 
   * Preceding siblings has the same meaning here as with the preceding-sibling axis.
   * 
   * When level="multiple", it constructs a list of all ancestors of the current node 
   * in document order followed by the element itself; it then selects from the list 
   * those nodes that match the count pattern; it then maps each node in the list to 
   * one plus the number of preceding siblings of that node that match the count pattern. 
   * If the from attribute is specified, then the only ancestors that are searched are 
   * those that are descendants of the nearest ancestor that matches the from pattern. 
   * Preceding siblings has the same meaning here as with the preceding-sibling axis.
   * 
   * When level="any", it constructs a list of length one containing the number of 
   * nodes that match the count pattern and belong to the set containing the current 
   * node and all nodes at any level of the document that are before the current node 
   * in document order, excluding any namespace and attribute nodes (in other words 
   * the union of the members of the preceding and ancestor-or-self axes). If the 
   * from attribute is specified, then only nodes after the first node before the 
   * current node that match the from pattern are considered.
   */
  public int m_level = Constants.NUMBERLEVEL_SINGLE;

  /**
   * The value attribute contains an expression. The expression is evaluated 
   * and the resulting object is converted to a number as if by a call to the 
   * number function. 
   */
  public XPath m_valueExpr = null;
  
  public AVT m_format_avt = null;
  public AVT m_lang_avt = null;
  public AVT m_lettervalue_avt = null;
  public AVT m_groupingSeparator_avt = null;
  public AVT m_groupingSize_avt = null;

  private transient XSLTResourceBundle thisBundle;

  /**
   * Table to help in converting decimals to roman numerals.
   * @see XSLTEngineImpl#DecimalToRoman
   * @see XSLTEngineImpl#long2roman
   */
  private final static DecimalToRoman m_romanConvertTable[] =
  {
    new DecimalToRoman(1000, "M", 900, "CM"),
    new DecimalToRoman(500, "D", 400, "CD"),
    new DecimalToRoman(100L, "C", 90L, "XC"),
    new DecimalToRoman(50L, "L", 40L, "XL"),
    new DecimalToRoman(10L, "X", 9L, "IX"),
    new DecimalToRoman(5L, "V", 4L, "IV"),
    new DecimalToRoman(1L, "I", 1L, "I")
  };

  /**
   * Chars for converting integers into alpha counts.
   * @see XSLTEngineImpl#int2alphaCount
   */
  private static char[] m_alphaCountTable = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_NUMBER;
  }

  public ElemNumber (XSLTEngineImpl processor,
                     Stylesheet stylesheetTree,
                     String name,
                     AttributeList atts,
                     int lineNumber, int columnNumber)
    throws SAXException
  {
    super(processor, stylesheetTree, name, atts, lineNumber, columnNumber);
    int nAttrs = atts.getLength();
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_LEVEL))
      {
        String levelValue = atts.getValue(i);
        if(null != levelValue)
        {
          if(levelValue.equals(Constants.ATTRVAL_MULTI))
            m_level = Constants.NUMBERLEVEL_MULTI;
          else if(levelValue.equals(Constants.ATTRVAL_ANY))
            m_level = Constants.NUMBERLEVEL_ANY;
          else if(levelValue.equals(Constants.ATTRVAL_SINGLE))
            m_level = Constants.NUMBERLEVEL_SINGLE;
          else
            error(XSLTErrorResources.ER_BAD_VAL_ON_LEVEL_ATTRIB, new Object[] {levelValue}); //"Bad value on level attribute: "+levelValue);
        }
      }
      else if(aname.equals(Constants.ATTRNAME_COUNT))
      {
        m_countMatchPattern = m_stylesheet.createMatchPattern(atts.getValue(i), this);
      }
      else if(aname.equals(Constants.ATTRNAME_FROM))
      {
        m_fromMatchPattern = m_stylesheet.createMatchPattern(atts.getValue(i), this);
      }
      else if(aname.equals(Constants.ATTRNAME_VALUE))
      {
        m_valueExpr = m_stylesheet.createXPath(atts.getValue(i), this);
      }
      else if(aname.equals(Constants.ATTRNAME_FORMAT))
      {
        m_format_avt = new AVT(aname, atts.getType(i), atts.getValue(i),
                               this, m_stylesheet, processor);
      }
      else if(aname.equals(Constants.ATTRNAME_LANG))
      {
        m_lang_avt = new AVT(aname, atts.getType(i), atts.getValue(i),
                             this, m_stylesheet, processor);
      }
      else if(aname.equals(Constants.ATTRNAME_LETTERVALUE))
      {
        //processor.warn(Constants.ATTRNAME_LETTERVALUE+" not supported yet!");
        m_lettervalue_avt = new AVT(aname, atts.getType(i), atts.getValue(i),
                                    this, m_stylesheet, processor);
      }
      else if(aname.equals(Constants.ATTRNAME_GROUPINGSEPARATOR))
      {
        String sepValue = atts.getValue(i);
        if (sepValue.length()== 1)
        {    
          m_groupingSeparator_avt = new AVT(aname, atts.getType(i), sepValue,
                                          this, m_stylesheet, processor);
        }
        else
          processor.warn(XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_VALUE, new Object[] {aname, sepValue});
      }
      else if(aname.equals(Constants.ATTRNAME_GROUPINGSIZE))
      {
        m_groupingSize_avt = new AVT(aname, atts.getType(i), atts.getValue(i),
                                     this, m_stylesheet, processor);
      }
      else if(!isAttrOK(aname, atts, i))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }

    try
    {
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, getLocale(processor, processor.getSourceNode()) );
      char[] alphabet;
      alphabet= (char[]) thisBundle.getObject(Constants.LANG_ALPHABET);
      m_alphaCountTable = alphabet;
    }
    catch(Exception e)
    {
      System.out.println(e.toString());
    }

  }

  public void execute(XSLTEngineImpl processor,
                      Node sourceTree,
                      Node sourceNode,
                      QName mode)
    throws XSLProcessorException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException,
           SAXException
  {
    super.execute(processor, sourceTree, sourceNode, mode);
    String countString = getCountString(processor, sourceTree, sourceNode);

    processor.m_resultTreeHandler.characters(countString.toCharArray(), 0, countString.length());
  }

  /**
   * Add a child to the child list.
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    error(XSLTErrorResources.ER_CANNOT_ADD, new Object[] {((ElemTemplateElement)newChild).m_elemName, this.m_elemName}); //"Can not add " +((ElemTemplateElement)newChild).m_elemName +
    //" to " + this.m_elemName);
    return null;
  }


  /**
   * Given a 'from' pattern (ala xsl:number), a match pattern
   * and a context, find the first ancestor that matches the
   * pattern (including the context handed in).
   * @param fromMatchPattern The ancestor must match this pattern.
   * @param countMatchPattern The ancestor must also match this pattern.
   * @param context The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * queries are supposed to be expanded.
   */
  Node findAncestor(XPathSupport execContext, XPath fromMatchPattern, 
                    XPath countMatchPattern,
                    Node context,
                    Element namespaceContext)
    throws org.xml.sax.SAXException
  {
    while(null != context)
    {
      if(null != fromMatchPattern)
      {
        if(fromMatchPattern.getMatchScore(execContext, context) != XPath.MATCH_SCORE_NONE)
        {
          //context = null;
          break;
        }
      }

      if(null != countMatchPattern)
      {
        if(countMatchPattern.getMatchScore(execContext, context) != XPath.MATCH_SCORE_NONE)
        {
          break;
        }
      }

      context = execContext.getParentOfNode(context);
    }
    return context;    
  }


  /**
   * Given a 'from' pattern (ala xsl:number), a match pattern
   * and a context, find the first ancestor that matches the
   * pattern (including the context handed in).
   * @param matchPatternString The match pattern.
   * @param node The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * queries are supposed to be expanded.
   */
  private Node findPrecedingOrAncestorOrSelf(XPathSupport execContext, 
                                     XPath fromMatchPattern, 
                                     XPath countMatchPattern,
                                     Node context,
                                     Element namespaceContext)
    throws org.xml.sax.SAXException
  {
    while(null != context)
    {
      if(null != fromMatchPattern)
      {
        if(fromMatchPattern.getMatchScore(execContext, context) != XPath.MATCH_SCORE_NONE)
        {
          context = null;
          break;
        }
      }

      if(null != countMatchPattern)
      {
        if(countMatchPattern.getMatchScore(execContext, context) != XPath.MATCH_SCORE_NONE)
        {
          break;
        }
      }

      Node prevSibling = context.getPreviousSibling();
      if(null == prevSibling)
      {
        context = execContext.getParentOfNode(context);
      }
      else
      {
        // Now go down the chain of children of this sibling 
        context = prevSibling.getLastChild();
        if (context == null)
          context = prevSibling;
      }
    }
    return context;
  }
  
  /**
   * Get the count match pattern, or a default value.
   */
  XPath getCountMatchPattern(XPathSupport support, Node contextNode)
    throws org.xml.sax.SAXException
  {
    XPath countMatchPattern = m_countMatchPattern;
    if(null == countMatchPattern)
    {
      XPathFactory xpathFactory = SimpleNodeLocator.factory();
      XPathProcessor xpathProcessor = new XPathProcessorImpl(support);
      countMatchPattern = xpathFactory.create();
      switch( contextNode.getNodeType())
      {
      case Node.ELEMENT_NODE:
        // countMatchPattern = m_stylesheet.createMatchPattern(contextNode.getNodeName(), this);
        xpathProcessor.initMatchPattern(countMatchPattern, contextNode.getNodeName(), this);
        break;
      case Node.ATTRIBUTE_NODE:
        // countMatchPattern = m_stylesheet.createMatchPattern("@"+contextNode.getNodeName(), this);
        xpathProcessor.initMatchPattern(countMatchPattern, "@"+contextNode.getNodeName(), this);
        break;
      case Node.CDATA_SECTION_NODE:
      case Node.TEXT_NODE:
        // countMatchPattern = m_stylesheet.createMatchPattern("text()", this);
        xpathProcessor.initMatchPattern(countMatchPattern, "text()", this);
        break;
      case Node.COMMENT_NODE:
        // countMatchPattern = m_stylesheet.createMatchPattern("comment()", this);
        xpathProcessor.initMatchPattern(countMatchPattern, "comment()", this);
        break;
      case Node.DOCUMENT_NODE:
        // countMatchPattern = m_stylesheet.createMatchPattern("/", this);
        xpathProcessor.initMatchPattern(countMatchPattern, "/", this);
        break;
      case Node.PROCESSING_INSTRUCTION_NODE:
        // countMatchPattern = m_stylesheet.createMatchPattern("pi("+contextNode.getNodeName()+")", this);
        xpathProcessor.initMatchPattern(countMatchPattern, "pi("+contextNode.getNodeName()+")", this);
        break;
      default:
        countMatchPattern = null;
      }
    }
    return countMatchPattern;
  }
  
  /**
   * Given an XML source node, get the count according to the
   * parameters set up by the xsl:number attributes.
   * @param processor The node being counted.
   * @param sourceTree The root of the source tree.
   * @param sourceNode The source node being counted.
   */
  String getCountString(XSLTEngineImpl processor, Node sourceTree, Node sourceNode)
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    int[] list = null;
    XPathSupport execContext = processor.getXMLProcessorLiaison();
    CountersTable ctable = processor.getCountersTable();

    if(null != m_valueExpr)
    {
      XObject countObj = m_valueExpr.execute(execContext, sourceNode, this);
      int count = (int)countObj.num();
      list = new int[1];
      list[0] = count;
    }
    else
    {
      if(Constants.NUMBERLEVEL_ANY == m_level)
      {
        list = new int[1];
        list[0] = ctable.countNode(execContext, this, sourceNode);
      }
      else
      {
        NodeList ancestors = getMatchingAncestors(execContext, sourceNode,
                                                  Constants.NUMBERLEVEL_SINGLE == m_level);
        int lastIndex = ancestors.getLength() - 1;
        if(lastIndex >= 0)
        {
          list = new int[lastIndex+1];
          for(int i = lastIndex; i >= 0; i--)
          {
            Node target = ancestors.item(i);
            list[lastIndex-i] = ctable.countNode(execContext, this, target);
          }
        }
      }
    }
    
    return (null != list) ? formatNumberList(processor, list, sourceNode) : "";
  }
  
  /**
   * Get the previous node to be counted.
   */
  Node getPreviousNode(XPathSupport execContext, Node pos)
    throws SAXException
  {    
    XPath countMatchPattern = getCountMatchPattern(execContext, pos);
    if(Constants.NUMBERLEVEL_ANY == m_level)
    {
      XPath fromMatchPattern = m_fromMatchPattern;

      // Do a backwards document-order walk 'till a node is found that matches 
      // the 'from' pattern, or a node is found that matches the 'count' pattern, 
      // or the top of the tree is found.
      while(null != pos)
      {            
        // Get the previous sibling, if there is no previous sibling, 
        // then count the parent, but if there is a previous sibling, 
        // dive down to the lowest right-hand (last) child of that sibling.
        Node next = pos.getPreviousSibling();
        if(null == next)
        {
          next = pos.getParentNode();
          if((null != next) && ((((null != fromMatchPattern) &&
                                  (fromMatchPattern.getMatchScore(execContext, next) !=
                                   XPath.MATCH_SCORE_NONE))) || 
                                (next.getNodeType() == Node.DOCUMENT_NODE)))
          {
            pos = null; // return null from function.
            break; // from while loop
          }
        }
        else
        {
          // dive down to the lowest right child.
          Node child = next;
          while(null != child)
          {
            child = next.getLastChild();
            if(null != child)
              next = child;
          }
        }
        pos = next;
        
        if((null != pos) && ((null == countMatchPattern) ||
                             (countMatchPattern.getMatchScore(execContext, pos) !=
                              XPath.MATCH_SCORE_NONE)))
        {
          break;
        }
      }
    }
    else // NUMBERLEVEL_MULTI or NUMBERLEVEL_SINGLE
    {
      while(null != pos)
      {            
        pos = pos.getPreviousSibling();
        if((null != pos) && ((null == countMatchPattern) ||
                             (countMatchPattern.getMatchScore(execContext, pos) !=
                              XPath.MATCH_SCORE_NONE)))
        {
          break;
        }
      }
    }
    return pos;
  }
  
  /**
   * Get the target node that will be counted..
   */
  Node getTargetNode(XPathSupport execContext, Node sourceNode)
    throws SAXException
  {
    Node target = null;
    XPath countMatchPattern = getCountMatchPattern(execContext, sourceNode);
    if(Constants.NUMBERLEVEL_ANY == m_level)
    {
      target= findPrecedingOrAncestorOrSelf(execContext, m_fromMatchPattern, 
                                            countMatchPattern,
                                            sourceNode, this);
      
    }
    else
    {
      target = findAncestor(execContext, m_fromMatchPattern,
                            countMatchPattern, sourceNode, this);
    }
    return target;
  }




  /**
   * Get the ancestors, up to the root, that match the
   * pattern.
   * @param patterns if non-null, count only nodes
   * that match this pattern, if null count all ancestors.
   * @param node Count this node and it's ancestors.
   * @return The number of ancestors that match the pattern.
   */
  NodeList getMatchingAncestors(XPathSupport execContext, 
                                       Node node, 
                                       boolean stopAtFirstFound)
    throws org.xml.sax.SAXException
  {
    MutableNodeList ancestors = new MutableNodeListImpl();
    XPath countMatchPattern = getCountMatchPattern(execContext, node);
    while( null != node )
    {
      if((null != m_fromMatchPattern) &&
         (m_fromMatchPattern.getMatchScore(execContext, node) !=
          XPath.MATCH_SCORE_NONE))
      { 
        // The following if statement gives level="single" different 
        // behavior from level="multiple", which seems incorrect according 
        // to the XSLT spec.  For now we are leaving this in to replicate 
        // the same behavior in XT, but, for all intents and purposes we 
        // think this is a bug, or there is something about level="single" 
        // that we still don't understand.
        if(!stopAtFirstFound)
          break;
      }  
      
      if(null == countMatchPattern)
        System.out.println("Programmers error! countMatchPattern should never be null!");
      
      if(countMatchPattern.getMatchScore(execContext, node) !=
         XPath.MATCH_SCORE_NONE)
      {
        ancestors.addNode(node);
        if(stopAtFirstFound)
          break;
      }
      
      node = execContext.getParentOfNode(node);
    }
    return ancestors;
  } // end getMatchingAncestors method


  /**
   * Get the locale we should be using.
   */
  Locale getLocale(XSLTEngineImpl processor, Node contextNode)
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    Locale locale = null;
    if(null != m_lang_avt)
    {
      String langValue = m_lang_avt.evaluate(processor.getExecContext(), contextNode, this,
                                             new StringBuffer());
      if(null != langValue)
      {
        // Not really sure what to do about the country code, so I use the
        // default from the system.
        // TODO: fix xml:lang handling.
        locale = new Locale(langValue.toUpperCase(),"");
        //Locale.getDefault().getDisplayCountry());
        if(null == locale)
        {
          processor.warn(null, contextNode,
                         XSLTErrorResources.WG_LOCALE_NOT_FOUND, new Object[] {langValue}); //"Warning: Could not find locale for xml:lang="+langValue);
          locale = Locale.getDefault();
        }
      }
    }
    else
    {
      locale = Locale.getDefault();
    }
    return locale;
  }

  /**
   *
   */
  private DecimalFormat getNumberFormatter(XSLTEngineImpl processor, Node contextNode)
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    Locale locale = getLocale(processor, contextNode);

    // Helper to format local specific numbers to strings.
    DecimalFormat formatter;
    synchronized(locale)
    {
      formatter = (DecimalFormat)NumberFormat.getNumberInstance(locale);
    }

    String digitGroupSepValue = (null != m_groupingSeparator_avt)
                                ?  m_groupingSeparator_avt.evaluate(processor.getExecContext(),
                                                                    contextNode, this,
                                                                    new StringBuffer())
                                   : null;

    String nDigitsPerGroupValue = (null != m_groupingSize_avt)
                                  ?  m_groupingSize_avt.evaluate(processor.getExecContext(),
                                                                 contextNode, this,
                                                                 new StringBuffer())
                                     : null;

    // TODO: Handle digit-group attributes
    if((null != digitGroupSepValue) && (null != nDigitsPerGroupValue))
    {
      try 
      {
        formatter.setGroupingSize(Integer.valueOf(nDigitsPerGroupValue).intValue());	
        formatter.getDecimalFormatSymbols().setGroupingSeparator(digitGroupSepValue.charAt(0));	
        formatter.setGroupingUsed(true);
      }	
      catch(NumberFormatException ex)
      {
        formatter.setGroupingUsed(false);
      }
      
    }

    return formatter;
  }

  /**
   * Format a vector of numbers into a formatted string.
   * @param xslNumberElement Element that takes %conversion-atts; attributes.
   * @param list Array of one or more integer numbers.
   * @return String that represents list according to
   * %conversion-atts; attributes.
   * TODO: Optimize formatNumberList so that it caches the last count and
   * reuses that info for the next count.
   */
  String formatNumberList(XSLTEngineImpl processor, int[] list, Node contextNode)
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    StringBuffer formattedNumber = new StringBuffer();
    int nNumbers = list.length, numberWidth = 1;
    char numberType = '1';
    String formatToken, lastSepString = null, formatTokenString = null;
    // If a seperator hasn't been specified, then use "."  
    // as a default separator. 
    // For instance: [2][1][5] with a format value of "1 "
    // should format to "2.1.5 " (I think).
    // Otherwise, use the seperator specified in the format string.
    // For instance: [2][1][5] with a format value of "01-001. "
    // should format to "02-001-005 ".
    String lastSep = ".";                
    boolean isFirstToken = true;        // true if first token  

    String formatValue = (null != m_format_avt)
                         ? m_format_avt.evaluate(processor.getExecContext(), contextNode, this,
                                                 new StringBuffer())
                           : null;
    if(null == formatValue) formatValue = "1";

    NumberFormatStringTokenizer formatTokenizer = new NumberFormatStringTokenizer(formatValue);
    
    int sepCount = 0;                  // keep track of seperators
    // Loop through all the numbers in the list.
    for(int i = 0; i < nNumbers; i++)
    {
      // Loop to the next digit, letter, or separator.
      if(formatTokenizer.hasMoreTokens())
      {
        formatToken = formatTokenizer.nextToken();
        
        // If the first character of this token is a character or digit, then 
        // it is a number format directive.
        if(Character.isLetterOrDigit(formatToken.charAt(formatToken.length()-1)))
        {
          numberWidth = formatToken.length();
          numberType = formatToken.charAt(numberWidth-1);
        }
        // If there is a number format directive ahead, 
        // then append the formatToken.
        else if(formatTokenizer.isLetterOrDigitAhead())
        {          
          formatTokenString = formatToken;
          
          // Append the formatToken string...
          // For instance [2][1][5] with a format value of "1--1. "
          // should format to "2--1--5. " (I guess).
          while(formatTokenizer.nextIsSep())
          {
            formatToken = formatTokenizer.nextToken();
            formatTokenString += formatToken;
          }
          // Record this separator, so it can be used as the 
          // next separator, if the next is the last.
          // For instance: [2][1][5] with a format value of "1-1 "
          // should format to "2-1-5 ".
          if (!isFirstToken)
            lastSep = formatTokenString;
          
          // Since we know the next is a number or digit, we get it now.
          formatToken = formatTokenizer.nextToken();
          numberWidth = formatToken.length();
          numberType = formatToken.charAt(numberWidth-1);
        }
        else // only separators left
        {
          // Set up the string for the trailing characters after 
          // the last number is formatted (i.e. after the loop).
          lastSepString = formatToken;
          
          // And append any remaining characters to the lastSepString.
          while(formatTokenizer.hasMoreTokens())
          {
            formatToken = formatTokenizer.nextToken();
            lastSepString += formatToken;
          }
        } // else
        
      } // end if(formatTokenizer.hasMoreTokens())
      
      // if this is the first token and there was a prefix
      // append the prefix else, append the separator
      // For instance, [2][1][5] with a format value of "(1-1.) "
      // should format to "(2-1-5.) " (I guess).
      if(null != formatTokenString && isFirstToken)
      {
        formattedNumber.append(formatTokenString);
      }  
      else if(null != lastSep && !isFirstToken)
        formattedNumber.append(lastSep);
      
      getFormattedNumber(processor, contextNode, numberType, numberWidth, list[i], formattedNumber);
      isFirstToken = false;              // After the first pass, this should be false
      
    } // end for loop
    

    // Check to see if we finished up the format string...
    
    // Skip past all remaining letters or digits
    while(formatTokenizer.isLetterOrDigitAhead())
      formatTokenizer.nextToken();
    
    if(lastSepString != null)
      formattedNumber.append(lastSepString);
    
    while(formatTokenizer.hasMoreTokens())
    {
      formatToken = formatTokenizer.nextToken();
      formattedNumber.append(formatToken);
    }

    return formattedNumber.toString();
  } // end formatNumberList method

  /*
  * Get Formatted number
  */
  private void getFormattedNumber(XSLTEngineImpl processor,
                                  Node contextNode,
                                  char numberType,
                                  int numberWidth,
                                  int listElement,
                                  StringBuffer formattedNumber)
    throws org.xml.sax.SAXException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException
  {
    DecimalFormat formatter = getNumberFormatter(processor, contextNode);
    String padString = formatter.format(0);
    String letterVal = (m_lettervalue_avt != null) ? m_lettervalue_avt.evaluate(processor.getExecContext(),
                                                                                contextNode, this,
                                                                                new StringBuffer()) : null;
    switch(numberType)
    {
    case 'A':
      int2alphaCount(listElement, m_alphaCountTable, formattedNumber);
      break;
    case 'a':
      {
        StringBuffer stringBuf = new StringBuffer();
        int2alphaCount(listElement, m_alphaCountTable, stringBuf);
        formattedNumber.append(stringBuf.toString().toLowerCase(getLocale(processor, contextNode)));
      }
      break;
    case 'I':
      formattedNumber.append( long2roman(listElement, true));
      break;
    case 'i':
      formattedNumber.append( long2roman(listElement, true).toLowerCase( getLocale(processor, contextNode)));
      break;
    case 0x3042:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("ja","JP","HA" ) );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        formattedNumber.append( int2singlealphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET)));			
      break;
    case 0x3044:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("ja","JP", "HI") );
      if ((letterVal != null) && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        formattedNumber.append( int2singlealphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET)));			
      break;
    case 0x30A2:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("ja","JP","A" ) );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        formattedNumber.append( int2singlealphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET)));			
      break;
    case 0x30A4:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("ja","JP", "I") );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        formattedNumber.append( int2singlealphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET)));			
      break;
    case 0x4E00:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("zh","CN" ) );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
      {
        formattedNumber.append(tradAlphaCount(listElement));
      }	
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        int2alphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET), formattedNumber);
      break;
    case 0x58F9:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("zh","TW") );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        int2alphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET), formattedNumber);			
      break;
    case 0x0E51:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("th","") );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        int2alphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET), formattedNumber);	
      break;
    case 0x05D0:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("he","") );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        int2alphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET), formattedNumber);	
      break;
    case 0x10D0:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("ka","") );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        int2alphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET), formattedNumber);	
      break;
    case 0x03B1:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("el","") );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        int2alphaCount(listElement, (char[])thisBundle.getObject(Constants.LANG_ALPHABET), formattedNumber);		
      break;
    case 0x0430:
      thisBundle = (XSLTResourceBundle)XSLTResourceBundle.loadResourceBundle( Constants.LANG_BUNDLE_NAME, new Locale("cy","") );
      if (letterVal != null && letterVal.equals(Constants.ATTRVAL_TRADITIONAL))
        formattedNumber.append( tradAlphaCount(listElement));
      else //if (m_lettervalue_avt != null && m_lettervalue_avt.equals(Constants.ATTRVAL_ALPHABETIC))
        int2alphaCount(listElement,(char[])thisBundle.getObject(Constants.LANG_ALPHABET), formattedNumber);		
      break;
    default: // "1"
      String numString = formatter.format(listElement);
      int nPadding = numberWidth - numString.length();
      for(int k = 0; k < nPadding; k++)
      {
        formattedNumber.append(padString);
      }
      formattedNumber.append(numString);
    }
    
  }

  /**
   * Convert a long integer into alphabetic counting, in other words
   * count using the sequence A B C ... Z.
   * @param val Value to convert -- must be greater than zero.
   * @param table a table containing one character for each digit in the radix
   * @return String representing alpha count of number.
   * @see XSLTEngineImpl#DecimalToRoman
   *
   * Note that the radix of the conversion is inferred from the size
   * of the table.
   */
  protected String int2singlealphaCount(int val, char [] table)
  {
    int radix = table.length;

    // TODO:  throw error on out of range input
    if (val > radix)
      return "#E("+val+")";
    else
      return (new Character(table[val-1])).toString();        // index into table is off one, starts at 0
    
  }

  /**
   * Convert a long integer into alphabetic counting, in other words
   * count using the sequence A B C ... Z AA AB AC.... etc.
   * @param val Value to convert -- must be greater than zero.
   * @param table a table containing one character for each digit in the radix
   * @return String representing alpha count of number.
   * @see XSLTEngineImpl#DecimalToRoman
   *
   * Note that the radix of the conversion is inferred from the size
   * of the table.
   */
  protected void int2alphaCount(int val, char [] aTable, StringBuffer stringBuf)
  {

    int radix = aTable.length;
    char[] table = new char[aTable.length];
    // start table at 1, add last char at index 0. Reason explained above and below.
    int i;
    for (i=0; i<aTable.length-1;i++)		
      table[i+1] = aTable[i];
    table[0] = aTable[i];

    // Create a buffer to hold the result
    // TODO:  size of the table can be detereined by computing
    // logs of the radix.  For now, we fake it.
    char buf[] = new char[100];

    //some languages go left to right(ie. english), right to left (ie. Hebrew),
    //top to bottom (ie.Japanese), etc... Handle them differently
    //String orientation = thisBundle.getString(Constants.LANG_ORIENTATION);

    // next character to set in the buffer
    int charPos;
    charPos= buf.length -1 ;    // work backward through buf[]	
    

    // index in table of the last character that we stored
    int lookupIndex = 1;  // start off with anything other than zero to make correction work

    

    //						Correction number
    //
    //	Correction can take on exactly two values:
    //
    //		0	if the next character is to be emitted is usual
    //
    //      radix - 1
    //			if the next char to be emitted should be one less than
    //			you would expect
    //			
    // For example, consider radix 10, where 1="A" and 10="J"
    //
    // In this scheme, we count: A, B, C ...   H, I, J (not A0 and certainly
    // not AJ), A1
    //
    // So, how do we keep from emitting AJ for 10?  After correctly emitting the
    // J, lookupIndex is zero.  We now compute a correction number of 9 (radix-1).
    // In the following line, we'll compute (val+correction) % radix, which is,
    // (val+9)/10.  By this time, val is 1, so we compute (1+9) % 10, which
    // is 10 % 10 or zero.  So, we'll prepare to emit "JJ", but then we'll
    // later suppress the leading J as representing zero (in the mod system,
    // it can represent either 10 or zero).  In summary, the correction value of
    // "radix-1" acts like "-1" when run through the mod operator, but with the
    // desireable characteristic that it never produces a negative number.

    int correction = 0;

    // TODO:  throw error on out of range input

    do
    {
      // most of the correction calculation is explained above,  the reason for the
      // term after the "|| " is that it correctly propagates carries across
      // multiple columns.
      correction = ((lookupIndex == 0) ||
                    (correction != 0 && lookupIndex == radix-1 )) ? (radix-1) : 0;

      // index in "table" of the next char to emit
      lookupIndex  = (val+correction) % radix;

      // shift input by one "column"
      val = (val / radix);

      // if the next value we'd put out would be a leading zero, we're done.
      if (lookupIndex == 0 && val == 0)
        break;

      // put out the next character of output
      buf[charPos--] = table[lookupIndex];  // left to right or top to bottom  	
    }
      while (val > 0);
    
    stringBuf.append(buf, charPos+1, (buf.length - charPos -1));
  }

  /**
   *Convert a long integer into traditional alphabetic counting, in other words
   * count using the traditional numbering.
   * @param val Value to convert -- must be greater than zero.
   * @param table a table containing one character for each digit in the radix
   * @return String representing alpha count of number.
   * @see XSLProcessor#DecimalToRoman
   *
   * Note that the radix of the conversion is inferred from the size
   * of the table.
   */
  protected String tradAlphaCount(int val)
  {
    // if this number is larger than the largest number we can represent, error!
    //if (val > ((Integer)thisBundle.getObject("MaxNumericalValue")).intValue())
    //return XSLTErrorResources.ERROR_STRING;
    char[] table = null;
    // index in table of the last character that we stored
    int lookupIndex = 1;  // start off with anything other than zero to make correction work
    // Create a buffer to hold the result
    // TODO:  size of the table can be detereined by computing
    // logs of the radix.  For now, we fake it.
    char buf[] = new char[100];
    
    //some languages go left to right(ie. english), right to left (ie. Hebrew),
    //top to bottom (ie.Japanese), etc... Handle them differently
    //String orientation = thisBundle.getString(Constants.LANG_ORIENTATION);

    // next character to set in the buffer
    int charPos;
    charPos= 0;                  //start at 0
    
    // array of number groups: ie.1000, 100, 10, 1
    int[] groups = (int[])thisBundle.getObject(Constants.LANG_NUMBERGROUPS);	
    
    // array of tables of hundreds, tens, digits...
    String[] tables = (String[])(thisBundle.getObject(Constants.LANG_NUM_TABLES));
    
    
    //some languages have additive alphabetical notation,
    //some multiplicative-additive, etc... Handle them differently.
    String numbering = thisBundle.getString(Constants.LANG_NUMBERING);	
    
    // do multiplicative part first
    if (numbering.equals(Constants.LANG_MULT_ADD))
    {
      String mult_order = thisBundle.getString(Constants.MULT_ORDER);
      int[] multiplier = (int[])(thisBundle.getObject(Constants.LANG_MULTIPLIER));	
      char[]zeroChar = (char[])thisBundle.getObject("zero");			
      
      int i= 0;
      // skip to correct multiplier
      while (i < multiplier.length && val < multiplier[i] )
        i++;
      
      do
      {
        if (i >= multiplier.length)
          break;              //number is smaller than multipliers
        
        // some languages (ie chinese) put a zero character (and only one) when
        // the multiplier is multiplied by zero. (ie, 1001 is 1X1000 + 0X100 + 0X10 + 1)
        // 0X100 is replaced by the zero character, we don't need one for 0X10
        if (val< multiplier[i])
        {
          if (zeroChar.length == 0)
          {
            i++;
          }	
          else
          {
            if (buf[charPos-1]!= zeroChar[0])
              buf[charPos++] = zeroChar[0];
            i++;
          }
        }	
        else if (val>= multiplier[i])
        {	
          int mult = val/multiplier[i];
          val = val % multiplier[i];         // save this.
          
          int k = 0;
          while (k < groups.length)
          {
            lookupIndex = 1;                 // initialize for each table
            if (mult/groups[k]<= 0)		     // look for right table
              k++;
            else
            {
              // get the table
              char[] THEletters= (char[]) thisBundle.getObject(tables[k]);					
              table = new char[THEletters.length+1];					
              int j;
              for (j=0; j<THEletters.length;j++)		
                table[j+1] = THEletters[j];
              table[0] = THEletters[j-1];    // don't need this										
              // index in "table" of the next char to emit
              lookupIndex  = mult/ groups[k];

              //this should not happen
              if (lookupIndex == 0 && mult == 0)
                break;
              char multiplierChar = ((char[])(thisBundle.getObject(Constants.LANG_MULTIPLIER_CHAR)))[i];
              // put out the next character of output	
              if (lookupIndex < table.length)
              {				
                if( mult_order.equals(Constants.MULT_PRECEDES))
                {
                  buf[charPos++] = multiplierChar;  				
                  buf[charPos++] = table[lookupIndex];							
                }	
                else
                {	
                  // don't put out 1 (ie 1X10 is just 10)
                  if (lookupIndex == 1 && i == multiplier.length -1)
                  {}
                  else
                    buf[charPos++] =  table[lookupIndex];
                  buf[charPos++] =  multiplierChar ;
                }
                
                break;       // all done!
              }
              else
                return XSLTErrorResources.ERROR_STRING;
            } //end else
          } // end while	
          
          i++;
        } // end else if
      } // end do while
              while ( i < multiplier.length);	  	
    }

    // Now do additive part...
    
    int count = 0;
    String tableName;
    // do this for each table of hundreds, tens, digits...
    while (count < groups.length)
    {
      if (val/groups[count]<= 0)		 // look for correct table
        count++;
      else
      {
        char[] theletters= (char[]) thisBundle.getObject(tables[count]);			
        table = new char[theletters.length+1];
        int j;
        // need to start filling the table up at index 1
        for (j=0; j<theletters.length;j++)
        {
          table[j+1] = theletters[j];
        }	
        table[0] = theletters[j-1];  // don't need this
        
        // index in "table" of the next char to emit
        lookupIndex  = val / groups[count];

        // shift input by one "column"
        val = val % groups[count];

        // this should not happen
        if (lookupIndex == 0 && val == 0)
          break;					
        
        if (lookupIndex < table.length)
        {
          // put out the next character of output	
          buf[charPos++] = table[lookupIndex];  // left to right or top to bottom					
        }
        else
          return XSLTErrorResources.ERROR_STRING;
        count++;
      }
    } // end while

    String s = new String(buf, 0, charPos);
    return new String(buf, 0,  charPos);
  }

  /**
   * Convert a long integer into roman numerals.
   * @param val Value to convert.
   * @param prefixesAreOK true_ to enable prefix notation (e.g. 4 = "IV"),
   * false_ to disable prefix notation (e.g. 4 = "IIII").
   * @return Roman numeral string.
   * @see DecimalToRoman
   * @see m_romanConvertTable
   */
  protected String long2roman(long val, boolean prefixesAreOK)
  {
    if(val <= 0)
    {
      return "#E("+val+")";
    }

    String roman = "";
    int place = 0;
    if (val <= 3999L)
    {
      do
      {
        while (val >= m_romanConvertTable[place].m_postValue)
        {
          roman += m_romanConvertTable[place].m_postLetter;
          val -= m_romanConvertTable[place].m_postValue;
        }
        if (prefixesAreOK)
        {
          if (val >= m_romanConvertTable[place].m_preValue)
          {
            roman += m_romanConvertTable[place].m_preLetter;
            val -= m_romanConvertTable[place].m_preValue;
          }
        }
        place++;
      }
        while (val > 0);
    }
    else
    {
      roman = XSLTErrorResources.ERROR_STRING;
    }
    return roman;
  } // end long2roman

/**
 * This class returns tokens using non-alphanumberic
 * characters as delimiters.
 */
  class NumberFormatStringTokenizer
  {
    private int currentPosition;
    private int maxPosition;
    private String str;

    /**
     * Construct a NumberFormatStringTokenizer.
     */
    public NumberFormatStringTokenizer(String str)
    {
      this.str = str;
      maxPosition = str.length();
    }

    /**
     * Reset tokenizer so that nextToken() starts from the beginning.
     */
    public void reset()
    {
      currentPosition = 0;
    }

    /**
     * Returns the next token from this string tokenizer.
     *
     * @return     the next token from this string tokenizer.
     * @exception  NoSuchElementException  if there are no more tokens in this
     *               tokenizer's string.
     */
    public String nextToken()
    {
      if (currentPosition >= maxPosition)
      {
        throw new NoSuchElementException();
      }

      int start = currentPosition;
      while ((currentPosition < maxPosition) &&
             Character.isLetterOrDigit(str.charAt(currentPosition)))
      {
        currentPosition++;
      }
      if ((start == currentPosition) &&
          (!Character.isLetterOrDigit(str.charAt(currentPosition))))
      {
        currentPosition++;
      }
      return str.substring(start, currentPosition);
    }
    
    /**
     * Tells if there is a digit or a letter character ahead.
     *
     * @return     true if there is a number or character ahead.
     */
    public boolean isLetterOrDigitAhead()
    {
      int pos = currentPosition;

      while (pos < maxPosition)
      {
        if(Character.isLetterOrDigit(str.charAt(pos)))
          return true;
        pos++;
      }
      return false;
    }

    /**
     * Tells if there is a digit or a letter character ahead.
     *
     * @return     true if there is a number or character ahead.
     */
    public boolean nextIsSep()
    {
      if(Character.isLetterOrDigit(str.charAt(currentPosition)))
        return false;
      else
        return true;
    }


    /**
     * Tells if <code>nextToken</code> will throw an exception
     * if it is called.
     *
     * @return true if <code>nextToken</code> can be called
     * without throwing an exception.
     */
    public boolean hasMoreTokens()
    {
      return (currentPosition >= maxPosition) ? false : true;
    }

    /**
     * Calculates the number of times that this tokenizer's
     * <code>nextToken</code> method can be called before it generates an
     * exception.
     *
     * @return  the number of tokens remaining in the string using the current
     *          delimiter set.
     * @see     java.util.StringTokenizer#nextToken()
     */
    public int countTokens()
    {
      int count = 0;
      int currpos = currentPosition;

      while (currpos < maxPosition)
      {
        int start = currpos;
        while ((currpos < maxPosition) &&
               Character.isLetterOrDigit(str.charAt(currpos)))
        {
          currpos++;
        }
        if ((start == currpos) &&
            (Character.isLetterOrDigit(str.charAt(currpos)) == false))
        {
          currpos++;
        }
        count++;
      }
      return count;
    }

  } // end NumberFormatStringTokenizer
}