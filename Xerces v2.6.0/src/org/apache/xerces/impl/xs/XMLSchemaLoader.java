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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMStringListImpl;
import org.apache.xerces.dom3.DOMConfiguration;
import org.apache.xerces.dom3.DOMError;
import org.apache.xerces.dom3.DOMErrorHandler;
import org.apache.xerces.dom3.DOMStringList;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.models.CMNodeFactory;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.util.DOMErrorHandlerWrapper;
import org.apache.xerces.util.DefaultErrorHandler;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarLoader;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.DOMException;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;

/**
 * This class implements xni.grammars.XMLGrammarLoader.
 * It also serves as implementation of xs.XSLoader interface and DOMConfiguration interface.
 * 
 * This class is designed to interact either with a proxy for a user application 
 * which wants to preparse schemas, or with our own Schema validator.  
 * It is hoped that none of these "external" classes will therefore need to communicate directly
 * with XSDHandler in future.
 * <p>This class only knows how to make XSDHandler do its thing.
 * The caller must ensure that all its properties (schemaLocation, JAXPSchemaSource
 * etc.) have been properly set.
 *
 * @author Neil Graham, IBM
 * @version $Id: XMLSchemaLoader.java,v 1.1 2006/02/02 01:09:32 vauchers Exp $
 */

public class XMLSchemaLoader implements XMLGrammarLoader, XMLComponent,
                                        // XML Component API 
                                        XSLoader, DOMConfiguration {

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
        
    /** Feature: disallow doctype*/
    protected static final String DISALLOW_DOCTYPE = 
        Constants.XERCES_FEATURE_PREFIX + Constants.DISALLOW_DOCTYPE_DECL_FEATURE;
        
         
    protected static final String AUGMENT_PSVI = 
       Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_AUGMENT_PSVI;
           
    protected static final String PARSER_SETTINGS = 
                Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;   

    // recognized features:
    private static final String[] RECOGNIZED_FEATURES = {
        SCHEMA_FULL_CHECKING,
        AUGMENT_PSVI,
        CONTINUE_AFTER_FATAL_ERROR,
        ALLOW_JAVA_ENCODINGS,
        STANDARD_URI_CONFORMANT_FEATURE, 
        DISALLOW_DOCTYPE
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
   
    protected static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;  
        
   protected static final String ENTITY_MANAGER =
                Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;   

    // recognized properties
    private static final String [] RECOGNIZED_PROPERTIES = {
        ENTITY_MANAGER,
        SYMBOL_TABLE,
        ERROR_REPORTER,
        ERROR_HANDLER,
        ENTITY_RESOLVER,
        XMLGRAMMAR_POOL,
        SCHEMA_LOCATION,
        SCHEMA_NONS_LOCATION,
        JAXP_SCHEMA_SOURCE,
        SECURITY_MANAGER
    };

    // Data
    
    // features and properties
    private ParserConfigurationSettings fLoaderConfig = new ParserConfigurationSettings();
    private SymbolTable fSymbolTable = null;
    private XMLErrorReporter fErrorReporter = new XMLErrorReporter ();
    private XMLEntityManager fEntityManager = null;
    private XMLEntityResolver fUserEntityResolver = null;
    private XMLGrammarPool fGrammarPool = null;
    private String fExternalSchemas = null;
    private String fExternalNoNSSchema = null;
    // JAXP property: schema source 
    private Object fJAXPSource = null;
    // is Schema Full Checking enabled
    private boolean fIsCheckedFully = false;
    // boolean that tells whether we've tested the JAXP property.
    private boolean fJAXPProcessed = false;
    // if features/properties has not been changed, the value of this attribute is "false"
    private boolean fSettingsChanged = true;

    // xml schema parsing
    private XSDHandler fSchemaHandler;
    private XSGrammarBucket fGrammarBucket;
    private XSDeclarationPool fDeclPool = null;
    private SubstitutionGroupHandler fSubGroupHandler;
    private CMBuilder fCMBuilder;
    private XSDDescription fXSDDescription = new XSDDescription();
    
    private Hashtable fJAXPCache;
    private Locale fLocale = Locale.getDefault();

    // XSLoader attributes
    private DOMStringList fRecognizedParameters = null;
	/** DOM L3 error handler */
	private DOMErrorHandlerWrapper fErrorHandler = null;

    // default constructor.  Create objects we absolutely need:
    public XMLSchemaLoader() {
        this( new SymbolTable(), null, new XMLEntityManager(), null, null, null);
    }

    public XMLSchemaLoader(SymbolTable symbolTable) {
        this( symbolTable, null, new XMLEntityManager(), null, null, null);
    }

    /**
     * This constractor is used by the XMLSchemaValidator. Additional properties, i.e. XMLEntityManager, 
     * will be passed during reset(XMLComponentManager).
     * @param errorReporter
     * @param grammarBucket
     * @param sHandler
     * @param builder
     */
    XMLSchemaLoader(XMLErrorReporter errorReporter,
                XSGrammarBucket grammarBucket,
                SubstitutionGroupHandler sHandler, CMBuilder builder) {
        this(null, errorReporter, null, grammarBucket, sHandler, builder);
    }

    XMLSchemaLoader(SymbolTable symbolTable,
                XMLErrorReporter errorReporter,
                XMLEntityManager entityResolver,
                XSGrammarBucket grammarBucket,
                SubstitutionGroupHandler sHandler,
                CMBuilder builder) {
                    
        // store properties and features in configuration
        fLoaderConfig.addRecognizedFeatures(RECOGNIZED_FEATURES);
        fLoaderConfig.addRecognizedProperties(RECOGNIZED_PROPERTIES); 
        if (symbolTable != null){ 
            fLoaderConfig.setProperty(SYMBOL_TABLE, symbolTable);       
        }
                 
        if(errorReporter == null) {
            errorReporter = new XMLErrorReporter ();
            errorReporter.setLocale(fLocale);
            errorReporter.setProperty(ERROR_HANDLER, new DefaultErrorHandler());
            
        }
        fErrorReporter = errorReporter;
        // make sure error reporter knows about schemas...
        if(fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
            fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
        }
        fLoaderConfig.setProperty(ERROR_REPORTER, fErrorReporter);
        fEntityManager = entityResolver;   
        // entity manager is null if XMLSchemaValidator creates the loader  
        if (fEntityManager != null){   
            fLoaderConfig.setProperty(ENTITY_MANAGER, fEntityManager);
        }
              
        // by default augment PSVI (i.e. don't use declaration pool)
        fLoaderConfig.setFeature(AUGMENT_PSVI, true);
        
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

        if(builder == null) {
            builder = new CMBuilder(nodeFactory);
        }
        fCMBuilder = builder;
        fSchemaHandler = new XSDHandler(fGrammarBucket);
        fDeclPool = new XSDeclarationPool();
        fJAXPCache = new Hashtable();

        fSettingsChanged = true;
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
        return fLoaderConfig.getFeature(featureId);        
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
        fSettingsChanged = true; 
        if(featureId.equals(CONTINUE_AFTER_FATAL_ERROR)) {
            fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, state);
        } 
        fLoaderConfig.setFeature(featureId, state);
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
        return fLoaderConfig.getProperty(propertyId);
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
                Object state) throws XMLConfigurationException {                   
        fSettingsChanged = true;            
        // REVISIT: We cannot set null values on this configuration
        // because it is backed by a java.util.Hashtable which doesn't
        // permit null values. -- mrglavas
        if (state != null) fLoaderConfig.setProperty(propertyId, state);    
        if(propertyId.equals( JAXP_SCHEMA_SOURCE)) {
            fJAXPSource = state;
            fJAXPProcessed = false;
        }  
        else if(propertyId.equals( XMLGRAMMAR_POOL)) {
             fGrammarPool = (XMLGrammarPool)state;
        } 
        else if (propertyId.equals(SCHEMA_LOCATION)){
            fExternalSchemas = (String)state;
        }
        else if (propertyId.equals(SCHEMA_NONS_LOCATION)){
            fExternalNoNSSchema = (String) state;
        }
        else if (propertyId.equals(ENTITY_RESOLVER)){
            fEntityManager.setProperty(ENTITY_RESOLVER, state);
        }
        else if (propertyId.equals(ERROR_REPORTER)){
            fErrorReporter = (XMLErrorReporter)state;
            if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
            	fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
            }
        }
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
        fErrorReporter.setLocale(locale);
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
        fUserEntityResolver = entityResolver;
        fLoaderConfig.setProperty(ENTITY_RESOLVER, entityResolver);
        fEntityManager.setProperty(ENTITY_RESOLVER, entityResolver);
    } // setEntityResolver(XMLEntityResolver)

    /** Returns the registered entity resolver.  */
    public XMLEntityResolver getEntityResolver() {
        return fUserEntityResolver;
    } // getEntityResolver():  XMLEntityResolver

    
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
                	
        // REVISIT: this method should have a namespace parameter specified by 
        // user. In this case we can easily detect if a schema asked to be loaded
        // is already in the local cache.
              	
        reset(fLoaderConfig);
        fSettingsChanged = false;
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
            // NOTE: we only need to verify full checking in case the schema was not provided via JAXP
            // since full checking already verified for all JAXP schemas
            if(fIsCheckedFully && fJAXPCache.get(grammar) != grammar) {
                XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
            }
        }
        return grammar;
    } // loadGrammar(XMLInputSource):  Grammar

    /**
     * This method is called either from XMLGrammarLoader.loadGrammar or from XMLSchemaValidator.
     * Note: in either case, the EntityManager (or EntityResolvers) are not going to be invoked
     * to resolve the location of the schema in XSDDescription 
     * @param desc
     * @param source
     * @param locationPairs
     * @return An XML Schema grammar
     * @throws IOException
     * @throws XNIException
     */
    SchemaGrammar loadSchema(XSDDescription desc,
            XMLInputSource source,
            Hashtable locationPairs) throws IOException, XNIException {

        // this should only be done once per invocation of this object;
        // unless application alters JAXPSource in the mean time.
        if(!fJAXPProcessed) {
            processJAXPSchemaSource(locationPairs);
        }
        SchemaGrammar grammar = fSchemaHandler.parseSchema(source, desc, locationPairs);

        return grammar;
    } // loadSchema(XSDDescription, XMLInputSource):  SchemaGrammar

    /** This method tries to resolve location of the given schema.
     * The loader stores the namespace/location pairs in a hashtable (use "" as the
     * namespace of absent namespace). When resolving an entity, loader first tries
     * to find in the hashtable whether there is a value for that namespace,
     * if so, pass that location value to the user-defined entity resolver.
     *
     * @param desc
     * @param locationPairs
     * @param entityResolver
     * @return
     * @throws IOException
     */
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
                attrDecl.fType.validate(sl, null, null);
                if (!tokenizeSchemaLocationStr(sl, locations)) {
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
     * Note: all JAXP schema files will be checked for full-schema validity if the feature was set up
     * 
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
				fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
                fXSDDescription.setLiteralSystemId(sid);
                fXSDDescription.setExpandedSystemId(sid);
                fXSDDescription.fLocationHints = new String[]{sid};
            }
            SchemaGrammar g = loadSchema(fXSDDescription, xis, locationPairs);
            // it is possible that we won't be able to resolve JAXP schema-source location
            if (g != null){
                if(fJAXPSource instanceof InputStream ||
                    fJAXPSource instanceof InputSource) {
                        fJAXPCache.put(fJAXPSource, g);
                        if(fIsCheckedFully) {
                            XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
                        }
                }
                fGrammarBucket.putGrammar(g);
            }
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
				fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
                fXSDDescription.setLiteralSystemId(sid);
                fXSDDescription.setExpandedSystemId(sid);
                fXSDDescription.fLocationHints = new String[]{sid};
            }
            String targetNamespace = null ;
            // load schema
			SchemaGrammar grammar = fSchemaHandler.parseSchema(xis,fXSDDescription, locationPairs);

			if(fIsCheckedFully) {
				XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
			}                                   
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
                fXSDDescription.reset();
                fXSDDescription.setValues(null, loc, null, null);
                XMLInputSource xis = null;
                try {
                    xis = fEntityManager.resolveEntity(fXSDDescription);
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

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.parser.XMLComponent#getFeatureDefault(java.lang.String)
	 */
	public Boolean getFeatureDefault(String featureId) {
		if (featureId.equals(AUGMENT_PSVI)){
            return Boolean.TRUE;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.parser.XMLComponent#getPropertyDefault(java.lang.String)
	 */
	public Object getPropertyDefault(String propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.parser.XMLComponent#reset(org.apache.xerces.xni.parser.XMLComponentManager)
	 */
	public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
		
		fGrammarBucket.reset();
        
		fSubGroupHandler.reset();		
		
        boolean parser_settings;
        try {
            parser_settings = componentManager.getFeature(PARSER_SETTINGS);     
        }
        catch (XMLConfigurationException e){
            parser_settings = true;
        }
        if (!parser_settings || !fSettingsChanged){            
            // reinitialize grammar bucket
            initGrammarBucket();
            return;           
        } 
        
        // get registered entity manager to be able to resolve JAXP schema-source property:
        // Note: in case XMLSchemaValidator has created the loader, 
        // the entity manager property is null
        fEntityManager = (XMLEntityManager)componentManager.getProperty(ENTITY_MANAGER);      
		
        boolean psvi = true;
        try {
            psvi = componentManager.getFeature(AUGMENT_PSVI);
        } catch (XMLConfigurationException e) {
            psvi = false;
        }
        
        if (!psvi) {
            fDeclPool.reset();
            fCMBuilder.setDeclPool(fDeclPool);
            fSchemaHandler.setDeclPool(fDeclPool);
        } else {
            fCMBuilder.setDeclPool(null);
            fSchemaHandler.setDeclPool(null);
        }
        		
		// get schema location properties
		try {
			fExternalSchemas = (String) componentManager.getProperty(SCHEMA_LOCATION);
			fExternalNoNSSchema =
				(String) componentManager.getProperty(SCHEMA_NONS_LOCATION);
		} catch (XMLConfigurationException e) {
			fExternalSchemas = null;
			fExternalNoNSSchema = null;
		}
		// get JAXP sources if available
		try {
			fJAXPSource = componentManager.getProperty(JAXP_SCHEMA_SOURCE);
			fJAXPProcessed = false;

		} catch (XMLConfigurationException e) {
			fJAXPSource = null;
			fJAXPProcessed = false;
		}
		
		// clear grammars, and put the one for schema namespace there
		try {
			fGrammarPool = (XMLGrammarPool) componentManager.getProperty(XMLGRAMMAR_POOL);
		} catch (XMLConfigurationException e) {
			fGrammarPool = null;
		}
        initGrammarBucket();
		// get continue-after-fatal-error feature
		try {
			boolean fatalError = componentManager.getFeature(CONTINUE_AFTER_FATAL_ERROR);
			fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, fatalError);
		} catch (XMLConfigurationException e) {
		}
        // set full validation to false        
        try {
            fIsCheckedFully = componentManager.getFeature(SCHEMA_FULL_CHECKING);
        }
        catch (XMLConfigurationException e){
            fIsCheckedFully = false;
        }
        		
		fSchemaHandler.reset(componentManager);		 
	}
	
	private void initGrammarBucket(){
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
		}
	}
		

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.XSLoader#getConfig()
     */
    public DOMConfiguration getConfig() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.XSLoader#load(org.w3c.dom.ls.LSInput)
     */
    public XSModel load(LSInput is) {
        try {
            Grammar g = loadGrammar(dom2xmlInputSource(is));
            return ((XSGrammar) g).toXSModel();
        } catch (Exception e) {
            if (fErrorHandler != null) {
                DOMErrorImpl error = new DOMErrorImpl();
                error.fException = e;
                error.fMessage = e.getMessage();
                error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                fErrorHandler.getErrorHandler().handleError(error);
            }
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.XSLoader#loadInputList(org.apache.xerces.xs.DOMInputList)
     */
    public XSModel loadInputList(LSInputList is) {
        int length = is.getLength();
        if (length == 0) {
            return null;
        }
        SchemaGrammar[] gs = new SchemaGrammar[length];
        for (int i = 0; i < length; i++) {
            try {
                gs[i] = (SchemaGrammar) loadGrammar(dom2xmlInputSource(is.item(i)));
            } catch (Exception e) {
                if (fErrorHandler != null) {
                    DOMErrorImpl error = new DOMErrorImpl();
                    error.fException = e;
                    error.fMessage = e.getMessage();
                    error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                    fErrorHandler.getErrorHandler().handleError(error);
                }
                return null;
            }
        }
        return new XSModelImpl(gs);
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.XSLoader#loadURI(java.lang.String)
     */
    public XSModel loadURI(String uri) {
    	try {
			Grammar g = loadGrammar(new XMLInputSource(null, uri, null));
			return ((XSGrammar)g).toXSModel();
    	}
    	catch (Exception e){
			if (fErrorHandler != null) {
				DOMErrorImpl error = new DOMErrorImpl();
				error.fException = e;
				error.fMessage = e.getMessage();
				error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
				fErrorHandler.getErrorHandler().handleError(error);
			}
    		return null;
    	}
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.XSLoader#loadURIList(org.apache.xerces.xs.StringList)
     */
    public XSModel loadURIList(StringList uriList) {
        int length = uriList.getLength();
        if (length == 0) {
            return null;
        }
        SchemaGrammar[] gs = new SchemaGrammar[length];
        for (int i = 0; i < length; i++) {
            try {
                gs[i] =
                    (SchemaGrammar) loadGrammar(new XMLInputSource(null, uriList.item(i), null));
            } catch (Exception e) {
                if (fErrorHandler != null) {
                    DOMErrorImpl error = new DOMErrorImpl();
                    error.fException = e;
                    error.fMessage = e.getMessage();
                    error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                    fErrorHandler.getErrorHandler().handleError(error);
                }
                return null;
            }
        }
        return new XSModelImpl(gs);
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.dom3.DOMConfiguration#canSetParameter(java.lang.String, java.lang.Object)
     */
    public boolean canSetParameter(String name, Object value) {
		if(value instanceof Boolean){
			boolean state = ((Boolean)value).booleanValue();
			if (name.equals(Constants.DOM_VALIDATE) ||
			    name.equals(SCHEMA_FULL_CHECKING) ||
			    name.equals(CONTINUE_AFTER_FATAL_ERROR) ||
			    name.equals(ALLOW_JAVA_ENCODINGS) ||
			    name.equals(STANDARD_URI_CONFORMANT_FEATURE)){
			    	return true;

			}
			return false;			
		}
		if (name.equals(Constants.DOM_ERROR_HANDLER)||
		    name.equals(SYMBOL_TABLE) ||
			name.equals(ERROR_REPORTER) ||
			name.equals(ERROR_HANDLER) ||
			name.equals(ENTITY_RESOLVER) ||
			name.equals(XMLGRAMMAR_POOL) ||
			name.equals(SCHEMA_LOCATION) ||
			name.equals(SCHEMA_NONS_LOCATION) ||
			name.equals(JAXP_SCHEMA_SOURCE)){
				return true;
		}
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.dom3.DOMConfiguration#getParameter(java.lang.String)
     */
    public Object getParameter(String name) throws DOMException {
		if (name.equals(Constants.DOM_ERROR_HANDLER)){
			if (fErrorHandler != null){
				return fErrorHandler.getErrorHandler();
			}
		}
        try {
            boolean feature = getFeature(name);
            return (feature) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception e) {
            Object property;
            try {
                property = getProperty(name);
                return property;
            } catch (Exception ex) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "FEATURE_NOT_SUPPORTED",
                        new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.dom3.DOMConfiguration#getParameterNames()
     */
    public DOMStringList getParameterNames() {
        if (fRecognizedParameters == null){
        	Vector v = new Vector();
			v.add("validate");
			v.add(SYMBOL_TABLE);
			v.add(ERROR_REPORTER);
			v.add(ERROR_HANDLER);
			v.add(ENTITY_RESOLVER);
			v.add(XMLGRAMMAR_POOL);
			v.add(SCHEMA_LOCATION);
			v.add(SCHEMA_NONS_LOCATION);
			v.add(JAXP_SCHEMA_SOURCE);
			v.add(SCHEMA_FULL_CHECKING);
			v.add(CONTINUE_AFTER_FATAL_ERROR);
			v.add(ALLOW_JAVA_ENCODINGS);
			v.add(STANDARD_URI_CONFORMANT_FEATURE);
        	fRecognizedParameters = new DOMStringListImpl(v);      	
        }
        return fRecognizedParameters;
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.dom3.DOMConfiguration#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) throws DOMException {
        if (value instanceof Boolean) {
            boolean state = ((Boolean) value).booleanValue();
            if (name.equals("validate") && state) {
                return;
            }
            try {
                setFeature(name, state);
            } catch (Exception e) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "FEATURE_NOT_SUPPORTED",
                        new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
            return;
        }
        if (name.equals(Constants.DOM_ERROR_HANDLER)) {
            if (value instanceof DOMErrorHandler) {
                try {
                    fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler) value);
                    setErrorHandler(fErrorHandler);
                } catch (XMLConfigurationException e) {
                }
            } else {
                // REVISIT: type mismatch
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "FEATURE_NOT_SUPPORTED",
                        new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
            return;

        }

        try {
            setProperty(name, value);
        } catch (Exception ex) {

            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_SUPPORTED",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);

        }

    }
    
	private XMLInputSource dom2xmlInputSource(LSInput is) {
		// need to wrap the LSInput with an XMLInputSource
		XMLInputSource xis = null;
		// if there is a string data, use a StringReader
		// according to DOM, we need to treat such data as "UTF-16".
		if (is.getStringData() != null) {
			xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
									 is.getBaseURI(), new StringReader(is.getStringData()),
									 "UTF-16");
		}
		// check whether there is a Reader
		// according to DOM, we need to treat such reader as "UTF-16".
		else if (is.getCharacterStream() != null) {
			xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
									 is.getBaseURI(), is.getCharacterStream(),
									 "UTF-16");
		}
		// check whether there is an InputStream
		else if (is.getByteStream() != null) {
			xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
									 is.getBaseURI(), is.getByteStream(),
									 is.getEncoding());
		}
		// otherwise, just use the public/system/base Ids
		else {
			xis = new XMLInputSource(is.getPublicId(), is.getSystemId(),
									 is.getBaseURI());
		}

		return xis;
	}

} // XMLGrammarLoader

