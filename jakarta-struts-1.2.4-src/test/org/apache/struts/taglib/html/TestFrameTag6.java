/*
 * $Header: /home/cvs/jakarta-struts/src/test/org/apache/struts/taglib/html/TestFrameTag6.java,v 1.8 2004/03/14 06:23:41 sraeburn Exp $
 * $Revision: 1.8 $
 * $Date: 2004/03/14 06:23:41 $
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
package org.apache.struts.taglib.html;

import java.util.Locale;

import javax.servlet.jsp.PageContext;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.cactus.JspTestCase;
import org.apache.struts.Globals;
import org.apache.struts.taglib.SimpleBeanForTesting;

/**
 * Suite of unit tests for the
 * <code>org.apache.struts.taglib.html.FrameTag</code> class.
 *
 */
public class TestFrameTag6 extends JspTestCase {

    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestFrameTag6(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(new String[] {TestFrameTag6.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestFrameTag6.class);
    }

    private void runMyTest(String whichTest, String locale) throws Exception {
        pageContext.setAttribute(Globals.LOCALE_KEY, new Locale(locale, locale), PageContext.SESSION_SCOPE);
        pageContext.setAttribute(Constants.BEAN_KEY, new SimpleBeanForTesting("Test Value"), PageContext.REQUEST_SCOPE);
        request.setAttribute("runTest", whichTest);
        pageContext.forward("/test/org/apache/struts/taglib/html/TestFrameTag6.jsp");
    }

    /*
     * Testing FrameTag.
     */

//--------Testing attributes using forward------
    public void testFrameHrefParamIdParamNameNoScope() throws Exception {
                pageContext.setAttribute("paramName", "paramValue", PageContext.REQUEST_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameNoScope", "");
    }

    public void testFrameHrefParamIdParamNameParamPropertyNoScope() throws Exception {
        SimpleBeanForTesting sbft = new SimpleBeanForTesting("paramPropertyValue");
                pageContext.setAttribute("testingParamProperty", sbft, PageContext.REQUEST_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameParamPropertyNoScope", "");
    }

    public void testFrameHrefParamIdParamNameApplicationScope() throws Exception {
                pageContext.setAttribute("paramName", "paramValue", PageContext.APPLICATION_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameApplicationScope", "");
    }

    public void testFrameHrefParamIdParamNameParamPropertyApplicationScope() throws Exception {
        SimpleBeanForTesting sbft = new SimpleBeanForTesting("paramPropertyValue");
                pageContext.setAttribute("testingParamProperty", sbft, PageContext.APPLICATION_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameParamPropertyApplicationScope", "");
    }

    public void testFrameHrefParamIdParamNameSessionScope() throws Exception {
                pageContext.setAttribute("paramName", "paramValue", PageContext.SESSION_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameSessionScope", "");
    }

    public void testFrameHrefParamIdParamNameParamPropertySessionScope() throws Exception {
        SimpleBeanForTesting sbft = new SimpleBeanForTesting("paramPropertyValue");
                pageContext.setAttribute("testingParamProperty", sbft, PageContext.SESSION_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameParamPropertySessionScope", "");
    }

    public void testFrameHrefParamIdParamNameRequestScope() throws Exception {
                pageContext.setAttribute("paramName", "paramValue", PageContext.REQUEST_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameRequestScope", "");
    }

    public void testFrameHrefParamIdParamNameParamPropertyRequestScope() throws Exception {
        SimpleBeanForTesting sbft = new SimpleBeanForTesting("paramPropertyValue");
                pageContext.setAttribute("testingParamProperty", sbft, PageContext.REQUEST_SCOPE);
        runMyTest("testFrameHrefParamIdParamNameParamPropertyRequestScope", "");
    }

    public void testFrameHrefScrolling1() throws Exception {
        runMyTest("testFrameHrefScrolling1", "");
    }

    public void testFrameHrefScrollin2g() throws Exception {
        runMyTest("testFrameHrefScrolling2", "");
    }

    public void testFrameHrefScrolling3() throws Exception {
        runMyTest("testFrameHrefScrolling3", "");
    }

    public void testFrameHrefScrolling4() throws Exception {
        runMyTest("testFrameHrefScrolling4", "");
    }

    public void testFrameHrefScrolling5() throws Exception {
        runMyTest("testFrameHrefScrolling5", "");
    }

    public void testFrameHrefScrolling6() throws Exception {
        runMyTest("testFrameHrefScrolling6", "");
    }

    public void testFrameHrefScrolling7() throws Exception {
        runMyTest("testFrameHrefScrolling7", "");
    }

    public void testFrameHrefScrolling8() throws Exception {
        runMyTest("testFrameHrefScrolling8", "");
    }

    public void testFrameHrefScrolling9() throws Exception {
        runMyTest("testFrameHrefScrolling9", "");
    }

    public void testFrameHrefScrolling10() throws Exception {
        runMyTest("testFrameHrefScrolling10", "");
    }

    public void testFrameHrefStyle() throws Exception {
        runMyTest("testFrameHrefStyle", "");
    }

    public void testFrameHrefTitle() throws Exception {
        runMyTest("testFrameHrefTitle", "");
    }

    public void testFrameHrefTitleKey() throws Exception {
        runMyTest("testFrameHrefTitleKey", "");
    }

    public void testFrameHrefTransaction() throws Exception {
        pageContext.setAttribute(Globals.TRANSACTION_TOKEN_KEY, "Some_Token_Here", PageContext.SESSION_SCOPE);
        runMyTest("testFrameHrefTransaction", "");
    }





}
