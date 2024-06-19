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

public interface FilterConstants
{
	int DISABLED_MODE = -2;
	int DEMO_MODE = -1;
	int SINGLE_SINGLE_MODE = 0;
	int SINGLE_MULTI_MODE = 1;
	int MULTI_MULTI_MODE = 2;

	int MATCH_ALL = 0;
	int MATCH_ALL_NEGATE = 1;
	int MATCH_ANY = 2;
	int MATCH_ANY_NEGATE = 3;

	int BASE = 0;
	int MONSTER = 1;
	int NPC = 2;
	int PC = 3;
	int PRESTIGE = 4;

	String AND = "AND";
	String OR = "OR";

	String ALLOWED = "allowed";
	String DEFAULT = "default";
	String REQUIRED = "required";
}
