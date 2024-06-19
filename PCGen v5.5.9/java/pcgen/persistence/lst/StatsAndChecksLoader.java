/*
 * StatsAndChecksLoader.java
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 * Created on October 13, 2003, 11:50 AM
 *
 * Current Ver: $Revision: 1.1 $ <br>
 * Last Editor: $Author: vauchers $ <br>
 * Last Edited: $Date: 2006/02/21 01:28:23 $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.List;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;

/**
 * This class is a LstFileLoader that processes the statsandchecks.lst file,
 * handing its multiple types of content off to the appropriate loader
 * for Attributes, Bonus Spells, Checks, and Alignments.
 *
 * @author AD9C15
 */
public class StatsAndChecksLoader extends LstLineFileLoader
{

	/**
	 * StatsAndChecksLoader Constructor.
	 *
	 */
	public StatsAndChecksLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstFileLoader#loadLstFile(java.lang.String)
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		// Clear relevant Globals
		Globals.setAttribLong(null);
		Globals.setAttribShort(null);
		Globals.setAttribRoll(null);
		SystemCollections.clearCheckList();
		SystemCollections.clearAlignmentList();
		SystemCollections.clearStatList();

		super.loadLstFile(fileName);

		// Reinit relevant globals from SystemCollections
		List statList = SystemCollections.getUnmodifiableStatList();
		int statCount = statList.size();
		Globals.setAttribLong( new String[statCount] );
		Globals.setAttribRoll( new boolean[statCount] );
		Globals.setAttribShort( new String[statCount] );
		for( int i=0; i<statCount; i++ )
		{
			PCStat stat = (PCStat) statList.get(i);
			Globals.setAttribLong(i, stat.getName());
			Globals.setAttribRoll(i, true);
			Globals.setAttribShort(i, stat.getAbb());
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		if (lstLine.startsWith("STATNAME:"))
		{
			statLoader.parseLine(lstLine, sourceURL);
		}
		else if (lstLine.startsWith("CHECKNAME:"))
		{
			checkLoader.parseLine(lstLine, sourceURL);
		}
		else if (lstLine.startsWith("BONUSSPELLLEVEL:"))
		{
			bonusSpellLoader.parseLine(lstLine, sourceURL);
		}
		else if (lstLine.startsWith("ALIGNMENTNAME:"))
		{
			alignmentLoader.parseLine(lstLine, sourceURL);
		}
	}

	private BonusSpellLoader bonusSpellLoader = new BonusSpellLoader();
	private PCStatLoader statLoader = new PCStatLoader();
	private PCCheckLoader checkLoader = new PCCheckLoader();
	private PCAlignmentLoader alignmentLoader = new PCAlignmentLoader();
}
