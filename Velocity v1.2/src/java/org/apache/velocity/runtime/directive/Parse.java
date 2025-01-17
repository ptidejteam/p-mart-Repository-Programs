package org.apache.velocity.runtime.directive;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Velocity", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.StringUtils;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * Pluggable directive that handles the #parse() statement in VTL. 
 *
 * Notes:
 * -----
 *  1) The parsed source material can only come from somewhere in 
 *    the TemplateRoot tree for security reasons. There is no way 
 *    around this.  If you want to include content from elsewhere on
 *    your disk, use a link from somwhere under Template Root to that 
 *    content.
 *
 *  2) There is a limited parse depth.  It is set as a property 
 *    "parse_directive.maxdepth = 10"  for example.  There is a 20 iteration
 *    safety in the event that the parameter isn't set.
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:Christoph.Reck@dlr.de">Christoph Reck</a>
 * @version $Id: Parse.java,v 1.22 2001/09/07 05:03:49 geirm Exp $
 */
public class Parse extends Directive
{
    private boolean ready = false;

    /**
     * Return name of this directive.
     */
    public String getName()
    {
        return "parse";
    }        
    
    /**
     * Return type of this directive.
     */
    public int getType()
    {
        return LINE;
    }        
    
    /**
     *  iterates through the argument list and renders every
     *  argument that is appropriate.  Any non appropriate
     *  arguments are logged, but render() continues.
     */
    public boolean render( InternalContextAdapter context, 
                           Writer writer, Node node)
        throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException
    {
        /*
         *  did we get an argument?
         */
        if ( node.jjtGetChild(0) == null)
        {
            rsvc.error( "#parse() error :  null argument" );
            return false;
        }
        
        /*
         *  does it have a value?  If you have a null reference, then no.
         */
        Object value =  node.jjtGetChild(0).value( context );

        if ( value == null)
        {
            rsvc.error( "#parse() error :  null argument" );
            return  false;
        }

        /*
         *  get the path
         */
        String arg = value.toString();
        
        /*
         *   see if we have exceeded the configured depth.
         *   If it isn't configured, put a stop at 20 just in case.
         */

        Object[] templateStack = context.getTemplateNameStack();

        if ( templateStack.length >= 
                rsvc.getInt(RuntimeConstants.PARSE_DIRECTIVE_MAXDEPTH, 20) )
        {
            StringBuffer path = new StringBuffer();

            for( int i = 0; i < templateStack.length; ++i)
            {
                path.append( " > " + templateStack[i] );
            }

            rsvc.error( "Max recursion depth reached (" + 
                templateStack.length + ")"  + " File stack:" + path );
            return false;
        }

        Resource current = context.getCurrentResource();

        /*
         *  get the resource, and assume that we use the encoding of the current template
         *  the 'current resource' can be null if we are processing a stream....
         */

        String encoding = null;

        if ( current != null)
        {
            encoding = current.getEncoding();
        }
        else
        {
            encoding = (String) rsvc.getProperty( RuntimeConstants.INPUT_ENCODING);
        }

        /*
         *  now use the Runtime resource loader to get the template
         */
       
        Template t = null;

        try 
        {
            t = rsvc.getTemplate( arg, encoding );   
        }
        catch ( ResourceNotFoundException rnfe )
        {
       		/*
       		 * the arg wasn't found.  Note it and throw
       		 */
       		 
        	rsvc.error("#parse(): cannot find template '" + arg + "', called from template " 
        		+ context.getCurrentTemplateName() + " at (" + getLine() + ", " + getColumn() + ")" );       	
        	throw rnfe;
        }
        catch ( ParseErrorException pee )
        {
        	/*
        	 * the arg was found, but didn't parse - syntax error
        	 *  note it and throw
        	 */

        	rsvc.error("#parse(): syntax error in #parse()-ed template '" + arg + "', called from template " 
        		+ context.getCurrentTemplateName() + " at (" + getLine() + ", " + getColumn() + ")" );    
        		
        	throw pee;
        } 
        catch ( Exception e)
        {	
        	rsvc.error("#parse() : arg = " + arg + ".  Exception : " + e);
            return false;
        }
    
        /*
         *  and render it
         */
        try
        {
            context.pushCurrentTemplateName(arg);
            ((SimpleNode) t.getData()).render( context, writer );
        }
        catch ( Exception e )
        {        
            /*
             *  if it's a MIE, it came from the render.... throw it...
             */

            if ( e instanceof MethodInvocationException)
            {
                throw (MethodInvocationException) e;
            }

            rsvc.error( "Exception rendering #parse( " + arg + " )  : " + e );
            return false;
        }
        finally
        {
            context.popCurrentTemplateName();
        }

        return true;
    }
}

