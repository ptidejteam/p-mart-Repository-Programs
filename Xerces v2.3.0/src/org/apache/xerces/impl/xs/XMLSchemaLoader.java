/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.
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

package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarLoader;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.grammars.Grammar;
import org.xml.sax.InputSource;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.util.DefaultErrorHandler;

import java.util.Locale;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;
import org.apache.xerces.impl.xs.models.CMNodeFactory;

/**
 * This class implements XMLGrammarLoader.  It is designed to interact
 * either with a proxy for a user application which wants to preparse schemas,
 * or with our own Schema validator.  It is hoped that none of these "external"
 * classes will therefore need to communicate directly
 * with XSDHandler in future.
 * <p>This class only knows how to make XSDHandler do its thing.
 * The caller must ensure that all its properties (schemaLocation, JAXPSchemaSource
 * etc.) have been properly set.
 *
 * @author Neil Graham, IBM
 * @version $Id: XMLSchemaLoader.java,v 1.1 2006/02/02 01:35:17 vauchers Exp $
 */

public class XMLSchemaLoader implements XMLGrammarLoader {

    // Feature identifiers:

    /** Feature identifier: schema full checking*/
    protected static final String SCHEMA_FULL_CHECKING =
     Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;

    /** Feature identifier: continue after fatal error. */
    protected static final String CONTINUE_AFTER_FATAL_ERROR =
        Constants.XERCES_FEATURE_PREFIX + Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE;

    /** Feature identifier: allow java encodings to be recognized when parsing schema docs. */
    protected static final String ALLOW_JAVA_ENCODINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE;

    /** Feature identifier: standard uri conformant feature. */
    protected static final String STANDARD_URI_CONFORMANT_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.STANDARD_URI_CONFORMANT_FEATURE;

    // recognized features:
    private static final String[] RECOGNIZED_FEATURES = {
        SCHEMA_FULL_CHECKING,
        CONTINUE_AFTER_FATAL_ERROR,
        ALLOW_JAVA_ENCODINGS,
        STANDARD_URI_CONFORMANT_FEATURE
    };

    // property identifiers

    /** Property identifier: symbol table. */
    public static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    /** Property identifier: error reporter. */
    public static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    /** Property identifier: error handler. */
    protected static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;

    /** Property identifier: entity resolver. */
    public static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;

    /** Property identifier: grammar pool. */
    public static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

    /** Property identifier: schema location. */
    protected static final String SCHEMA_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION;

    /** Property identifier: no namespace schema location. */
    protected static final String SCHEMA_NONS_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_NONS_LOCATION;

    /** Property identifier: JAXP schema source. */
    protected static final String JAXP_SCHEMA_SOURCE =
        Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE;

    // recognized properties
    private static final String [] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        ERROR_REPORTER,
        ERROR_HANDLER,
        ENTITY_RESOLVER,
        XMLGRAMMAR_POOL,
        SCHEMA_LOCATION,
        SCHEMA_NONS_LOCATION,
        JAXP_SCHEMA_SOURCE,
    };

    // Data

    // is Schema Full Checking enabled
    private boolean fIsCheckedFully = false;

    // is allow-java-encodings enabled?
    private boolean fAllowJavaEncodings = false;
    
    // enforcing strict uri?
    private boolean fStrictURI = false;

    private SymbolTable fSymbolTable = null;
    private XMLErrorReporter fErrorReporter = new XMLErrorReporter ();
    private XMLEntityResolver fEntityResolver = null;
    private XMLGrammarPool fGrammarPool = null;
    private String fExternalSchemas = null;
    private String fExternalNoNSSchema = null;
    private Object fJAXPSource = null;
    private Hashtable fJAXPCache;
    private Locale fLocale = Locale.getDefault();

    private XSDHandler fSchemaHandler;
    // the grammar bucket
    private XSGrammarBucket fGrammarBucket;
    private XSDeclarationPool fDeclPool = null;
    private SubstitutionGroupHandler fSubGroupHandler;
    private CMBuilder fCMBuilder;
    // boolean that tells whether we've tested the JAXP property.
    private boolean fJAXPProcessed = false;


    // containers
    private XSDDescription fXSDDescription = new XSDDescription();

    // default constructor.  Create objects we absolutely need:
    public XMLSchemaLoader() {
        this( new SymbolTable(), null, new XMLEntityManager(), null, null, null);
    }

    public XMLSchemaLoader(SymbolTable symbolTable) {
        this( symbolTable, null, new XMLEntityManager(), null, null, null);
    }

    XMLSchemaLoader(XMLErrorReporter errorReporter,
                XSGrammarBucket grammarBucket,
                SubstitutionGroupHandler sHandler, CMBuilder builder) {
        this(null, errorReporter, null, grammarBucket, sHandler, builder);
    }

    XMLSchemaLoader(SymbolTable symbolTable,
                XMLErrorReporter errorReporter,
                XMLEntityResolver entityResolver,
                XSGrammarBucket grammarBucket,
                SubstitutionGroupHandler sHandler,
                CMBuilder builder) {
        fSymbolTable = symbolTable;
        if(errorReporter == null) {
            errorReporter = new XMLErrorReporter ();
            errorReporter.setProperty(ERROR_HANDLER, new DefaultErrorHandler());
        }
        fErrorReporter = errorReporter;
        fEntityResolver = entityResolver;
        if(grammarBucket == null ) {
            grammarBucket = new XSGrammarBucket();
        }
        fGrammarBucket = grammarBucket;
        if(sHandler == null) {
            sHandler = new SubstitutionGroupHandler(fGrammarBucket);
        }
        fSubGroupHandler = sHandler;
        //get an instance of the CMNodeFactory */
        CMNodeFactory nodeFactory = new CMNodeFactory() ;
        //REVISIT: shouldn't the SecurityManager be allowed to set, if an application tries to load standalone schema - nb.
        if(builder == null) {
            builder = new CMBuilder(nodeFactory);
        }
        fCMBuilder = builder;
        fSchemaHandler = new XSDHandler(fGrammarBucket);
        fDeclPool = new XSDeclarationPool();
        fJAXPCache = new Hashtable();
    }

    /**
     * Returns a list of feature identifiers that are recognized by
     * this XMLGrammarLoader.  This method may return null if no features
     * are recognized.
     */
    public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } // getRecognizedFeatures():  String[]

    /**
     * Returns the state of a feature.
     *
     * @param featureId The feature identifier.
     *
     * @throws XMLConfigurationException Thrown on configuration error.
     */
    public boolean getFeature(String featureId)
            throws XMLConfigurationException {
        if(featureId.equals(SCHEMA_FULL_CHECKING)) {
            return fIsCheckedFully;
        } else if(featureId.equals(CONTINUE_AFTER_FATAL_ERROR)) {
            return fErrorReporter.getFeature(CONTINUE_AFTER_FATAL_ERROR);
        }
        throw new XMLConfigurationException(XMLConfigurationException.NOT_RECOGNIZED, featureId);
    } // getFeature (String):  boolean

    /**
     * Sets the state of a feature.
     *
     * @param featureId The feature identifier.
     * @param state     The state of the feature.
     *
     * @throws XMLConfigurationException Thrown when a feature is not
     *                  recognized or cannot be set.
     */
    public void setFeature(String featureId,
                boolean state) throws XMLConfigurationException {
        if(featureId.equals(SCHEMA_FULL_CHECKING)) {
            fIsCheckedFully = state;
        } else if(featureId.equals(CONTINUE_AFTER_FATAL_ERROR)) {
            fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, state);
        } else if(featureId.equals(ALLOW_JAVA_ENCODINGS)) {
            fAllowJavaEncodings = state;
        } else if(featureId.equals(STANDARD_URI_CONFORMANT_FEATURE)) {
            fStrictURI = state;
        } else {
            throw new XMLConfigurationException(XMLConfigurationException.NOT_RECOGNIZED, featureId);
        }
    } // setFeature(String, boolean)

    /**
     * Returns a list of property identifiers that are recognized by
     * this XMLGrammarLoader.  This method may return null if no properties
     * are recognized.
     */
    public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } // getRecognizedProperties():  String[]

    /**
     * Returns the state of a property.
     *
     * @param propertyId The property identifier.
     *
     * @throws XMLConfigurationException Thrown on configuration error.
     */
    public Object getProperty(String propertyId)
            throws XMLConfigurationException {
        if(propertyId.equals( SYMBOL_TABLE)) {
            return fSymbolTable;
        } else if(propertyId.equals( ERROR_REPORTER)) {
            return fErrorReporter;
        } else if(propertyId.equals( ERROR_HANDLER)) {
            return fErrorReporter.getErrorHandler();
        } else if(propertyId.equals( ENTITY_RESOLVER)) {
            return fEntityResolver;
        } else if(propertyId.equals( XMLGRAMMAR_POOL)) {
            return fGrammarPool;
        } else if(propertyId.equals( SCHEMA_LOCATION)) {
            return fExternalSchemas;
        } else if(propertyId.equals( SCHEMA_NONS_LOCATION) ){
            return fExternalNoNSSchema;
        } else if(propertyId.equals( JAXP_SCHEMA_SOURCE)) {
            return fJAXPSource;
        }
        throw new XMLConfigurationException(XMLConfigurationException.NOT_RECOGNIZED, propertyId);
    } // getProperty(String):  Object

    /**
     * Sets the state of a property.
     *
     * @param propertyId The property identifier.
     * @param state     The state of the property.
     *
     * @throws XMLConfigurationException Thrown when a property is not
     *                  recognized or cannot be set.
     */
    public void setProperty(String propertyId,
                Object state) throws XMLConfigurationException, ClassCastException {
        if(propertyId.equals( SYMBOL_TABLE)) {
            fSymbolTable = (SymbolTable)state;
        } else if(propertyId.equals( ERROR_REPORTER)) {
            fErrorReporter = (XMLErrorReporter)state;
        } else if(propertyId.equals( ERROR_HANDLER)) {
            fErrorReporter.setProperty(propertyId, state);
        } else if(propertyId.equals( ENTITY_RESOLVER)) {
            fEntityResolver = (XMLEntityResolver)state;
        } else if(propertyId.equals( XMLGRAMMAR_POOL)) {
            fGrammarPool = (XMLGrammarPool)state;
        } else if(propertyId.equals( SCHEMA_LOCATION)) {
            fExternalSchemas = (String)state;
        } else if(propertyId.equals( SCHEMA_NONS_LOCATION)) {
            fExternalNoNSSchema = (String)state;
        } else if(propertyId.equals( JAXP_SCHEMA_SOURCE)) {
            fJAXPSource = state;
            fJAXPProcessed = false;
        } else
            throw new XMLConfigurationException(XMLConfigurationException.NOT_RECOGNIZED, propertyId);
    } // setProperty(String, Object)

    /**
     * Set the locale to use for messages.
     *
     * @param locale The locale object to use for localization of messages.
     *
     * @exception XNIException Thrown if the parser does not support the
     *                         specified locale.
     */
    public void setLocale(Locale locale) {
        fLocale = locale;
    } // setLocale(Locale)

    /** Return the Locale the XMLGrammarLoader is using. */
    public Locale getLocale() {
        return fLocale;
    } // getLocale():  Locale

    /**
     * Sets the error handler.
     *
     * @param errorHandler The error handler.
     */
    public void setErrorHandler(XMLErrorHandler errorHandler) {
        fErrorReporter.setProperty(ERROR_HANDLER, errorHandler);
    } // setErrorHandler(XMLErrorHandler)

    /** Returns the registered error handler.  */
    public XMLErrorHandler getErrorHandler() {
        return fErrorReporter.getErrorHandler();
    } // getErrorHandler():  XMLErrorHandler

    /**
     * Sets the entity resolver.
     *
     * @param entityResolver The new entity resolver.
     */
    public void setEntityResolver(XMLEntityResolver entityResolver) {
        fEntityResolver = entityResolver;
    } // setEntityResolver(XMLEntityResolver)

    /** Returns the registered entity resolver.  */
    public XMLEntityResolver getEntityResolver() {
        return fEntityResolver;
    } // getEntityResolver():  XMLEntityResolver

    // reset all objects that "belong" to this one.
    public void reset () {
        fGrammarBucket.reset();
        //we should retreive the initial grammar set given by the application
        //to the parser and put it in local grammar bucket.

        // make sure error reporter knows about schemas...
        if(fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
            fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
        }

        if(fGrammarPool != null) {

            Grammar [] initialGrammars = fGrammarPool.retrieveInitialGrammarSet(XMLGrammarDescription.XML_SCHEMA);
            for (int i = 0; i < initialGrammars.length; i++) {
                // put this grammar into the bucket, along with grammars
                // imported by it (directly or indirectly)
                if (!fGrammarBucket.putGrammar((SchemaGrammar)(initialGrammars[i]), true)) {
                    // REVISIT: a conflict between new grammar(s) and grammars
                    // in the bucket. What to do? A warning? An exception?
                    fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                                                                 "GrammarConflict", null,
                                                                 XMLErrorReporter.SEVERITY_WARNING);
                }
            }
            fCMBuilder.setDeclPool(null);
        } else {
            fDeclPool.reset();
            fCMBuilder.setDeclPool(fDeclPool);
        }

        fSchemaHandler.reset(fErrorReporter, fEntityResolver,
                fSymbolTable, fGrammarPool, fAllowJavaEncodings, fStrictURI);
        if(fGrammarPool == null) {
            fDeclPool.reset();
            fSchemaHandler.setDeclPool(fDeclPool);
        } else {
            fSchemaHandler.setDeclPool(null);
        }
        fSubGroupHandler.reset();
        fJAXPProcessed = false;
    } // reset()

    /**
     * Returns a Grammar object by parsing the contents of the
     * entity pointed to by source.
     *
     * @param source        the location of the entity which forms
     *                          the starting point of the grammar to be constructed.
     * @throws IOException      When a problem is encountered reading the entity
     *          XNIException    When a condition arises (such as a FatalError) that requires parsing
     *                              of the entity be terminated.
     */
    public Grammar loadGrammar(XMLInputSource source)
                throws IOException, XNIException {
        reset();
        XSDDescription desc = new XSDDescription();
        desc.fContextType = XSDDescription.CONTEXT_PREPARSE;
        desc.setBaseSystemId(source.getBaseSystemId());
        desc.setLiteralSystemId( source.getSystemId());
        // none of the other fields make sense for preparsing
        Hashtable locationPairs = new Hashtable();
        // Process external schema location properties.
        // We don't call tokenizeSchemaLocationStr here, because we also want
        // to check whether the values are valid URI.
        processExternalHints(fExternalSchemas, fExternalNoNSSchema,
                             locationPairs, fErrorReporter);
        SchemaGrammar grammar = loadSchema(desc, source, locationPairs);
        if(grammar != null && fGrammarPool != null) {
            fGrammarPool.cacheGrammars(XMLGrammarDescription.XML_SCHEMA, fGrammarBucket.getGrammars());
        }
        return grammar;
    } // loadGrammar(XMLInputSource):  Grammar

    SchemaGrammar loadSchema(XSDDescription desc,
            XMLInputSource source,
            Hashtable locationPairs) throws IOException, XNIException {

        // this should only be done once per invocation of this object;
        // unless application alters JAXPSource in the mean time.
        if(!fJAXPProcessed) {
            processJAXPSchemaSource(locationPairs);
        }
        SchemaGrammar grammar = fSchemaHandler.parseSchema(source, desc, locationPairs);
        // is full-checking enabled?  If so, if we're preparsing we'll
        // need to let XSConstraints have a go at the new grammar.
        if(fIsCheckedFully) {
            XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
        }
        return grammar;
    } // loadSchema(XSDDescription, XMLInputSource):  SchemaGrammar

    // this makes use of the schema location property values.
    // we store the namespace/location pairs in a hashtable (use "" as the
    // namespace of absent namespace). when resolving an entity, we first try
    // to find in the hashtable whether there is a value for that namespace,
    // if so, pass that location value to the user-defined entity resolver.
    public static XMLInputSource resolveDocument(XSDDescription desc, Hashtable locationPairs,
            XMLEntityResolver entityResolver) throws IOException {
        String loc = null;
        // we consider the schema location properties for import
        if (desc.getContextType() == XSDDescription.CONTEXT_IMPORT ||
            desc.fromInstance()) {
            // use empty string as the key for absent namespace
            String namespace = desc.getTargetNamespace();
            String ns = namespace == null ? XMLSymbols.EMPTY_STRING : namespace;
            // get the location hint for that namespace
            LocationArray tempLA = (LocationArray)locationPairs.get(ns);
            if(tempLA != null)
                loc = tempLA.getFirstLocation();
        }

        // if it's not import, or if the target namespace is not set
        // in the schema location properties, use location hint
        if (loc == null) {
            String[] hints = desc.getLocationHints();
            if (hints != null && hints.length > 0)
                loc = hints[0];
        }

        String expandedLoc = XMLEntityManager.expandSystemId(loc, desc.getBaseSystemId(), false);
        desc.setLiteralSystemId(loc);
        desc.setExpandedSystemId(expandedLoc);
        return entityResolver.resolveEntity(desc);
    }

    // add external schema locations to the location pairs
    public static void processExternalHints(String sl, String nsl,
                                            Hashtable locations,
                                            XMLErrorReporter er) {
        if (sl != null) {
            try {
                // get the attribute decl for xsi:schemaLocation
                // because external schema location property has the same syntax
                // as xsi:schemaLocation
                XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
                // validation the string value to get the list of URI's
                Object actualValue = attrDecl.fType.validate(sl, null, null);
                Object[] uris = (Object[])actualValue;
                // if there are even number of URI's
                // add them to the location pairs
                if (uris.length % 2 == 0) {
                    String namespace, location;
                    for (int i = 0; i < uris.length;) {
                        namespace = (String)uris[i++];
                        location = (String)uris[i++];
                        LocationArray la = ((LocationArray)locations.get(namespace));
                        if(la == null) {
                            la = new LocationArray();
                            locations.put(namespace, la);
                        }
                        la.addLocation(location);
                    }
                }
                else {
                    // report warning (odd number of items)
                    er.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                                   "SchemaLocation",
                                   new Object[]{sl},
                                   XMLErrorReporter.SEVERITY_WARNING);
                }
            }
            catch (InvalidDatatypeValueException ex) {
                // report warning (not list of URI's)
                er.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                               ex.getKey(), ex.getArgs(),
                               XMLErrorReporter.SEVERITY_WARNING);
            }
        }

        if (nsl != null) {
            try {
                // similarly for no ns schema location property
                XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
                attrDecl.fType.validate(nsl, null, null);
                LocationArray la = ((LocationArray)locations.get(XMLSymbols.EMPTY_STRING));
                if(la == null) {
                    la = new LocationArray();
                    locations.put(XMLSymbols.EMPTY_STRING, la);
                }
                la.addLocation(nsl);
            }
            catch (InvalidDatatypeValueException ex) {
                // report warning (not a URI)
                er.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                               ex.getKey(), ex.getArgs(),
                               XMLErrorReporter.SEVERITY_WARNING);
            }
        }
    }
    // this method takes a SchemaLocation string.
    // If an error is encountered, false is returned;
    // otherwise, true is returned.  In either case, locations
    // is augmented to include as many tokens as possible.
    // @param schemaStr     The schemaLocation string to tokenize
    // @param locations     Hashtable mapping namespaces to LocationArray objects holding lists of locaitons
    // @return true if no problems; false if string could not be tokenized
    public static boolean tokenizeSchemaLocationStr(String schemaStr, Hashtable locations) {
        if (schemaStr!= null) {
            StringTokenizer t = new StringTokenizer(schemaStr, " \n\t\r");
            String namespace, location;
            while (t.hasMoreTokens()) {
                namespace = t.nextToken ();
                if (!t.hasMoreTokens()) {
                    return false; // error!
                }
                location = t.nextToken();
                LocationArray la = ((LocationArray)locations.get(namespace));
                if(la == null) {
                    la = new LocationArray();
                    locations.put(namespace, la);
                }
                la.addLocation(location);
            }
        }
        return true;
    } // tokenizeSchemaLocation(String, Hashtable):  boolean

    /**
     * Translate the various JAXP SchemaSource property types to XNI
     * XMLInputSource.  Valid types are: String, org.xml.sax.InputSource,
     * InputStream, File, or Object[] of any of previous types.
     * REVISIT:  the JAXP 1.2 spec is less than clear as to whether this property
     * should be available to imported schemas.  I have assumed
     * that it should.  - NG
     */
    private void processJAXPSchemaSource(Hashtable locationPairs) throws IOException {
        fJAXPProcessed = true;
        if (fJAXPSource == null) {
            return;
        }

        Class componentType = fJAXPSource.getClass().getComponentType();
        XMLInputSource xis = null;
        String sid = null;
        if (componentType == null) {
            // Not an array
            if(fJAXPSource instanceof InputStream ||
                    fJAXPSource instanceof InputSource) {
                SchemaGrammar g = (SchemaGrammar)fJAXPCache.get(fJAXPSource);
                if(g != null) {
                    fGrammarBucket.putGrammar(g);
                    return;
                }
            }
            fXSDDescription.reset();
            xis = xsdToXMLInputSource(fJAXPSource);
            sid = xis.getSystemId();
            fXSDDescription.fContextType = XSDDescription.CONTEXT_PREPARSE;
            if (sid != null) {
                fXSDDescription.setLiteralSystemId(sid);
                fXSDDescription.setExpandedSystemId(sid);
                fXSDDescription.fLocationHints = new String[]{sid};
            }
            SchemaGrammar g = loadSchema(fXSDDescription, xis, locationPairs);
            if(fJAXPSource instanceof InputStream ||
                    fJAXPSource instanceof InputSource) {
                fJAXPCache.put(fJAXPSource, g);
            }
            fGrammarBucket.putGrammar(g);
            return ;
        } else if ( (componentType != Object.class) &&
                    (componentType != String.class) &&
                    (componentType != File.class) &&
                    (componentType != InputStream.class) &&
                    (componentType != InputSource.class)
                  ) {
            // Not an Object[], String[], File[], InputStream[], InputSource[]
            throw new XMLConfigurationException(
                XMLConfigurationException.NOT_SUPPORTED, "\""+JAXP_SCHEMA_SOURCE+
                "\" property cannot have an array of type {"+componentType.getName()+
                "}. Possible types of the array supported are Object, String, File, "+
                "InputStream, InputSource.");
        }

        // JAXP spec. allow []s of type String, File, InputStream,
        // InputSource also, apart from [] of type Object.
        Object[] objArr = (Object[]) fJAXPSource;
        //make local vector for storing targetn namespaces of schemasources specified in object arrays.
        Vector jaxpSchemaSourceNamespaces = new Vector() ;
        for (int i = 0; i < objArr.length; i++) {
            if(objArr[i] instanceof InputStream ||
                    objArr[i] instanceof InputSource) {
                SchemaGrammar g = (SchemaGrammar)fJAXPCache.get(objArr[i]);
                if (g != null) {
                    fGrammarBucket.putGrammar(g);
                    continue;
                }
            }
            fXSDDescription.reset();
            xis = xsdToXMLInputSource(objArr[i]);
            sid = xis.getSystemId();
            fXSDDescription.fContextType = XSDDescription.CONTEXT_PREPARSE;
            if (sid != null) {
                fXSDDescription.setLiteralSystemId(sid);
                fXSDDescription.setExpandedSystemId(sid);
                fXSDDescription.fLocationHints = new String[]{sid};
            }
            String targetNamespace = null ;
            SchemaGrammar grammar = loadSchema(fXSDDescription, xis, locationPairs);
            if(grammar != null){
                targetNamespace = grammar.getTargetNamespace() ;
                if(jaxpSchemaSourceNamespaces.contains(targetNamespace)){
                    //when an array of objects is passed it is illegal to have two schemas that share same namespace.
                    throw new java.lang.IllegalArgumentException(
                        " When using array of Objects as the value of SCHEMA_SOURCE property , " +
                        "no two Schemas should share the same targetNamespace. " );
                }
                else{
                    jaxpSchemaSourceNamespaces.add(targetNamespace) ;
                }
                if(objArr[i] instanceof InputStream ||
                        objArr[i] instanceof InputSource) {
                    fJAXPCache.put(objArr[i], grammar);
                }
                fGrammarBucket.putGrammar(grammar);
            }
            else{
                //REVISIT: What should be the acutal behavior if grammar can't be loaded as specified in schema source?
            }
        }
    }//processJAXPSchemaSource

    private XMLInputSource xsdToXMLInputSource(
            Object val)
    {
        if (val instanceof String) {
            // String value is treated as a URI that is passed through the
            // EntityResolver
            String loc = (String) val;

            if (fEntityResolver != null) {

                fXSDDescription.reset();
                fXSDDescription.setValues(null, loc, null, null);
                XMLInputSource xis = null;
                try {
                    xis = fEntityResolver.resolveEntity(fXSDDescription);
                } catch (IOException ex) {
                    fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                        "schema_reference.4",
                                      new Object[] { loc }, XMLErrorReporter.SEVERITY_ERROR);
                }
                if (xis == null) {
                    // REVISIT: can this happen?
                    // Treat value as a URI and pass in as systemId
                    return new XMLInputSource(null, loc, null);
                }
                return xis;
            }
        } else if (val instanceof InputSource) {
            return saxToXMLInputSource((InputSource) val);
        } else if (val instanceof InputStream) {
            return new XMLInputSource(null, null, null,
                                      (InputStream) val, null);
        } else if (val instanceof File) {
            File file = (File) val;
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                "schema_reference.4", new Object[] { file.toString() },
                XMLErrorReporter.SEVERITY_ERROR);
            }
            return new XMLInputSource(null, null, null, is, null);
        }
        throw new XMLConfigurationException(
            XMLConfigurationException.NOT_SUPPORTED, "\""+JAXP_SCHEMA_SOURCE+
            "\" property cannot have a value of type {"+val.getClass().getName()+
            "}. Possible types of the value supported are String, File, InputStream, "+
            "InputSource OR an array of these types.");
    }


     //Convert a SAX InputSource to an equivalent XNI XMLInputSource

    private static XMLInputSource saxToXMLInputSource(InputSource sis) {
        String publicId = sis.getPublicId();
        String systemId = sis.getSystemId();

        Reader charStream = sis.getCharacterStream();
        if (charStream != null) {
            return new XMLInputSource(publicId, systemId, null, charStream,
                                      null);
        }

        InputStream byteStream = sis.getByteStream();
        if (byteStream != null) {
            return new XMLInputSource(publicId, systemId, null, byteStream,
                                      sis.getEncoding());
        }

        return new XMLInputSource(publicId, systemId, null);
    }

    static class LocationArray{

        int length ;
        String [] locations = new String[2];

        public void resize(int oldLength , int newLength){
            String [] temp = new String[newLength] ;
            System.arraycopy(locations, 0, temp, 0, Math.min(oldLength, newLength));
            locations = temp ;
            length = Math.min(oldLength, newLength);
        }

        public void addLocation(String location){
            if(length >= locations.length ){
                resize(length, Math.max(1, length*2));
            }
            locations[length++] = location;
        }//setLocation()

        public String [] getLocationArray(){
            if(length < locations.length ){
                resize(locations.length, length);
            }
            return locations;
        }//getLocationArray()

        public String getFirstLocation(){
            return length > 0 ? locations[0] : null;
        }

        public int getLength(){
            return length ;
        }

    } //locationArray

} // XMLGrammarLoader

