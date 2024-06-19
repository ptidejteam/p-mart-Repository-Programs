/*
 * AllJUnitTests.java
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on May 2, 2001, 9:25 AM
 */

package test.pcgen.gui;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestSuite that is composed of the individual test classes.  Pick up
 * all the individual PCGen GUI test cases into this one.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 * @see <a
 * href="http://www-106.ibm.com/developerworks/library/j-ant/?dwzone=java">Incremental
 * development with Ant and JUnit</a>
 */
public class AllJUnitTests
	extends TestCase
{
	public AllJUnitTests(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite();
// suite.addTest (new TestSuite (BootTest.class));

		return suite;
	}
}
