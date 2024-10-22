/*
 * $Header: /home/cvs/jakarta-struts/src/test/org/apache/struts/util/TestRequestUtils.java,v 1.26 2004/03/14 06:23:54 sraeburn Exp $
 * $Revision: 1.26 $
 * $Date: 2004/03/14 06:23:54 $
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


package org.apache.struts.util;


import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.mock.MockFormBean;
import org.apache.struts.mock.MockPrincipal;
import org.apache.struts.mock.TestMockBase;
import org.apache.struts.taglib.html.Constants;


/**
 * <p>Unit tests for <code>org.apache.struts.util.RequestUtils</code>.</p>
 *
 * @version $Revision: 1.26 $ $Date: 2004/03/14 06:23:54 $
 */

public class TestRequestUtils extends TestMockBase {


    // ----------------------------------------------------------------- Basics


    public TestRequestUtils(String name) {
        super(name);
    }


    public static void main(String args[]) {
        junit.awtui.TestRunner.main
            (new String[] { TestRequestUtils.class.getName() } );
    }


    public static Test suite() {
        return (new TestSuite(TestRequestUtils.class));
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


    // ---------------------------------------------------------- absoluteURL()


    public void testAbsoluteURL() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.absoluteURL(request, "/foo/bar.jsp").toString();
            assertEquals("absoluteURL is correct",
                         "http://localhost:8080/myapp/foo/bar.jsp",
                         url);
        } catch (MalformedURLException e) {
            fail("Threw MalformedURLException: " + e);
        }

    }


    // ------------------------------------------------------------ actionURL()


    // Default application -- extension mapping
    public void testActionURL1() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig);
        request.setPathElements("/myapp", "/foo.do", null, null);
        String url = RequestUtils.actionURL
            (request, moduleConfig.findActionConfig("/dynamic"), "*.do");
        assertNotNull("URL was returned", url);
        assertEquals("URL value",
                     "/dynamic.do",
                     url);

    }


    // Second application -- extension mapping
    public void testActionURL2() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/foo.do", null, null);
        String url = RequestUtils.actionURL
            (request, moduleConfig2.findActionConfig("/dynamic2"), "*.do");
        assertNotNull("URL was returned", url);
        assertEquals("URL value",
                     "/2/dynamic2.do",
                     url);

    }


    // Default application -- path mapping
    public void testActionURL3() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig);
        request.setPathElements("/myapp", "/do/foo", null, null);
        String url = RequestUtils.actionURL
            (request, moduleConfig.findActionConfig("/dynamic"), "/do/*");
        assertNotNull("URL was returned", url);
        assertEquals("URL value",
                     "/do/dynamic",
                     url);

    }


    // ---------------------------------------------------- computeParameters()


    // No parameters and no transaction token
    public void testComputeParameters0a() {

        Map map = null;
        try {
            map = RequestUtils.computeParameters(page,
                                                 null, null, null, null,
                                                 null, null, null, false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNull("Map is null", map);

    }


    // No parameters but add transaction token
    public void testComputeParameters0b() {

        session.setAttribute(Globals.TRANSACTION_TOKEN_KEY, "token");
        Map map = null;
        try {
            map = RequestUtils.computeParameters
                (page, null, null, null, null,
                 null, null, null, true);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("One parameter in the returned map",
                     1, map.size());
        assertTrue("Transaction token parameter present",
                   map.containsKey(Constants.TOKEN_KEY));
        assertEquals("Transaction token parameter value",
                     "token",
                     (String) map.get(Constants.TOKEN_KEY));

    }


    // Single parameter -- name
    public void testComputeParameters1a() {

        session.setAttribute("attr", "bar");
        Map map = null;
        try {
            map = RequestUtils.computeParameters
                (page, "foo", "attr", null, null,
                 null, null, null, false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("One parameter in the returned map",
                     1, map.size());
        assertTrue("Parameter present",
                   map.containsKey("foo"));
        assertEquals("Parameter value",
                     "bar",
                     (String) map.get("foo"));

    }


    // Single parameter -- scope + name
    public void testComputeParameters1b() {

        request.setAttribute("attr", "bar");
        Map map = null;
        try {
            map = RequestUtils.computeParameters
                (page, "foo", "attr", null, "request",
                 null, null, null, false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("One parameter in the returned map",
                     1, map.size());
        assertTrue("Parameter present",
                   map.containsKey("foo"));
        assertEquals("Parameter value",
                     "bar",
                     (String) map.get("foo"));

    }


    // Single parameter -- scope + name + property
    public void testComputeParameters1c() {

        request.setAttribute("attr", new MockFormBean("bar"));

        Map map = null;
        try {
            map = RequestUtils.computeParameters
                (page, "foo", "attr", "stringProperty", "request",
                 null, null, null, false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("One parameter in the returned map",
                     1, map.size());
        assertTrue("Parameter present",
                   map.containsKey("foo"));
        assertEquals("Parameter value",
                     "bar",
                     (String) map.get("foo"));

    }


    // Provided map -- name
    public void testComputeParameters2a() {

        Map map = new HashMap();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        session.setAttribute("attr", map);

        try {
            map = RequestUtils.computeParameters
                (page, null, null, null, null,
                 "attr", null, null, false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("Two parameter in the returned map",
                     2, map.size());
        assertTrue("Parameter foo1 present",
                   map.containsKey("foo1"));
        assertEquals("Parameter foo1 value",
                     "bar1",
                     (String) map.get("foo1"));
        assertTrue("Parameter foo2 present",
                   map.containsKey("foo2"));
        assertEquals("Parameter foo2 value",
                     "bar2",
                     (String) map.get("foo2"));

    }


    // Provided map -- scope + name
    public void testComputeParameters2b() {

        Map map = new HashMap();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        request.setAttribute("attr", map);

        try {
            map = RequestUtils.computeParameters
                (page, null, null, null, null,
                 "attr", null, "request", false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("Two parameter in the returned map",
                     2, map.size());
        assertTrue("Parameter foo1 present",
                   map.containsKey("foo1"));
        assertEquals("Parameter foo1 value",
                     "bar1",
                     (String) map.get("foo1"));
        assertTrue("Parameter foo2 present",
                   map.containsKey("foo2"));
        assertEquals("Parameter foo2 value",
                     "bar2",
                     (String) map.get("foo2"));

    }


    // Provided map -- scope + name + property
    public void testComputeParameters2c() {

        request.setAttribute("attr", new MockFormBean());

        Map map = null;
        try {
            map = RequestUtils.computeParameters
                (page, null, null, null, null,
                 "attr", "mapProperty", "request", false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("Two parameter in the returned map",
                     2, map.size());
        assertTrue("Parameter foo1 present",
                   map.containsKey("foo1"));
        assertEquals("Parameter foo1 value",
                     "bar1",
                     (String) map.get("foo1"));
        assertTrue("Parameter foo2 present",
                   map.containsKey("foo2"));
        assertEquals("Parameter foo2 value",
                     "bar2",
                     (String) map.get("foo2"));

    }


    // Provided map -- name with one key and two values
    public void testComputeParameters2d() {

        Map map = new HashMap();
        map.put("foo", new String[] { "bar1", "bar2" });
        session.setAttribute("attr", map);

        try {
            map = RequestUtils.computeParameters
                (page, null, null, null, null,
                 "attr", null, null, false);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("One parameter in the returned map",
                     1, map.size());
        assertTrue("Parameter foo present",
                   map.containsKey("foo"));
        assertTrue("Parameter foo value type",
                   map.get("foo") instanceof String[]);
        String values[] = (String[]) map.get("foo");
        assertEquals("Values count",
                     2, values.length);

    }


    // Kitchen sink combination of parameters with a merge
    public void testComputeParameters3a() {

        request.setAttribute("attr", new MockFormBean("bar3"));
        session.setAttribute(Globals.TRANSACTION_TOKEN_KEY, "token");

        Map map = null;
        try {
            map = RequestUtils.computeParameters
                (page, "foo1", "attr", "stringProperty", "request",
                 "attr", "mapProperty", "request", true);
        } catch (JspException e) {
            fail("JspException: " + e);
        }
        assertNotNull("Map is not null", map);
        assertEquals("Three parameter in the returned map",
                     3, map.size());

        assertTrue("Parameter foo1 present",
                   map.containsKey("foo1"));
        assertTrue("Parameter foo1 value type",
                   map.get("foo1") instanceof String[]);
        String values[] = (String[]) map.get("foo1");
        assertEquals("Values count",
                     2, values.length);

        assertTrue("Parameter foo2 present",
                   map.containsKey("foo2"));
        assertEquals("Parameter foo2 value",
                     "bar2",
                     (String) map.get("foo2"));

        assertTrue("Transaction token parameter present",
                   map.containsKey(Constants.TOKEN_KEY));
        assertEquals("Transaction token parameter value",
                     "token",
                     (String) map.get(Constants.TOKEN_KEY));
    }


    // ----------------------------------------------------------- computeURL()


    // Default module -- Forward only
    public void testComputeURL1a() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "foo",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/bar.jsp",
                     url);

    }


    // Default module -- Href only
    public void testComputeURL1b() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 "http://foo.com/bar", null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "http://foo.com/bar",
                     url);

    }


    // Default module -- Page only
    public void testComputeURL1c() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/bar",
                     url);

    }


    // Default module -- Forward with pattern
    public void testComputeURL1d() {

        moduleConfig.getControllerConfig().setForwardPattern
            ("$C/WEB-INF/pages$M$P");
        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "foo",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/WEB-INF/pages/bar.jsp",
                     url);

    }


    // Default module -- Page with pattern
    public void testComputeURL1e() {

        moduleConfig.getControllerConfig().setPagePattern
            ("$C/WEB-INF/pages$M$P");
        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/WEB-INF/pages/bar",
                     url);

    }


    // Default module -- Forward with relative path (non-context-relative)
    public void testComputeURL1f() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "relative1",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     //                     "/myapp/relative.jsp",
                     "relative.jsp",
                     url);
    }


    // Default module -- Forward with relative path (context-relative)
    public void testComputeURL1g() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "relative2",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     //                     "/myapp/relative.jsp",
                     "relative.jsp",
                     url);
    }


    // Default module -- Forward with external path
    public void testComputeURL1h() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "external",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "http://jakarta.apache.org/",
                     url);
    }


    // Second module -- Forward only
    public void testComputeURL2a() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "foo",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/2/baz.jsp",
                     url);

    }


    // Second module -- Href only
    public void testComputeURL2b() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 "http://foo.com/bar", null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "http://foo.com/bar",
                     url);

    }


    // Second module -- Page only
    public void testComputeURL2c() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/2/bar",
                     url);

    }


    // Default module -- Forward with pattern
    public void testComputeURL2d() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        moduleConfig2.getControllerConfig().setForwardPattern
            ("$C/WEB-INF/pages$M$P");
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "foo",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/WEB-INF/pages/2/baz.jsp",
                     url);

    }


    // Second module -- Page with pattern
    public void testComputeURL2e() {

        moduleConfig2.getControllerConfig().setPagePattern
            ("$C/WEB-INF/pages$M$P");
        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/WEB-INF/pages/2/bar",
                     url);

    }


    // Second module -- Forward with relative path (non-context-relative)
    public void testComputeURL2f() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "relative1",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     //                     "/myapp/2/relative.jsp",
                     "relative.jsp",
                     url);
    }


    // Second module -- Forward with relative path (context-relative)
    public void testComputeURL2g() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "relative2",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     //                     "/myapp/relative.jsp",
                     "relative.jsp",
                     url);
    }


    // Second module -- Forward with external path
    public void testComputeURL2h() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, "external",
                 null, null,
                 null, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "http://jakarta.apache.org/",
                     url);
    }


    // Add parameters only -- forward URL
    public void testComputeURL3a() {

        request.setPathElements("/myapp", "/action.do", null, null);
        Map map = new HashMap();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 map, null, false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertTrue("url value",
                   url.equals("/myapp/bar?foo1=bar1&amp;foo2=bar2") ||
                   url.equals("/myapp/bar?foo2=bar2&amp;foo1=bar1"));

    }


    // Add anchor only -- forward URL
    public void testComputeURL3b() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 null, "anchor", false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/bar#anchor",
                     url);

    }


    // Add parameters + anchor -- forward URL
    public void testComputeURL3c() {

        request.setPathElements("/myapp", "/action.do", null, null);
        Map map = new HashMap();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 map, "anchor", false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertTrue("url value",
                   url.equals("/myapp/bar?foo1=bar1&amp;foo2=bar2#anchor") ||
                   url.equals("/myapp/bar?foo2=bar2&amp;foo1=bar1#anchor"));

    }


    // Add parameters only -- redirect URL
    public void testComputeURL3d() {

        request.setPathElements("/myapp", "/action.do", null, null);
        Map map = new HashMap();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 map, null, true);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertTrue("url value",
                   url.equals("/myapp/bar?foo1=bar1&foo2=bar2") ||
                   url.equals("/myapp/bar?foo2=bar2&foo1=bar1"));

    }


    // Add anchor only -- redirect URL
    public void testComputeURL3e() {

        request.setPathElements("/myapp", "/action.do", null, null);
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 null, "anchor", true);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertEquals("url value",
                     "/myapp/bar#anchor",
                     url);

    }


    // Add parameters + anchor -- redirect URL
    public void testComputeURL3f() {

        request.setPathElements("/myapp", "/action.do", null, null);
        Map map = new HashMap();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        String url = null;
        try {
            url = RequestUtils.computeURL
                (page, null,
                 null, "/bar",
                 map, "anchor", false);
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("url present", url);
        assertTrue("url value",
                   url.equals("/myapp/bar?foo1=bar1&amp;foo2=bar2#anchor") ||
                   url.equals("/myapp/bar?foo2=bar2&amp;foo1=bar1#anchor"));

    }


    // ----------------------------------------------------- createActionForm()



    // Default module -- No ActionForm should be created
    public void testCreateActionForm1a() {

        request.setPathElements("/myapp", "/noform.do", null, null);
        ActionMapping mapping = (ActionMapping)
            moduleConfig.findActionConfig("/noform");
        assertNotNull("Found /noform mapping", mapping);
        ActionForm form = RequestUtils.createActionForm
            (request, mapping, moduleConfig, null);
        assertNull("No ActionForm returned", form);

    }


    // Second module -- No ActionForm should be created
    public void testCreateActionForm1b() {

        request.setPathElements("/myapp", "/2/noform.do", null, null);
        ActionMapping mapping = (ActionMapping)
            moduleConfig2.findActionConfig("/noform");
        assertNotNull("Found /noform mapping", mapping);
        ActionForm form = RequestUtils.createActionForm
            (request, mapping, moduleConfig2, null);
        assertNull("No ActionForm returned", form);

    }


    // Default module -- Standard ActionForm should be created
    public void testCreateActionForm2a() {

        request.setPathElements("/myapp", "/static.do", null, null);
        ActionMapping mapping = (ActionMapping)
            moduleConfig.findActionConfig("/static");
        assertNotNull("Found /static mapping", mapping);
        assertNotNull("Mapping has non-null name",
                      mapping.getName());
        assertEquals("Mapping has correct name",
                     "static",
                     mapping.getName());
        assertNotNull("AppConfig has form bean " + mapping.getName(),
                      moduleConfig.findFormBeanConfig(mapping.getName()));
        ActionForm form = RequestUtils.createActionForm
            (request, mapping, moduleConfig, null);
        assertNotNull("ActionForm returned", form);
        assertTrue("ActionForm of correct type",
                   form instanceof MockFormBean);

    }


    // Second module -- Standard ActionForm should be created
    public void testCreateActionForm2b() {

        request.setPathElements("/myapp", "/2/static.do", null, null);
        ActionMapping mapping = (ActionMapping)
            moduleConfig2.findActionConfig("/static");
        assertNotNull("Found /static mapping", mapping);
        assertNotNull("Mapping has non-null name",
                      mapping.getName());
        assertEquals("Mapping has correct name",
                     "static",
                     mapping.getName());
        assertNotNull("AppConfig has form bean " + mapping.getName(),
                      moduleConfig.findFormBeanConfig(mapping.getName()));
        ActionForm form = RequestUtils.createActionForm
            (request, mapping, moduleConfig2, null);
        assertNotNull("ActionForm returned", form);
        assertTrue("ActionForm of correct type",
                   form instanceof MockFormBean);

    }


    // Default module -- Dynamic ActionForm should be created
    public void testCreateActionForm3a() {

        request.setPathElements("/myapp", "/dynamic.do", null, null);
        ActionMapping mapping = (ActionMapping)
            moduleConfig.findActionConfig("/dynamic");
        assertNotNull("Found /dynamic mapping", mapping);
        assertNotNull("Mapping has non-null name",
                      mapping.getName());
        assertEquals("Mapping has correct name",
                     "dynamic",
                     mapping.getName());
        assertNotNull("AppConfig has form bean " + mapping.getName(),
                      moduleConfig.findFormBeanConfig(mapping.getName()));
        ActionForm form = RequestUtils.createActionForm
            (request, mapping, moduleConfig, null);
        assertNotNull("ActionForm returned", form);
        assertTrue("ActionForm of correct type",
                   form instanceof DynaActionForm);

    }


    // Second module -- Dynamic ActionForm should be created
    public void testCreateActionForm3b() {

        request.setPathElements("/myapp", "/2/dynamic2.do", null, null);
        ActionMapping mapping = (ActionMapping)
            moduleConfig2.findActionConfig("/dynamic2");
        assertNotNull("Found /dynamic2 mapping", mapping);
        assertNotNull("Mapping has non-null name",
                      mapping.getName());
        assertEquals("Mapping has correct name",
                     "dynamic2",
                     mapping.getName());
        assertNotNull("AppConfig has form bean " + mapping.getName(),
                      moduleConfig2.findFormBeanConfig(mapping.getName()));
        ActionForm form = RequestUtils.createActionForm
            (request, mapping, moduleConfig2, null);
        assertNotNull("ActionForm returned", form);
        assertTrue("ActionForm of correct type",
                   form instanceof DynaActionForm);

    }


    // Default module -- Dynamic ActionForm with initializers
    public void testCreateActionForm4a() {

        // Retrieve an appropriately configured DynaActionForm instance
        request.setPathElements("/myapp", "/dynamic0.do", null, null);
        ActionMapping mapping = (ActionMapping)
            moduleConfig.findActionConfig("/dynamic0");
        assertNotNull("Found /dynamic0 mapping", mapping);
        assertNotNull("Mapping has non-null name",
                      mapping.getName());
        assertEquals("Mapping has correct name",
                     "dynamic0",
                     mapping.getName());
        assertNotNull("AppConfig has form bean " + mapping.getName(),
                      moduleConfig.findFormBeanConfig(mapping.getName()));
        ActionForm form = RequestUtils.createActionForm
            (request, mapping, moduleConfig, null);
        assertNotNull("ActionForm returned", form);
        assertTrue("ActionForm of correct type",
                   form instanceof DynaActionForm);

        // Validate the property values
        DynaActionForm dform = (DynaActionForm) form;
        Boolean booleanProperty = (Boolean) dform.get("booleanProperty");
        assertTrue("booleanProperty is true", booleanProperty.booleanValue());
        String stringProperty = (String) dform.get("stringProperty");
        assertEquals("stringProperty is correct",
                     "String Property",
                     stringProperty);
        Object value = null;

        value = dform.get("intArray1");
        assertNotNull("intArray1 exists", value);
        assertTrue("intArray1 is int[]", value instanceof int[]);
        int intArray1[] = (int[]) value;
        assertEquals("intArray1 length is correct", 3, intArray1.length);
        assertEquals("intArray1[0] value is correct", 1, intArray1[0]);
        assertEquals("intArray1[1] value is correct", 2, intArray1[1]);
        assertEquals("intArray1[2] value is correct", 3, intArray1[2]);

        value = dform.get("intArray2");
        assertNotNull("intArray2 exists", value);
        assertTrue("intArray2 is int[]", value instanceof int[]);
        int intArray2[] = (int[]) value;
        assertEquals("intArray2 length is correct", 5, intArray2.length);
        assertEquals("intArray2[0] value is correct", 0, intArray2[0]);
        assertEquals("intArray2[1] value is correct", 0, intArray2[1]);
        assertEquals("intArray2[2] value is correct", 0, intArray2[2]);
        assertEquals("intArray2[3] value is correct", 0, intArray2[3]);
        assertEquals("intArray2[4] value is correct", 0, intArray2[4]);

        value = dform.get("principal");
        assertNotNull("principal exists", value);
        assertTrue("principal is MockPrincipal",
                   value instanceof MockPrincipal);

        value = dform.get("stringArray1");
        assertNotNull("stringArray1 exists", value);
        assertTrue("stringArray1 is int[]", value instanceof String[]);
        String stringArray1[] = (String[]) value;
        assertEquals("stringArray1 length is correct", 3, stringArray1.length);
        assertEquals("stringArray1[0] value is correct",
                     "aaa", stringArray1[0]);
        assertEquals("stringArray1[1] value is correct",
                     "bbb", stringArray1[1]);
        assertEquals("stringArray1[2] value is correct",
                     "ccc", stringArray1[2]);

        value = dform.get("stringArray2");
        assertNotNull("stringArray2 exists", value);
        assertTrue("stringArray2 is int[]", value instanceof String[]);
        String stringArray2[] = (String[]) value;
        assertEquals("stringArray2 length is correct", 3, stringArray2.length);
        assertEquals("stringArray2[0] value is correct",
                     new String(), stringArray2[0]);
        assertEquals("stringArray2[1] value is correct",
                     new String(), stringArray2[1]);
        assertEquals("stringArray2[2] value is correct",
                     new String(), stringArray2[2]);

        // Different form beans should get different property value instances
        Object value1 = null;
        DynaActionForm dform1 = (DynaActionForm)
            RequestUtils.createActionForm(request, mapping, moduleConfig, null);

        value = dform.get("principal");
        value1 = dform1.get("principal");
        assertEquals("Different form beans get equal instance values",
                     value, value1);
        assertTrue("Different form beans get different instances 1",
                   value != value1);

        value = dform.get("stringArray1");
        value1 = dform1.get("stringArray1");
        assertTrue("Different form beans get different instances 2",
                   value != value1);

        dform1.set("stringProperty", "Different stringProperty value");
        value = dform.get("stringProperty");
        value1 = dform1.get("stringProperty");
        assertTrue("Different form beans get different instances 3",
                   value != value1);

    }



    // ----------------------------------------------------------- forwardURL()


    // Default module (default forwardPattern)
    public void testForwardURL1() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig);
        request.setPathElements("/myapp", "/action.do", null, null);
        ForwardConfig forward = null;
        String result = null;

        // redirect=false, contextRelative=false
        forward = moduleConfig.findForwardConfig("moduleForward");
        assertNotNull("moduleForward found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleForward computed", result);
        assertEquals("moduleForward value",
                     "/module/forward",
                     result);

        // redirect=true, contextRelative=false
        forward = moduleConfig.findForwardConfig("moduleRedirect");
        assertNotNull("moduleRedirect found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleRedirect computed", result);
        assertEquals("moduleRedirect value",
                     "/module/redirect",
                     result);

        // redirect=false, contextRelative=true
        forward = moduleConfig.findForwardConfig("contextForward");
        assertNotNull("contextForward found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextForward computed", result);
        assertEquals("contextForward value",
                     "/context/forward",
                     result);

        // redirect=true, contextRelative=true
        forward = moduleConfig.findForwardConfig("contextRedirect");
        assertNotNull("contextRedirect found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextRedirect computed", result);
        assertEquals("contextRedirct value",
                     "/context/redirect",
                     result);

        // noslash, contextRelative=false
        forward = moduleConfig.findForwardConfig("moduleNoslash");
        assertNotNull("moduleNoslash found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleNoslash computed", result);
        assertEquals("moduleNoslash value",
                     "/module/noslash",
                     result);

        // noslash, contextRelative=true
        forward = moduleConfig.findForwardConfig("contextNoslash");
        assertNotNull("contextNoslash found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextNoslash computed", result);
        assertEquals("contextNoslash value",
                     "/context/noslash",
                     result);

    }


    // Second module (default forwardPattern)
    public void testForwardURL2() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        ForwardConfig forward = null;
        String result = null;

        // redirect=false, contextRelative=false
        forward = moduleConfig2.findForwardConfig("moduleForward");
        assertNotNull("moduleForward found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleForward computed", result);
        assertEquals("moduleForward value",
                     "/2/module/forward",
                     result);

        // redirect=true, contextRelative=false
        forward = moduleConfig2.findForwardConfig("moduleRedirect");
        assertNotNull("moduleRedirect found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleRedirect computed", result);
        assertEquals("moduleRedirect value",
                     "/2/module/redirect",
                     result);

        // redirect=false, contextRelative=true
        forward = moduleConfig2.findForwardConfig("contextForward");
        assertNotNull("contextForward found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextForward computed", result);
        assertEquals("contextForward value",
                     "/context/forward",
                     result);

        // redirect=true, contextRelative=true
        forward = moduleConfig2.findForwardConfig("contextRedirect");
        assertNotNull("contextRedirect found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextRedirect computed", result);
        assertEquals("contextRedirct value",
                     "/context/redirect",
                     result);

        // noslash, contextRelative=false
        forward = moduleConfig2.findForwardConfig("moduleNoslash");
        assertNotNull("moduleNoslash found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleNoslash computed", result);
        assertEquals("moduleNoslash value",
                     "/2/module/noslash",
                     result);

        // noslash, contextRelative=true
        forward = moduleConfig2.findForwardConfig("contextNoslash");
        assertNotNull("contextNoslash found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextNoslash computed", result);
        assertEquals("contextNoslash value",
                     "/context/noslash",
                     result);

    }


    // Third module (custom forwardPattern)
    public void testForwardURL3() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig3);
        request.setPathElements("/myapp", "/3/action.do", null, null);
        ForwardConfig forward = null;
        String result = null;

        // redirect=false, contextRelative=false
        forward = moduleConfig3.findForwardConfig("moduleForward");
        assertNotNull("moduleForward found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleForward computed", result);
        assertEquals("moduleForward value",
                     "/forwarding/3/module/forward",
                     result);

        // redirect=true, contextRelative=false
        forward = moduleConfig3.findForwardConfig("moduleRedirect");
        assertNotNull("moduleRedirect found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleRedirect computed", result);
        assertEquals("moduleRedirect value",
                     "/forwarding/3/module/redirect",
                     result);

        // redirect=false, contextRelative=true
        forward = moduleConfig3.findForwardConfig("contextForward");
        assertNotNull("contextForward found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextForward computed", result);
        assertEquals("contextForward value",
                     "/context/forward",
                     result);

        // redirect=true, contextRelative=true
        forward = moduleConfig3.findForwardConfig("contextRedirect");
        assertNotNull("contextRedirect found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextRedirect computed", result);
        assertEquals("contextRedirct value",
                     "/context/redirect",
                     result);

        // noslash, contextRelative=false
        forward = moduleConfig3.findForwardConfig("moduleNoslash");
        assertNotNull("moduleNoslash found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("moduleNoslash computed", result);
        assertEquals("moduleNoslash value",
                     "/forwarding/3/module/noslash",
                     result);

        // noslash, contextRelative=true
        forward = moduleConfig3.findForwardConfig("contextNoslash");
        assertNotNull("contextNoslash found", forward);
        result = RequestUtils.forwardURL(request, forward, null);
        assertNotNull("contextNoslash computed", result);
        assertEquals("contextNoslash value",
                     "/context/noslash",
                     result);

    }


	// Cross module forwards
	public void testForwardURLa() {

		request.setAttribute(Globals.MODULE_KEY, moduleConfig);
		request.setPathElements("/myapp", "/action.do", null, null);
		ForwardConfig forward = null;
		String result = null;

		// redirect=false, contextRelative=false, link to module 3
		forward = moduleConfig3.findForwardConfig("moduleForward");
		assertNotNull("moduleForward found", forward);
		result = RequestUtils.forwardURL(request, forward, moduleConfig3);
		assertNotNull("moduleForward computed", result);
		assertEquals("moduleForward value",
					 "/forwarding/3/module/forward",
					 result);

		// redirect=true, contextRelative=false, link to module 3
		forward = moduleConfig3.findForwardConfig("moduleRedirect");
		assertNotNull("moduleRedirect found", forward);
		result = RequestUtils.forwardURL(request, forward, moduleConfig3);
		assertNotNull("moduleRedirect computed", result);
		assertEquals("moduleRedirect value",
					 "/forwarding/3/module/redirect",
					 result);

		// redirect=false, contextRelative=true, link to module 3
		forward = moduleConfig3.findForwardConfig("contextForward");
		assertNotNull("contextForward found", forward);
		result = RequestUtils.forwardURL(request, forward, moduleConfig3);
		assertNotNull("contextForward computed", result);
		assertEquals("contextForward value",
					 "/context/forward",
					 result);

		// redirect=true, contextRelative=true, link to module 3
		forward = moduleConfig3.findForwardConfig("contextRedirect");
		assertNotNull("contextRedirect found", forward);
		result = RequestUtils.forwardURL(request, forward, moduleConfig3);
		assertNotNull("contextRedirect computed", result);
		assertEquals("contextRedirct value",
					 "/context/redirect",
					 result);

		// noslash, contextRelative=false, link to module 3
		forward = moduleConfig3.findForwardConfig("moduleNoslash");
		assertNotNull("moduleNoslash found", forward);
		result = RequestUtils.forwardURL(request, forward, moduleConfig3);
		assertNotNull("moduleNoslash computed", result);
		assertEquals("moduleNoslash value",
					 "/forwarding/3/module/noslash",
					 result);

		// noslash, contextRelative=true, link to module 3
		forward = moduleConfig3.findForwardConfig("contextNoslash");
		assertNotNull("contextNoslash found", forward);
		result = RequestUtils.forwardURL(request, forward, moduleConfig3);
		assertNotNull("contextNoslash computed", result);
		assertEquals("contextNoslash value",
					 "/context/noslash",
					 result);

	}


    // -------------------------------------------------------------- pageURL()


    // Default module (default pagePattern)
    public void testPageURL1() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig);
        request.setPathElements("/myapp", "/action.do", null, null);
        String page = null;
        String result = null;

        // Straight substitution
        page = "/mypages/index.jsp";
        result = RequestUtils.pageURL(request, page);
        assertNotNull("straight sub found", result);
        assertEquals("straight sub value",
                     "/mypages/index.jsp", result);


    }


    // Second module (default pagePattern)
    public void testPageURL2() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig2);
        request.setPathElements("/myapp", "/2/action.do", null, null);
        String page = null;
        String result = null;

        // Straight substitution
        page = "/mypages/index.jsp";
        result = RequestUtils.pageURL(request, page);
        assertNotNull("straight sub found", result);
        assertEquals("straight sub value",
                     "/2/mypages/index.jsp", result);


    }


    // Third module (custom pagePattern)
    public void testPageURL3() {

        request.setAttribute(Globals.MODULE_KEY, moduleConfig3);
        request.setPathElements("/myapp", "/3/action.do", null, null);
        String page = null;
        String result = null;

        // Straight substitution
        page = "/mypages/index.jsp";
        result = RequestUtils.pageURL(request, page);
        assertNotNull("straight sub found", result);
        assertEquals("straight sub value",
                     "/paging/3/mypages/index.jsp", result);


    }


    // ----------------------------------------------------------- requestURL()


    public void testRequestURL() {

        request.setPathElements("/myapp", "/foo.do", null, null);
        String url = null;
        try {
            url = RequestUtils.requestURL(request).toString();
        } catch (MalformedURLException e) {
            fail("MalformedURLException: " + e);
        }
        assertNotNull("URL was returned", url);
        assertEquals("URL value",
                     "http://localhost:8080/myapp/foo.do",
                     url);

    }


    // ---------------------------------------------------- selectApplication()


    // Map to the default module -- direct
    public void testSelectApplication1a() {

        request.setPathElements("/myapp", "/noform.do", null, null);
        ModuleUtils.getInstance().selectModule(request, context);
        ModuleConfig moduleConfig = (ModuleConfig)
            request.getAttribute(Globals.MODULE_KEY);
        assertNotNull("Selected a module", moduleConfig);
        assertEquals("Selected correct module",
                     "", moduleConfig.getPrefix());
        // FIXME - check module resources?

    }


    // Map to the second module -- direct
    public void testSelectApplication1b() {
        String[] prefixes = { "/1", "/2" };
        context.setAttribute(Globals.MODULE_PREFIXES_KEY, prefixes);    
        request.setPathElements("/myapp", "/2/noform.do", null, null);
        
        ModuleUtils.getInstance().selectModule(request, context);
        ModuleConfig moduleConfig = (ModuleConfig)
            request.getAttribute(Globals.MODULE_KEY);
        assertNotNull("Selected a module", moduleConfig);
        assertEquals("Selected correct module",
                     "/2", moduleConfig.getPrefix());
        // FIXME - check module resources?

    }


    // Map to the default module -- include
    public void testSelectApplication2a() {

        request.setPathElements("/myapp", "/2/noform.do", null, null);
        request.setAttribute(RequestProcessor.INCLUDE_SERVLET_PATH,
                             "/noform.do");
        ModuleUtils.getInstance().selectModule(request, context);
        ModuleConfig moduleConfig = (ModuleConfig)
            request.getAttribute(Globals.MODULE_KEY);
        assertNotNull("Selected an application", moduleConfig);
        assertEquals("Selected correct application",
                     "", moduleConfig.getPrefix());
        // FIXME - check application resources?

    }


    // Map to the second module -- include
    public void testSelectApplication2b() {
        String[] prefixes = { "/1", "/2" };
        context.setAttribute(Globals.MODULE_PREFIXES_KEY, prefixes);    
        request.setPathElements("/myapp", "/noform.do", null, null);
        request.setAttribute(RequestProcessor.INCLUDE_SERVLET_PATH,
                             "/2/noform.do");
        ModuleUtils.getInstance().selectModule(request, context);
        ModuleConfig moduleConfig = (ModuleConfig)
            request.getAttribute(Globals.MODULE_KEY);
        assertNotNull("Selected a module", moduleConfig);
        assertEquals("Selected correct module",
                     "/2", moduleConfig.getPrefix());
        // FIXME - check application resources?

    }


    // ------------------------------------------------------------ serverURL()


    // Basic test on values in mock objects
    public void testServerURL() {

        String url = null;
        try {
            url = RequestUtils.serverURL(request).toString();
        } catch (MalformedURLException e) {
            fail("Threw MalformedURLException: " + e);
        }
        assertNotNull("serverURL is present", url);
        assertEquals("serverURL value",
                     "http://localhost:8080",
                     url);

    }


}
