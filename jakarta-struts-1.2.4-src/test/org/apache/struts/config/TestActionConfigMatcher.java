/*
 * $Header: /home/cvs/jakarta-struts/src/test/org/apache/struts/config/TestActionConfigMatcher.java,v 1.8 2004/04/01 17:56:47 mrdon Exp $
 * $Revision: 1.8 $
 * $Date: 2004/04/01 17:56:47 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
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


package org.apache.struts.config;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.mock.TestMockBase;


/**
 * <p>Unit tests for <code>org.apache.struts.util.ActionConfigMatcher</code>.</p>
 *
 * @version $Revision: 1.8 $ $Date: 2004/04/01 17:56:47 $
 */

public class TestActionConfigMatcher extends TestMockBase {


    // ----------------------------------------------------------------- Basics


    public TestActionConfigMatcher(String name) {
        super(name);
    }


    public static void main(String args[]) {
        junit.awtui.TestRunner.main
            (new String[] { TestActionConfigMatcher.class.getName() } );
    }


    public static Test suite() {
        return (new TestSuite(TestActionConfigMatcher.class));
    }


    // ----------------------------------------------------- Instance Variables



    // ----------------------------------------------------- Setup and Teardown


    public void setUp() {

        super.setUp();

    }


    public void tearDown() {

        super.tearDown();

    }


    // ------------------------------------------------------- Individual Tests


    // ---------------------------------------------------------- match()


    public void testNoMatch() {
        ActionConfig[] configs = new ActionConfig[1];
        ActionConfig mapping = buildActionConfig("/foo");
        configs[0] = mapping;
        ActionConfigMatcher matcher = new ActionConfigMatcher(configs);
        
        assertNull("ActionConfig shouldn't be matched", matcher.match("/test"));
    }
    
    public void testNoWildcardMatch() {
        ActionConfig[] configs = new ActionConfig[1];
        ActionConfig mapping = buildActionConfig("/fooBar");
        configs[0] = mapping;
        ActionConfigMatcher matcher = new ActionConfigMatcher(configs);
        
        assertNull("ActionConfig shouldn't be matched", matcher.match("/fooBar"));
    }
    
    public void testShouldMatch() {
        ActionConfig[] configs = new ActionConfig[1];
        ActionConfig mapping = buildActionConfig("/foo*");
        configs[0] = mapping;
        ActionConfigMatcher matcher = new ActionConfigMatcher(configs);
        
        ActionConfig matched = matcher.match("/fooBar");
        assertNotNull("ActionConfig should be matched", matched);
        assertTrue("ActionConfig should have two action forward", matched.findForwardConfigs().length == 2);
        assertTrue("ActionConfig should have two exception forward", matched.findExceptionConfigs().length == 2);
    }
    
    public void testCheckSubstitutionsMatch() {
        ActionConfig[] configs = new ActionConfig[1];
        ActionConfig mapping = buildActionConfig("/foo*");
        configs[0] = mapping;
        ActionConfigMatcher matcher = new ActionConfigMatcher(configs);
        ActionConfig m = matcher.match("/fooBar");
        
        assertTrue("Name hasn't been replaced", "name,Bar".equals(m.getName()));
        assertTrue("Path hasn't been replaced", "/fooBar".equals(m.getPath()));
        assertTrue("Prefix isn't correct", "foo".equals(m.getPrefix()));
        assertTrue("Scope isn't correct", "request".equals(m.getScope()));
        assertTrue("Suffix isn't correct", "bar".equals(m.getSuffix()));
        assertTrue("Unknown isn't correct", !m.getUnknown());
        assertTrue("Validate isn't correct", m.getValidate());

        assertTrue("Type hasn't been replaced", "foo.bar.BarAction".equals(m.getType()));
        assertTrue("Roles hasn't been replaced", "public,Bar".equals(m.getRoles()));
        assertTrue("Parameter hasn't been replaced", "param,Bar".equals(m.getParameter()));
        assertTrue("Attribute hasn't been replaced", "attrib,Bar".equals(m.getAttribute()));
        assertTrue("Forward hasn't been replaced", "fwd,Bar".equals(m.getForward()));
        assertTrue("Include hasn't been replaced", "include,Bar".equals(m.getInclude()));
        assertTrue("Input hasn't been replaced", "input,Bar".equals(m.getInput()));

        ForwardConfig[] fConfigs = m.findForwardConfigs();
        boolean found = false;
        for (int x=0; x<fConfigs.length; x++) {
            ForwardConfig cfg = fConfigs[x];
            if ("name".equals(cfg.getName())) {
                found = true;
                assertTrue("ContextRelative isn't correct", cfg.getContextRelative());
                assertTrue("Path hasn't been replaced", "path,Bar".equals(cfg.getPath()));
            }
        }
        assertTrue("The forward config 'name' cannot be found", found);
    }
 
    public void testCheckMultipleSubstitutions() {
        ActionMapping[] mapping = new ActionMapping[1];
        mapping[0] = new ActionMapping();
        mapping[0].setPath("/foo*");
        mapping[0].setName("name,{1}-{1}");

        ActionConfigMatcher matcher = new ActionConfigMatcher(mapping);
        ActionConfig m = matcher.match("/fooBar");
        
        assertTrue("Name hasn't been replaced correctly: "+m.getName(), "name,Bar-Bar".equals(m.getName()));
    }
    
    private ActionConfig buildActionConfig(String path) {
        ActionMapping mapping = new ActionMapping();

        mapping.setName("name,{1}");
        mapping.setPath(path);
        mapping.setPrefix("foo");
        mapping.setScope("request");
        mapping.setSuffix("bar");
        mapping.setUnknown(false);
        mapping.setValidate(true);

        mapping.setType("foo.bar.{1}Action");
        mapping.setRoles("public,{1}");
        mapping.setParameter("param,{1}");
        mapping.setAttribute("attrib,{1}");
        mapping.setForward("fwd,{1}");
        mapping.setInclude("include,{1}");
        mapping.setInput("input,{1}");

        ForwardConfig cfg = new ActionForward();
        cfg.setContextRelative(true);
        cfg.setName("name");
        cfg.setPath("path,{1}");
        mapping.addForwardConfig(cfg);
        
        cfg = new ActionForward();
        cfg.setContextRelative(true);
        cfg.setName("name2");
        cfg.setPath("path2");
        mapping.addForwardConfig(cfg);
 
        ExceptionConfig excfg = new ExceptionConfig();
        excfg.setKey("foo");
        excfg.setType("foo.Bar");
        excfg.setPath("path");
        mapping.addExceptionConfig(excfg);

        excfg = new ExceptionConfig();
        excfg.setKey("foo2");
        excfg.setType("foo.Bar2");
        excfg.setPath("path2");
        mapping.addExceptionConfig(excfg);


        mapping.freeze();

        return mapping;
    }


}
