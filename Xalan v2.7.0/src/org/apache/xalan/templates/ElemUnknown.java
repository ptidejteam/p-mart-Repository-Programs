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
 * $Id: ElemUnknown.java,v 1.1 2006/03/01 21:15:13 vauchers Exp $
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;


/**
 * Implement an unknown element
 * @xsl.usage advanced
 */
public class ElemUnknown extends ElemLiteralResult
{
    static final long serialVersionUID = -4573981712648730168L;

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   *@return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_UNDEFINED;
  }
  
  /**
   * Execute the fallbacks when an extension is not available.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  private void executeFallbacks(
          TransformerImpl transformer)
            throws TransformerException
  {
    for (ElemTemplateElement child = m_firstChild; child != null;
             child = child.m_nextSibling)
    {
      if (child.getXSLToken() == Constants.ELEMNAME_FALLBACK)
      {
        try
        {
          transformer.pushElemTemplateElement(child);
          ((ElemFallback) child).executeFallback(transformer);
        }
        finally
        {
          transformer.popElemTemplateElement();
        }
      }
    }

  }
  
  /**
   * Return true if this extension element has a <xsl:fallback> child element.
   *
   * @return true if this extension element has a <xsl:fallback> child element.
   */
  private boolean hasFallbackChildren()
  {
    for (ElemTemplateElement child = m_firstChild; child != null;
             child = child.m_nextSibling)
    {
      if (child.getXSLToken() == Constants.ELEMNAME_FALLBACK)
        return true;
    }
    
    return false;
  }


  /**
   * Execute an unknown element.
   * Execute fallback if fallback child exists or do nothing
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer)
            throws TransformerException
  {


    if (transformer.getDebug())
		transformer.getTraceManager().fireTraceEvent(this);

	try {

		if (hasFallbackChildren()) {
			executeFallbacks(transformer);
		} else {
			// do nothing
		}
		
	} catch (TransformerException e) {
		transformer.getErrorListener().fatalError(e);
	}
    if (transformer.getDebug())
		transformer.getTraceManager().fireTraceEndEvent(this);
  }

}
