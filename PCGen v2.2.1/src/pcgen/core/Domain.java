/*
 * Domain.java
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * <code>Domain</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Domain extends PObject
{
	private String grantedPower = new String();
	private String skillList = new String();
	private String featList = new String();
	private String spellList = new String();
	private boolean isLocked = false;

	/**
	 * @return the name of this domain
	 */
	public String toString()
	{
		return name;
	}

	private void setGrantedPower(String aString)
	{
		grantedPower = aString;
	}

	public String getGrantedPower()
	{
		return grantedPower;
	}

	public String skillList()
	{
		return skillList;
	}

	private void setSkillList(String aString)
	{
		skillList = aString;
	}

	public String getPreReqString()
	{
		return preReqStrings();
	}

	public boolean isLocked()
	{
		return isLocked;
	}

	public void setIsLocked(boolean aBool)
	{
		if (isLocked == aBool)
			return;
		isLocked = aBool;
		if (aBool)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (getSpellList().length() > 0)
			{
				final StringTokenizer aTok = new StringTokenizer(getSpellList(), ",", false);
				final Collection clericSpells = aPC.getClassNamed("Cleric").spellList();
				while (aTok.hasMoreTokens())
				{
					final String spellName = aTok.nextToken();
					final Spell aSpell = (Spell)Globals.getSpellMap().get(spellName);
					if (aSpell != null)
					{
						final Spell bSpell = (Spell)aSpell.clone();
						bSpell.setClassLevels("Cleric,1");
						clericSpells.add(bSpell);
					}
/*
					for (Iterator e = Globals.getSpellList().iterator(); e.hasNext();)
					{
						Spell aSpell = (Spell)e.next();
						if (aSpell.getName().equals(aString))
						{
							Spell bSpell = (Spell)aSpell.clone();
							bSpell.setClassLevels("Cleric,1");
							aPC.getClassNamed("Cleric").spellList().add(bSpell);
							break;
						}
					}
					*/
				}
			}
			if (aPC != null && getFeatList().length() > 0)
				aPC.modFeatsFromList(featList, aBool, false);
			if (choiceString.length() > 0)
				getChoices(choiceString, new ArrayList());
		}
	}

	public String getFeatList()
	{
		return featList;
	}

	private void setFeatList(String aString)
	{
		featList = aString;
	}

	public String getSpellList()
	{
		return spellList;
	}

	private void setSpellList(String aString)
	{
		spellList = aString;
	}

	public boolean hasSkill(String skillName)
	{
		final StringTokenizer aTok = new StringTokenizer(skillList, ",", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.equals(skillName) || skillName.startsWith(aString))
				return true;
		}
		return false;
	}

	public boolean hasFeat(String featName)
	{
		final StringTokenizer aTok = new StringTokenizer(featList, ",", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.equals(featName))
				return true;
		}
		return false;
	}

	public boolean qualifiesForDomain()
	{
		return passesPreReqTests();
	}

	/**
	 * Parses a line from an lst file
	 * @param String inputLine
	 * @param File sourceFile
	 * @param int lineNum
	 */
	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		int colMax = colToken.countTokens();
		if (colMax == 0)
			return;
		for (int col = 0; col < colMax; col++)
		{
			final String aString = colToken.nextToken();
			if (super.parseTag(aString))
				continue;
			final int aLen = aString.length();
			if (col == 0)
				setName(aString);
			else if (col == 1)
				setGrantedPower(aString);
			else if ((aLen > 6) && aString.startsWith("SKILL"))
				setSkillList(aString.substring(6));
			else if ((aLen > 5) && aString.startsWith("FEAT"))
				setFeatList(aString.substring(5));
			else if ((aLen > 6) && aString.startsWith("SPELL"))
				setSpellList(aString.substring(6));
			else if ((aLen > 4) && aString.startsWith("KEY:"))
				setKeyName(aString.substring(4));
			else if ((aLen > 7) && aString.startsWith("CHOOSE:"))
				choiceString = aString.substring(7);
			else if (aString.startsWith("PRE"))
				preReqArrayList.add(aString);
			else if (aString.startsWith("QUALIFY:"))
				addToQualifyListing(aString.substring(8));
			else
				JOptionPane.showMessageDialog
					(null, "Illegal domain info " +
					sourceFile.getName() + ":" +
					Integer.toString(lineNum) + " \"" + aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
		}
	}
}
