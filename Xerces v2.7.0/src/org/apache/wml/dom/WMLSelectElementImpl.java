/*
 * Copyright 1999,2000,2004,2005 The Apache Software Foundation.
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
package org.apache.wml.dom;

import org.apache.wml.*;

/**
 * @xerces.internal
 * @version $Id: WMLSelectElementImpl.java,v 1.1 2006/02/02 00:59:52 vauchers Exp $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */
public class WMLSelectElementImpl extends WMLElementImpl implements WMLSelectElement {
    
    private static final long serialVersionUID = 3905808595126661684L;

    public WMLSelectElementImpl (WMLDocumentImpl owner, String tagName) {
        super( owner, tagName);
    }
    
    public void setMultiple(boolean newValue) {
        setAttribute("multiple", newValue);
    }
    
    public boolean getMultiple() {
        return getAttribute("multiple", false);
    }
    
    public void setValue(String newValue) {
        setAttribute("value", newValue);
    }
    
    public String getValue() {
        return getAttribute("value");
    }
    
    public void setTabIndex(int newValue) {
        setAttribute("tabindex", newValue);
    }
    
    public int getTabIndex() {
        return getAttribute("tabindex", 0);
    }
    
    public void setClassName(String newValue) {
        setAttribute("class", newValue);
    }
    
    public String getClassName() {
        return getAttribute("class");
    }
    
    public void setXmlLang(String newValue) {
        setAttribute("xml:lang", newValue);
    }
    
    public String getXmlLang() {
        return getAttribute("xml:lang");
    }
    
    public void setTitle(String newValue) {
        setAttribute("title", newValue);
    }
    
    public String getTitle() {
        return getAttribute("title");
    }
    
    public void setIValue(String newValue) {
        setAttribute("ivalue", newValue);
    }
    
    public String getIValue() {
        return getAttribute("ivalue");
    }
    
    public void setId(String newValue) {
        setAttribute("id", newValue);
    }
    
    public String getId() {
        return getAttribute("id");
    }
    
    public void setIName(String newValue) {
        setAttribute("iname", newValue);
    }
    
    public String getIName() {
        return getAttribute("iname");
    }
    
    public void setName(String newValue) {
        setAttribute("name", newValue);
    }
    
    public String getName() {
        return getAttribute("name");
    }
}
