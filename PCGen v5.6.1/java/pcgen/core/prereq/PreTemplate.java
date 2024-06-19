/*
 * PreTemplate.java
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
 * Last Edited: $Date: 2006/02/21 01:33:20 $
 *
 */
package pcgen.core.prereq;

import java.util.Iterator;
import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;


/**
 * @author wardc
 *
 */
public class PreTemplate extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		if (character.getTemplateList().isEmpty())
		{
			return false;
		}

		final StringTokenizer aTok = new StringTokenizer(getParameters(), "|");
		while (aTok.hasMoreTokens())
		{
			String templateName = aTok.nextToken().toUpperCase();
			final int wildCard = templateName.indexOf('%');
			//handle wildcards (always assume they end the line)
			if (wildCard >= 0)
			{
				templateName = templateName.substring(0, wildCard);
				for (Iterator ti = character.getTemplateList().iterator(); ti.hasNext();)
				{
					final PCTemplate aTemplate = (PCTemplate) ti.next();
					if (aTemplate.getName().toUpperCase().startsWith(templateName))
					{
						return true;
					}
				}
			}
			else if (character.getTemplateNamed(templateName) != null)
			{
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"TEMPLATE"};
	}

}
