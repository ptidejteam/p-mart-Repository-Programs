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
 * $Id: ElemExsltFuncResult.java,v 1.1 2006/03/09 00:07:21 vauchers Exp $
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * Handles the EXSLT result element within an EXSLT function element.
 */
public class ElemExsltFuncResult extends ElemVariable
{
 
  /**
   * Generate the EXSLT function return value, and assign it to the variable
   * index slot assigned for it in ElemExsltFunction compose().
   * 
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {    
    XPathContext context = transformer.getXPathContext();

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);
    
    // Verify that result has not already been set by another result
    // element. Recursion is allowed: intermediate results are cleared 
    // in the owner ElemExsltFunction execute().
    if (transformer.currentFuncResultSeen()) {
        throw new TransformerException("An EXSLT function cannot set more than one result!");
    }

    int sourceNode = context.getCurrentNode();

    // Set the return value;
    XObject var = getValue(transformer, sourceNode);
    transformer.popCurrentFuncResult();
    transformer.pushCurrentFuncResult(var);

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEndEvent(this);    
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.EXSLT_ELEMNAME_FUNCRESULT;
  }
  
  /**
   * Return the node name, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   * @return The node name
   * 
   */
   public String getNodeName()
  {
    return Constants.EXSLT_ELEMNAME_FUNCRESULT_STRING;
  }
}
