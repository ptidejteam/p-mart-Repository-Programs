package junit.extensions;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * A TestSuite for active Tests. It runs each
 * test in a separate thread and waits until all
 * threads have terminated.
 * -- Aarhus Radisson Scandinavian Center 11th floor
 */ 
public class ActiveTestSuite extends TestSuite {
	private volatile int fActiveTestDeathCount;
	
	public void run(TestResult result) {
		this.fActiveTestDeathCount= 0;
		super.run(result);
		waitUntilFinished();
	}
	
	public void runTest(final Test test, final TestResult result) {
		Thread t= new Thread() {
			public void run() {
				try {
					// inlined due to limitation in VA/Java 
					//ActiveTestSuite.super.runTest(test, result);
					test.run(result);
				} finally {
					ActiveTestSuite.this.runFinished(test);
				}
			}
		};
		t.start();
	}

	synchronized void waitUntilFinished() {
		while (this.fActiveTestDeathCount < testCount()) {
			try {
				wait();
			} catch (InterruptedException e) {
				return; // ignore
			}
		}
	}
	
	synchronized public void runFinished(Test test) {
		this.fActiveTestDeathCount++;
		notifyAll();
	}
}