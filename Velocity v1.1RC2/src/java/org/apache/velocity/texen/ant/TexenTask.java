package org.apache.velocity.texen.ant;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.texen.Generator;
import org.apache.velocity.util.StringUtils;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.apache.commons.collections.ExtendedProperties;

/**
 * An ant task for generating output by using Velocity
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="robertdonkin@mac.com">Robert Burrell Donkin</a>
 * @version $Id: TexenTask.java,v 1.25 2001/05/11 03:55:32 geirm Exp $
 */
public class TexenTask extends Task
{
    /**
     * This field contains a message.
     * This message (telling users to consult the log) is appended to rethrown exception messages.
     */
    private final static String MSG_CONSULT_LOG= ". For more information consult the velocity log.";
    
    /**
     * This is the control template that governs the output.
     * It may or may not invoke the services of worker
     * templates.
     */
    protected String controlTemplate;
    
    /**
     * This is where Velocity will look for templates
     * using the file template loader.
     */
    protected String templatePath;
    
    /**
     * This is where texen will place all the output
     * that is a product of the generation process.
     */
    protected String outputDirectory;
    
    /**
     * This is the file where the generated text
     * will be placed.
     */
    protected String outputFile;
    
    /**
     * <p>
     * These are properties that are fed into the
     * initial context from a properties file. This
     * is simply a convenient way to set some values
     * that you wish to make available in the context.
     * </p>
     * <p>
     * These values are not critical, like the template path
     * or output path, but allow a convenient way to
     * set a value that may be specific to a particular
     * generation task.
     * </p>
     * <p>
     * For example, if you are generating scripts to allow
     * user to automatically create a database, then
     * you might want the <code>$databaseName</code> 
     * to be placed
     * in the initial context so that it is available
     * in a script that might look something like the
     * following:
     * <code><pre>
     * #!bin/sh
     * 
     * echo y | mysqladmin create $databaseName
     * </pre></code>
     * The value of <code>$databaseName</code> isn't critical to
     * output, and you obviously don't want to change
     * the ant task to simply take a database name.
     * So initial context values can be set with
     * properties file.
     */
    protected ExtendedProperties contextProperties;

    /**
     * [REQUIRED] Set the control template for the
     * generating process.
     */
    public void setControlTemplate (String controlTemplate)
    {
        this.controlTemplate = controlTemplate;
    }

    /**
     * Get the control template for the
     * generating process.
     */
    public String getControlTemplate()
    {
        return controlTemplate;
    }

    /**
     * [REQUIRED] Set the path where Velocity will look
     * for templates using the file template
     * loader.
     */
    
    public void setTemplatePath(File templatePath)
    {
         try 
         {
             this.templatePath = templatePath.getCanonicalPath();
         } 
         catch (java.io.IOException ioe) 
         {
             throw new BuildException(ioe);
         }
     }

    /**
     * Get the path where Velocity will look
     * for templates using the file template
     * loader.
     */
    public String getTemplatePath()
    {
        return templatePath;
    }        

    /**
     * [REQUIRED] Set the output directory. It will be
     * created if it doesn't exist.
     */
    public void setOutputDirectory(File outputDirectory)
    {
        try
        {
            this.outputDirectory = outputDirectory.getCanonicalPath();
        }
        catch (java.io.IOException ioe)
        {
            throw new BuildException(ioe);
        }
    }
      
    /**
     * Get the output directory.
     */
    public String getOutputDirectory()
    {
        return outputDirectory;
    }        

    /**
     * [REQUIRED] Set the output file for the
     * generation process.
     */
    public void setOutputFile(String outputFile)
    {
        this.outputFile = outputFile;
    }
    
    /**
     * Get the output file for the
     * generation process.
     */
    public String getOutputFile()
    {
        return outputFile;
    }        

    /**
     * Set the context properties that will be
     * fed into the initial context be the
     * generating process starts.
     */
    public void setContextProperties( File file )
    {
        contextProperties = new ExtendedProperties();
        
        try
        {
            contextProperties.load(new FileInputStream(file));
        }
        catch (Exception e)
        {
            contextProperties = null;
        }
    }

    /**
     * Get the context properties that will be
     * fed into the initial context be the
     * generating process starts.
     */
    public ExtendedProperties getContextProperties()
    {
        return contextProperties;
    }
    
    /**
     * Creates a VelocityContext.
     *
     * @return new Context
     * @throws Exception the execute method will catch 
     *         and rethrow as a <code>BuildException</code>
     */
    public Context initControlContext() 
        throws Exception
    {
        return new VelocityContext();
    }
    
    /**
     * Execute the input script with Velocity
     *
     * @throws BuildException  
     * BuildExceptions are thrown when required attributes are missing.
     * Exceptions thrown by Velocity are rethrown as BuildExceptions.
     */
    public void execute () 
        throws BuildException
    {
        /*
         * Make sure the template path is set.
         */
        if (templatePath == null)
        {
            throw new BuildException("The template path needs to be defined!");
        }            
        
        /*
         * Make sure the control template is set.
         */
        if (controlTemplate == null)
        {
            throw new BuildException("The control template needs to be defined!");
        }            

        /*
         * Make sure the output directory is set.
         */
        if (outputDirectory == null)
        {
            throw new BuildException("The output directory needs to be defined!");
        }            
        
        /*
         * Make sure there is an output file.
         */
        if (outputFile == null)
        {
            throw new BuildException("The output file needs to be defined!");
        }            

        try
        {
            /* 
             * Setup the Velocity Runtime.
             */
            Velocity.setProperty(
                Velocity.FILE_RESOURCE_LOADER_PATH, templatePath);
            
            Velocity.init();

            /* 
             * Create the text generator.
             */
            Generator generator = Generator.getInstance();
            generator.setOutputPath(outputDirectory);
            generator.setTemplatePath(templatePath);
            
            /* 
             * Make sure the output directory exists, if it doesn't
             * then create it.
             */
            
            File file = new File(outputDirectory);
            if (! file.exists())
            {
                file.mkdirs();
            }                
            
            String path = outputDirectory + File.separator + outputFile;
            System.out.println(path);
            FileWriter writer = new FileWriter(path);
            
            /* 
             * The generator and the output path should
             * be placed in the init context here and
             * not in the generator class itself.
             */
            Context c = initControlContext();
            
            /*
             * Everything in the generator class should be
             * pulled out and placed in here. What the generator
             * class does can probably be added to the Velocity
             * class and the generator class can probably
             * be removed all together.
             */
            populateInitialContext(c);
            
            /* 
             * Feed all the options into the initial
             * control context so they are available
             * in the control/worker templates.
             */
            
            if (contextProperties != null)
            {
                Iterator i = contextProperties.getKeys();
        
                while (i.hasNext())
                {
                    String property = (String) i.next();
                    String value = contextProperties.getString(property);
                    
                    /* 
                     * Now lets quickly check to see if what
                     * we have is numeric and try to put it
                     * into the context as an Integer.
                     */
                    try
                    {
                        c.put(property, new Integer(value)); 
                    }
                    catch (NumberFormatException nfe)
                    {
                        /*
                         * Now we will try to place the value into
                         * the context as a boolean value if it
                         * maps to a valid boolean value.
                         */
                         
                        String booleanString = 
                            contextProperties.testBoolean(value);
                        
                        if (booleanString != null)
                        {    
                            c.put(property, new Boolean(booleanString));
                        }
                        else
                        {
                        
                            /*
                             * We are going to do something special
                             * for properties that have a "file.contents"
                             * suffix: for these properties will pull
                             * in the contents of the file and make
                             * them available in the context. So for
                             * a line like the following in a properties file:
                             *
                             * license.file.contents = license.txt
                             *
                             * We will pull in the contents of license.txt
                             * and make it available in the context as
                             * $license. This should make texen a little
                             * more flexible.
                             */
                            if (property.endsWith("file.contents"))
                            {
                                /*
                                 * We need to turn the license file from relative to
                                 * absolute, and let Ant help :)
                                 */

                                value = StringUtils.fileContentsToString(   
                                    project.resolveFile(value).getCanonicalPath());
                            
                                property = property.substring(
                                    0, property.indexOf("file.contents") - 1);
                            }
                        
                            c.put(property, value);
                        }
                    }
                }
            }
            
            writer.write(generator.parse(controlTemplate, c));
            writer.flush();
            writer.close();
            generator.shutdown();
        }
        catch( BuildException e)
        {
            throw e;
        }
        catch( MethodInvocationException e )
        {
            throw new BuildException(
                "Exception thrown by '" + e.getReferenceName() + "." + e.getMethodName() +"'" + MSG_CONSULT_LOG,
                e.getWrappedThrowable());
        }       
        catch( ParseErrorException e )
        {
            throw new BuildException("Velocity syntax error" + MSG_CONSULT_LOG ,e);
        }        
        catch( ResourceNotFoundException e )
        {
            throw new BuildException("Resource not found" + MSG_CONSULT_LOG,e);
        }
        catch( Exception e )
        {
            throw new BuildException("Generation failed" + MSG_CONSULT_LOG ,e);
        }
    }

    /**
     * <p>
     * Place useful objects into the initial context.
     * </p><p>
     * TexenTask places <code>Date().toString()</code> into the context as <code>$now</code>.
     * Subclasses who want to vary the objects in the context should override this method.
     * </p><p>
     * <code>$generator</code> is not put into the context in this method.
     * </p>
     * @param Context initial context
     *
     * @throws Exception the execute method will catch and rethrow as a <code>BuildException</code>
     */
    protected void populateInitialContext(Context context) 
        throws Exception
    {
        /*
         * Place the current date in the context. Hmm,
         * this should probably be standard and moved
         * into the Texen task proper.
         */
        context.put("now", new Date().toString());
    }
}
