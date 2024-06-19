/*
 * UnitSetLoader.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on November 20, 2003
 *
 * $Id: UnitSetLoader.java,v 1.1 2006/02/21 01:28:23 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.UnitSet;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * <code>UnitSetLoader</code>.
 *
 * @author Stefan Radermacher <stefan@zaister.de>
 * @version $Revision: 1.1 $
 */

final class UnitSetLoader extends LstLineFileLoader
{

	/** Creates a new instance of UnitSetLoader */
	public UnitSetLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstFileLoader#parseLine(PObject, String, URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		final UnitSet units = new UnitSet();

		final StringTokenizer aTok = new StringTokenizer(lstLine, "\t");
		int iCount = 0;
		while (aTok.hasMoreElements())
		{
			final String colString = (String) aTok.nextElement();
			try
			{
				switch (iCount)
				{
					case 0:
						units.setName(colString);
						break;
					case 1:
						units.setHeightUnit(colString);
						break;
					case 2:
						units.setHeightFactor(Double.parseDouble(colString));
						break;
					case 3:
						units.setHeightDisplayPattern(colString);
						break;
					case 4:
						units.setDistanceUnit(colString);
						break;
					case 5:
						units.setDistanceFactor(Double.parseDouble(colString));
						break;
					case 6:
						units.setDistanceDisplayPattern(colString);
						break;
					case 7:
						units.setWeightUnit(colString);
						break;
					case 8:
						units.setWeightFactor(Double.parseDouble(colString));
						break;
					case 9:
						units.setWeightDisplayPattern(colString);
						break;
					default:
						Logging.errorPrint("Unexpected token '" + colString + "' in " + sourceURL.toString() );
						break;
				}
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Illegal unit set info '" + lstLine + "' in " + sourceURL.toString() );
			}
			iCount += 1;
		}
		SystemCollections.addToUnitSetList(units);
	}

	/**
	 * @see pcgen.persistence.lst.LstFileLoader#loadLstFile(String)
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		// initialize unitSetList with "Imperial" standard unit set
		final UnitSet units = new UnitSet();

		units.setName("Imperial");
		units.setHeightUnit("ftin"); // "ftin" is harcoded to translate to x'y"
		units.setHeightFactor(1.0);
		units.setHeightDisplayPattern("#.#");
		units.setDistanceUnit("~'"); // use of "~" as first character means the unit name is appended without a leading space
		units.setDistanceFactor(1.0);
		units.setDistanceDisplayPattern("#");
		units.setWeightUnit("lbs.");
		units.setWeightFactor(1.0);
		units.setWeightDisplayPattern("#.###");

		SystemCollections.clearUnitSetList();
		SystemCollections.addToUnitSetList(units);

		super.loadLstFile(fileName);
		Globals.selectUnitSet(SettingsHandler.getPCGenOption("unitSetName", "Imperial"));
	}
}
