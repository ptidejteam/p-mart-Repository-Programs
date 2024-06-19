/*
 * FilterConstants.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 9, 2002, 2:30 PM
 */
package pcgen.gui.filter;

/**
 * <code>FilterConstants</code>
 *
 * @author Add author here
 * @version $Revision: 1.1 $
 */

interface FilterConstants
{
	public static final int DISABLED_MODE = -2;
	public static final int DEMO_MODE = -1;
	public static final int SINGLE_SINGLE_MODE = 0;
	public static final int SINGLE_MULTI_MODE = 1;
	public static final int MULTI_MULTI_MODE = 2;

	public static final int MATCH_ALL = 0;
	public static final int MATCH_ALL_NEGATE = 1;
	public static final int MATCH_ANY = 2;
	public static final int MATCH_ANY_NEGATE = 3;

	public static final int BASE = 0;
	public static final int MONSTER = 1;
	public static final int NPC = 2;
	public static final int PC = 3;
	public static final int PRESTIGE = 4;

	public static final String AND = "AND";
	public static final String OR = "OR";

	public static final String ALLOWED = "allowed";
	public static final String DEFAULT = "default";
	public static final String REQUIRED = "required";
}
