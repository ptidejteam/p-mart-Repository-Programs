/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999, 2000, 2001 The Apache Software Foundation.  All rights 
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

package org.apache.xerces.validators.datatype;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.reflect.*;
import org.apache.xerces.validators.datatype.*;
import org.apache.xerces.validators.schema.SchemaSymbols;
import org.apache.xerces.validators.datatype.DatatypeValidatorFactory;
import org.apache.xerces.validators.datatype.InvalidDatatypeFacetException;


/**
 * 
 * This class implements a factory of datatype validators. Internally the
 * DatatypeValidators are kept in three registries:<BR>
 * (i) DTDRegistry - stores DTD datatype validators
 * <ii> SchemaRegistry - stores Schema datatype validators
 * <iii> UserDefinedRegistry - stores Schema user defined datatypes.
 * <BR>
 * The above registries will be initialized on demand (for XML document with a DTD, only
 * DTDRegistry will be initialized).
 * <BR>
 * <B>Note: </B>Between multiple parse() calls, only _user_defined_ registry will be reset.
 * DTD registry and schema registry are initialized only once and are kept for the *life-time* of the parser .
 * <BR>
 * This implementation uses a Hahtable as a registry table but future implementation
 * should use a lighter object, maybe a Map class ( not use a derived Map class
 * because of JDK 1.1.8 no supporting Map).<BR>
 * <BR>
 * As the Parser parses an instance document it knows if validation needs
 * to be checked. If no validation is necessary we should not instantiate a
 * DatatypeValidatorFactoryImpl.<BR>
 * <BR>
 * 
 * @author Elena Litani
 * @author Jeffrey Rodriguez
 * @author Mark Swinkles - List Validation refactoring
 * @version $Id: DatatypeValidatorFactoryImpl.java,v 1.1 2006/02/02 02:30:33 vauchers Exp $
 */
public class DatatypeValidatorFactoryImpl implements DatatypeValidatorFactory {

    private static final boolean fDebug = false;
    private Hashtable fRegistry;
    private Hashtable fDTDDatatypeRegistry;
    private Hashtable fSchemaDatatypeRegistry;

    // 0 -> not expanded, 1-> dtd registry ready, 2 -> schema registry ready
    private byte fRegistryExpanded = 0;


    // fSchemaValidation allows to determine between different parse() calls
    // what registy can be accessable (e.g only DTDRegistry)
    // 0 -> dtd validation, 1->schema validation
    private byte fSchemaValidation = 0;

    public DatatypeValidatorFactoryImpl() {
        // registry for user-defined datatypes
        fRegistry = new Hashtable(30);

        // schema has total of 44 datatypes: primitive and derived
        // note: for schema validation we always instantiate DTDDatatypes as well..
        fSchemaDatatypeRegistry = new Hashtable (40);
        // dtd has total of 9 datatypes
        fDTDDatatypeRegistry = new Hashtable (10);
    }

    /**
     * Initializes fDTDRegistry with (9) DTD related datatypes . 
     */
    public void initializeDTDRegistry() {

        //Register Primitive Datatypes

        if ( fRegistryExpanded == 0 ) { //Core datatypes shared by DTD attributes and Schema
            try {
                fDTDDatatypeRegistry.put("string",            new StringDatatypeValidator() );
                fDTDDatatypeRegistry.put("ID",                new IDDatatypeValidator());
                fDTDDatatypeRegistry.put("IDREF",             new IDREFDatatypeValidator());
                fDTDDatatypeRegistry.put("ENTITY",            new ENTITYDatatypeValidator());
                fDTDDatatypeRegistry.put("NOTATION",          new NOTATIONDatatypeValidator());

                createDTDDatatypeValidator( "IDREFS", new IDREFDatatypeValidator(), null , true );

                createDTDDatatypeValidator( "ENTITIES", new ENTITYDatatypeValidator(),  null, true );

                Hashtable facets = new Hashtable(2);
                facets.put(SchemaSymbols.ELT_PATTERN , "\\c+" );
                facets.put(SchemaSymbols.ELT_WHITESPACE, SchemaSymbols.ATT_COLLAPSE);
                createDTDDatatypeValidator("NMTOKEN", new StringDatatypeValidator(), facets, false );

                createDTDDatatypeValidator("NMTOKENS",  getDatatypeValidator( "NMTOKEN" ), null, true );
                fRegistryExpanded = 1;
            }
            catch ( InvalidDatatypeFacetException ex ) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Initializes fSchemaDatatypeRegistry with schema primitive and derived datatypes.
     * See W3C Schema Datatype REC. 
     * If DTD registry is not initialized yet, this method will initialize it as well.
     */
    public void expandRegistryToFullSchemaSet() {
        fSchemaValidation = 1;
        //Register Primitive Datatypes 
        if ( fRegistryExpanded != 2 ) {
            DatatypeValidator v;
            try {
                fSchemaDatatypeRegistry.put("boolean",           new BooleanDatatypeValidator()  );
                fSchemaDatatypeRegistry.put("float",             new FloatDatatypeValidator());
                fSchemaDatatypeRegistry.put("double",            new DoubleDatatypeValidator());
                fSchemaDatatypeRegistry.put("decimal",           new DecimalDatatypeValidator());
                fSchemaDatatypeRegistry.put("hexBinary",         new HexBinaryDatatypeValidator());
                fSchemaDatatypeRegistry.put("base64Binary",      new Base64BinaryDatatypeValidator());
                fSchemaDatatypeRegistry.put("anyURI",            new AnyURIDatatypeValidator());
                fSchemaDatatypeRegistry.put("QName",             new QNameDatatypeValidator()); 
                fSchemaDatatypeRegistry.put("duration",          new DurationDatatypeValidator());
                fSchemaDatatypeRegistry.put("gDay",              new DayDatatypeValidator()); 
                fSchemaDatatypeRegistry.put("time",              new TimeDatatypeValidator());

                if ( fRegistryExpanded == 0 ) {
                    initializeDTDRegistry(); //Initialize common Schema/DTD Datatype validator set if not already initialized
                }
                Hashtable facets = new Hashtable (2);
                facets.put(SchemaSymbols.ELT_WHITESPACE, SchemaSymbols.ATT_REPLACE);
                createSchemaDatatypeValidator("normalizedString", new StringDatatypeValidator(), facets, false);


                facets.clear();
                facets.put(SchemaSymbols.ELT_WHITESPACE, SchemaSymbols.ATT_COLLAPSE);
                createSchemaDatatypeValidator("token", getDatatypeValidator("normalizedString"), facets, false);

                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN , "([a-zA-Z]{2}|[iI]-[a-zA-Z]+|[xX]-[a-zA-Z]+)(-[a-zA-Z]+)*" );
                createSchemaDatatypeValidator("language", getDatatypeValidator("token") , facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN , "\\i\\c*" );
                createSchemaDatatypeValidator("Name", getDatatypeValidator("token"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN , "[\\i-[:]][\\c-[:]]*"  );
                createSchemaDatatypeValidator("NCName", getDatatypeValidator("token"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_FRACTIONDIGITS, "0");
                createSchemaDatatypeValidator("integer", getDatatypeValidator("decimal"), facets, false);


                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE , "0" );
                createSchemaDatatypeValidator("nonPositiveInteger", 
                                              getDatatypeValidator("integer"), facets, false );


                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE , "-1" );
                createSchemaDatatypeValidator("negativeInteger", 
                                              getDatatypeValidator( "nonPositiveInteger"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE , "9223372036854775807");
                facets.put(SchemaSymbols.ELT_MININCLUSIVE,  "-9223372036854775808");
                createSchemaDatatypeValidator("long", getDatatypeValidator( "integer"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE , "2147483647");
                facets.put(SchemaSymbols.ELT_MININCLUSIVE,  "-2147483648");
                createSchemaDatatypeValidator("int", getDatatypeValidator( "long"), facets,false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE , "32767");
                facets.put(SchemaSymbols.ELT_MININCLUSIVE,  "-32768");
                createSchemaDatatypeValidator("short", getDatatypeValidator( "int"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE , "127");
                facets.put(SchemaSymbols.ELT_MININCLUSIVE,  "-128");
                createSchemaDatatypeValidator("byte",
                                              getDatatypeValidator( "short"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_MININCLUSIVE, "0" );
                createSchemaDatatypeValidator("nonNegativeInteger", 
                                              getDatatypeValidator( "integer"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE, "18446744073709551615" );
                createSchemaDatatypeValidator("unsignedLong",
                                              getDatatypeValidator( "nonNegativeInteger"), facets, false );


                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE, "4294967295" );
                createSchemaDatatypeValidator("unsignedInt",
                                              getDatatypeValidator( "unsignedLong"), facets, false );


                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE, "65535" );
                createSchemaDatatypeValidator("unsignedShort", 
                                              getDatatypeValidator( "unsignedInt"), facets, false );


                facets.clear();
                facets.put(SchemaSymbols.ELT_MAXINCLUSIVE, "255" );
                createSchemaDatatypeValidator("unsignedByte",
                                              getDatatypeValidator( "unsignedShort"), facets, false );

                facets.clear();
                facets.put(SchemaSymbols.ELT_MININCLUSIVE, "1" );
                createSchemaDatatypeValidator("positiveInteger",
                                              getDatatypeValidator( "nonNegativeInteger"), facets, false );


                //REVISIT: it looks like patterns are expensive
                //         should we rely on error reporting for date/times and not use pattern here?
                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN,"(-)?(\\d*)-(\\d\\d)-(\\d\\d)T(\\d\\d):(\\d\\d):(\\d\\d)(\\.(\\d)*)?(Z|(([-+])(\\d\\d)(:(\\d\\d))?))?");
                createSchemaDatatypeValidator("dateTime", new DateTimeDatatypeValidator(), facets, false);

                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN,"(-)?(\\d*)-(\\d\\d)-(\\d\\d)(Z|(([-+])(\\d\\d)(:(\\d\\d))?))?");
                createSchemaDatatypeValidator("date", new DateDatatypeValidator(), facets, false);

                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN,"--(\\d\\d)-(\\d\\d)(Z|(([-+])(\\d\\d)(:(\\d\\d))?))?");
                createSchemaDatatypeValidator("gMonthDay", new MonthDayDatatypeValidator(), facets, false);

                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN,"(-)?(\\d*)-(\\d\\d)(Z|(([-+])(\\d\\d)(:(\\d\\d))?))?");                
                createSchemaDatatypeValidator("gYearMonth", new YearMonthDatatypeValidator(), facets, false);

                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN,"(-)?(\\d*)(Z|(([-+])(\\d\\d)(:(\\d\\d))?))?");                
                createSchemaDatatypeValidator("gYear", new YearDatatypeValidator(), facets, false);


                facets.clear();
                facets.put(SchemaSymbols.ELT_PATTERN,"--(\\d\\d)--(Z)?");
                createSchemaDatatypeValidator("gMonth", new MonthDatatypeValidator(), facets, false);


                fRegistryExpanded = 2;
            }
            catch ( InvalidDatatypeFacetException ex ) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * An optimization option that we should write in the future is to separate the static list
     * of Datatype Validators from the dynamic part where anonymous, and user derived datatype are
     * kept, then when we resetRegistry only the dynamic part of the registry should be cleared.
     * So we don't end up clearing the static part of the table over and over every time that we
     * do a parse cycle.
     */
    public void resetRegistry() {
        fRegistry.clear();
        fSchemaValidation = 0;
        //reset some Primitive datatypes - static fields
        ((IDDatatypeValidator)fDTDDatatypeRegistry.get("ID")).resetIDs();
        ((IDREFDatatypeValidator)fDTDDatatypeRegistry.get("IDREF")).resetIDRefs();
    }

    public DatatypeValidator createDatatypeValidator(String typeName, 
                                                     DatatypeValidator base, Hashtable facets, boolean list ) throws InvalidDatatypeFacetException {
        if ( base == null ) {
            return null;
        }
        DatatypeValidator simpleType = createSchemaValidator(typeName, base, facets, list);
        registerUserDefinedValidator(typeName, simpleType);
        return simpleType;
    }


    public DatatypeValidator createDatatypeValidator(String typeName, Vector validators) {
        DatatypeValidator simpleType = null;
        if ( validators!=null ) {
            simpleType = new UnionDatatypeValidator(validators);
        }
        if ( simpleType !=null ) {
            registerUserDefinedValidator(typeName, simpleType);
        }
        return simpleType;
    }


    /**
     * Searches different datatype registries depending on validation mode (schema or dtd)
     * 
     * @param type
     * @return 
     */
    public DatatypeValidator getDatatypeValidator(String type) {
        AbstractDatatypeValidator simpleType = null;
        if ( type == null ) {
            return null;
        }
        simpleType = (AbstractDatatypeValidator) fDTDDatatypeRegistry.get(type);
        if ( simpleType == null && fSchemaValidation == 1 ) {
            simpleType = (AbstractDatatypeValidator) fSchemaDatatypeRegistry.get(type);
            if ( simpleType == null ) {
                return(DatatypeValidator) fRegistry.get(type);
            }

        }

        return(DatatypeValidator)simpleType;

    }


    private DatatypeValidator createSchemaDatatypeValidator(String typeName, 
                                                            DatatypeValidator base, Hashtable facets, boolean list ) throws InvalidDatatypeFacetException {
        DatatypeValidator primitive = createSchemaValidator(typeName, base, facets, list);
        registerSchemaValidator(typeName, primitive);
        return primitive;
    }

    private DatatypeValidator createDTDDatatypeValidator(String typeName, 
                                                         DatatypeValidator base, Hashtable facets, boolean list ) throws InvalidDatatypeFacetException {
        DatatypeValidator primitive = createSchemaValidator(typeName, base, facets, list);
        registerDTDValidator(typeName, primitive);
        return primitive;
    }

    private DatatypeValidator createSchemaValidator (String typeName, 
                                                     DatatypeValidator base, Hashtable facets, boolean list ) throws InvalidDatatypeFacetException{

        DatatypeValidator simpleType = null;
        if ( list ) {
            simpleType = new ListDatatypeValidator(base, facets, list);    
        }
        else {
            try {
                String value = (String)facets.get(SchemaSymbols.ELT_WHITESPACE);
                //for all datatypes other than string, we don't pass WHITESPACE Facet
                //its value is always 'collapse' and cannot be reset by user

                if ( value != null && !(base instanceof StringDatatypeValidator) ) {
                    if ( !value.equals(SchemaSymbols.ATT_COLLAPSE) )
                        throw new InvalidDatatypeFacetException( "whiteSpace value '" + value +
                                                                 "' for this type must be 'collapse'.");
                    facets.remove(SchemaSymbols.ELT_WHITESPACE);
                }

                Class validatorDef = base.getClass();

                Class [] validatorArgsClass = new Class[] {  
                    org.apache.xerces.validators.datatype.DatatypeValidator.class,
                    java.util.Hashtable.class,
                    boolean.class};

                Object [] validatorArgs     = new Object[] { base, facets, Boolean.FALSE};
                Constructor validatorConstructor = validatorDef.getConstructor( validatorArgsClass );
                simpleType = ( DatatypeValidator ) createDatatypeValidator ( validatorConstructor, validatorArgs );
            }
            catch ( NoSuchMethodException e ) {
                e.printStackTrace();
            }

        }
        return simpleType;
    }

    private void registerUserDefinedValidator (String typeName, DatatypeValidator simpleType) {
        if ( simpleType != null ) {
            fRegistry.put(typeName, simpleType);
        }
    }
    private void registerSchemaValidator (String typeName, DatatypeValidator simpleType) {
        if ( simpleType != null ) {
            fSchemaDatatypeRegistry.put(typeName, simpleType);
        }
    }
    private void registerDTDValidator (String typeName, DatatypeValidator simpleType) {
        if ( simpleType != null ) {
            fDTDDatatypeRegistry.put(typeName, simpleType);
        }
    }



    private static Object createDatatypeValidator(Constructor validatorConstructor, 
                                                  Object[] arguments)  throws  InvalidDatatypeFacetException {
        Object validator = null;
        try {
            validator = validatorConstructor.newInstance(arguments);
            return validator;
        }
        catch ( InstantiationException e ) {
            if ( fDebug ) {
                e.printStackTrace();
            }
            else {
                return null;
            }
        }
        catch ( IllegalAccessException e ) {
            if ( fDebug ) {
                e.printStackTrace();
            }
            else {
                return null;
            }
        }
        catch ( IllegalArgumentException e ) {
            if ( fDebug ) {
                e.printStackTrace();
            }
            else {
                return null;
            }
        }
        catch ( InvocationTargetException e ) {
            if ( fDebug ) {
                System.out.println("!! The original error message is: " + e.getTargetException().getMessage() );
                e.getTargetException().printStackTrace();
            }
            else {
                throw new InvalidDatatypeFacetException( e.getTargetException().getMessage() );
            }
        }
        return validator;
    }

}

