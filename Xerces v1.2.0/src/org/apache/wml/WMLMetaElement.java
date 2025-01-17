/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.wml;

/**
 * <p>The interface is modeled after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from 
 * <a href="http://www.wapforum.org/DTD/wml_1.1.xml">
 * http://www.wapforum.org/DTD/wml_1.1.xml</a></p>
 *
 * <p>The meta element contains meta-info of an WML deck
 * (Section 11.3.2, WAP WML Version 16-Jun-1999)</p>
 *
 * @version $Id: WMLMetaElement.java,v 1.1 2006/02/02 02:34:07 vauchers Exp $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */
public interface WMLMetaElement extends WMLElement {

    /**
     * 'name' attribute specific the property name
     */
    public void setName(String newValue);
    public String getName();

    /**
     * 'http-equiv' attribute indicates the property should be
     * interpret as HTTP header.
     */
    public void setHttpEquiv(String newValue);
    public String getHttpEquiv();

    /**
     * 'forua' attribute specifies whether a intermediate agent should
     * remove this meta element. A value of false means the
     * intermediate agent must remove the element.
     */
    public void setForua(boolean newValue);
    public boolean getForua();

    /**
     * 'scheme' attribute specifies a form that may be used to
     * interpret the property value 
     */
    public void setScheme(String newValue);
    public String getScheme();

    /**
     * 'content' attribute specifies the property value 
     */
    public void setContent(String newValue);
    public String getContent();
}
