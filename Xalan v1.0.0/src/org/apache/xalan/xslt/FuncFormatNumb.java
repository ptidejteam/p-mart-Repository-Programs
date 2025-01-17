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
import java.util.Vector;
import org.apache.xalan.xpath.*;
import org.apache.xalan.xpath.xml.*;
import org.apache.xalan.xslt.res.XSLTErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the FormatNumber() function.
 */
public class FuncFormatNumb extends Function
{
  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param path The executing xpath.
   * @param context The current context.
   * @param opPos The current op position.
   * @param args A list of XObject arguments.
   * @return A valid XObject.
   */
  public XObject execute(XPath path, XPathSupport execContext,
                         Node context, int opPos, Vector args)
    throws org.xml.sax.SAXException
  {
    // A bit of an ugly hack to get our context.
    ElemTemplateElement templElem = (ElemTemplateElement)execContext.getNamespaceContext();
    Stylesheet ss = templElem.m_stylesheet;

    java.text.DecimalFormat formatter = null;
    java.text.DecimalFormatSymbols dfs = null;
    double num = ((XObject)args.elementAt(0)).num();
    String patternStr = ((XObject)args.elementAt(1)).str();
    // TODO: what should be the behavior here??
    if (patternStr.indexOf(0x00A4)> 0)
      ss.error(XSLTErrorResources.ER_CURRENCY_SIGN_ILLEGAL); // currency sign not allowed
    int nArgs = args.size();
    // this third argument is not a locale name. It is the name of a
    // decimal-format declared in the stylesheet!(xsl:decimal-format
    try
    {
      if(nArgs == 3)
      {
        String formatStr = ((XObject)args.elementAt(2)).str();
        dfs = ss.getDecimalFormatElem(formatStr);
        if (null == dfs)
        {
          warn(execContext, XSLTErrorResources.WG_NO_DECIMALFORMAT_DECLARATION, new Object[]{formatStr}); //"not found!!!
          //formatter = new java.text.DecimalFormat(patternStr);
        }
        else
        {
          //formatter = new java.text.DecimalFormat(patternStr, dfs);
          formatter = new java.text.DecimalFormat();
          formatter.setDecimalFormatSymbols(dfs);
          formatter.applyLocalizedPattern(patternStr);
        }

      }
      //else
      if (null == formatter)
      {
        // Try for default decimal-format
        dfs = ss.getDecimalFormatElem(Constants.DEFAULT_DECIMAL_FORMAT);
        if (null == dfs)
          formatter = new java.text.DecimalFormat(patternStr);
        else
          //formatter = new java.text.DecimalFormat(patternStr, dfs);
          formatter = new java.text.DecimalFormat();
          formatter.setDecimalFormatSymbols(dfs);
          formatter.applyLocalizedPattern(patternStr);
      }
      return new XString(formatter.format(num));
    }
    catch(Exception iae)
    {
      ss.error(XSLTErrorResources.ER_MALFORMED_FORMAT_STRING, new Object[]{patternStr});
      return new XString("");
      //throw new XSLProcessorException(iae);
    }
  }

  /**
   * Warn the user of a problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(XPathSupport execContext, int msg, Object args[])
    throws org.xml.sax.SAXException
  {
    String formattedMsg = XSLMessages.createWarning(msg, args);

    XMLParserLiaison parserLiaison = (XMLParserLiaison)execContext;
    boolean shouldThrow = parserLiaison.getProblemListener().problem(ProblemListener.XSLPROCESSOR,
                                                    ProblemListener.WARNING,
                                                    null, null, formattedMsg,
                                                    null, 0, 0);

    if(shouldThrow)
    {
      throw new XSLProcessorException(formattedMsg);
    }
  }

}
