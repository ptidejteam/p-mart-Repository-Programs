package org.apache.velocity.test;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;

/**
 * Test parser exception is generated with appropriate info.
 *
 * @author <a href="mailto:wglass@apache.org">Will Glass-Husain</a>
 * @version $Id: ParseExceptionTestCase.java 329578 2005-10-30 14:54:06Z henning $
 */
public class ParseExceptionTestCase extends BaseTestCase
{
    /**
     * Path for templates. This property will override the
     * value in the default velocity properties file.
     */
    private final static String FILE_RESOURCE_LOADER_PATH = "test/parseexception";


    /**
     * Default constructor.
     */
    public ParseExceptionTestCase(String name)
    {
        super(name);
    }

    public void setUp()
            throws Exception
    {

    }

    public static Test suite ()
    {
        return new TestSuite(ParseExceptionTestCase.class);
    }

    /**
     * Tests that parseException has useful info when called by template.marge()
     * @throws Exception
     */
    public void testParseExceptionFromTemplate ()
            throws Exception
    {

        VelocityEngine ve = new VelocityEngine();
        
        ve.setProperty("file.resource.loader.cache", "true");
        ve.setProperty("file.resource.loader.path", FILE_RESOURCE_LOADER_PATH);
        ve.init();
        

        Writer writer = new StringWriter();

        VelocityContext context = new VelocityContext();

        try 
        {
            Template template = ve.getTemplate("badtemplate.vm");
            template.merge(context, writer);
            fail("Should have thown a ParseErrorException");
        } 
        catch (ParseErrorException e) 
        {
            assertEquals("badtemplate.vm",e.getTemplateName());
            assertEquals(5,e.getLineNumber());
            assertEquals(9,e.getColumnNumber());
        } 
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    /**
     * Tests that parseException has useful info when thrown in VelocityEngine.evaluate()
     * @throws Exception
     */
    public void testParseExceptionFromEval ()
            throws Exception
    {

        VelocityEngine ve = new VelocityEngine();
        ve.init();
        
        VelocityContext context = new VelocityContext();
        
        Writer writer = new StringWriter();
        
        try 
        {
            ve.evaluate(context,writer,"test","   #set($abc)   ");     
            fail("Should have thown a ParseErrorException");
        } 
        catch (ParseErrorException e) 
        {
            assertEquals("test",e.getTemplateName());
            assertEquals(1,e.getLineNumber());
            assertEquals(13,e.getColumnNumber());
        } 
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }
    
    /**
     * Tests that parseException has useful info when thrown in VelocityEngine.evaluate()
     * and the problem comes from a macro definition
     * @throws Exception
     */
    public void testParseExceptionFromMacroDef ()
            throws Exception
    {
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        
        VelocityContext context = new VelocityContext();
        
        Writer writer = new StringWriter();
        
        try 
        {
            ve.evaluate(context,writer,"testMacro","#macro($blarg) foo #end");     
            fail("Should have thown a ParseErrorException");
        } 
        catch (ParseErrorException e) 
        {
            assertEquals("testMacro",e.getTemplateName());
            assertEquals(1,e.getLineNumber());
            assertEquals(7,e.getColumnNumber());
        } 
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }
 
    /**
     * Tests that parseException has useful info when thrown in VelocityEngine.evaluate()
     * and the problem comes from a macro invocation
     * @throws Exception
     */
    public void testParseExceptionFromMacroInvoke ()
            throws Exception
    {
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        
        VelocityContext context = new VelocityContext();
        
        Writer writer = new StringWriter();
        
        try 
        {
            ve.evaluate(context,writer,"testMacroInvoke", "#macro(   foo $a) $a #end #foo(woogie)");     
            fail("Should have thown a ParseErrorException");
        } 
        catch (ParseErrorException e) 
        {
            assertEquals("testMacroInvoke",e.getTemplateName());
            assertEquals(1,e.getLineNumber());
            assertEquals(31,e.getColumnNumber());
        } 
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }
 
}
