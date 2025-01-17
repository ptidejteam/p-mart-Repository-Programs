/*
 *  EmptyIterator.java
 *  Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * $Id: ResetableListIterator.java,v 1.1 2006/02/21 01:13:27 vauchers Exp $
 */

package pcgen.util;

import java.util.ListIterator;

/**
 * An iterator which may be reset.
 *
 * @author B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 */
public interface ResetableListIterator extends ListIterator
{
	/**
	 * Return iterator to original state after construction.
	 */
	void reset();
}
