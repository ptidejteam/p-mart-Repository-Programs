package org.apache.velocity.test;

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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.File;

import java.util.Properties;

import org.apache.velocity.VelocityContext;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.test.provider.TestProvider;
import org.apache.velocity.util.StringUtils;
import org.apache.velocity.runtime.VelocimacroFactory;

import junit.framework.TestCase;

/**
 * Multiple paths in the file resource loader.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id: MultipleFileResourcePathTest.java,v 1.7 2001/08/07 22:20:28 geirm Exp $
 */
public class MultipleFileResourcePathTest extends BaseTestCase
{
     /**
     * VTL file extension.
     */
    private static final String TMPL_FILE_EXT = "vm";

    /**
     * Comparison file extension.
     */
    private static final String CMP_FILE_EXT = "cmp";

    /**
     * Comparison file extension.
     */
    private static final String RESULT_FILE_EXT = "res";

    /**
     * Path for templates. This property will override the
     * value in the default velocity properties file.
     */
    private final static String FILE_RESOURCE_LOADER_PATH1 = "../test/multi/path1";

    /**
     * Path for templates. This property will override the
     * value in the default velocity properties file.
     */
    private final static String FILE_RESOURCE_LOADER_PATH2 = "../test/multi/path2";

    /**
     * Results relative to the build directory.
     */
    private static final String RESULTS_DIR = "../test/multi/results";

    /**
     * Results relative to the build directory.
     */
    private static final String COMPARE_DIR = "../test/multi/compare";

    /**
     * Default constructor.
     */
    MultipleFileResourcePathTest()
    {
        super("MultipleFileResourcePathTest");

        try
        {
            assureResultsDirectoryExists(RESULTS_DIR);

            Velocity.addProperty(
                Velocity.FILE_RESOURCE_LOADER_PATH, FILE_RESOURCE_LOADER_PATH1);

            Velocity.addProperty(
                Velocity.FILE_RESOURCE_LOADER_PATH, FILE_RESOURCE_LOADER_PATH2);

            Velocity.init();
        }
        catch (Exception e)
        {
            System.err.println("Cannot setup MultipleFileResourcePathTest!");
            e.printStackTrace();
            System.exit(1);
        }            
    }

    public static junit.framework.Test suite ()
    {
        return new MultipleFileResourcePathTest();
    }

    /**
     * Runs the test.
     */
    public void runTest ()
    {
        try
        {
            Template template1 = RuntimeSingleton.getTemplate(
                getFileName(null, "path1", TMPL_FILE_EXT));
            
            Template template2 = RuntimeSingleton.getTemplate(
                getFileName(null, "path2", TMPL_FILE_EXT));
           
            FileOutputStream fos1 = 
                new FileOutputStream (
                    getFileName(RESULTS_DIR, "path1", RESULT_FILE_EXT));

            FileOutputStream fos2 = 
                new FileOutputStream (
                    getFileName(RESULTS_DIR, "path2", RESULT_FILE_EXT));

            Writer writer1 = new BufferedWriter(new OutputStreamWriter(fos1));
            Writer writer2 = new BufferedWriter(new OutputStreamWriter(fos2));
            
            /*
             *  put the Vector into the context, and merge both
             */

            VelocityContext context = new VelocityContext();

            template1.merge(context, writer1);
            writer1.flush();
            writer1.close();
            
            template2.merge(context, writer2);
            writer2.flush();
            writer2.close();

            if (!isMatch(RESULTS_DIR, COMPARE_DIR, "path1", 
                    RESULT_FILE_EXT, CMP_FILE_EXT) ||
                !isMatch(RESULTS_DIR, COMPARE_DIR, "path2", 
                    RESULT_FILE_EXT, CMP_FILE_EXT))
            {
                fail("Output incorrect.");
            }
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
}
