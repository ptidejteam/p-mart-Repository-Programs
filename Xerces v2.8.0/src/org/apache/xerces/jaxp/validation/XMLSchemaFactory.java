/*
 * Copyright 2005,2006 The Apache Software Foundation.
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

package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.apache.xerces.util.DOMInputSource;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.SAXInputSource;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * {@link SchemaFactory} for XML Schema.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * @version $Id: XMLSchemaFactory.java 381134 2006-02-26 18:20:57Z mrglavas $
 */
public final class XMLSchemaFactory extends SchemaFactory {
    
    // feature identifiers
    
    /** Feature identifier: schema full checking. */
    private static final String SCHEMA_FULL_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;
    
    /** Feature identifier: use grammar pool only. */
    private static final String USE_GRAMMAR_POOL_ONLY =
        Constants.XERCES_FEATURE_PREFIX + Constants.USE_GRAMMAR_POOL_ONLY_FEATURE;
    
    // property identifiers
    
    /** Property identifier: grammar pool. */
    private static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;
    
    /** Property identifier: SecurityManager. */
    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;
    
    //
    // Data
    //
    
    /** The XMLSchemaLoader */
    private final XMLSchemaLoader fXMLSchemaLoader = new XMLSchemaLoader();
    
    /** User-specified ErrorHandler; can be null. */
    private ErrorHandler fErrorHandler;
    
    /** The LSResrouceResolver */
    private LSResourceResolver fLSResourceResolver;
    
    /** The DOMEntityResolverWrapper */
    private final DOMEntityResolverWrapper fDOMEntityResolverWrapper;
    
    /** The ErrorHandlerWrapper */
    private ErrorHandlerWrapper fErrorHandlerWrapper;
    
    /** The SecurityManager. */
    private SecurityManager fSecurityManager;
    
    /** The container for the real grammar pool. */ 
    private XMLGrammarPoolWrapper fXMLGrammarPoolWrapper;
    
    /** Whether or not to allow new schemas to be added to the grammar pool */
    private boolean fUseGrammarPoolOnly;
    
    public XMLSchemaFactory() {
        fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
        fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
        fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
        fXMLSchemaLoader.setFeature(SCHEMA_FULL_CHECKING, true);
        fXMLSchemaLoader.setProperty(XMLGRAMMAR_POOL, fXMLGrammarPoolWrapper);
        fXMLSchemaLoader.setEntityResolver(fDOMEntityResolverWrapper);
        fXMLSchemaLoader.setErrorHandler(fErrorHandlerWrapper);
        fUseGrammarPoolOnly = true;
    }
    
    /**
     * <p>Is specified schema supported by this <code>SchemaFactory</code>?</p>
     *
     * @param schemaLanguage Specifies the schema language which the returned <code>SchemaFactory</code> will understand.
     *    <code>schemaLanguage</code> must specify a <a href="#schemaLanguage">valid</a> schema language.
     *
     * @return <code>true</code> if <code>SchemaFactory</code> supports <code>schemaLanguage</code>, else <code>false</code>.
     *
     * @throws NullPointerException If <code>schemaLanguage</code> is <code>null</code>.
     * @throws IllegalArgumentException If <code>schemaLanguage.length() == 0</code>
     *   or <code>schemaLanguage</code> does not specify a <a href="#schemaLanguage">valid</a> schema language.
     */
    public boolean isSchemaLanguageSupported(String schemaLanguage) {
        if (schemaLanguage == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "SchemaLanguageNull", null));
        }
        if (schemaLanguage.length() == 0) {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "SchemaLanguageLengthZero", null));
        }
        // only W3C XML Schema 1.0 is supported 
        return schemaLanguage.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }
    
    public LSResourceResolver getResourceResolver() {
        return fLSResourceResolver;
    }
    
    public void setResourceResolver(LSResourceResolver resourceResolver) {
        fLSResourceResolver = resourceResolver;
        fDOMEntityResolverWrapper.setEntityResolver(resourceResolver);
        fXMLSchemaLoader.setEntityResolver(fDOMEntityResolverWrapper);
    }
    
    public ErrorHandler getErrorHandler() {
        return fErrorHandler;
    }
    
    public void setErrorHandler(ErrorHandler errorHandler) {
        fErrorHandler = errorHandler;
        fErrorHandlerWrapper.setErrorHandler(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance());
        fXMLSchemaLoader.setErrorHandler(fErrorHandlerWrapper);
    }  
    
    public Schema newSchema( Source[] schemas ) throws SAXException {
        
        // this will let the loader store parsed Grammars into the pool.
        XMLGrammarPoolImplExtension pool = new XMLGrammarPoolImplExtension();
        fXMLGrammarPoolWrapper.setGrammarPool(pool);
        
        XMLInputSource[] xmlInputSources = new XMLInputSource[schemas.length];
        InputStream inputStream;
        Reader reader;
        for( int i=0; i<schemas.length; i++ ) {
            Source source = schemas[i];
            if (source instanceof StreamSource) {
                StreamSource streamSource = (StreamSource) source;
                String publicId = streamSource.getPublicId();
                String systemId = streamSource.getSystemId();
                inputStream = streamSource.getInputStream();
                reader = streamSource.getReader();
                xmlInputSources[i] = new XMLInputSource(publicId, systemId, null);
                xmlInputSources[i].setByteStream(inputStream);
                xmlInputSources[i].setCharacterStream(reader);
            }
            else if (source instanceof SAXSource) {
                SAXSource saxSource = (SAXSource) source;
                InputSource inputSource = saxSource.getInputSource();
                if (inputSource == null) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                            "SAXSourceNullInputSource", null));
                }
                xmlInputSources[i] = new SAXInputSource(saxSource.getXMLReader(), inputSource);
            }
            else if (source instanceof DOMSource) {
                DOMSource domSource = (DOMSource) source;
                Node node = domSource.getNode();
                String systemID = domSource.getSystemId();          
                xmlInputSources[i] = new DOMInputSource(node, systemID);
            }
            else if (source == null) {
                throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                        "SchemaSourceArrayMemberNull", null));
            }
            else {
                throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                        "SchemaFactorySourceUnrecognized", 
                        new Object [] {source.getClass().getName()}));
            }
        }
        
        try {
            fXMLSchemaLoader.loadGrammar(xmlInputSources);
        } 
        catch (XNIException e) {
            // this should have been reported to users already.
            throw Util.toSAXException(e);
        } 
        catch (IOException e) {
            // this hasn't been reported, so do so now.
            SAXParseException se = new SAXParseException(e.getMessage(),null,e);
            fErrorHandler.error(se);
            throw se; // and we must throw it.
        }
        
        // Clear reference to grammar pool.
        fXMLGrammarPoolWrapper.setGrammarPool(null);
        
        // Select Schema implementation based on grammar count.
        final int grammarCount = pool.getGrammarCount();
        AbstractXMLSchema schema = null;
        if (fUseGrammarPoolOnly) {
            if (grammarCount > 1) {
                schema = new XMLSchema(new ReadOnlyGrammarPool(pool));
            }
            else if (grammarCount == 1) {
                Grammar[] grammars = pool.retrieveInitialGrammarSet(XMLGrammarDescription.XML_SCHEMA);
                schema = new SimpleXMLSchema(grammars[0]);
            }
            else {
                schema = new EmptyXMLSchema();
            }
        }
        else {
            schema = new XMLSchema(new ReadOnlyGrammarPool(pool), false);
        }
        propagateFeatures(schema);
        return schema;
    }
    
    public Schema newSchema() throws SAXException {
        /*
         * It would make sense to return an EmptyXMLSchema object here, if
         * fUseGrammarPoolOnly is set to true. However, because the default
         * value of this feature is true, doing so would change the default
         * behaviour of this method. Thus, we return a WeakReferenceXMLSchema
         * regardless of the value of fUseGrammarPoolOnly. -PM
         */
        
        // Use a Schema that uses the system id as the equality source.
        AbstractXMLSchema schema = new WeakReferenceXMLSchema();
        propagateFeatures(schema);
        return schema;
    }
    
    public boolean getFeature(String name) 
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "FeatureNameNull", null));
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            return (fSecurityManager != null);
        }
        else if (name.equals(USE_GRAMMAR_POOL_ONLY)) {
            return fUseGrammarPoolOnly;
        }
        try {
            return fXMLSchemaLoader.getFeature(name);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "feature-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "feature-not-supported", new Object [] {identifier}));
            }
        }
    }
    
    public Object getProperty(String name) 
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "ProperyNameNull", null));
        }
        if (name.equals(SECURITY_MANAGER)) {
            return fSecurityManager;
        }
        else if (name.equals(XMLGRAMMAR_POOL)) {
            throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                    "property-not-supported", new Object [] {name}));
        }
        try {
            return fXMLSchemaLoader.getProperty(name);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "property-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "property-not-supported", new Object [] {identifier}));
            }
        }
    }
    
    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "FeatureNameNull", null));
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            fSecurityManager = value ? new SecurityManager() : null;
            fXMLSchemaLoader.setProperty(SECURITY_MANAGER, fSecurityManager);
            return;
        }
        else if (name.equals(USE_GRAMMAR_POOL_ONLY)) {
            fUseGrammarPoolOnly = value;
            return;
        }
        try {
            fXMLSchemaLoader.setFeature(name, value);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "feature-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "feature-not-supported", new Object [] {identifier}));
            }
        }
    }
    
    public void setProperty(String name, Object object)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                    "ProperyNameNull", null));
        }
        if (name.equals(SECURITY_MANAGER)) {
            fSecurityManager = (SecurityManager) object;
            fXMLSchemaLoader.setProperty(SECURITY_MANAGER, fSecurityManager);
            return;
        }
        else if (name.equals(XMLGRAMMAR_POOL)) {
            throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                    "property-not-supported", new Object [] {name}));
        }
        try {
            fXMLSchemaLoader.setProperty(name, object);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "property-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(Locale.getDefault(), 
                        "property-not-supported", new Object [] {identifier}));
            }
        }
    }
    
    private void propagateFeatures(AbstractXMLSchema schema) {
        schema.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, fSecurityManager != null);
        String[] features = fXMLSchemaLoader.getRecognizedFeatures();
        for (int i = 0; i < features.length; ++i) {
            boolean state = fXMLSchemaLoader.getFeature(features[i]);
            schema.setFeature(features[i], state);
        }
    }
    
    /** 
     * Extension of XMLGrammarPoolImpl which exposes the number of
     * grammars stored in the grammar pool.
     */
    static class XMLGrammarPoolImplExtension extends XMLGrammarPoolImpl {
        
        /** Constructs a grammar pool with a default number of buckets. */
        public XMLGrammarPoolImplExtension() {
            super();
        }

        /** Constructs a grammar pool with a specified number of buckets. */
        public XMLGrammarPoolImplExtension(int initialCapacity) {
            super(initialCapacity);
        }
        
        /** Returns the number of grammars contained in this pool. */
        int getGrammarCount() {
            return fGrammarCount;
        }
        
    } // XMLSchemaFactory.XMLGrammarPoolImplExtension
    
    /**
     * A grammar pool which wraps another.
     */
    static class XMLGrammarPoolWrapper implements XMLGrammarPool {

        private XMLGrammarPool fGrammarPool;
        
        /*
         * XMLGrammarPool methods
         */
        
        public Grammar[] retrieveInitialGrammarSet(String grammarType) {
            return fGrammarPool.retrieveInitialGrammarSet(grammarType);
        }

        public void cacheGrammars(String grammarType, Grammar[] grammars) {
            fGrammarPool.cacheGrammars(grammarType, grammars);
        }

        public Grammar retrieveGrammar(XMLGrammarDescription desc) {
            return fGrammarPool.retrieveGrammar(desc);
        }

        public void lockPool() {
            fGrammarPool.lockPool();
        }

        public void unlockPool() {
            fGrammarPool.unlockPool();
        }

        public void clear() {
            fGrammarPool.clear();
        }
        
        /*
         * Other methods
         */
        
        void setGrammarPool(XMLGrammarPool grammarPool) {
            fGrammarPool = grammarPool;
        }
        
        XMLGrammarPool getGrammarPool() {
            return fGrammarPool;
        }
        
    } // XMLSchemaFactory.XMLGrammarPoolWrapper
    
} // XMLSchemaFactory
