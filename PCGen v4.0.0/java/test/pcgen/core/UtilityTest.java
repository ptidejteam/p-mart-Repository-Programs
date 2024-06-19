/*
 * UtilityTest.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author Pat Ludwig <havoc@boldo.com>
 * Created on May 20th, 2002
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:47:22 $
 */

package test.pcgen.core;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class UtilityTest extends TestCase
{

	public void testUnSplitString()
	{
		final String sep = "|";
		List list = constructList();
		final String result = pcgen.core.Utility.unSplit(list, sep);
		final String trueResult = "one|two|three|four";
		assertTrue("unSplit returned bad String: got '" + result +
			"' should be '" + trueResult + "'", trueResult.equals(result));
	}

	public void testUnSplitChar()
	{
		final char sep = ',';
		List list = constructList();
		final String result = pcgen.core.Utility.unSplit(list, sep);
		final String trueResult = "one, two, three, four";
		assertTrue("unSplit returned bad String: got '" + result +
			"' should be '" + trueResult + "'", trueResult.equals(result));
	}

	public void testCommaDelimit()
	{
		List list = constructList();
		final String result = pcgen.core.Utility.commaDelimit(list);
		final String trueResult = "one, two, three, four";
		assertTrue("commaDelimit returned bad String: got '" + result +
			"' should be '" + trueResult + "'", trueResult.equals(result));
	}

	private List constructList()
	{
		List list = new ArrayList();
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");
		return list;
	}

	public UtilityTest(String name)
	{
		super(name);
	}
}
