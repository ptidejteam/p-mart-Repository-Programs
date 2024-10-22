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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * This class is intended to test the app.Velocity.java class.
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id: VelocityAppTestCase.java 291585 2005-09-26 08:56:23Z henning $
 */
public class VelocityAppTestCase extends BaseTestCase implements TemplateTestBase
{
    private StringWriter compare1 = new StringWriter();
    private String input1 = "My name is $name -> $Floog";
    private String result1 = "My name is jason -> floogie woogie";

    public VelocityAppTestCase(String name)
    {
        super(name);
    }

    public void setUp()
            throws Exception
    {
        Velocity.setProperty(
                Velocity.FILE_RESOURCE_LOADER_PATH, FILE_RESOURCE_LOADER_PATH);

        Velocity.init();
    }

    public static Test suite()
    {
        return new TestSuite(VelocityAppTestCase.class);
    }

    /**
     * Runs the test.
     */
    public void testVelocityApp ()
            throws Exception
    {
        VelocityContext context = new VelocityContext();
        context.put("name", "jason");
        context.put("Floog", "floogie woogie");

            Velocity.evaluate(context, compare1, "evaltest", input1);

/*
            @todo FIXME: Not tested right now.

            StringWriter result2 = new StringWriter();
            Velocity.mergeTemplate("mergethis.vm",  context, result2);

            StringWriter result3 = new StringWriter();
            Velocity.invokeVelocimacro("floog", "test", new String[2],
                                        context, result3);
*/
            if (!result1.equals(compare1.toString()))
            {
                fail("Output incorrect.");
            }
    }
}
