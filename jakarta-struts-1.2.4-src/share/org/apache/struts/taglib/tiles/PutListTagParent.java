/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/tiles/PutListTagParent.java,v 1.4 2004/03/14 06:23:49 sraeburn Exp $
 * $Revision: 1.4 $
 * $Date: 2004/03/14 06:23:49 $
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


package org.apache.struts.taglib.tiles;

import javax.servlet.jsp.JspException;

/**
 * Tag classes implementing this interface can contains nested PutTag.
 * This interface defines a method called by nested tags.
 */
public interface PutListTagParent {
  /**
   * Add an attribute to container.
   * @param nestedTag Nested PutTag defining the attribute.
   */
  void processNestedTag(PutListTag nestedTag) throws JspException;

}
