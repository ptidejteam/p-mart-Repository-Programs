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
 * @version $Id: WMLDoElementImpl.java,v 1.1 2006/02/02 00:59:52 vauchers Exp $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */
public class WMLDoElementImpl extends WMLElementImpl implements WMLDoElement {
    
    private static final long serialVersionUID = 3978707298497737012L;
    
    public WMLDoElementImpl (WMLDocumentImpl owner, String tagName) {
        super( owner, tagName);
    }
    
    public void setOptional(String newValue) {
        setAttribute("optional", newValue);
    }
    
    public String getOptional() {
        return getAttribute("optional");
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
    
    public void setId(String newValue) {
        setAttribute("id", newValue);
    }
    
    public String getId() {
        return getAttribute("id");
    }
    
    public void setLabel(String newValue) {
        setAttribute("label", newValue);
    }
    
    public String getLabel() {
        return getAttribute("label");
    }
    
    public void setType(String newValue) {
        setAttribute("type", newValue);
    }
    
    public String getType() {
        return getAttribute("type");
    }
    
    public void setName(String newValue) {
        setAttribute("name", newValue);
    }
    
    public String getName() {
        return getAttribute("name");
    }
    
}
