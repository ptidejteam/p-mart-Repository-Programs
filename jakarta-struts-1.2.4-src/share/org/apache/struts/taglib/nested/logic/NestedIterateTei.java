/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/nested/logic/NestedIterateTei.java,v 1.8 2004/03/14 06:23:45 sraeburn Exp $
 * $Revision: 1.8 $
 * $Date: 2004/03/14 06:23:45 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.taglib.nested.logic;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import org.apache.struts.taglib.logic.IterateTei;

/**
 * NestedIterateTei
 * Extending the original tag's tei class, so that we can make the "id"
 * attribute optional, so that those who want to script can add it if they need
 * it otherwise we can maintain the nice lean tag markup.
 *
 *  TODO - Look at deleting this class. Potentially a pointless existance now
 *         that the super class is towing the line. Left alone because it's not
 *         hurting anything as-is.
 *         Note: When done, it requires pointing the tei reference in the
 *               struts-nested.tld to org.apache.struts.taglib.logic.IterateTei
 *
 *
 * @since Struts 1.1
 * @version $Revision: 1.8 $ $Date: 2004/03/14 06:23:45 $
 */

public class NestedIterateTei extends IterateTei {

  /**
   * Return information about the scripting variables to be created.
   */
  public VariableInfo[] getVariableInfo(TagData data) {
    /* It just lets the result through. */
    return super.getVariableInfo(data);
  }
}
