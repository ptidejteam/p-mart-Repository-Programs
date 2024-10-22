/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.slick.slickless;

import java.util.*;

import org.osgi.framework.*;

import junit.framework.*;

import net.java.sip.communicator.util.*;


/**
 * Runs all unit tests that do not belong to any SLICK.
 *
 * @author Emil Ivov
 */
public class SlicklessTests
    extends TestSuite
    implements BundleActivator
{
    private Logger logger = Logger.getLogger(getClass().getName());

    protected static BundleContext bc = null;

    /**
     * The name of the property that contains the list of standalone
     * test classes that we need to run.
     */
    private static final String TEST_LIST_PROPERTY_NAME =
        "net.java.sip.communicator.slick.runner.SLICKLESS_TEST_LIST";

    /**
     * Start the Configuration Sevice Implementation Compatibility Kit.
     *
     * @param bundleContext BundleContext
     * @throws Exception
     */
    public void start(BundleContext bundleContext) throws Exception
    {
        bc = bundleContext;
        setName("SlicklessTests");
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put("service.pid", getName());

        addTest(createSuite());
        bundleContext.registerService(getClass().getName(), this, properties);

        logger.debug("Successfully registered " + getClass().getName());
    }

    /**
     * stop
     *
     * @param bundlecontext BundleContext
     * @throws Exception
     */
    public void stop(BundleContext bundlecontext) throws Exception
    {
    }
    
    /**
     * allow these tests to be run directly under jUnit
     */
     public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        SlicklessTests tests = new SlicklessTests();
        return tests.createSuite();
    }

    /**
     * collect all the slickless tests for running, either under felix
     * or simply under jUnit
     */
    private Test createSuite()
    {
        //Let's discover what tests have been scheduled for execution.
        // (we expect a list of fully qualified test class names)
        String tests = System.getProperty(TEST_LIST_PROPERTY_NAME);
        if (tests == null || tests.trim().length() == 0)
        {
            tests = "";
        }
        logger.debug("specfied test list is: " + tests);

        StringTokenizer st = new StringTokenizer(tests);
        String[] ids = new String[st.countTokens()];
        int n = 0;
        while (st.hasMoreTokens())
        {
            ids[n++] = st.nextToken().trim();
        }

        TestSuite suite = new TestSuite();
        for (int i=0; i<n; i++)
        {
            String testName = ids[i];
            if (testName != null && testName.trim().length() > 0)
            {
                try
                {
                    Class<?> testClass = Class.forName(testName);
                    if ((bc == null)
                            && BundleActivator.class.isAssignableFrom(testClass))
                    {
                        logger.error("test " + testName
                                + " skipped - it must run under felix");
                    }
                    else
                    {
                        suite.addTest(new TestSuite(testClass));
                    }
                }
                catch (ClassNotFoundException e)
                {
                    logger.error("Failed to load standalone test " + testName);
                }
            }
        }
        return suite;
    }
}
