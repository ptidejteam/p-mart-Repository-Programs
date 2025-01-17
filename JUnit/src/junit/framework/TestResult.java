package junit.framework;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A <code>TestResult</code> collects the results of executing
 * a test case. It is an instance of the Collecting Parameter pattern.
 * The test framework distinguishes between <i>failures</i> and <i>errors</i>.
 * A failure is anticipated and checked for with assertions. Errors are
 * unanticipated problems like an <code>ArrayIndexOutOfBoundsException</code>.
 *
 * @see Test
 */
public class TestResult extends Object {
	protected Vector fFailures;
	protected Vector fErrors;
	protected Vector fListeners;
	protected int fRunTests;
	private boolean fStop;
	
	public TestResult() {
		this.fFailures= new Vector();
		this.fErrors= new Vector();
		this.fListeners= new Vector();
		this.fRunTests= 0;
		this.fStop= false;
	}
	/**
	 * Adds an error to the list of errors. The passed in exception
	 * caused the error.
	 */
	public synchronized void addError(Test test, Throwable t) {
		this.fErrors.addElement(new TestFailure(test, t));
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).addError(test, t);
		}
	}
	/**
	 * Adds a failure to the list of failures. The passed in exception
	 * caused the failure.
	 */
	public synchronized void addFailure(Test test, AssertionFailedError t) {
		this.fFailures.addElement(new TestFailure(test, t));
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).addFailure(test, t);
		}
	}
	/**
	 * Registers a TestListener
	 */
	public synchronized void addListener(TestListener listener) {
		this.fListeners.addElement(listener);
	}
	/**
	 * Unregisters a TestListener
	 */
	public synchronized void removeListener(TestListener listener) {
		this.fListeners.removeElement(listener);
	}
	/**
	 * Returns a copy of the listeners.
	 */
	private synchronized Vector cloneListeners() {
		return (Vector)this.fListeners.clone();
	}
	/**
	 * Informs the result that a test was completed.
	 */
	public void endTest(Test test) {
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).endTest(test);
		}
	}
	/**
	 * Gets the number of detected errors.
	 */
	public synchronized int errorCount() {
		return this.fErrors.size();
	}
	/**
	 * Returns an Enumeration for the errors
	 */
	public synchronized Enumeration errors() {
		return this.fErrors.elements();
	}
	/**
	 * Gets the number of detected failures.
	 */
	public synchronized int failureCount() {
		return this.fFailures.size();
	}
	/**
	 * Returns an Enumeration for the failures
	 */
	public synchronized Enumeration failures() {
		return this.fFailures.elements();
	}
	/**
	 * Runs a TestCase.
	 */
	protected void run(final TestCase test) {
		startTest(test);
		Protectable p= new Protectable() {
			public void protect() throws Throwable {
				test.runBare();
			}
		};
		runProtected(test, p);

		endTest(test);
	}
	/**
	 * Gets the number of run tests.
	 */
	public synchronized int runCount() {
		return this.fRunTests;
	}
	/**
	 * Runs a TestCase.
	 */
	public void runProtected(final Test test, Protectable p) {
		try {
			p.protect();
		} 
		catch (AssertionFailedError e) {
			addFailure(test, e);
		}
		catch (ThreadDeath e) { // don't catch ThreadDeath by accident
			throw e;
		}
		catch (Throwable e) {
			addError(test, e);
		}
	}
	/**
	 * Gets the number of run tests.
	 * @deprecated use <code>runCount</code> instead
	 */
	public synchronized int runTests() {
		return runCount();
	}
	/**
	 * Checks whether the test run should stop
	 */
	public synchronized boolean shouldStop() {
		return this.fStop;
	}
	/**
	 * Informs the result that a test will be started.
	 */
	public void startTest(Test test) {
		final int count= test.countTestCases();
		synchronized(this) {
			this.fRunTests+= count;
		}
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).startTest(test);
		}
	}
	/**
	 * Marks that the test run should stop.
	 */
	public synchronized void stop() {
		this.fStop= true;
	}
	/**
	 * Gets the number of detected errors.
	 * @deprecated use <code>errorCount</code> instead
	 */
	public synchronized int testErrors() {
		return errorCount();
	}
	/**
	 * Gets the number of detected failures.
	 * @deprecated use <code>failureCount</code> instead
	 */
	public synchronized int testFailures() {
		return failureCount();
	}
	/**
	 * Returns whether the entire test was successful or not.
	 */
	public synchronized boolean wasSuccessful() {
		return testFailures() == 0 && testErrors() == 0;
	}
}