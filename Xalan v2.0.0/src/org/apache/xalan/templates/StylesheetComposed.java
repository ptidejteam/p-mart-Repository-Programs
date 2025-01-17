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
package org.apache.xalan.templates;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.xml.transform.TransformerConfigurationException;
import org.apache.xpath.XPath;
import org.apache.xml.utils.QName;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Represents a stylesheet that has methods that resolve includes and
 * imports.  It has methods on it that
 * return "composed" properties, which mean that:
 * <ol>
 * <li>Properties that are aggregates, like OutputProperties, will
 * be composed of properties declared in this stylsheet and all
 * included stylesheets.</li>
 * <li>Properties that aren't found, will be searched for first in
 * the includes, and, if none are located, will be searched for in
 * the imports.</li>
 * <li>Properties in that are not atomic on a stylesheet will
 * have the form getXXXComposed. Some properties, like version and id,
 * are not inherited, and so won't have getXXXComposed methods.</li>
 * </ol>
 * <p>In some cases getXXXComposed methods may calculate the composed
 * values dynamically, while in other cases they may store the composed
 * values.</p>
 */
public class StylesheetComposed extends Stylesheet
{

  /**
   * Uses an XSL stylesheet document.
   * @param parent  The including or importing stylesheet.
   */
  public StylesheetComposed(Stylesheet parent)
  {
    super(parent);
  }

  /**
   * Tell if this can be cast to a StylesheetComposed, meaning, you
   * can ask questions from getXXXComposed functions.
   *
   * @return True since this is a StylesheetComposed 
   */
  public boolean isAggregatedType()
  {
    return true;
  }

  /**
   * Adds all recomposable values for this precedence level into the recomposableElements Vector
   * that was passed in as the first parameter.  All elements added to the
   * recomposableElements vector should extend ElemTemplateElement.
   * @param recomposableElements a Vector of ElemTemplateElement objects that we will add all of
   *        our recomposable objects to.
   */
  public void recompose(Vector recomposableElements) throws TransformerException
  {

    recomposeImports();         // Calculate the number of this import.
    recomposeIncludes(this);    // Build the global include list for this stylesheet.

    // Now add in all of the recomposable elements at this precedence level

    int n = getIncludeCountComposed();

    for (int i = -1; i < n; i++)
    {
      Stylesheet included = getIncludeComposed(i);

      // Add in the output elements

      int s = included.getOutputCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getOutput(j));
      }

      // Next, add in the attribute-set elements

      s = included.getAttributeSetCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getAttributeSet(j));
      }

      // Now the decimal-formats

      s = included.getDecimalFormatCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getDecimalFormat(j));
      }

      // Now the keys

      s = included.getKeyCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getKey(j));
      }

      // And the namespace aliases

      s = included.getNamespaceAliasCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getNamespaceAlias(j));
      }

      // Next comes the templates

      s = included.getTemplateCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getTemplate(j));
      }

      // Then, the variables

      s = included.getVariableOrParamCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getVariableOrParam(j));
      }

      // And lastly the whitespace preserving and stripping elements

      s = included.getStripSpaceCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getStripSpace(j));
      }

      s = included.getPreserveSpaceCount();
      for (int j = 0; j < s; j++)
      {
        recomposableElements.addElement(included.getPreserveSpace(j));
      }
    }
  }

  /** Order in import chain.
   *  @serial         */
  private int m_importNumber = -1;

  /** The precedence of this stylesheet in the global import list.
   *  The lowest precedence stylesheet is 0.  A higher
   *  number has a higher precedence.
   *  @serial
   */
  private int m_importCountComposed;

  /**
   * Recalculate the precedence of this stylesheet in the global
   * import list.  The lowest precedence stylesheet is 0.  A higher
   * number has a higher precedence.
   */
  void recomposeImports()
  {

    m_importNumber = getStylesheetRoot().getImportNumber(this);

    StylesheetRoot root = getStylesheetRoot();
    int globalImportCount = root.getGlobalImportCount();

    m_importCountComposed = (globalImportCount - m_importNumber) - 1;
  }

  /**
   * Get a stylesheet from the "import" list.
   * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
   *
   * @param i Index of stylesheet in import list 
   *
   * @return The stylesheet at the given index
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public StylesheetComposed getImportComposed(int i)
          throws ArrayIndexOutOfBoundsException
  {

    StylesheetRoot root = getStylesheetRoot();

    // Get the stylesheet that is offset past this stylesheet.
    // Thus, if the index of this stylesheet is 3, an argument 
    // to getImportComposed of 0 will return the 4th stylesheet 
    // in the global import list.
    return root.getGlobalImport(1 + m_importNumber + i);
  }

  /**
   * Get the precedence of this stylesheet in the global import list.
   * The lowest precedence is 0.  A higher number has a higher precedence.
   * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
   *
   * @return the precedence of this stylesheet in the global import list.
   */
  public int getImportCountComposed()
  {
    return m_importCountComposed;
  }

  /**
   * The combined list of includes.
   * @serial
   */
  private transient Vector m_includesComposed;

  /**
   * Recompose the value of the composed include list.  Builds a composite
   * list of all stylesheets included by this stylesheet to any depth.
   *
   * @param including Stylesheet to recompose
   */
  void recomposeIncludes(Stylesheet including)
  {

    int n = including.getIncludeCount();

    if (n > 0)
    {
      if (null == m_includesComposed)
        m_includesComposed = new Vector();

      for (int i = 0; i < n; i++)
      {
        Stylesheet included = including.getInclude(i);
        m_includesComposed.addElement(included);
        recomposeIncludes(included);
      }
    }
  }

  /**
   * Get an "xsl:include" property.
   * @see <a href="http://www.w3.org/TR/xslt#include">include in XSLT Specification</a>
   *
   * @param i Index of stylesheet in "include" list 
   *
   * @return The stylesheet at the given index in the "include" list 
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public Stylesheet getIncludeComposed(int i)
          throws ArrayIndexOutOfBoundsException
  {

    if (-1 == i)
      return this;

    if (null == m_includesComposed)
      throw new ArrayIndexOutOfBoundsException();

    return (Stylesheet) m_includesComposed.elementAt(i);
  }

  /**
   * Get the number of included stylesheets.
   * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
   *
   * @return the number of included stylesheets.
   */
  public int getIncludeCountComposed()
  {
    return (null != m_includesComposed) ? m_includesComposed.size() : 0;
  }

  /**
   * For compilation support, we need the option of overwriting
   * (rather than appending to) previous composition.
   * We could phase out the old API in favor of this one, but I'm
   * holding off until we've made up our minds about compilation.
   * ADDED 9/5/2000 to support compilation experiment.
   * NOTE: GLP 29-Nov-00 I've left this method in so that CompilingStylesheetHandler will compile.  However,
   *                     I'm not sure why it's needed or what it does and I've commented out the body.
   *
   * @see <a href="http://www.w3.org/TR/xslt#section-Defining-Template-Rules">section-Defining-Template-Rules in XSLT Specification</a>
   * @param flushFirst Flag indicating the option of overwriting
   * (rather than appending to) previous composition.
   *
   * @throws TransformerException
   */
  public void recomposeTemplates(boolean flushFirst) throws TransformerException
  {
/***************************************  KEEP METHOD IN FOR COMPILATION
    if (flushFirst)
      m_templateList = new TemplateList(this);

    recomposeTemplates();
*****************************************/
  }
}
