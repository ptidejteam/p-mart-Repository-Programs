/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/nested/logic/NestedIterateTag.java,v 1.7 2003/02/28 05:14:39 arron Exp $
 * $Revision: 1.7 $
 * $Date: 2003/02/28 05:14:39 $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.struts.taglib.nested.logic;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.logic.IterateTag;
import org.apache.struts.taglib.nested.NestedNameSupport;
import org.apache.struts.taglib.nested.NestedPropertyHelper;

/**
 * NestedIterateTag.
 * Slightly more complex that the other extensions. This one has to yield a
 * proper index property. Very taxing.
 *
 * @author Arron Bates
 * @since Struts 1.1
 * @version $Revision: 1.7 $ $Date: 2003/02/28 05:14:39 $
 */
public class NestedIterateTag extends IterateTag implements NestedNameSupport {

  /**
   * Overriding method of the heart of the matter. Gets the relative property
   * and leaves the rest up to the original tag implementation. Sweet.
   * @return int JSP continuation directive.
   *             This is in the hands of the super class.
   */
  public int doStartTag() throws JspException {
    // original values
    originalName = getName();
    originalProperty = getProperty();

    // set the ID to make the super tag happy
    if (id == null || id.trim().length() == 0) { id = property; }

    // the request object
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

    // original nesting details
    originalNesting = NestedPropertyHelper.getCurrentProperty(request);
    originalNestingName = NestedPropertyHelper.getCurrentName(request, this);

    // set the bean if it's been provided
    // (the bean that's been provided! get it!?... nevermind)
    if (getName() == null) {
      // the qualified nesting value
      nesting = NestedPropertyHelper.getAdjustedProperty(request, getProperty());
    } else {
      // it's just the property
      nesting = getProperty();
    }

    // set the properties
    NestedPropertyHelper.setNestedProperties(request, this);

    // get the original result
    int temp = super.doStartTag();

    // set the new reference (including the index etc)
    NestedPropertyHelper.setName(request, getName());
    NestedPropertyHelper.setProperty(request, deriveNestedProperty());

    // return the result
    return temp;
  }

  /**
   * The only added property to the class. For use in proper nesting.
   * @return String value of the property and the current index or mapping.
   */
  private String deriveNestedProperty() {
    Object idObj = pageContext.getAttribute(id);
    if (idObj instanceof Map.Entry) {
      return nesting + "("+ ((Map.Entry)idObj).getKey() +")";
    } else {
      return nesting + "["+ this.getIndex() +"]";
    }
  }

  /**
   * This is only overriden as the include reference will need it's index
   * updated.
   *
   * @return int JSP continuation directive.
   */
  public int doAfterBody() throws JspException {
    // store original result
    int temp = super.doAfterBody();
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    if (temp != SKIP_BODY) {
      // set the new reference
      NestedPropertyHelper.setProperty(request, deriveNestedProperty());
    }
    // return super result
    return temp;
  }

  /**
   * Complete the processing of the tag. The nested tags here will restore
   * all the original value for the tag itself and the nesting context.
   * @return int to describe the next step for the JSP processor
   * @throws JspException for the bad things JSP's do
   */
  public int doEndTag() throws JspException {
    // the super's thing
    int i = super.doEndTag();

    // request
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    // reset the original tag values
    super.setName(originalName);
    super.setProperty(originalProperty);

    // reset the original nesting values
    if (originalNesting == null) {
      NestedPropertyHelper.deleteReference(request);
    } else {
      NestedPropertyHelper.setProperty(request, originalNesting);
      NestedPropertyHelper.setName(request, originalNestingName);
    }
    // job done
    return i;
  }

  /**
   * Release the tag's resources and reset the values.
   */
  public void release() {
    // let the super release
    super.release();
    // reset the original value place holders
    originalName = null;
    originalProperty = null;
    originalNesting = null;
    originalNestingName = null;
  }


  // The current nesting
  private String nesting = null;

  // original tag properties
  private String originalName = null;
  private String originalProperty = null;

  // original nesting environment
  private String originalNesting = null;
  private String originalNestingName = null;
}