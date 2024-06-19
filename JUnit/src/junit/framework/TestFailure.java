package junit.framework;

/**
 * A <code>TestFailure</code> collects a failed test together with
 * the caught exception.
 * @see TestResult
 */
public class TestFailure extends Object {
	protected Test fFailedTest;
	protected Throwable fThrownException;

	/**
	 * Constructs a TestFailure with the given test and exception.
	 */
	public TestFailure(Test failedTest, Throwable thrownException) {
		this.fFailedTest= failedTest;
		this.fThrownException= thrownException;
	}
	/**
	 * Gets the failed test.
	 */
	public Test failedTest() {
	    return this.fFailedTest;
	}
	/**
	 * Gets the thrown exception.
	 */
	public Throwable thrownException() {
	    return this.fThrownException;
	}
	/**
	 * Returns a short description of the failure.
	 */
	public String toString() {
	    StringBuffer buffer= new StringBuffer();
	    buffer.append(this.fFailedTest+": "+this.fThrownException.getMessage());
	    return buffer.toString();
	}
}