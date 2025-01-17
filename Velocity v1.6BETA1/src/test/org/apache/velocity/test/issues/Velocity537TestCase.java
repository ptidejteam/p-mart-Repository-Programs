package org.apache.velocity.test.issues;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.test.BaseTestCase;

/**
 * Test Case for <a href="https://issues.apache.org/jira/browse/VELOCITY-537">Velocity Issue 537</a>.
 */
public class Velocity537TestCase extends BaseTestCase
{
    /**
     * Comparison file extension.
     */
    private static final String CMP_FILE_EXT    = "cmp";

    /**
     * Comparison file extension.
     */
    private static final String RESULT_FILE_EXT = "res";

    /**
     * Results relative to the build directory.
     */
    private static final String RESULTS_DIR     = TEST_RESULT_DIR + "/issues/velocity-537";

    /**
     * Template Directory
     */
    private static final String TEMPLATE_DIR    = TEST_COMPARE_DIR + "/issues/velocity-537/templates";

    /**
     * Results relative to the build directory.
     */
    private static final String COMPARE_DIR     = TEST_COMPARE_DIR + "/issues/velocity-537/compare";

    public Velocity537TestCase(final String name) throws Exception
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(Velocity537TestCase.class);
    }

    private VelocityEngine velocityEngine;
    public void setUp() throws Exception
    {

        assureResultsDirectoryExists(RESULTS_DIR);

        velocityEngine = new VelocityEngine();
        velocityEngine.addProperty(Velocity.FILE_RESOURCE_LOADER_PATH, TEMPLATE_DIR);

        velocityEngine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());

        velocityEngine.init();
    }

    public void testVelocity537() throws Exception
    {
        executeTest("velocity537.vm");
    }

    public void testVelocity537Again() throws Exception
    {
        executeTest("velocity537b.vm");
    }

    protected Template executeTest(final String templateName) throws Exception
    {
        Template template = velocityEngine.getTemplate(templateName);

        FileOutputStream fos = new FileOutputStream(getFileName(RESULTS_DIR, templateName, RESULT_FILE_EXT));

        Writer writer = new BufferedWriter(new OutputStreamWriter(fos));

        VelocityContext context = new VelocityContext();

        template.merge(context, writer);
        writer.flush();
        writer.close();

        if (!isMatch(RESULTS_DIR, COMPARE_DIR, templateName, RESULT_FILE_EXT, CMP_FILE_EXT))
        {
            // just to be useful, output the output in the fail message
            StringWriter out = new StringWriter();
            template.merge(context, out);

            String compare = getFileContents(COMPARE_DIR, templateName, CMP_FILE_EXT);

            fail("Output incorrect for Template: " + templateName + ": \""+out+"\""+
                 "; it did not match: \""+compare+"\"");
        }

        return template;
    }
}
