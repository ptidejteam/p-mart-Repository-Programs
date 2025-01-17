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
 * $Id: WhitespaceInfoPaths.java,v 1.1 2006/03/09 00:07:09 vauchers Exp $
 */
package org.apache.xalan.processor;

import java.util.Vector;

import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.WhiteSpaceInfo;

public class WhitespaceInfoPaths extends WhiteSpaceInfo
{
	
  /**
   * Bean property to allow setPropertiesFromAttributes to
   * get the elements attribute.
   */
  private Vector m_elements;

  /**
   * Set from the elements attribute.  This is a list of 
   * whitespace delimited element qualified names that specify
   * preservation of whitespace.
   *
   * @param elems Should be a non-null reference to a list 
   *              of {@link org.apache.xpath.XPath} objects.
   */
  public void setElements(Vector elems)
  {
    m_elements = elems;
  }

  /**
   * Get the property set by setElements().  This is a list of 
   * whitespace delimited element qualified names that specify
   * preservation of whitespace.
   *
   * @return A reference to a list of {@link org.apache.xpath.XPath} objects, 
   *         or null.
   */
  Vector getElements()
  {
    return m_elements;
  }
  
  public void clearElements()
  {
  	m_elements = null;
  }

 /**
   * Constructor WhitespaceInfoPaths
   *
   * @param thisSheet The current stylesheet
   */
  public WhitespaceInfoPaths(Stylesheet thisSheet)
  {
  	super(thisSheet);
  	setStylesheet(thisSheet);
  }


}

