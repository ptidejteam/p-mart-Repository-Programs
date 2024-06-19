/*
 * AnyInputStreamTest.java
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
 * Created on April 21, 2001, 2:15 PM
 */

package test.pcgen;

import junit.framework.TestCase;

/**
 * JUnit 3.6 testcases for <code>pcgen.AnyInputStream</code>.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 */
public class AnyInputStreamTest
	extends TestCase
{
	public AnyInputStreamTest(String name)
	{
		super(name);
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(AnyInputStreamTest.class);
	}


	public void testCreateLocation()
	{
		/*
try {
		assert (AnyInputStream.createLocation ("pcgen.ini")
			!= null);
}

catch (IOException ioe) {
		assert (false);
}
*/
	}

	public void testCreateLocationFromJar()
	{
		/*
try {
		assert (AnyInputStream.createLocationFromJar
			("META-INF/MANIFEST.MF")
			!= null);
}

catch (IOException ioe) {
		assert (false);
}     */
	}
}
