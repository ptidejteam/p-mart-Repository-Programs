/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: KeyIterator.java,v 1.1 2006/03/09 00:07:33 vauchers Exp $
 */
package org.apache.xalan.transformer;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.KeyDeclaration;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.axes.OneStepIteratorForward;

/**
 * This class implements an optimized iterator for 
 * "key()" patterns, matching each node to the 
 * match attribute in one or more xsl:key declarations.
 * @xsl.usage internal
 */
public class KeyIterator extends OneStepIteratorForward
{

  /** Key name.
   *  @serial           */
  private QName m_name;

  /**
   * Get the key name from a key declaration this iterator will process
   *
   *
   * @return Key name
   */
  public QName getName()
  {
    return m_name;
  }

  /** Vector of Key declarations in the stylesheet.
   *  @serial          */
  private Vector m_keyDeclarations;

  /**
   * Get the key declarations from the stylesheet 
   *
   *
   * @return Vector containing the key declarations from the stylesheet
   */
  public Vector getKeyDeclarations()
  {
    return m_keyDeclarations;
  }

  /**
    * Create a KeyIterator object.
    *
    * @param compiler A reference to the Compiler that contains the op map.
    * @param opPos The position within the op map, which contains the
    * location path expression for this itterator.
    *
    * @throws javax.xml.transform.TransformerException
    */
  KeyIterator(QName name, Vector keyDeclarations)
  {
    super(Axis.ALL);
    m_keyDeclarations = keyDeclarations;
    // m_prefixResolver = nscontext;
    m_name = name;
  }

  /**
   *  Test whether a specified node is visible in the logical view of a
   * TreeWalker or NodeIterator. This function will be called by the
   * implementation of TreeWalker and NodeIterator; it is not intended to
   * be called directly from user code.
   * 
   * @param testnode  The node to check to see if it passes the filter or not.
   *
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(int testNode)
  {
    boolean foundKey = false;
    KeyIterator ki = (KeyIterator) m_lpi;
    org.apache.xpath.XPathContext xctxt = ki.getXPathContext();
    Vector keys = ki.getKeyDeclarations();

    QName name = ki.getName();
    try
    {
      // System.out.println("lookupKey: "+lookupKey);
      int nDeclarations = keys.size();

      // Walk through each of the declarations made with xsl:key
      for (int i = 0; i < nDeclarations; i++)
      {
        KeyDeclaration kd = (KeyDeclaration) keys.elementAt(i);

        // Only continue if the name on this key declaration
        // matches the name on the iterator for this walker. 
        if (!kd.getName().equals(name))
          continue;

        foundKey = true;
        // xctxt.setNamespaceContext(ki.getPrefixResolver());

        // See if our node matches the given key declaration according to 
        // the match attribute on xsl:key.
        XPath matchExpr = kd.getMatch();
        double score = matchExpr.getMatchScore(xctxt, testNode);

        if (score == kd.getMatch().MATCH_SCORE_NONE)
          continue;

        return DTMIterator.FILTER_ACCEPT;

      } // end for(int i = 0; i < nDeclarations; i++)
    }
    catch (TransformerException se)
    {

      // TODO: What to do?
    }

    if (!foundKey)
      throw new RuntimeException(
        XSLMessages.createMessage(
          XSLTErrorResources.ER_NO_XSLKEY_DECLARATION,
          new Object[] { name.getLocalName()}));
          
    return DTMIterator.FILTER_REJECT;
  }

}
