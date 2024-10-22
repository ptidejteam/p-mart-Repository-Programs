/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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

package org.apache.tools.ant.taskdefs.optional.junit;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import junit.framework.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Simple Testrunner for JUnit that runs all tests of a testsuite.
 *
 * <p>This TestRunner expects a name of a TestCase class as its
 * argument. If this class provides a static suite() method it will be
 * called and the resulting Test will be run.
 *
 * <p>Otherwise all public methods starting with "test" and taking no
 * argument will be run.
 *
 * <p>Summary output is generated at the end.
 *
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 */

public class JUnitTestRunner implements TestListener {

    /**
     * No problems with this test.
     */
    public static final int SUCCESS = 0;

    /**
     * Some tests failed.
     */
    public static final int FAILURES = 1;

    /**
     * An error occured.
     */
    public static final int ERRORS = 2;

    /**
     * Holds the registered formatters.
     */
    private Vector formatters = new Vector();

    /**
     * Collects TestResults.
     */
    private TestResult res;

    /**
     * Do we stop on errors.
     */
    private boolean haltOnError = false;

    /**
     * Do we stop on test failures.
     */
    private boolean haltOnFailure = false;

    /**
     * The corresponding testsuite.
     */
    private Test suite = null;

    /**
     * Exception caught in constructor.
     */
    private Exception exception;

    /**
     * Returncode
     */
    private int retCode = SUCCESS;

    /**
     * The TestSuite we are currently running.
     */
    private JUnitTest junitTest;

    /**
     * Constructor for fork=true or when the user hasn't specified a
     * classpath.  
     */
    public JUnitTestRunner(JUnitTest test, boolean haltOnError,
                           boolean haltOnFailure) {
        this(test, haltOnError, haltOnFailure, null);
    }

    /**
     * Constructor to use when the user has specified a classpath.
     */
    public JUnitTestRunner(JUnitTest test, boolean haltOnError,
                           boolean haltOnFailure, ClassLoader loader) {
        this.junitTest = test;
        this.haltOnError = haltOnError;
        this.haltOnFailure = haltOnFailure;

        try {
            Class testClass = null;
            if (loader == null) {
                testClass = Class.forName(test.getName());
            } else {
                testClass = loader.loadClass(test.getName());
            }
            
            try {
                Method suiteMethod= testClass.getMethod("suite", new Class[0]);
                suite = (Test)suiteMethod.invoke(null, new Class[0]);
            } catch(NoSuchMethodException e) {
            } catch(InvocationTargetException e) {
            } catch(IllegalAccessException e) {
            }
            
            if (suite == null) {
                // try to extract a test suite automatically
                // this will generate warnings if the class is no suitable Test
                suite= new TestSuite(testClass);
            }
            
        } catch(Exception e) {
            retCode = ERRORS;
            exception = e;
        }
    }

    public void run() {
        res = new TestResult();
        res.addListener(this);
        for (int i=0; i < formatters.size(); i++) {
            res.addListener((TestListener)formatters.elementAt(i));
        }

        long start = System.currentTimeMillis();

        fireStartTestSuite();
        if (exception != null) { // had an exception in the constructor
            for (int i=0; i < formatters.size(); i++) {
                ((TestListener)formatters.elementAt(i)).addError(null, 
                                                                 exception);
            }
            junitTest.setCounts(1, 0, 1);
            junitTest.setRunTime(0);
        } else {
            suite.run(res);
            junitTest.setCounts(res.runCount(), res.failureCount(), 
                                res.errorCount());
            junitTest.setRunTime(System.currentTimeMillis() - start);
        }
        fireEndTestSuite();

        if (retCode != SUCCESS || res.errorCount() != 0) {
            retCode = ERRORS;
        } else if (res.failureCount() != 0) {
            retCode = FAILURES;
        }
    }

    /**
     * Returns what System.exit() would return in the standalone version.
     *
     * @return 2 if errors occurred, 1 if tests failed else 0.
     */
    public int getRetCode() {
        return retCode;
    }

    /**
     * Interface TestListener.
     *
     * <p>A new Test is started.
     */
    public void startTest(Test t) {}

    /**
     * Interface TestListener.
     *
     * <p>A Test is finished.
     */
    public void endTest(Test test) {}

    /**
     * Interface TestListener.
     *
     * <p>A Test failed.
     */
    public void addFailure(Test test, Throwable t) {
        if (haltOnFailure) {
            res.stop();
        }
    }

    /**
     * Interface TestListener.
     *
     * <p>An error occured while running the test.
     */
    public void addError(Test test, Throwable t) {
        if (haltOnError) {
            res.stop();
        }
    }

    private void fireStartTestSuite() {
        for (int i=0; i<formatters.size(); i++) {
            ((JUnitResultFormatter)formatters.elementAt(i)).startTestSuite(junitTest);
        }
    }

    private void fireEndTestSuite() {
        for (int i=0; i<formatters.size(); i++) {
            ((JUnitResultFormatter)formatters.elementAt(i)).endTestSuite(junitTest);
        }
    }

    public void addFormatter(JUnitResultFormatter f) {
        formatters.addElement(f);
    }

    /**
     * Entry point for standalone (forked) mode.
     *
     * Parameters: testcaseclassname plus parameters in the format
     * key=value, none of which is required.
     *
     * <table cols="4" border="1">
     * <tr><th>key</th><th>description</th><th>default value</th></tr>
     *
     * <tr><td>haltOnError</td><td>halt test on
     * errors?</td><td>false</td></tr>
     *
     * <tr><td>haltOnFailure</td><td>halt test on
     * failures?</td><td>false</td></tr>
     *
     * <tr><td>formatter</td><td>A JUnitResultFormatter given as
     * classname,filename. If filename is ommitted, System.out is
     * assumed.</td><td>none</td></tr>
     *
     * </table> 
     */
    public static void main(String[] args) throws IOException {
        boolean exitAtEnd = true;
        boolean haltError = false;
        boolean haltFail = false;

        if (args.length == 0) {
            System.err.println("required argument TestClassName missing");
            System.exit(ERRORS);
        }

        for (int i=1; i<args.length; i++) {
            if (args[i].startsWith("haltOnError=")) {
                haltError = Project.toBoolean(args[i].substring(12));
            } else if (args[i].startsWith("haltOnFailure=")) {
                haltFail = Project.toBoolean(args[i].substring(14));
            } else if (args[i].startsWith("formatter=")) {
                try {
                    createAndStoreFormatter(args[i].substring(10));
                } catch (BuildException be) {
                    System.err.println(be.getMessage());
                    System.exit(ERRORS);
                }
            }
        }
        
        JUnitTest t = new JUnitTest(args[0]);
        JUnitTestRunner runner = new JUnitTestRunner(t, haltError, haltFail);
        transferFormatters(runner);
        runner.run();
        System.exit(runner.getRetCode());
    }

    private static Vector fromCmdLine = new Vector();

    private static void transferFormatters(JUnitTestRunner runner) {
        for (int i=0; i<fromCmdLine.size(); i++) {
            runner.addFormatter((JUnitResultFormatter) fromCmdLine.elementAt(i));
        }
    }

    private static void createAndStoreFormatter(String line) 
        throws BuildException {

        FormatterElement fe = new FormatterElement();
        StringTokenizer tok = new StringTokenizer(line, ",");
        fe.setClassname(tok.nextToken());
        if (tok.hasMoreTokens()) {
            fe.setOutfile(new java.io.File(tok.nextToken()));
        }
        fromCmdLine.addElement(fe.createFormatter());
    }

} // JUnitTestRunner
