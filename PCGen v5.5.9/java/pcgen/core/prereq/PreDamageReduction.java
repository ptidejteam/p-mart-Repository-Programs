/*
 * PreDamageReduction.java
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
import java.util.*;
import pcgen.util.*;

/**
 * @author wardc
 *
 */
public class PreDamageReduction
	extends Prerequisite
	implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		/*
		 * 1,
		 */
		
		final StringTokenizer inputTokenizer = new StringTokenizer(getParameters(), ",");

		// the number of DRs which must match
		int number;
		String tok = inputTokenizer.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Badly formed passesPreDR/number of DRs attribute: " + tok);
			number = 1;
		}
		
		
		// Parse the character's DR into a lookup map
		final String aDR = character.calcDR(); // Silver/10;Good/5;Magic/15
		Map drMap = new HashMap();
		if (aDR != null)
		{
			final StringTokenizer characterDRTokenizer = new StringTokenizer(aDR, ";");
			while (characterDRTokenizer.hasMoreTokens())
			{
				final StringTokenizer drTokenizer = new StringTokenizer(characterDRTokenizer.nextToken(), "/");
				final String aValue = drTokenizer.nextToken();
				final String aType = drTokenizer.nextToken();
				drMap.put(aType, aValue);
			}
		}
		
		
		// Parse all of the tokens in the input list
		while (inputTokenizer.hasMoreTokens())
		{
			final StringTokenizer inputDRTokenizer = new StringTokenizer(inputTokenizer.nextToken(), "=.");
			final String bType = inputDRTokenizer.nextToken(); // either Good.10 or Good=10
			final int bValue;
			if (inputDRTokenizer.hasMoreTokens())
			{
				try
				{
					bValue = Integer.parseInt(inputDRTokenizer.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Badly formed passesPreDR value: " + getParameters());
					return false;
				}
			}
			else
			{
				bValue = 0;
			}
			if (drMap.get(bType) != null)
			{
				if (Integer.parseInt((String) drMap.get(bType)) >= bValue)
				{
					--number;
					if (number == 0)
					{
						return true;
					}
				}
			}
		}
		return false;	
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"DR"};
	}

}
