/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/nested/html/NestedLinkTag.java,v 1.11 2004/03/14 06:23:50 sraeburn Exp $
 * $Revision: 1.11 $
 * $Date: 2004/03/14 06:23:50 $
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
package org.apache.struts.taglib.nested.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.LinkTag;
import org.apache.struts.taglib.nested.NestedNameSupport;
import org.apache.struts.taglib.nested.NestedPropertyHelper;

/**
 * NestedLinkTag.
 * @since Struts 1.1
 * @version $Revision: 1.11 $ $Date: 2004/03/14 06:23:50 $
 */
public class NestedLinkTag extends LinkTag implements NestedNameSupport {

  /**
   * Overriding method of the heart of the matter. Gets the relative property
   * and leaves the rest up to the original tag implementation. Sweet.
   * @return int JSP continuation directive.
   *             This is in the hands of the super class.
   */
  public int doStartTag() throws JspException {
    origName = super.getName();
    origProperty = super.getProperty();
    origParamProperty = super.getParamProperty();

    /* decide the incoming options. Always two there are */
    boolean doProperty = (origProperty != null && origProperty.length() > 0);
    boolean doParam = (origParamProperty != null && origParamProperty.length() > 0);

    // request
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

    boolean hasName = (getName() != null && getName().trim().length() > 0);
    String currentName;
    if (hasName) {
      currentName = getName();
    } else {
      currentName = NestedPropertyHelper.getCurrentName(request, this);
    }
    // set the bean name
    super.setName(currentName);

    // set property details
    if (doProperty && !hasName) {
      super.setProperty(NestedPropertyHelper.getAdjustedProperty(request, origProperty));
    }
    // do the param property details
    if (doParam) {
      super.setName(null);
      super.setParamName(currentName);
      super.setParamProperty(NestedPropertyHelper.getAdjustedProperty(request, origParamProperty));
    }
    
    /* do the tag */
    return super.doStartTag();
  }

  /**
   * Complete the processing of the tag. The nested tags here will restore
   * all the original value for the tag itself and the nesting context.
   * @return int to describe the next step for the JSP processor
   * @throws JspException for the bad things JSP's do
   */
  public int doEndTag() throws JspException {
    // do the super's ending part
    int i = super.doEndTag();

    // reset the properties
    setName(origName);
    setProperty(origProperty);
    setParamProperty(origParamProperty);

    // continue
    return i;
  }

  /**
   * Release the tag's resources and reset the values.
   */
  public void release() {
    super.release();
    // reset the originals
    origName = null;
    origProperty = null;
    origParamProperty = null;
  }


  /* hold original property */
  private String origName = null;
  private String origProperty = null;
  private String origParamProperty = null;
}
