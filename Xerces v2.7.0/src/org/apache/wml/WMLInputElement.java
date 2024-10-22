/*
 * Copyright 1999,2000,2004 The Apache Software Foundation.
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
package org.apache.wml;

/**
 * <p>The interface is modeled after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from 
 * <a href="http://www.wapforum.org/DTD/wml_1.1.xml">
 * http://www.wapforum.org/DTD/wml_1.1.xml</a></p>
 *
 * <p>'input' element specifies a text entry object.
 * (Section 11.6.3, WAP WML Version 16-Jun-1999)</p>
 *
 * @version $Id: WMLInputElement.java,v 1.1 2006/02/02 01:00:02 vauchers Exp $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */

public interface WMLInputElement extends WMLElement {

    /**
     * 'name' specifies the name of a variable after the user enters the text.
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setName(String newValue);
    public String getName();

    /**
     * 'value' specifies the default value of the variable in 'name' attribute
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setValue(String newValue);
    public String getValue();

    /**
     * 'type' specifies the type of text input area. 
     * Two values are allowed: 'text' and 'password' and default is 'text'
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setType(String newValue);
    public String getType();

    /**
     * 'format' specifies the input mask for user input.
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setFormat(String newValue);
    public String getFormat();

    /**
     * 'emptyok' specifies whether a empty input is allowed when a
     * non-empty 'format' is specified. Default to be 'false'
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setEmptyOk(boolean newValue);
    public boolean getEmptyOk();

    /**
     * 'size' specifies the width of the input in characters
     * (Section 11.6.3, WAP WML Version 16-Jun-1999)
     */
    public void setSize(int newValue);
    public int getSize();

    /**
     * 'maxlength' specifies the maximum number of characters to be
     * enter.
     * (Section 11.6.3, WAP WML Version 16-Jun-1999) 
     */
    public void setMaxLength(int newValue);
    public int getMaxLength();

    /**
     * 'title' specifies a title for this element
     * (Section 11.6.3, WAP WML Version 16-Jun-1999) 
     */
    public void setTitle(String newValue);
    public String getTitle();

    /**
     * 'tabindex' specifies the tabbing position of the element
     * (Section 11.6.1, WAP WML Version 16-Jun-1999)
     */
    public void setTabIndex(int newValue);
    public int getTabIndex();

    /**
     * 'xml:lang' specifics the natural or formal language in which
     * the document is written.  
     * (Section 8.8, WAP WML Version 16-Jun-1999) 
     */
    public void setXmlLang(String newValue);
    public String getXmlLang();
}
