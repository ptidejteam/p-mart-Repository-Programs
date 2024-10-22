/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/validator/Resources.java,v 1.28 2004/03/14 06:23:47 sraeburn Exp $
 * $Revision: 1.28 $
 * $Date: 2004/03/14 06:23:47 $
 *
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.struts.validator;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.util.RequestUtils;

/**
 * This class helps provides some useful methods for retrieving objects
 * from different scopes of the application.
 *
 * @version $Revision: 1.28 $ $Date: 2004/03/14 06:23:47 $
 * @since Struts 1.1
 */
public class Resources {

    /**
     * Resources key the <code>ServletContext</code> is stored under.
     */
    private static String SERVLET_CONTEXT_PARAM = "javax.servlet.ServletContext";

    /**
     * Resources key the <code>ServletContext</code> is stored under.
     * @deprecated This will be removed after Struts 1.2
     */
    public static String SERVLET_CONTEXT_KEY = SERVLET_CONTEXT_PARAM;

    /**
     * Resources key the <code>HttpServletRequest</code> is stored under.
     */
    private static String HTTP_SERVLET_REQUEST_PARAM =
        "javax.servlet.http.HttpServletRequest";

    /**
     * Resources key the <code>HttpServletRequest</code> is stored under.
     * @deprecated This will be removed after Struts 1.2
     */
    public static String HTTP_SERVLET_REQUEST_KEY = HTTP_SERVLET_REQUEST_PARAM;

    /**
     * Resources key the <code>ActionMessages</code> is stored under.
     */
    private static String ACTION_MESSAGES_PARAM =
        "org.apache.struts.action.ActionMessages";

    /**
     * Resources key the <code>ActionErrors</code> is stored under.
     * @deprecated This will be removed after Struts 1.2
     */
    public static String ACTION_ERRORS_KEY = ACTION_MESSAGES_PARAM;

    /**
     * Retrieve <code>ValidatorResources</code> for the current module.
     * @param application Application Context
     * @param request The ServletRequest
     */
    public static ValidatorResources getValidatorResources(
        ServletContext application,
        HttpServletRequest request) {

        String prefix =
            ModuleUtils
                .getInstance()
                .getModuleConfig(request, application)
                .getPrefix();

        return (ValidatorResources) application.getAttribute(
            ValidatorPlugIn.VALIDATOR_KEY + prefix);
    }

    /**
     * Retrieve <code>MessageResources</code> for the module.
     * @param request the servlet request
     */
    public static MessageResources getMessageResources(HttpServletRequest request) {
        return (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
    }

    /**
     * Get the <code>Locale</code> of the current user.
     * @param request servlet request
     * @deprecated Use RequestUtils.getUserLocale() instead.  This will be removed
     * after Struts 1.2.
     */
    public static Locale getLocale(HttpServletRequest request) {
        return RequestUtils.getUserLocale(request, null);
    }

    /**
     * Gets the <code>Locale</code> sensitive value based on the key passed in.
     * @param messages The Message resources
     * @param locale The locale.
     * @param key Key used to lookup the message
     */
    public static String getMessage(
        MessageResources messages,
        Locale locale,
        String key) {
        String message = null;

        if (messages != null) {
            message = messages.getMessage(locale, key);
        }

        return (message == null) ? "" : message;
    }

    /**
     * Gets the <code>Locale</code> sensitive value based on the key passed in.
     * @param request servlet request
     * @param key the request key
     */
    public static String getMessage(HttpServletRequest request, String key) {
        MessageResources messages = getMessageResources(request);

        return getMessage(messages, RequestUtils.getUserLocale(request, null), key);
    }

    /**
     * Gets the locale sensitive message based on the 
     * <code>ValidatorAction</code> message and the <code>Field</code>'s 
     * arg objects.
     * @param messages  The Message resources
     * @param locale The locale
     * @param va The Validator Action
     * @param field The Validator Field
     */
    public static String getMessage(
        MessageResources messages,
        Locale locale,
        ValidatorAction va,
        Field field) {

        String args[] = getArgs(va.getName(), messages, locale, field);
        String msg =
            field.getMsg(va.getName()) != null
                ? field.getMsg(va.getName())
                : va.getMsg();

        return messages.getMessage(locale, msg, args);
    }

    /**
     * Gets the <code>ActionError</code> based on the 
     * <code>ValidatorAction</code> message and the <code>Field</code>'s 
     * arg objects.
     * @param request the servlet request
     * @param va Validator action
     * @param field the validator Field
     * @deprecated Use getActionMessage() instead.  This will be removed after
     * Struts 1.2.
     */
    public static ActionError getActionError(
        HttpServletRequest request,
        ValidatorAction va,
        Field field) {

        String args[] =
            getArgs(
                va.getName(),
                getMessageResources(request),
                RequestUtils.getUserLocale(request, null),
                field);

        String msg =
            field.getMsg(va.getName()) != null
                ? field.getMsg(va.getName())
                : va.getMsg();

        return new ActionError(msg, args);
    }
    
    /**
     * Gets the <code>ActionMessage</code> based on the 
     * <code>ValidatorAction</code> message and the <code>Field</code>'s 
     * arg objects.
     * @param request the servlet request
     * @param va Validator action
     * @param field the validator Field
     */
    public static ActionMessage getActionMessage(
        HttpServletRequest request,
        ValidatorAction va,
        Field field) {

        String args[] =
            getArgs(
                va.getName(),
                getMessageResources(request),
                RequestUtils.getUserLocale(request, null),
                field);

        String msg =
            field.getMsg(va.getName()) != null
                ? field.getMsg(va.getName())
                : va.getMsg();

        return new ActionMessage(msg, args);
    }

    /**
     * Gets the message arguments based on the current 
     * <code>ValidatorAction</code> and <code>Field</code>.
     * @param actionName action name
     * @param messages message resources
     * @param locale the locale
     * @param field the validator field
     */
    public static String[] getArgs(
        String actionName,
        MessageResources messages,
        Locale locale,
        Field field) {

        String[] argMessages = new String[4];

        Arg[] args =
            new Arg[] {
                field.getArg(actionName,0),
                field.getArg(actionName,1),
                field.getArg(actionName,2),
                field.getArg(actionName,3)};

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }

            if (args[i].isResource()) {
                argMessages[i] = getMessage(messages, locale, args[i].getKey());
            } else {
                argMessages[i] = args[i].getKey();
            }

        }

        return argMessages;
    }

    /**
     * Initialize the <code>Validator</code> to perform validation.
     *
     * @param key The key that the validation rules are under (the form elements 
     * name attribute).
     * @param bean The bean validation is being performed on.
     * @param application servlet context
     * @param request The current request object.
     * @param errors The object any errors will be stored in.
     * @param page This in conjunction with  the page property of a 
     * <code>Field<code> can control the processing of fields.  If the field's 
     * page is less than or equal to this page value, it will be processed.
     */
    public static Validator initValidator(
        String key,
        Object bean,
        ServletContext application,
        HttpServletRequest request,
        ActionMessages errors,
        int page) {

        ValidatorResources resources =
            Resources.getValidatorResources(application, request);

        Locale locale = RequestUtils.getUserLocale(request, null);

        Validator validator = new Validator(resources, key);
        validator.setUseContextClassLoader(true);

        validator.setPage(page);

        validator.setParameter(SERVLET_CONTEXT_PARAM, application);
        validator.setParameter(HTTP_SERVLET_REQUEST_PARAM, request);
        validator.setParameter(Validator.LOCALE_PARAM, locale);
        validator.setParameter(ACTION_MESSAGES_PARAM, errors);
        validator.setParameter(Validator.BEAN_PARAM, bean);

        return validator;
    }

}
