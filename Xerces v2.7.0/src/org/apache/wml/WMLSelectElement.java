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
 * <p>'select' element lets user pick from a list of options.
 * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)</p>
 *
 * @version $Id: WMLSelectElement.java,v 1.1 2006/02/02 01:00:02 vauchers Exp $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */
public interface WMLSelectElement extends WMLElement {
    
    /**
     * 'tabindex' specifies the tabbing position of the element
     * (Section 11.6.1, WAP WML Version 16-Jun-1999)
     */
    public void setTabIndex(int newValue);
    public int getTabIndex();

    /**
     * 'multiple' indicates whether a list accept multiple selection
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    public void setMultiple(boolean newValue);
    public boolean getMultiple();

    /**
     * 'name' specifies the name of variable to be set.
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    public void setName(String newValue);
    public String getName();

    /**
     * 'value' specifics the default value of the variable of 'name'
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    public void setValue(String newValue);
    public String getValue();

    /**
     * 'title' specifies a title for this element
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    public void setTitle(String newValue);
    public String getTitle();

    /**
     * 'iname' specifies name of variable to be set with the index
     * result of selection.  
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    public void setIName(String newValue);
    public String getIName();

    /**
     * 'ivalue' specifies the default of the variable 'iname'
     */
    public void setIValue(String newValue);
    public String getIValue();

    /**
     * 'xml:lang' specifics the natural or formal language in which
     * the document is written.  
     * (Section 8.8, WAP WML Version 16-Jun-1999) 
     */
    public void setXmlLang(String newValue);
    public String getXmlLang();
}
