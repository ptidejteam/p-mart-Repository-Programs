package junit.tests;

import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Test an implementor of junit.framework.Test other than TestCase or TestSuite
 */
public class TestImplementorTest extends TestCase {
	public static class DoubleTestCase implements Test {
		private TestCase fTestCase;
		
		public DoubleTestCase(TestCase testCase) {
			this.fTestCase= testCase;
		}
		
		public int countTestCases() {
			return 2;
		}
		
		public void run(TestResult result) {
			result.startTest(this);
			Protectable p= new Protectable() {
				public void protect() throws Throwable {
					DoubleTestCase.this.fTestCase.runBare();
					DoubleTestCase.this.fTestCase.runBare();
				}
			};
			result.runProtected(this, p);
			result.endTest(this);
		}
	}
	
	private DoubleTestCase fTest;
	
	public TestImplementorTest(String name) {
		super(name);
		TestCase testCase= new TestCase("noop") {
			public void runTest() {
			}
		};
		this.fTest= new DoubleTestCase(testCase);
	}
	
	public void testSuccessfulRun() {
		TestResult result= new TestResult();
		this.fTest.run(result);
		assertEquals(this.fTest.countTestCases(), result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
}
