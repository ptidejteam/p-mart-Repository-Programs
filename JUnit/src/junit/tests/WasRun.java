package junit.tests;

import junit.framework.TestCase;

/**
 * A helper test case for testing whether the testing method
 * is run.
 */
class WasRun extends TestCase {
	boolean fWasRun= false;
		WasRun(String name) {
			super(name);
		}
		protected void runTest() {
			this.fWasRun= true;
		}
}