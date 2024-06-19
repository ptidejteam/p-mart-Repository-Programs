package junit.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * A fixture for testing the "auto" test suite feature.
 *
 */
public class SuiteTest extends TestCase {
	protected TestResult fResult;
	public SuiteTest(String name) {
		super(name);
	}
	protected void setUp() {
		this.fResult= new TestResult();
	}
	public static Test suite() {
		TestSuite suite= new TestSuite("Suite Tests");
		// build the suite manually
		suite.addTest(new SuiteTest("testNoTestCaseClass"));
		suite.addTest(new SuiteTest("testNoTestCases"));
		suite.addTest(new SuiteTest("testOneTestCase"));
		suite.addTest(new SuiteTest("testNotPublicTestCase"));
		suite.addTest(new SuiteTest("testNotVoidTestCase"));
		suite.addTest(new SuiteTest("testNotExistingTestCase"));
		suite.addTest(new SuiteTest("testInheritedTests"));
		suite.addTest(new SuiteTest("testShadowedTests"));
		suite.addTest(new SuiteTest("testAddTestSuite"));
		
		return suite;
	}
	public void testInheritedTests() {
		TestSuite suite= new TestSuite(InheritedTestCase.class);
		suite.run(this.fResult);
		assertTrue(this.fResult.wasSuccessful());
		assertEquals(2, this.fResult.runCount());
	}
	public void testNoTestCaseClass() {
		Test t= new TestSuite(NoTestCaseClass.class);
		t.run(this.fResult);
		assertEquals(1, this.fResult.runCount());  // warning test
		assertTrue(! this.fResult.wasSuccessful());
	}
	public void testNoTestCases() {
		Test t= new TestSuite(NoTestCases.class);
		t.run(this.fResult);
		assertTrue(this.fResult.runCount() == 1);  // warning test
		assertTrue(this.fResult.failureCount() == 1);
		assertTrue(! this.fResult.wasSuccessful());
	}
	public void testNotExistingTestCase() {
		Test t= new SuiteTest("notExistingMethod");
		t.run(this.fResult);
		assertTrue(this.fResult.runCount() == 1);  
		assertTrue(this.fResult.failureCount() == 1);
		assertTrue(this.fResult.errorCount() == 0);
	}
	public void testNotPublicTestCase() {
		TestSuite suite= new TestSuite(NotPublicTestCase.class);
		// 1 public test case + 1 warning for the non-public test case
		assertEquals(2, suite.countTestCases());
	}
	public void testNotVoidTestCase() {
		TestSuite suite= new TestSuite(NotVoidTestCase.class);
		assertTrue(suite.countTestCases() == 1);
	}
	public void testOneTestCase() {
		Test t= new TestSuite(OneTestCase.class);
		t.run(this.fResult);
		assertTrue(this.fResult.runCount() == 1);  
		assertTrue(this.fResult.failureCount() == 0);
		assertTrue(this.fResult.errorCount() == 0);
		assertTrue(this.fResult.wasSuccessful());
	}
	public void testShadowedTests() {
		TestSuite suite= new TestSuite(OverrideTestCase.class);
		suite.run(this.fResult);
		assertEquals(1, this.fResult.runCount());
	}
	public void testAddTestSuite() {
		TestSuite suite= new TestSuite();
		suite.addTestSuite(OneTestCase.class);
		suite.run(this.fResult);
		assertEquals(1, this.fResult.runCount());
	}
}