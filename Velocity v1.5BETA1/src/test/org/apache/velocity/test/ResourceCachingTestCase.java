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

/**
 * Test resource caching related issues.
 *
 * @author <a href="mailto:wglass@apache.org">Will Glass-Husain</a>
 * @version $Id: ResourceCachingTestCase.java 291585 2005-09-26 08:56:23Z henning $
 */
public class ResourceCachingTestCase extends BaseTestCase
{
    /**
     * Path for templates. This property will override the
     * value in the default velocity properties file.
     */
    private final static String FILE_RESOURCE_LOADER_PATH = "test/resourcecaching";


    /**
     * Default constructor.
     */
    public ResourceCachingTestCase(String name)
    {
        super(name);
    }

    public void setUp()
            throws Exception
    {

    }

    public static Test suite ()
    {
        return new TestSuite(ResourceCachingTestCase.class);
    }

    /**
     * Tests for fix of bug VELOCITY-98 where a #include followed by #parse
     * of the same file throws ClassCastException when caching is on.
     * @throws Exception
     */
    public void testIncludeParseCaching ()
            throws Exception
    {

        VelocityEngine ve = new VelocityEngine();
        
        ve.setProperty("file.resource.loader.cache", "true");
        ve.setProperty("file.resource.loader.path", FILE_RESOURCE_LOADER_PATH);
        ve.init();
        
        Template template = ve.getTemplate("testincludeparse.vm");

        Writer writer = new StringWriter();

        VelocityContext context = new VelocityContext();

        // will produce a ClassCastException if Velocity-98 is not solved
        template.merge(context, writer);
        writer.flush();
        writer.close();
    }
    
 
}
