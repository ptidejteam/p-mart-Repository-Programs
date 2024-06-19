package junit.tests;

/**
 * Test class used in SuiteTest
 */
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;

public class TestListenerTest extends TestCase implements TestListener {
	private TestResult fResult;
	private int fStartCount;
	private int fEndCount;
	private int fFailureCount;
	private int fErrorCount;
	public TestListenerTest(String name) {
		super(name);
	}
	public void addError(Test test, Throwable t) {
		this.fErrorCount++;
	}
	public void addFailure(Test test, AssertionFailedError t) {
		this.fFailureCount++;
	}
	public void endTest(Test test) {
		this.fEndCount++;
	}
	protected void setUp() {
		this.fResult= new TestResult();
		this.fResult.addListener(this);
	
		this.fStartCount= 0;
		this.fEndCount= 0;
		this.fFailureCount= 0;
	}
	public void startTest(Test test) {
		this.fStartCount++;
	}
	public void testError() {
		TestCase test= new TestCase("noop") {
			public void runTest() {
				throw new Error();
			}
		};
		test.run(this.fResult);
		assertEquals(1, this.fErrorCount);
		assertEquals(1, this.fEndCount);
	}
	public void testFailure() {
		TestCase test= new TestCase("noop") {
			public void runTest() {
				fail();
			}
		};
		test.run(this.fResult);
		assertEquals(1, this.fFailureCount);
		assertEquals(1, this.fEndCount);
	}
	public void testStartStop() {
		TestCase test= new TestCase("noop") {
			public void runTest() {
			}
		};
		test.run(this.fResult);
		assertEquals(1, this.fStartCount);
		assertEquals(1, this.fEndCount);
	}
}