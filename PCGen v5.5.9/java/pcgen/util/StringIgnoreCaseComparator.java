/*
 * StringIgnoreCaseComparator.java
 * Copyright 2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on July 30, 2003, 8:34 AM
 */

package pcgen.util;

import java.util.Comparator;

/**
 * A <code>Comparator</code> to compare objects as
 * <code>String</code>s ignoring case.  This is particularly useful
 * for applications such as maintaining a sorted
 * <code>JComboBoxEx</code> and the like.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 1.1 $
 */
public final class StringIgnoreCaseComparator
	implements Comparator
{
	/** Constructs a <code>StringIgnoreCaseComparator</code>. */
	public StringIgnoreCaseComparator()
	{
	}

	/** {@inheritDoc} */
	public int compare(Object o1, Object o2)
	{
		// Treat null as the empty string.
		return (o1 == null ? "" : o1.toString()).compareToIgnoreCase(o2 == null ? "" : o2.toString());
	}
}
