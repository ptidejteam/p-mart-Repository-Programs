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
package org.apache.html.dom;

import org.w3c.dom.html.HTMLBaseElement;

/**
 * @xerces.internal
 * @version $Revision: 329271 $ $Date: 2005-10-28 15:25:05 -0400 (Fri, 28 Oct 2005) $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLBaseElement
 * @see org.apache.xerces.dom.ElementImpl
 */
public class HTMLBaseElementImpl
    extends HTMLElementImpl
    implements HTMLBaseElement
{
    
    private static final long serialVersionUID = -396648580810072153L;

    public String getHref()
    {
        return getAttribute( "href" );
    }
    
    
    public void setHref( String href )
    {
        setAttribute( "href", href );
    }
    
    public String getTarget()
    {
        return getAttribute( "target" );
    }
    
    
    public void setTarget( String target )
    {
        setAttribute( "target", target );
    }


    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLBaseElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

