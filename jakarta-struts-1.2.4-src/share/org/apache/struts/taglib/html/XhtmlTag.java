/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/html/XhtmlTag.java,v 1.8 2004/03/14 06:23:46 sraeburn Exp $
 * $Revision: 1.8 $
 * $Date: 2004/03/14 06:23:46 $
 *
 * Copyright 2003,2004 The Apache Software Foundation.
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
 
package org.apache.struts.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;

/**
 * This tag tells all other html taglib tags to render themselves in xhtml.  It has no 
 * attributes; it's presence in a page turns on xhtml.
 * <p>
 * Example:<br/>
 * &lt;html:xhtml/&gt;
 * </p>
 * 
 */
public class XhtmlTag extends TagSupport {

    /**
     * Constructor for XhtmlTag.
     */
    public XhtmlTag() {
        super();
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
        this.pageContext.setAttribute(Globals.XHTML_KEY, "true", PageContext.PAGE_SCOPE);
        return EVAL_PAGE;
    }

}
