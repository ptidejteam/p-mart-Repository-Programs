/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import java.util.Locale;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.PSVIProvider;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * <p>Implementation of Validator for W3C XML Schemas.</p>
 *
 * @author <a href="mailto:Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @author Michael Glavassevich, IBM
 * 
 * @version $Id: ValidatorImpl.java 447235 2006-09-18 05:01:44Z mrglavas $
 */
final class ValidatorImpl extends Validator implements PSVIProvider {
    
    // property identifiers
    
    /** Property identifier: Current element node. */
    private static final String CURRENT_ELEMENT_NODE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.CURRENT_ELEMENT_NODE_PROPERTY;
    
    //
    // Data
    //
    
    /** Component manager. **/
    private XMLSchemaValidatorComponentManager fComponentManager;
    
    /** SAX validator helper. **/
    private ValidatorHandlerImpl fSAXValidatorHelper;
    
    /** DOM validator helper. **/
    private DOMValidatorHelper fDOMValidatorHelper;
    
    /** Stream validator helper. **/
    private StreamValidatorHelper fStreamValidatorHelper;
    
    /** Flag for tracking whether features/properties changed since last reset. */
    private boolean fConfigurationChanged = false;
    
    /** Flag for tracking whether the error handler changed since last reset. */
    private boolean fErrorHandlerChanged = false;
    
    /** Flag for tracking whether the resource resolver changed since last reset. */
    private boolean fResourceResolverChanged = false;
    
    public ValidatorImpl(XSGrammarPoolContainer grammarContainer) {
        fComponentManager = new XMLSchemaValidatorComponentManager(grammarContainer);
        setErrorHandler(null);
        setResourceResolver(null);
    }

    public void validate(Source source, Result result)
        throws SAXException, IOException {
        if (source instanceof SAXSource) {
            // Hand off to SAX validator helper.
            if (fSAXValidatorHelper == null) {
                fSAXValidatorHelper = new ValidatorHandlerImpl(fComponentManager);
            }
            fSAXValidatorHelper.validate(source, result);
        }
        else if (source instanceof DOMSource) {
            // Hand off to DOM validator helper.
            if (fDOMValidatorHelper == null) {
                fDOMValidatorHelper = new DOMValidatorHelper(fComponentManager);
            }
            fDOMValidatorHelper.validate(source, result);
        }
        else if (source instanceof StreamSource) {
            // Hand off to stream validator helper.
            if (fStreamValidatorHelper == null) {
                fStreamValidatorHelper = new StreamValidatorHelper(fComponentManager);
            }
            fStreamValidatorHelper.validate(source, result);
        }
        // Source parameter cannot be null.
        else if (source == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "SourceParameterNull", null));
        }
        // Source parameter must be a SAXSource, DOMSource or StreamSource
        else {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "SourceNotAccepted", new Object [] {source.getClass().getName()}));
        }
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        fErrorHandlerChanged = (errorHandler != null);
        fComponentManager.setErrorHandler(errorHandler);
    }

    public ErrorHandler getErrorHandler() {
        return fComponentManager.getErrorHandler();
    }

    public void setResourceResolver(LSResourceResolver resourceResolver) {
        fResourceResolverChanged = (resourceResolver != null);
        fComponentManager.setResourceResolver(resourceResolver);
    }

    public LSResourceResolver getResourceResolver() {
        return fComponentManager.getResourceResolver();
    }
    
    public boolean getFeature(String name) 
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            return fComponentManager.getFeature(name);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == XMLConfigurationException.NOT_RECOGNIZED ?
                    "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                    key, new Object [] {identifier}));
        }
    }
    
    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            fComponentManager.setFeature(name, value);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == XMLConfigurationException.NOT_RECOGNIZED ?
                    "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                    key, new Object [] {identifier}));
        }
        fConfigurationChanged = true;
    }
    
    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (CURRENT_ELEMENT_NODE.equals(name)) {
            return (fDOMValidatorHelper != null) ? 
                    fDOMValidatorHelper.getCurrentElement() : null;
        }
        try {
            return fComponentManager.getProperty(name);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == XMLConfigurationException.NOT_RECOGNIZED ?
                    "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                    key, new Object [] {identifier}));
        }
    }
    
    public void setProperty(String name, Object object)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (CURRENT_ELEMENT_NODE.equals(name)) {
            throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                    "property-read-only", new Object [] {name}));
        }
        try {
            fComponentManager.setProperty(name, object);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == XMLConfigurationException.NOT_RECOGNIZED ?
                    "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                    key, new Object [] {identifier}));
        }
        fConfigurationChanged = true;
    }
    
    public void reset() {
        // avoid resetting features and properties if the state the validator
        // is currently in, is the same as it will be after reset.
        if (fConfigurationChanged) {
            fComponentManager.restoreInitialState();
            setErrorHandler(null);
            setResourceResolver(null);
            fConfigurationChanged = false;
            fErrorHandlerChanged = false;
            fResourceResolverChanged = false;
        }
        else {
            if (fErrorHandlerChanged) {
                setErrorHandler(null);
                fErrorHandlerChanged = false;
            }
            if (fResourceResolverChanged) {
                setResourceResolver(null);
                fResourceResolverChanged = false;
            }
        }
    }
    
    /*
     * PSVIProvider methods
     */
    
    public ElementPSVI getElementPSVI() {
        return (fSAXValidatorHelper != null) ? fSAXValidatorHelper.getElementPSVI() : null;
    }
    
    public AttributePSVI getAttributePSVI(int index) {
        return (fSAXValidatorHelper != null) ? fSAXValidatorHelper.getAttributePSVI(index) : null;
    }
    
    public AttributePSVI getAttributePSVIByName(String uri, String localname) {
        return (fSAXValidatorHelper != null) ? fSAXValidatorHelper.getAttributePSVIByName(uri, localname) : null;
    }
    
} // ValidatorImpl
