/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  
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

package org.apache.xerces.impl;

import java.util.Hashtable;
import java.util.Locale;

import org.apache.xerces.util.DefaultErrorHandler;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;

/**
 * This class is a common element of all parser configurations and is
 * used to report errors that occur. This component can be queried by
 * parser components from the component manager using the following
 * property ID:
 * <pre>
 *   http://apache.org/xml/properties/internal/error-reporter
 * </pre>
 * <p>
 * Errors are separated into domains that categorize a class of errors.
 * In a parser configuration, the parser would register a
 * <code>MessageFormatter</code> for each domain that is capable of
 * localizing error messages and formatting them based on information 
 * about the error. Any parser component can invent new error domains
 * and register additional message formatters to localize messages in
 * those domains.
 * <p>
 * This component requires the following features and properties from the
 * component manager that uses it:
 * <ul>
 *  <li>http://apache.org/xml/properties/internal/error-handler</li>
 * </ul>
 * <p>
 * This component can use the following features and properties but they
 * are not required:
 * <ul>
 *  <li>http://apache.org/xml/features/continue-after-fatal-error</li>
 * </ul>
 *
 * @see MessageFormatter
 *
 * @author Eric Ye, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLErrorReporter.java,v 1.7 2002/05/27 23:07:15 neilg Exp $
 */
public class XMLErrorReporter
    implements XMLComponent {

    //
    // Constants
    //

    // severity

    /** 
     * Severity: warning. Warnings represent informational messages only
     * that should not be considered serious enough to stop parsing or 
     * indicate an error in the document's validity.
     */
    public static final short SEVERITY_WARNING = 0;

    /**
     * Severity: error. Common causes of errors are document structure and/or
     * content that that does not conform to the grammar rules specified for
     * the document. These are typically validation errors.
     */
    public static final short SEVERITY_ERROR = 1;

    /** 
     * Severity: fatal error. Fatal errors are errors in the syntax of the
     * XML document or invalid byte sequences for a given encoding. The
     * XML 1.0 Specification mandates that errors of this type are not
     * recoverable.
     * <p>
     * <strong>Note:</strong> The parser does have a "continue after fatal
     * error" feature but it should be used with extreme caution and care.
     */
    public static final short SEVERITY_FATAL_ERROR = 2;
    
    // feature identifiers

    /** Feature identifier: continue after fatal error. */
    protected static final String CONTINUE_AFTER_FATAL_ERROR =
        Constants.XERCES_FEATURE_PREFIX + Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE;

    // property identifiers

    /** Property identifier: error handler. */
    protected static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;

    // recognized features and properties

    /** Recognized features. */
    protected static final String[] RECOGNIZED_FEATURES = {
        CONTINUE_AFTER_FATAL_ERROR,
    };

    /** Recognized properties. */
    protected static final String[] RECOGNIZED_PROPERTIES = {
        ERROR_HANDLER,
    };

    //
    // Data
    //

    /** The locale to be used to format error messages. */
    protected Locale fLocale;

    /** Mapping of Message formatters for domains. */
    protected Hashtable fMessageFormatters;

    /** Error handler. */
    protected XMLErrorHandler fErrorHandler;

    /** Document locator. */
    protected XMLLocator fLocator;

    /** Continue after fatal error feature. */
    protected boolean fContinueAfterFatalError;

    /** 
     * Default error handler. This error handler is only used in the
     * absence of a registered error handler so that errors are not
     * "swallowed" silently. This is one of the most common "problems"
     * reported by users of the parser.
     */
    protected XMLErrorHandler fDefaultErrorHandler;

    //
    // Constructors
    //

    /** Constructs an error reporter with a locator. */
    public XMLErrorReporter() {

        // REVISIT: [Q] Should the locator be passed to the reportError
        //              method? Otherwise, there is no way for a parser
        //              component to store information about where an
        //              error occurred so as to report it later. 
        //
        //              An example would be to record the location of
        //              IDREFs so that, at the end of the document, if
        //              there is no associated ID declared, the error
        //              could report the location information of the
        //              reference. -Ac
        //
        // NOTE: I added another reportError method that allows the
        //       caller to specify the location of the error being
        //       reported. -Ac

        fMessageFormatters = new Hashtable();

    } // <init>()

    //
    // Methods
    //

    /**
     * Sets the current locale.
     * 
     * @param locale The new locale.
     */
    public void setLocale(Locale locale) {
        fLocale = locale;
    } // setLocale(Locale)

    /**
     * Sets the document locator.
     *
     * @param locator The locator.
     */
    public void setDocumentLocator(XMLLocator locator) {
        fLocator = locator;
    } // setDocumentLocator(XMLLocator)

    /**
     * Registers a message formatter for the specified domain.
     * <p>
     * <strong>Note:</strong> Registering a message formatter for a domain
     * when there is already a formatter registered will cause the previous
     * formatter to be lost. This method replaces any previously registered
     * message formatter for the specified domain.
     * 
     * @param domain 
     * @param messageFormatter 
     */
    public void putMessageFormatter(String domain, 
                                    MessageFormatter messageFormatter) {
        fMessageFormatters.put(domain, messageFormatter);
    } // putMessageFormatter(String,MessageFormatter)

    /**
     * Returns the message formatter associated with the specified domain,
     * or null if no message formatter is registered for that domain.
     * 
     * @param domain The domain of the message formatter.
     */
    public MessageFormatter getMessageFormatter(String domain) {
        return (MessageFormatter)fMessageFormatters.get(domain);
    } // getMessageFormatter(String):MessageFormatter

    /**
     * Removes the message formatter for the specified domain and
     * returns the removed message formatter.
     * 
     * @param domain The domain of the message formatter.
     */
    public MessageFormatter removeMessageFormatter(String domain) {
        return (MessageFormatter) fMessageFormatters.remove(domain);
    } // removeMessageFormatter(String):MessageFormatter

    /**
     * Reports an error. The error message passed to the error handler
     * is formatted for the locale by the message formatter installed
     * for the specified error domain.
     * 
     * @param domain    The error domain.
     * @param key       The key of the error message.
     * @param arguments The replacement arguments for the error message,
     *                  if needed.
     * @param severity  The severity of the error.
     *
     * @see #SEVERITY_WARNING
     * @see #SEVERITY_ERROR
     * @see #SEVERITY_FATAL_ERROR
     */
    public void reportError(String domain, String key, Object[] arguments, 
                            short severity) throws XNIException {
        reportError(fLocator, domain, key, arguments, severity);
    } // reportError(String,String,Object[],short)

    /**
     * Reports an error at a specific location.
     * 
     * @param location  The error location.
     * @param domain    The error domain.
     * @param key       The key of the error message.
     * @param arguments The replacement arguments for the error message,
     *                  if needed.
     * @param severity  The severity of the error.
     *
     * @see #SEVERITY_WARNING
     * @see #SEVERITY_ERROR
     * @see #SEVERITY_FATAL_ERROR
     */
    public void reportError(XMLLocator location,
                            String domain, String key, Object[] arguments, 
                            short severity) throws XNIException {

        // REVISIT: [Q] Should we do anything about invalid severity
        //              parameter? -Ac
        
        // format error message and create parse exception
        MessageFormatter messageFormatter = getMessageFormatter(domain);
        String message;
        if (messageFormatter != null) {
            message = messageFormatter.formatMessage(fLocale, key, arguments);
        }
        else {
            StringBuffer str = new StringBuffer();
            str.append(domain);
            str.append('#');
            str.append(key);
            int argCount = arguments != null ? arguments.length : 0;
            if (argCount > 0) {
                str.append('?');
                for (int i = 0; i < argCount; i++) {
                    str.append(arguments[i]);
                    if (i < argCount -1) {
                        str.append('&');
                    }
                }
            }
            message = str.toString();
        }
        XMLParseException parseException = 
            new XMLParseException(location, message);

        // get error handler
        XMLErrorHandler errorHandler = fErrorHandler;
        if (errorHandler == null) {
            if (fDefaultErrorHandler == null) {
                fDefaultErrorHandler = new DefaultErrorHandler();
            }
            errorHandler = fDefaultErrorHandler;
        }

        // call error handler
        switch (severity) {
            case SEVERITY_WARNING: {
                errorHandler.warning(domain, key, parseException);
                break;
            }
            case SEVERITY_ERROR: {
                errorHandler.error(domain, key, parseException);
                break;
            }
            case SEVERITY_FATAL_ERROR: {
                errorHandler.fatalError(domain, key, parseException);
                if (!fContinueAfterFatalError) {
                    throw parseException;
                }
                break;
            }
        }

    } // reportError(XMLLocator,String,String,Object[],short)

    //
    // XMLComponent methods
    //

    /**
     * Resets the component. The component can query the component manager
     * about any features and properties that affect the operation of the
     * component.
     * 
     * @param componentManager The component manager.
     *
     * @throws SAXException Thrown by component on initialization error.
     *                      For example, if a feature or property is
     *                      required for the operation of the component, the
     *                      component manager may throw a 
     *                      SAXNotRecognizedException or a
     *                      SAXNotSupportedException.
     */
    public void reset(XMLComponentManager componentManager)
        throws XNIException {

        // features
        try {
            fContinueAfterFatalError = componentManager.getFeature(CONTINUE_AFTER_FATAL_ERROR);
        }
        catch (XNIException e) {
            fContinueAfterFatalError = false;
        }

        // properties
        fErrorHandler = (XMLErrorHandler)componentManager.getProperty(ERROR_HANDLER);

    } // reset(XMLComponentManager)

    /**
     * Returns a list of feature identifiers that are recognized by
     * this component. This method may return null if no features
     * are recognized by this component.
     */
    public String[] getRecognizedFeatures() {
        return RECOGNIZED_FEATURES;
    } // getRecognizedFeatures():String[]

    /**
     * Sets the state of a feature. This method is called by the component
     * manager any time after reset when a feature changes state. 
     * <p>
     * <strong>Note:</strong> Components should silently ignore features
     * that do not affect the operation of the component.
     * 
     * @param featureId The feature identifier.
     * @param state     The state of the feature.
     *
     * @throws SAXNotRecognizedException The component should not throw
     *                                   this exception.
     * @throws SAXNotSupportedException The component should not throw
     *                                  this exception.
     */
    public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException {

        //
        // Xerces features
        //

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            String feature = featureId.substring(Constants.XERCES_FEATURE_PREFIX.length());
            //
            // http://apache.org/xml/features/continue-after-fatal-error
            //   Allows the parser to continue after a fatal error.
            //   Normally, a fatal error would stop the parse.
            //
            if (feature.equals(Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE)) {
                fContinueAfterFatalError = state;
            }
        }

    } // setFeature(String,boolean)

    // return state of given feature or false if unsupported.
    public boolean getFeature(String featureId)
        throws XMLConfigurationException {

        //
        // Xerces features
        //

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            String feature = featureId.substring(Constants.XERCES_FEATURE_PREFIX.length());
            //
            // http://apache.org/xml/features/continue-after-fatal-error
            //   Allows the parser to continue after a fatal error.
            //   Normally, a fatal error would stop the parse.
            //
            if (feature.equals(Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE)) {
                return fContinueAfterFatalError ;
            }
        }
        return false;

    } // setFeature(String,boolean)

    /**
     * Returns a list of property identifiers that are recognized by
     * this component. This method may return null if no properties
     * are recognized by this component.
     */
    public String[] getRecognizedProperties() {
        return RECOGNIZED_PROPERTIES;
    } // getRecognizedProperties():String[]

    /**
     * Sets the value of a property. This method is called by the component
     * manager any time after reset when a property changes value. 
     * <p>
     * <strong>Note:</strong> Components should silently ignore properties
     * that do not affect the operation of the component.
     * 
     * @param propertyId The property identifier.
     * @param value      The value of the property.
     *
     * @throws SAXNotRecognizedException The component should not throw
     *                                   this exception.
     * @throws SAXNotSupportedException The component should not throw
     *                                  this exception.
     */
    public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        //
        // Xerces properties
        //

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            String property = propertyId.substring(Constants.XERCES_PROPERTY_PREFIX.length());

            if (property.equals(Constants.ERROR_HANDLER_PROPERTY)) {
                fErrorHandler = (XMLErrorHandler)value;
            }
        }

    } // setProperty(String,Object)
    
    /**
     * Get the internal XMLErrrorHandler.
     */
    public XMLErrorHandler getErrorHandler() {
        return fErrorHandler;
    }
    
} // class XMLErrorReporter
