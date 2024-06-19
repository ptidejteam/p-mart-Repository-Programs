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
import java.text.DecimalFormatSymbols;
import org.apache.xalan.xpath.xml.QName;
import org.apache.xalan.xslt.res.*;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:decimal-format.
 */
public class ElemDecimalFormat extends ElemTemplateElement
{
  public XPath m_countMatchPattern = null;
  public XPath m_fromMatchPattern = null;
  public XPath m_valueExpr = null;
  public String m_name_avt = null;
  public String m_decimalSeparator_avt= null;
  public String m_groupingSeparator_avt = null;
  public String m_infinity_avt = null;
  public String m_minusSign_avt = null;
  public String m_NaN_avt = null;
  public String m_percent_avt = null;
  public String m_permille_avt = null;
  public String m_zeroDigit_avt = null;
  public String m_digit_avt = null;
  public String m_patternSeparator_avt = null;


  private XSLTResourceBundle thisBundle;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_DECIMALFORMAT;
  }

  public ElemDecimalFormat (XSLTEngineImpl processor,
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
      if(aname.equals(Constants.ATTRNAME_NAME))
      {
       m_name_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_DECIMALSEPARATOR))
      {
        m_decimalSeparator_avt = atts.getValue(i);
      }
	    else if(aname.equals(Constants.ATTRNAME_GROUPINGSEPARATOR))
      {
        m_groupingSeparator_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_INFINITY))
      {
        m_infinity_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_MINUSSIGN))
      {
        m_minusSign_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_NAN))
      {
        m_NaN_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_PERCENT))
      {
        m_percent_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_PERMILLE))
      {
        m_permille_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_ZERODIGIT))
      {
        m_zeroDigit_avt = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_DIGIT))
      {
        m_digit_avt = atts.getValue(i);
      }
	    else if(aname.equals(Constants.ATTRNAME_PATTERNSEPARATOR))
      {
        m_patternSeparator_avt = atts.getValue(i);
      }
      else if(!isAttrOK(aname, atts, i))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if (null == m_infinity_avt)
      m_infinity_avt = Constants.ATTRVAL_INFINITY;
    if (null == m_NaN_avt)
      m_NaN_avt = Constants.ATTRVAL_NAN;

    // Look for the default decimal-format element
    if (null == m_name_avt)
    {
      if (null != m_stylesheet.getDecimalFormatElem(Constants.DEFAULT_DECIMAL_FORMAT))
        processor.warn(XSLTErrorResources.WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED); //Only one default Decimal format is allowed
      m_name_avt = Constants.DEFAULT_DECIMAL_FORMAT;
    }
    // Look for duplicate decimal-format names
    else
    {
      if (null != m_stylesheet.getDecimalFormatElem(m_name_avt))
        processor.warn(XSLTErrorResources.WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE); // All declarations have to be unique
    }
  }

 /*
  *  Return the name of the decimal format
  */
  public String getName()
  {
	  return m_name_avt;
  }

 /*
  *  Return the decimal format Symbols for this element
  */
  public DecimalFormatSymbols getDecimalFormatSymbols()
  {
	  DecimalFormatSymbols dfs = new java.text.DecimalFormatSymbols();
	  if (m_decimalSeparator_avt != null)
		dfs.setDecimalSeparator(m_decimalSeparator_avt.charAt(0));
	  if (m_digit_avt != null)
		dfs.setDigit(m_digit_avt.charAt(0));
	  if (m_groupingSeparator_avt != null)
		dfs.setGroupingSeparator(m_groupingSeparator_avt.charAt(0));
	  if (m_infinity_avt != null)
		dfs.setInfinity(m_infinity_avt);
	  if (m_minusSign_avt != null)
		dfs.setMinusSign(m_minusSign_avt.charAt(0));
	  if (m_NaN_avt != null)
		dfs.setNaN(m_NaN_avt);
	  if (m_patternSeparator_avt != null)
		dfs.setPatternSeparator(m_patternSeparator_avt.charAt(0));
	  if (m_percent_avt != null)
		dfs.setPercent(m_percent_avt.charAt(0));
	  if (m_permille_avt != null)
		dfs.setPerMill(m_permille_avt.charAt(0));
	  if (m_zeroDigit_avt != null)
		dfs.setZeroDigit(m_zeroDigit_avt.charAt(0));
	  return dfs;
  }

/* do I need this??
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
  }*/

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

}
