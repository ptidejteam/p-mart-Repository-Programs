/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/logic/NotEmptyTag.java,v 1.5 2004/03/14 06:23:44 sraeburn Exp $
 * $Revision: 1.5 $
 * $Date: 2004/03/14 06:23:44 $
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


package org.apache.struts.taglib.logic;


import javax.servlet.jsp.JspException;


/**
 * Evalute the nested body content of this tag if the specified value
 * is not empty for this request.
 *
 * @version $Revision: 1.5 $ $Date: 2004/03/14 06:23:44 $
 * @since Struts 1.1
 */

public class NotEmptyTag extends EmptyTag {


    // ------------------------------------------------------ Protected Methods


    /**
     * Evaluate the condition that is being tested by this particular tag,
     * and return <code>true</code> if the nested body content of this tag
     * should be evaluated, or <code>false</code> if it should be skipped.
     * This method must be implemented by concrete subclasses.
     *
     * @exception JspException if a JSP exception occurs
     */
    protected boolean condition() throws JspException {

        return (condition(false));

    }


}
