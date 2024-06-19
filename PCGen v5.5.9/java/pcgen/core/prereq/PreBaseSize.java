/*
 * PreBaseSize.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:55 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.*;
import pcgen.util.*;

/**
 * @author wardc
 *
 */
public class PreBaseSize extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		boolean flag = false;
		if ((character.getRace() != null) && !character.getRace().equals(Globals.s_EMPTYRACE))
		{
			String comparator = getKind().substring(8);
			final int targetSize = Globals.sizeInt(getParameters(), -1);
			if (targetSize < 0)
			{
				Logging.errorPrint("Invalid size '" + getParameters() + "' in PREBASESIZE");
			}
			else
			{
				flag = PrereqHandler.doComparison(comparator, character.racialSizeInt(), targetSize);
			}
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[] {"BASESIZEEQ", "BASESIZEGT", "BASESIZEGTEQ", "BASESIZELT", "BASESIZELTEQ", "BASESIZENEQ"};
	}

}
