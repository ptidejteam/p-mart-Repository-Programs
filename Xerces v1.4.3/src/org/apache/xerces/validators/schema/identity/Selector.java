/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  
 * All rights reserved.
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

package org.apache.xerces.validators.schema.identity;

import org.apache.xerces.framework.XMLAttrList;
import org.apache.xerces.validators.schema.SchemaGrammar;
import org.apache.xerces.utils.QName;
import org.apache.xerces.utils.NamespacesScope;
import org.apache.xerces.utils.StringPool;

import org.xml.sax.SAXException;

/**
 * Schema identity constraint selector.
 *
 * @author Andy Clark, IBM
 * @version $Id: Selector.java,v 1.1 2006/02/02 01:54:21 vauchers Exp $
 */
public class Selector {

    //
    // Data
    //

    /** XPath. */
    protected Selector.XPath fXPath;

    /** Identity constraint. */
    protected IdentityConstraint fIdentityConstraint;

    //
    // Constructors
    //

    /** Constructs a selector. */
    public Selector(Selector.XPath xpath, 
                    IdentityConstraint identityConstraint) {
        fXPath = xpath;
        fIdentityConstraint = identityConstraint;
    } // <init>(Selector.XPath,IdentityConstraint)

    //
    // Public methods
    //

    /** Returns the selector XPath. */
    public org.apache.xerces.validators.schema.identity.XPath getXPath() {
        return fXPath;
    } // getXPath():org.apache.xerces.impl.xpath.XPath

    /** Returns the identity constraint. */
    public IdentityConstraint getIdentityConstraint() {
        return fIdentityConstraint;
    } // getIdentityConstraint():IdentityConstraint

    // factory method

    /** Creates a selector matcher. */
    public XPathMatcher createMatcher(FieldActivator activator) {
        return new Selector.Matcher(fXPath, activator);
    } // createMatcher(FieldActivator):XPathMatcher

    //
    // Object methods
    //

    /** Returns a string representation of this object. */
    public String toString() {
        return fXPath.toString();
    } // toString():String

    //
    // Classes
    //

    /**
     * Schema identity constraint selector XPath expression.
     *
     * @author Andy Clark, IBM
     * @version $Id: Selector.java,v 1.1 2006/02/02 01:54:21 vauchers Exp $
     */
    public static class XPath
        extends org.apache.xerces.validators.schema.identity.XPath {
    
        //
        // Constructors
        //
    
        /** Constructs a selector XPath expression. */
        public XPath(String xpath, StringPool stringPool, 
                     NamespacesScope context) throws XPathException {
            // NOTE: We have to prefix the selector XPath with "./" in
            //       order to handle selectors such as "." that select
            //       the element container because the fields could be
            //       relative to that element. -Ac
			//       Unless xpath starts with a descendant node -Achille Fokoue
            //      ... or a '.' or a '/' - NG
			super(((xpath.trim().startsWith("/") ||xpath.trim().startsWith("."))?
				xpath:"./"+xpath), stringPool, context);
    
            // verify that an attribute is not selected
			for (int i=0;i<fLocationPaths.length;i++) {
				org.apache.xerces.validators.schema.identity.XPath.Axis axis =
					fLocationPaths[i].steps[fLocationPaths[i].steps.length-1].axis;
            if (axis.type == axis.ATTRIBUTE) {
                throw new XPathException("selectors cannot select attributes");
            }
			}
    
        } // <init>(String,StringPool,NamespacesScope)
    
	} // class Selector.XPath

    /**
     * Selector matcher.
     *
     * @author Andy Clark, IBM
     */
    protected class Matcher
        extends XPathMatcher {
    
        //
        // Data
        //

        /** Field activator. */
        protected FieldActivator fFieldActivator;

        /** Element depth. */
        protected int fElementDepth;

        /** Depth at match. */
        protected int fMatchedDepth;

        //
        // Constructors
        //

        /** Constructs a selector matcher. */
        public Matcher(Selector.XPath xpath, FieldActivator activator) {
            super(xpath, false, Selector.this.fIdentityConstraint);
            fFieldActivator = activator;
        } // <init>(Selector.XPath,FieldActivator)

        //
        // XMLDocumentFragmentHandler methods
        //
    
        public void startDocumentFragment(StringPool stringPool)
            throws Exception {
            super.startDocumentFragment(stringPool);
            fElementDepth = 0;
            fMatchedDepth = -1;
        } // startDocumentFragment(StringPool,NamespacesScope)

        /**
         * The start of an element. If the document specifies the start element
         * by using an empty tag, then the startElement method will immediately
         * be followed by the endElement method, with no intervening methods.
         * 
         * @param element    The name of the element.
         * @param attributes The element attributes.
         * @param handle:  beginning of the attribute list 
         * @param elemIndex:  index of the element holding these attributes
         * @param grammar:  the SchemaGrammar that all this is being validated by
         *
         * @throws SAXException Thrown by handler to signal an error.
         */
        public void startElement(QName element, XMLAttrList attributes, 
                                 int handle, int elemIndex, SchemaGrammar grammar) throws Exception {
            super.startElement(element, attributes, handle, elemIndex, grammar);
            fElementDepth++;
    
            // activate the fields, if selector is matched
            if (fMatchedDepth == -1 && isMatched()) {
                fMatchedDepth = fElementDepth;
                fFieldActivator.startValueScopeFor(fIdentityConstraint);
                int count = fIdentityConstraint.getFieldCount();
                for (int i = 0; i < count; i++) {
                    Field field = fIdentityConstraint.getFieldAt(i);
                    XPathMatcher matcher = fFieldActivator.activateField(field);
                    matcher.startElement(element, attributes, handle, elemIndex, grammar);
                }
            }
    
        } // startElement(QName,XMLAttrList,int)
    
        public void endElement(QName element, int elemIndex, SchemaGrammar grammar) throws Exception {
            super.endElement(element, elemIndex, grammar);
            if (fElementDepth-- == fMatchedDepth) {
                fMatchedDepth = -1;
                fFieldActivator.endValueScopeFor(fIdentityConstraint);
            }
        }

    } // class Matcher

} // class Selector
