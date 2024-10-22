/*****************************************************************************************************
 * $Id: ApplyXSLProperties.java,v 1.1 2006/02/27 22:48:21 vauchers Exp $
 * 
 * Copyright (c) 1998-1999 Lotus Corporation, Inc. All Rights Reserved.
 *				This software is provided without a warranty of any kind.
 * 
 * $State: Exp $
 *****************************************************************************************************/

import java.net.MalformedURLException;
import javax.servlet.*;
import javax.servlet.http.*;

/*****************************************************************************************************
 * 
 * ApplyXSLProperties contains operational parameters for ApplyXSL based 
 * on program defaults and configuration.  
 * <p>This class is also used to return values for request-time parameters.</p>
 *
 * @author Spencer Shepard (sshepard@us.ibm.com)
 * @author R. Adam King (rak@us.ibm.com)
 * @author Tom Rowe (trowe@us.ibm.com)
 *
 *****************************************************************************************************/

public class ApplyXSLProperties {

    /**
      * Program default for parameter "URL"
      */
    private final String DEFAULT_URL;

    /**
      * Program default for parameter "xslURL"
      */
    private final String DEFAULT_xslURL;
    
    /**
      * Program default for parameter "debug"
      */
    private final boolean DEFAULT_debug;

    /**
      * Program default for parameter "noConflictWarnings"
      */
    private final boolean DEFAULT_noCW;
    
    /**
      * Constructor to use program defaults.
      */
    public ApplyXSLProperties() 
    {
	DEFAULT_URL = null;
	DEFAULT_xslURL = null;
	DEFAULT_debug = false;
	DEFAULT_noCW = false;
    }

    /**
      * Constructor to use to override program defaults.
      * @param config Servlet configuration
      */
    public ApplyXSLProperties(ServletConfig config)
    {
	String xm = config.getInitParameter("URL"),
	       xu = config.getInitParameter("xslURL"),
	       db = config.getInitParameter("debug"),
	       cw = config.getInitParameter("noConflictWarnings");
	       
	if (xm != null) DEFAULT_URL = xm;
	else DEFAULT_URL = null;
	if (xu != null) DEFAULT_xslURL = xu;
	else DEFAULT_xslURL = null;
	if (db != null) DEFAULT_debug = new Boolean(db).booleanValue();
	else DEFAULT_debug = false;
	if (cw != null) DEFAULT_noCW = new Boolean(cw).booleanValue();
	else DEFAULT_noCW = false;
    }
   
    /**
      * Given a parameter name, returns the HTTP request's String value; 
      * if not present in request, returns default String value.
      * @param request Request to check for default override
      * @param param Name of the parameter
      * @return String value of named parameter
      */
    public String getRequestParmString(HttpServletRequest request, String param)
    {
	if (request != null) { 
	    String[] paramVals = request.getParameterValues(param); 
	    if (paramVals != null) 
		return paramVals[0];
	}
	return null;
    }

    /**
      * Returns the current setting for "URL".
      * @param request Request to check for parameter value
      * @return String value for "URL"
      * @exception MalformedURLException Will not be thrown
      */
    public String getXMLurl(HttpServletRequest request)
    throws MalformedURLException
    {
	String temp = getRequestParmString(request, "URL");
	if (temp != null)
	    return temp;
	return DEFAULT_URL;
    }     
    
    /**
      * Returns the current setting for "xslURL".
      * @param request Request to check for parameter value
      * @return String value for "xslURL"
      * @exception MalformedURLException Will not be thrown
      */
    public String getXSLurl(HttpServletRequest request)
    throws MalformedURLException
    {  
	String temp = getRequestParmString(request, "xslURL");
	if (temp != null)
	    return temp;
	return DEFAULT_xslURL;
    }
    
    /**
      * Returns the current setting for "debug".
      * @param request Request to check for parameter value
      * @return Boolean value for "debug"
      */
    public boolean isDebug(HttpServletRequest request)
    {
	String temp = getRequestParmString(request, "debug");
	if (temp != null)
	    return new Boolean(temp).booleanValue();
	return DEFAULT_debug;
    }

    /**
      * Returns the current setting for "noConflictWarnings".
      * @param request Request to check for parameter value
      * @return Boolean value for "noConflictWarnings"
      */
    boolean isNoCW(HttpServletRequest request)
    {
	String temp = getRequestParmString(request, "noConflictWarnings");
	if (temp != null)
	    return new Boolean(temp).booleanValue();
	return DEFAULT_noCW;
    }    
}