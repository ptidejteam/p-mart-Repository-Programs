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
 * $Id: ProcessorTemplate.java,v 1.1 2006/03/09 00:07:09 vauchers Exp $
 */
package org.apache.xalan.processor;

import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;

/**
 * TransformerFactory for xsl:template markup.
 */
class ProcessorTemplate extends ProcessorTemplateElem
{
  
  /**
   * Append the current template element to the current
   * template element, and then push it onto the current template
   * element stack.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param elem Must be a non-null reference to a {@link org.apache.xalan.templates.ElemTemplate} object.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  protected void appendAndPush(
          StylesheetHandler handler, ElemTemplateElement elem)
            throws org.xml.sax.SAXException
  {

    super.appendAndPush(handler, elem);
    elem.setDOMBackPointer(handler.getOriginatingNode());
    handler.getStylesheet().setTemplate((ElemTemplate) elem);
  }
}
