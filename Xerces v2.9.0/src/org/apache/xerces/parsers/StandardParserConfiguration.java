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

package org.apache.xerces.parsers;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

/**
 * This is the "standard" parser configuration. It extends the DTD
 * configuration with the standard set of parser components. 
 * The standard set of parser components include those needed
 * to parse and validate with DTD's, and those needed for XML
 * Schema.</p>
 * <p>
 * In addition to the features and properties recognized by the base
 * parser configuration, this class recognizes these additional 
 * features and properties:
 * <ul>
 * <li>Features
 *  <ul>
 *  <li>http://apache.org/xml/features/validation/schema</li>
 *  <li>http://apache.org/xml/features/validation/schema-full-checking</li>
 *  <li>http://apache.org/xml/features/validation/schema/normalized-value</li>
 *  <li>http://apache.org/xml/features/validation/schema/element-default</li>
 *  </ul>
 * <li>Properties
 *  <ul>
 *   <li>http://apache.org/xml/properties/internal/error-reporter</li>
 *   <li>http://apache.org/xml/properties/internal/entity-manager</li>
 *   <li>http://apache.org/xml/properties/internal/document-scanner</li>
 *   <li>http://apache.org/xml/properties/internal/dtd-scanner</li>
 *   <li>http://apache.org/xml/properties/internal/grammar-pool</li>
 *   <li>http://apache.org/xml/properties/internal/validator/dtd</li>
 *   <li>http://apache.org/xml/properties/internal/datatype-validator-factory</li>
 *  </ul>
 * </ul>
 *
 * @author Arnaud  Le Hors, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id: StandardParserConfiguration.java 447239 2006-09-18 05:08:26Z mrglavas $
 */
public class StandardParserConfiguration
    extends DTDConfiguration {

    //
    // Constants
    //

    // feature identifiers

    /** Feature identifier: expose schema normalized value */
    protected static final String NORMALIZE_DATA =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_NORMALIZED_VALUE;


    /** Feature identifier: send element default value via characters() */
    protected static final String SCHEMA_ELEMENT_DEFAULT =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_ELEMENT_DEFAULT;


    /** Feature identifier: augment PSVI */
    protected static final String SCHEMA_AUGMENT_PSVI =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_AUGMENT_PSVI;


    /** feature identifier: XML Schema validation */
    protected static final String XMLSCHEMA_VALIDATION = 
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;

    /** feature identifier: XML Schema validation -- full checking */
    protected static final String XMLSCHEMA_FULL_CHECKING = 
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;
    
    /** Feature: generate synthetic annotations */
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = 
        Constants.XERCES_FEATURE_PREFIX + Constants.GENERATE_SYNTHETIC_ANNOTATIONS_FEATURE;
    
    /** Feature identifier: validate annotations */
    protected static final String VALIDATE_ANNOTATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.VALIDATE_ANNOTATIONS_FEATURE;
    
    /** Feature identifier: honour all schemaLocations */
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = 
        Constants.XERCES_FEATURE_PREFIX + Constants.HONOUR_ALL_SCHEMALOCATIONS_FEATURE;

    /** Feature identifier: whether to ignore xsi:type attributes until a global element declaration is encountered */
    protected static final String IGNORE_XSI_TYPE =
        Constants.XERCES_FEATURE_PREFIX + Constants.IGNORE_XSI_TYPE_FEATURE;
    
    /** Feature identifier: whether to ignore ID/IDREF errors */
    protected static final String ID_IDREF_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.ID_IDREF_CHECKING_FEATURE;
    
    /** Feature identifier: whether to ignore unparsed entity errors */
    protected static final String UNPARSED_ENTITY_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.UNPARSED_ENTITY_CHECKING_FEATURE;
    
    /** Feature identifier: whether to ignore identity constraint errors */
    protected static final String IDENTITY_CONSTRAINT_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.IDC_CHECKING_FEATURE;

    // property identifiers

    /** Property identifier: XML Schema validator. */
    protected static final String SCHEMA_VALIDATOR =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_VALIDATOR_PROPERTY;

    /** Property identifier: schema location. */
    protected static final String SCHEMA_LOCATION =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION;

    /** Property identifier: no namespace schema location. */
    protected static final String SCHEMA_NONS_LOCATION =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_NONS_LOCATION;

    /** Property identifier: root type definition. */
    protected static final String ROOT_TYPE_DEF =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ROOT_TYPE_DEFINITION_PROPERTY;

    //
    // Data
    //

    // components (non-configurable)

    /** XML Schema Validator. */
    protected XMLSchemaValidator fSchemaValidator;

    //
    // Constructors
    //

    /** Default constructor. */
    public StandardParserConfiguration() {
        this(null, null, null);
    } // <init>()

    /** 
     * Constructs a parser configuration using the specified symbol table. 
     *
     * @param symbolTable The symbol table to use.
     */
    public StandardParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    } // <init>(SymbolTable)

    /**
     * Constructs a parser configuration using the specified symbol table and
     * grammar pool.
     * <p>
     * <strong>REVISIT:</strong> 
     * Grammar pool will be updated when the new validation engine is
     * implemented.
     *
     * @param symbolTable The symbol table to use.
     * @param grammarPool The grammar pool to use.
     */
    public StandardParserConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } // <init>(SymbolTable,XMLGrammarPool)

    /**
     * Constructs a parser configuration using the specified symbol table,
     * grammar pool, and parent settings.
     * <p>
     * <strong>REVISIT:</strong> 
     * Grammar pool will be updated when the new validation engine is
     * implemented.
     *
     * @param symbolTable    The symbol table to use.
     * @param grammarPool    The grammar pool to use.
     * @param parentSettings The parent settings.
     */
    public StandardParserConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool,
                                       XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);

        // add default recognized features
        final String[] recognizedFeatures = {
            NORMALIZE_DATA,
            SCHEMA_ELEMENT_DEFAULT,
            SCHEMA_AUGMENT_PSVI,
            GENERATE_SYNTHETIC_ANNOTATIONS,
            VALIDATE_ANNOTATIONS,
            HONOUR_ALL_SCHEMALOCATIONS,
            // NOTE: These shouldn't really be here but since the XML Schema
            //       validator is constructed dynamically, its recognized
            //       features might not have been set and it would cause a
            //       not-recognized exception to be thrown. -Ac
            XMLSCHEMA_VALIDATION,
            XMLSCHEMA_FULL_CHECKING,
            IGNORE_XSI_TYPE,
            ID_IDREF_CHECKING,
            IDENTITY_CONSTRAINT_CHECKING,
            UNPARSED_ENTITY_CHECKING,
        };
        addRecognizedFeatures(recognizedFeatures);

        // set state for default features
        setFeature(SCHEMA_ELEMENT_DEFAULT, true);
        setFeature(NORMALIZE_DATA, true);
        setFeature(SCHEMA_AUGMENT_PSVI, true);
        setFeature(GENERATE_SYNTHETIC_ANNOTATIONS, false);
        setFeature(VALIDATE_ANNOTATIONS, false);
        setFeature(HONOUR_ALL_SCHEMALOCATIONS, false);
        
        setFeature(IGNORE_XSI_TYPE, false);
        setFeature(ID_IDREF_CHECKING, true);
        setFeature(IDENTITY_CONSTRAINT_CHECKING, true);
        setFeature(UNPARSED_ENTITY_CHECKING, true);

        // add default recognized properties
    
        final String[] recognizedProperties = {
            // NOTE: These shouldn't really be here but since the XML Schema
            //       validator is constructed dynamically, its recognized
            //       properties might not have been set and it would cause a
            //       not-recognized exception to be thrown. -Ac
            SCHEMA_LOCATION,
            SCHEMA_NONS_LOCATION,
            ROOT_TYPE_DEF,
        };
        
        addRecognizedProperties(recognizedProperties);
    } // <init>(SymbolTable,XMLGrammarPool)

    //
    // Public methods
    //

    /** Configures the pipeline. */
    protected void configurePipeline() {
        super.configurePipeline();
        if ( getFeature(XMLSCHEMA_VALIDATION )) {
            // If schema validator was not in the pipeline insert it.
            if (fSchemaValidator == null) {
                fSchemaValidator = new XMLSchemaValidator(); 
            
                // add schema component
                fProperties.put(SCHEMA_VALIDATOR, fSchemaValidator);
                addComponent(fSchemaValidator);
                 // add schema message formatter
                if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
                    XSMessageFormatter xmft = new XSMessageFormatter();
                    fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, xmft);
                }

            }
            fLastComponent = fSchemaValidator;
            fNamespaceBinder.setDocumentHandler(fSchemaValidator);
            
            fSchemaValidator.setDocumentHandler(fDocumentHandler);
            fSchemaValidator.setDocumentSource(fNamespaceBinder);
        } 


    } // configurePipeline()

    // features and properties

    /**
     * Check a feature. If feature is know and supported, this method simply
     * returns. Otherwise, the appropriate exception is thrown.
     *
     * @param featureId The unique identifier (URI) of the feature.
     *
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    protected void checkFeature(String featureId)
        throws XMLConfigurationException {

        //
        // Xerces Features
        //

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            final int suffixLength = featureId.length() - Constants.XERCES_FEATURE_PREFIX.length();
        	
            //
            // http://apache.org/xml/features/validation/schema
            //   Lets the user turn Schema validation support on/off.
            //
            if (suffixLength == Constants.SCHEMA_VALIDATION_FEATURE.length() && 
                featureId.endsWith(Constants.SCHEMA_VALIDATION_FEATURE)) {
                return;
            }
            // activate full schema checking
            if (suffixLength == Constants.SCHEMA_FULL_CHECKING.length() &&
                featureId.endsWith(Constants.SCHEMA_FULL_CHECKING)) {
                return;
            }
            // Feature identifier: expose schema normalized value 
            //  http://apache.org/xml/features/validation/schema/normalized-value
            if (suffixLength == Constants.SCHEMA_NORMALIZED_VALUE.length() && 
                featureId.endsWith(Constants.SCHEMA_NORMALIZED_VALUE)) {
                return;
            } 
            // Feature identifier: send element default value via characters() 
            // http://apache.org/xml/features/validation/schema/element-default
            if (suffixLength == Constants.SCHEMA_ELEMENT_DEFAULT.length() && 
                featureId.endsWith(Constants.SCHEMA_ELEMENT_DEFAULT)) {
                return;
            }
        }

        //
        // Not recognized
        //

        super.checkFeature(featureId);

    } // checkFeature(String)

    /**
     * Check a property. If the property is know and supported, this method
     * simply returns. Otherwise, the appropriate exception is thrown.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     *
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    protected void checkProperty(String propertyId)
        throws XMLConfigurationException {

        //
        // Xerces Properties
        //

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();
            
            if (suffixLength == Constants.SCHEMA_LOCATION.length() && 
                propertyId.endsWith(Constants.SCHEMA_LOCATION)) {
                return;
            }
            if (suffixLength == Constants.SCHEMA_NONS_LOCATION.length() && 
                propertyId.endsWith(Constants.SCHEMA_NONS_LOCATION)) {
                return;
            }
        }

        if (propertyId.startsWith(Constants.JAXP_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.JAXP_PROPERTY_PREFIX.length();
        	
            if (suffixLength == Constants.SCHEMA_SOURCE.length() && 
                propertyId.endsWith(Constants.SCHEMA_SOURCE)) {
                return;
            }
        }

        //
        // Not recognized
        //

        super.checkProperty(propertyId);

    } // checkProperty(String)

} // class StandardParserConfiguration
