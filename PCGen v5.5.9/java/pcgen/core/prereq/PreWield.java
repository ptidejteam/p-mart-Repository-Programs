/*
 * PreWield.java
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

import java.util.StringTokenizer;

import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;


/**
 * @author jayme cox <jaymecox@users.sourceforge.net>
 *
 */
public class PreWield
	extends Prerequisite
	implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		boolean flag;

		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		flag = false;
		int numberRequired = -1;
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (numberRequired == -1)
			{
				try
				{
					numberRequired = Integer.parseInt(aString);
					aString = aTok.nextToken();
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Syntax error: PREWIELD: " + getParameters());
				}
			}
			PObject anObj = getTheObj();
			Equipment eq = null;
			if (anObj instanceof Equipment)
			{
				eq = (Equipment) anObj;
			}
			else
			{
				return false;
			}
			if (eq.getWield().equalsIgnoreCase(aString))
			{
				--numberRequired;
			}
			if (numberRequired <= 0)
			{
				return true;
			}
		}
		return false;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"WIELD"};
	}

}
