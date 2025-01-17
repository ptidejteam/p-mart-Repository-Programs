/*
 * @(#)Test.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.test.samples.pert;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:mtnygard@charter.net">Michael T. Nygard</a>
 * @version $Revision: 1.2 $
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for CH.ifa.draw.test.samples.pert");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(PertAppletTest.class));
		suite.addTest(new TestSuite(PertApplicationTest.class));
		suite.addTest(new TestSuite(PertDependencyTest.class));
		suite.addTest(new TestSuite(PertFigureCreationToolTest.class));
		suite.addTest(new TestSuite(PertFigureTest.class));
		//$JUnit-END$
		return suite;
	}
}
