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
 * $Id: WhiteSpaceInfo.java,v 1.1 2006/03/09 00:07:21 vauchers Exp $
 */
package org.apache.xalan.templates;

import org.apache.xpath.XPath;

/**
 * This is used as a special "fake" template that can be
 * handled by the TemplateList to do pattern matching
 * on nodes.
 */
public class WhiteSpaceInfo extends ElemTemplate
{

  /** Flag indicating whether whitespaces should be stripped.
   *  @serial        */
  private boolean m_shouldStripSpace;

  /**
   * Return true if this element specifies that the node that
   * matches the match pattern should be stripped, otherwise
   * the space should be preserved.
   *
   * @return value of m_shouldStripSpace flag
   */
  public boolean getShouldStripSpace()
  {
    return m_shouldStripSpace;
  }
  
  /**
   * Constructor WhiteSpaceInfo
   *
   *
   * @param matchPattern Match pattern
   * @param shouldStripSpace Flag indicating whether or not
   * to strip whitespaces
   * @param thisSheet The current stylesheet
   */
  public WhiteSpaceInfo(Stylesheet thisSheet)
  {
  	setStylesheet(thisSheet);
  }


  /**
   * Constructor WhiteSpaceInfo
   *
   *
   * @param matchPattern Match pattern
   * @param shouldStripSpace Flag indicating whether or not
   * to strip whitespaces
   * @param thisSheet The current stylesheet
   */
  public WhiteSpaceInfo(XPath matchPattern, boolean shouldStripSpace, Stylesheet thisSheet)
  {

    m_shouldStripSpace = shouldStripSpace;

    setMatch(matchPattern);

    setStylesheet(thisSheet);
  }

  /**
   * This function is called to recompose() all of the WhiteSpaceInfo elements.
   */
  public void recompose(StylesheetRoot root)
  {
    root.recomposeWhiteSpaceInfo(this);
  }

}
