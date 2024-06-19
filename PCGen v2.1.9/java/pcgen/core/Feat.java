/*
 * @(#) $Id: Feat.java,v 1.1 2006/02/20 21:45:59 vauchers Exp $
 *
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
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.gui.Chooser;

/**
 * Definition and games rules for a Feat.
 *
 * @version $Revision: 1.1 $
 */
public class Feat extends PObject
{
	///////////////////////////////////////
	// Fields - Attributes

	private Integer levelsPerRepIncrease = new Integer(0);

	private int visible = 1; // Hidden Feats

	private String description = new String();
	private String type = new String();
	private boolean stacks = false;
	private boolean multiples = false;
	private String addString = new String();
	private ArrayList cSkillList = new ArrayList();
	private ArrayList ccSkillList = new ArrayList();
	private int addSpellLevel = 0;
	private double cost = 1.0;
	private ArrayList skillNameList = new ArrayList(); // of String

	///////////////////////////////////////
	// Fields - Associations

	/* no associations */

	///////////////////////////////////////
	// Constructor

	/* default constructor only */

	///////////////////////////////////////
	// Methods - Accessors
	public Integer getLevelsPerRepIncrease()
	{
		return levelsPerRepIncrease;
	}

	/** skill prereqs */
	public ArrayList getSkillNameList()
	{
		return skillNameList;
	}

	/** for metamagic feats increase in spelllevel */
	public int getAddSpellLevel()
	{
		return addSpellLevel;
	}

	public double getCost()
	{
		return cost;
	}

	public String getDescription()
	{
		return description;
	}

	public String getType()
	{
		return type;
	}

	public boolean isMultiples()
	{
		return multiples;
	}

	public boolean isStacks()
	{
		return stacks;
	}

	public String getAddString()
	{
		return addString;
	}

	public String getChoiceString()
	{
		return choiceString;
	}

	public ArrayList getCSkillList()
	{
		return cSkillList;
	}

	public ArrayList getCcSkillList()
	{
		return ccSkillList;
	}


	///////////////////////////////////////
	// Methods - Other

	public Object clone()
	{
		Feat aFeat = (Feat)super.clone();
		associatedList = (ArrayList)associatedList.clone();
		aFeat.visible = visible;
		aFeat.description = getDescription();
		aFeat.skillNameList = getSkillNameList();
		aFeat.type = getType();
		aFeat.multiples = isMultiples();
		aFeat.stacks = isStacks();
		aFeat.addString = getAddString();
		aFeat.cSkillList = (ArrayList)getCSkillList().clone();
		aFeat.ccSkillList = (ArrayList)getCcSkillList().clone();
		aFeat.levelsPerRepIncrease = levelsPerRepIncrease;
		aFeat.isSpecified = isSpecified;
		aFeat.addSpellLevel = addSpellLevel;
		return aFeat;
	}

	public String toString()
	{
		return name;
	}

	/**
	 * Factory for CharacterFeat, which links a Feat to a
	 * particular PlayerCharacter. The CharacterFeat has a link back
	 * to the Feat it relates to.
	 */
	public CharacterFeat createCharacterFeat()
	{
		return new CharacterFeat(this);
	}

	public String getRequirements()
	{
		return preReqStrings();
	}

	public boolean canBeSelectedBy(PlayerCharacter aPC)
	{
		return passesPreReqTests();
	}

	/**
	 * Returns true if the feat matches the given type (the type is
	 * contained in the type string of the feat).
	 */
	public boolean matchesType(String featType)
	{
		return type.toUpperCase().lastIndexOf(featType.toUpperCase()) > -1;
	}

	public ArrayList typeList()
	{
		ArrayList aArrayList = new ArrayList();
		StringTokenizer aTok = new StringTokenizer(type, ".", false);
		while (aTok.hasMoreTokens())
			aArrayList.add(aTok.nextToken());
		return aArrayList;
	}

	public void modAdds(boolean addIt)
	{
		final StringTokenizer aTok = new StringTokenizer(addString, "|", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aTok.countTokens() != 2 || aPC == null)
			return;
		String addType = new String(aTok.nextToken());
		String addSec = new String(aTok.nextToken());
		if (addType.equals("WEAPONPROFS"))
		{
			if (addIt)
			{
				if (Globals.getWeaponTypes().contains(addSec))
				{
					WeaponProf aProf = null;
					for (Iterator e = Globals.getWeaponProfs(addSec).iterator(); e.hasNext();)
					{
						aProf = (WeaponProf)e.next();
						if (!aPC.getWeaponProfList().contains(aProf.getName()))
							aPC.getWeaponProfList().add(aProf.getName());
					}
				}
				else
					aPC.getWeaponProfList().add(addSec);
			}
			else
			{
				String aString = null;
				for (Iterator setIter = aPC.getWeaponProfList().iterator();
						 setIter.hasNext();)
				{
					aString = (String)setIter.next();
					if (aString.equals(addSec))
					{
						setIter.remove();
						return;
					}
				}

				WeaponProf aProf = null;
				for (int j = 0; j < Globals.getWeaponProfList().size(); j++)
				{
					aProf = (WeaponProf)Globals.getWeaponProfList().get(j);
					if (aProf.getType().equalsIgnoreCase(addSec))
						aPC.getWeaponProfList().remove(aProf.getName());
				}
			}
		}
		else if (addType.equals("FAVOREDCLASS"))
		{
			if (addSec.equals("LIST"))
			{
				for (Iterator e = associatedList().iterator(); e.hasNext();)
				{
					if (addIt)
					{
						aPC.addFavoredClass((String)e.next());
					}
					else
					{
						aPC.removeFavoredClass((String)e.next());
					}

				}
			}
			else
			{
				if (addIt)
				{
					aPC.addFavoredClass(addSec);
					while (aTok.countTokens() > 0)
						aPC.addFavoredClass((String)aTok.nextToken());
				}
				else
				{
					aPC.addFavoredClass(addSec);
					while (aTok.countTokens() > 0)
						aPC.removeFavoredClass((String)aTok.nextToken());
				}
			}
		}
		else if (addType.equals("FORCEPOINT"))
		{
			if (addIt)
			{
				int X = Integer.parseInt(aPC.getFPoints());
				X += 1;
				aPC.setFPoints(String.valueOf(X));
			}
			else
			{
				int X = Integer.parseInt(aPC.getFPoints());
				X -= 1;
				aPC.setFPoints(String.valueOf(X));
			}
		}
	}

	public boolean hasCCSkill(String aName)
	{
		if (getCSkillList().contains(getName()))
			return true;
		String aString = null;
		for (Iterator e = getCcSkillList().iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			if (aString.lastIndexOf("%") > -1)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
					return true;
			}
		}
		return false;
	}

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;
		String aCol = null;
		for (col = 0; col < colMax; col++)
		{
			aCol = colToken.nextToken();
			if (super.parseTag(aCol))
				continue;
			final int len = aCol.length();
			if (col == 0)
				setName(aCol);
			else if ((len > 14) && aCol.startsWith("ADDSPELLLEVEL:"))
			{
				try
				{
					addSpellLevel = pcgen.util.Delta.parseInt(aCol.substring(14));
				}
				catch (NumberFormatException nfe)
				{
					JOptionPane.showMessageDialog(null, "Bad addSpellLevel " + aCol, "PCGen", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if (aCol.startsWith("ADD:"))
				setAddString(aCol.substring(4));
			else if (aCol.startsWith("BONUS"))
				addBonusList(aCol.substring(6));
			else if (aCol.startsWith("DESC"))
				setDescription(aCol.substring(5));
			//Is this like PRESKILl
			else if (aCol.startsWith("SKILL:"))
				setSkillNameList(aCol.substring(6));
			else if (aCol.startsWith("TYPE"))
				setType(aCol.substring(5));
			else if (aCol.startsWith("MULT"))
				setMultiples(aCol.substring(5));
			else if (aCol.startsWith("STACK"))
				setStacks(aCol.substring(6));
			else if (aCol.startsWith("CHOOSE"))
				setChoiceString(aCol.substring(7));
			else if (aCol.startsWith("CSKILL"))
				setCSkillList(aCol.substring(7));
			else if (aCol.startsWith("CCSKILL"))
				setCCSkillList(aCol.substring(8));
			else if (aCol.startsWith("REP"))
			{
				try
				{
					levelsPerRepIncrease = pcgen.util.Delta.decode(aCol.substring(4));
				}
				catch (NumberFormatException nfe)
				{
					JOptionPane.showMessageDialog(null, "Bad level per value " + aCol, "PCGen", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if (aCol.startsWith("DEFINE"))
				variableList.add("0|" + aCol.substring(7));
			else if (aCol.startsWith("KEY:"))
				setKeyName(aCol.substring(4));
			else if (aCol.startsWith("PRE"))
				preReqArrayList.add(aCol);
			else if (aCol.startsWith("VISIBLE:"))
			{
				if (aCol.substring(8).startsWith("Export")) {
					visible = 2; // output, no display: character sheet only
				} else if (aCol.substring(8).startsWith("No")) {
					visible = 0; // no Output, no display: Stealth
				} else if (aCol.substring(8).startsWith("Display")) {
					visible = 3; // Display only, no output: DisplayOnly
				} else {
					visible = 1; // default. Display and output: Yes
				}
			}
			else if (aCol.startsWith("COST"))
			{
				cost = Double.parseDouble(aCol.substring(5));
			}
			else
				JOptionPane.showMessageDialog
					(null, "Illegal feat info " +
					sourceFile.getName() + ":" + Integer.toString(lineNum) +
					" \"" + aCol + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setSkillNameList(String skillList)
	{
		final String commadelim = ",";
		final StringTokenizer colToken =
			new StringTokenizer(skillList, commadelim, false);
		final int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;
		skillNameList.ensureCapacity(skillNameList.size() + colMax);
		for (col = 0; col < colMax; col++)
		{
			skillNameList.add(colToken.nextToken());
		}
	}

	private void setDescription(String newString)
	{
		description = newString;
	}

	private void setType(String aString)
	{
		if (aString.lastIndexOf(":") > -1)
			type = aString.substring(aString.lastIndexOf(":"));
		else
			type = aString;
	}

	private void setMultiples(String aString)
	{
		if (aString.startsWith("Y"))
			multiples = true;
		else
			multiples = false;
	}

	private void setStacks(String aString)
	{
		if (aString.startsWith("Y"))
			stacks = true;
		else
			stacks = false;
	}

	private void setAddString(String aString)
	{
		addString = aString;
	}

	private void setChoiceString(String aString)
	{
		choiceString = aString;
	}

	private void setCSkillList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		String bString = null;
		while (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
			if (bString.startsWith("TYPE."))
			{
				Skill aSkill = null;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill)e1.next();
					if (aSkill.getType().indexOf(bString.substring(5)) >= 0)
					{
						getCSkillList().add(aSkill.getName());
					}
				}
			}
			else
			{
				/** add skill to list of class skills **/
				getCSkillList().add(bString);
			}
		}
	}

	private void setCCSkillList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			String bString = aTok.nextToken();
			if (bString.startsWith("TYPE."))
			{
				Skill aSkill = null;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill)e1.next();
					if (aSkill.getType().indexOf(bString.substring(5)) >= 0)
					{
						getCcSkillList().add(aSkill.getName());
					}
				}
			}
			else
			{
				/** add skill to list of class skills **/
				getCcSkillList().add(bString);
			}
		}
	}


	///////////////////////////////////////////////
	// move to CharacterFeat

	private ArrayList associatedList = new ArrayList(); // of String

	public ArrayList associatedList()
	{
		return associatedList;
	}

	/**
	 * Name of the full Feat, plus any sub-choices made for this
	 * character. Starts with the name of the feat, and then
	 * (for types other than weapon proficiencies), either appends
	 * a count of the times the feat is applied e.g. " (3x)",
	 * or a list of the sub-choices e.g. " (Sub1, Sub2, ...)".
	 */
	public String qualifiedName()
	{
		// start with the name of the feat
		// don't do for Weapon Profs
		StringBuffer aStrBuf = new StringBuffer(name);
		if (associatedList().size() > 0 && !name.endsWith("Weapon Proficiency"))
		{
			// has a sub-detail
			aStrBuf.append(" (");
			int i = 0;
			if (getChoiceString().length() == 0 || (multiples && stacks))
			{
				// number of items only (ie stacking), e.g. " (1x)"
				aStrBuf.append(Integer.toString((int)(associatedList().size() * cost)));
				aStrBuf.append("x)");
			}
			else
			{
				// list of items in associatedList, e.g. " (Sub1, Sub2, ...)"
				for (Iterator e = associatedList().iterator(); e.hasNext();)
				{
					if (i > 0)
						aStrBuf.append(", ");
					aStrBuf.append((String)e.next());
					i++;
				}
				aStrBuf.append(')');
			}
		}
		return aStrBuf.toString();
	}

	/**
	 * Enhanced associatedList.contains(), which parses the input
	 * parameter for "=", "+num" and "-num" to extract the value to look
	 * for.
	 */
	public boolean isInList(String aType)
	{
		final String numString = "0123456789";
		if (aType.lastIndexOf("=") > -1)
			aType = aType.substring(aType.lastIndexOf("=") + 1);
		if (aType.lastIndexOf("+") > -1) // truncate at + sign if following character is a number
		{
			final String aString = aType.substring(aType.lastIndexOf("+") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("+"));
		}
		if (aType.lastIndexOf("-") > -1) // truncate at - sign if following character is a number
		{
			final String aString = aType.substring(aType.lastIndexOf("-") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("-"));
		}
		for(Iterator i = associatedList.iterator(); i.hasNext();)
		{
			if (i.next().toString().equalsIgnoreCase(aType))
				return true;
		}
		return false;
	}

	/**
	 * Opens a Chooser to allow sub-choices for this feat.
	 * The actual items allowed to choose from are based on
	 * choiceString, as applied to current character. Choices
	 * already made (associatedList) are indicated in list B.
	 */
	public void modChoices(boolean addIt)
	{
		StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aTok.countTokens() < 1 || aPC == null || aPC.isImporting())
			return;
		int i = 0;
		int j = (int)((aPC.getFeats() + associatedList().size()) / cost);
		int num = 0;
		ArrayList availableList = new ArrayList(); // available list of choices
		ArrayList selectedList = new ArrayList(); // selected list of choices
		final ArrayList rootArrayList = new ArrayList();
		final String choiceType = aTok.nextToken();
		String choiceSec = getName();
		final Chooser chooser = new Chooser();
		chooser.setPoolFlag(false); // user is not required to make any changes
		chooser.setAllowsDups(isStacks()); // only stackable feats can be duped
		chooser.setVisible(false);
		chooser.setPool((int)(aPC.getFeats() / cost));
		Iterator iter = null;
		String title = "Choices";
		if (Globals.getWeaponTypes().contains(choiceType))
		{
			title = choiceType + " Weapon Choice";
			ArrayList tArrayList = Globals.getWeaponProfs(choiceType);
			WeaponProf tempProf = null;
			for (iter = tArrayList.iterator(); iter.hasNext();)
			{
				tempProf = (WeaponProf)iter.next();
				availableList.add(tempProf.getName());
			}
			SortedSet pcProfs = (SortedSet)
				aPC.getWeaponProfs(choiceType).clone();
			for (Iterator setIter = pcProfs.iterator(); setIter.hasNext();)
			{
				tempProf = (WeaponProf)setIter.next();
				selectedList.add(tempProf.getName());
				aPC.getWeaponProfList().remove(tempProf.getName());
			}
			j -= (int)(associatedList.size() * cost);
			associatedList = (ArrayList)selectedList.clone();
		}
		else if (choiceType.equals("SCHOOLS"))
		{
			title = "School Choice";
			availableList = Globals.getSchoolsList();
			selectedList = (ArrayList)associatedList.clone();
		}
		else if (choiceType.equals("SPELLLIST"))
		{
			title = "Spell Choice";
			final PCClass aClass = aPC.getClassNamed("Wizard");
			if (aClass != null && aClass.getLevel().intValue() > 0 && aPC.adjStats(Globals.INTELLIGENCE) > 11)
			{
				Spell aSpell = null;
				for (iter = aClass.spellList().iterator(); iter.hasNext();)
				{
					aSpell = (Spell)iter.next();
					if (!associatedList().contains(aSpell.getKeyName()))
						availableList.add(aSpell.getName());
				}
				selectedList = (ArrayList)associatedList.clone();
				num = selectedList.size();
				chooser.setPool(aPC.calcStatMod(Globals.INTELLIGENCE));
			}
		}
		else if (choiceType.equals("SKILLS"))
		{
			title = "Skill Choice";
			for (iter = aPC.getSkillList().iterator(); iter.hasNext();)
			{
				Skill aSkill = (Skill)iter.next();
				availableList.add(aSkill.getName());
			}
			selectedList = (ArrayList)associatedList.clone();
		}
		else if (choiceType.equals("SKILLSNAMED"))
		{
			title = "Skill Choice";
			String aString = null;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				boolean startsWith = false;
				if (aString.endsWith("%"))
				{
					startsWith = true;
					aString = aString.substring(0, aString.length() - 1);
				}
				Skill aSkill = null;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill)e1.next();
					if (aSkill.getKeyName().equals(aString) || (startsWith && aSkill.getKeyName().startsWith(aString)))
					{
						availableList.add(aSkill.getName());
					}
				}
			}
			selectedList = (ArrayList)associatedList.clone();
		}
		else if (choiceType.equals("SKILLLIST") || choiceType.equals("CCSKILLLIST") ||
			choiceType.equals("NONCLASSSKILLLIST"))
		{
			title = "Skill Choice";
			if (aTok.hasMoreTokens())
				choiceSec = new String(aTok.nextToken());
			if (choiceSec.length() > 0 && !choiceSec.equals("LIST"))
			{
				aTok = new StringTokenizer(choiceSec, ",", false);
				while (aTok.hasMoreTokens())
					availableList.add(aTok.nextToken());
			}
			else
			{
				Skill aSkill = null;
				for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
				{
					aSkill = (Skill)iter.next();
					if (choiceType.equals("NONCLASSSKILLLIST") && (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1 ||
						aSkill.isExclusive().startsWith("Y")))
						continue;
					final int rootNameLength = aSkill.getRootName().length();
					if (rootNameLength == 0)
						availableList.add(aSkill.getName());
					final boolean rootArrayContainsRootName = rootArrayList.contains(aSkill.getRootName());
					if (rootNameLength > 0 && !rootArrayContainsRootName)
						rootArrayList.add(aSkill.getRootName());
					if (rootNameLength > 0 && rootArrayContainsRootName)
						availableList.add(aSkill.getName());
				}
			}
			selectedList = (ArrayList)associatedList.clone();
		}
		else if (choiceType.equals("SPELLLEVEL"))
		{
			// this will need to be re-worked at some point when I can think
			// of a better way.  This feat is different from the others in that
			// it requires a bonus to be embedded in the choice.  Probably this
			// whole feat methodology needs to be re-thought as its getting a bit
			// bloated - a generic way to embed bonuses could be done to simplify
			// this all tremendously instead of so many special cases.
			ArrayList aBonusList = new ArrayList();
			StringTokenizer cTok = new StringTokenizer(choiceString, "[]", false);
			String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
				aBonusList.add(cTok.nextToken());
			getChoices(choices, aBonusList);
			associatedList().clear();
			for (Iterator ii = saveList.iterator(); ii.hasNext(); ii.next())
				associatedList.add("placeholder");
		}
		else if (choiceType.equals("WEAPONFOCUS"))
		{
			title = "WeaponFocus Choice";
			Feat aFeat = aPC.getFeatNamed("Weapon Focus");
			availableList = (ArrayList)aFeat.associatedList().clone();
			selectedList = (ArrayList)associatedList().clone();
		}
		else if (choiceType.equals("WEAPONPROFS"))
		{
			title = "Weapon Prof Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.equals("LIST"))
				{
					String bString = null;
					for (Iterator setIter = aPC.getWeaponProfList().iterator();
							 setIter.hasNext();)
					{
						bString = (String)setIter.next();
						if (!availableList.contains(bString))
							availableList.add(bString);
					}
				}
				else if (aString.startsWith("Size."))
				{
					if (aPC.sizeInt() >=
						aPC.sizeIntForSize(aString.substring(5, 6)) &&
						aPC.getWeaponProfList().contains(aString.substring(7)) &&
						!availableList.contains(aString.substring(7)))
						availableList.add(aString.substring(7));
				}
				else if (aString.startsWith("WSize."))
				{
					String bString = null;
					WeaponProf wp = null;
					for (Iterator setIter = aPC.getWeaponProfList().iterator();
							 setIter.hasNext();)
					{
						bString = (String)setIter.next();
						wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
							continue;
						final boolean availableListContainsBString = availableList.contains(bString);
						if (aString.endsWith("Light") && wp.isLight() && !availableListContainsBString)
							availableList.add(bString);
						if (aString.endsWith("1 handed") && wp.isOneHanded() && !availableListContainsBString)
							availableList.add(bString);
						if (aString.endsWith("2 handed") && wp.isTwoHanded() && !availableListContainsBString)
							availableList.add(bString);
					}
				}
				else if (aString.startsWith("SpellCaster."))
				{
					if (aPC.isSpellCaster(1) && !availableList.contains(aString.substring(12)))
						availableList.add(aString.substring(12));
				}
				else if (aString.startsWith("ADD."))
				{
					if (!availableList.contains(aString.substring(4)))
						availableList.add(aString.substring(4));
				}
				else if (aString.startsWith("TYPE."))
				{
					String sString = aString.substring(5);
					boolean adding = true;
					Iterator setIter = aPC.getWeaponProfList().iterator();
					if (sString.startsWith("Not."))
					{
						sString = sString.substring(4);
						setIter = availableList.iterator();
						adding = false;
					}
					String bString = null;
					WeaponProf wp = null;
					Equipment eq = null;
					for (; setIter.hasNext();)
					{
						bString = (String)setIter.next();
						wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
							continue;
						eq = Globals.getEquipmentKeyed(wp.getKeyName());
						if (eq == null)
						{
							if (!wp.getType().equals("Natural"))	//natural weapons are not in the global eq.list
								continue;

							if (Globals.isDebugMode())
								System.out.println("Prof Name: -" + wp.getName() + "- " + adding);

							if (adding && !availableList.contains(wp.getName()))
								availableList.add(wp.getName());
							// or try to remove it and reset the iterator since remove cause fits
/*							else if (adding==false && availableList.contains(wp.getName()))
							{
								availableList.remove(wp.getName());
								setIter = availableList.iterator();
							}
*/		//removed because depended on additonal types in (nonexistant) eq
						}
						else if (eq.typeStringContains(sString))
						{
							// if this item is of the desired type, add it to the list
							if (adding && !availableList.contains(wp.getName()))
								availableList.add(wp.getName());
							// or try to remove it and reset the iterator since remove cause fits
							else if (adding == false && availableList.contains(wp.getName()))
							{
								availableList.remove(wp.getName());
								setIter = availableList.iterator();
							}
						}
					}
				}
				else
				{
					if (aPC.getWeaponProfList().contains(aString) &&
						!availableList.contains(aString))
						availableList.add(aString);
				}
			}
			selectedList = (ArrayList)associatedList.clone();
		}
		else if (choiceType.equals("HP"))
		{
			if (aTok.hasMoreTokens())
				choiceSec = aTok.nextToken();
			availableList.add(choiceSec);
			for (Iterator e1 = associatedList.iterator(); e1.hasNext(); e1.next())
				selectedList.add(choiceSec);
		}
		else if (choiceType.startsWith("FEAT="))
		{
			Feat aFeat = aPC.getFeatNamed(choiceType.substring(5));
			if (aFeat != null)
				availableList = aFeat.associatedList();
			selectedList = (ArrayList)associatedList.clone();
		}
		else if (choiceType.equals("FEATLIST"))
		{
			selectedList = (ArrayList)associatedList.clone();
			String aString = null;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if (aString.startsWith("TYPE="))
				{
					aString = aString.substring(5);
					if (!stacks && availableList.contains(aString))
						continue;
					Feat aFeat = null;
					for (Iterator e1 = aPC.aggregateFeatList().iterator(); e1.hasNext();)
					{
						aFeat = (Feat)e1.next();
						if (aFeat.getType().equals(aString) && (stacks || (!stacks && !availableList.contains(aFeat.getName()))))
							availableList.add(aFeat.getName());
					}
				}
				else if (aPC.getFeatNamed(aString) != null)
				{
					if (stacks || (!stacks && !availableList.contains(aString)))
						availableList.add(aString);
				}
			}
		}
		else if (choiceType.equals("SPELLCLASSES"))
		{
			title = "Spellcaster Classes";
			PCClass aClass = null;
			for (iter = aPC.getClassList().iterator(); iter.hasNext();)
			{
				aClass = (PCClass)iter.next();
				if (!aClass.getSpellBaseStat().equals("None"))
					availableList.add(aClass.getName());
			}
			selectedList = (ArrayList)associatedList.clone();
		}
		else
		{
			title = "Selections";
			availableList.add(choiceType);
			String aString = null;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if (stacks || (!stacks && !availableList.contains(aString)))
					availableList.add(aString);
			}
			selectedList = (ArrayList)associatedList.clone();
		}
		title = title + " (" + name + ")";
		chooser.setTitle(title);
		if (!choiceType.equals("SPELLLEVEL"))
		{
			Globals.sortChooserLists(availableList, selectedList);
			chooser.setAvailableList(availableList);
			chooser.setSelectedList(selectedList);
			chooser.show();
		}
		if (choiceType.equals("SPELLLIST"))
		{
			int x = aPC.getFeats();
			if (chooser.getSelectedList().size() > num)
				aPC.setFeats(x - 1);
			if (chooser.getSelectedList().size() < num)
				aPC.setFeats(x + 1);
		}
		String tempString = null;
		for (iter = cSkillList.iterator(); iter.hasNext();)
		{
			tempString = (String)iter.next();
			if (!tempString.equals("LIST") && !Globals.getFeatNamed(name).cSkillList.contains(tempString))
				iter.remove();
		}
		if (!choiceType.equals("SPELLLEVEL"))
		{
			getCcSkillList().clear();
			associatedList().clear();
		}
		for (i = 0; i < chooser.getSelectedList().size(); i++)
		{
			if (choiceType.equals("HP"))
				associatedList().add("CURRENTMAX");
			else if (multiples && !stacks)
			{
				if (!associatedList.contains(chooser.getSelectedList().get(i)))
					associatedList.add(chooser.getSelectedList().get(i));
			}
			else
				associatedList().add(chooser.getSelectedList().get(i));
			if (choiceType.equals("SKILLLIST") || choiceType.equals("NONCLASSSKILLLIST"))
			{
				String aString = (String)chooser.getSelectedList().get(i);
				if (rootArrayList.contains(aString))
				{
					Skill aSkill = null;
					for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
					{
						aSkill = (Skill)e2.next();
						if (aSkill.getRootName().equals(aString))
							getCSkillList().add(aSkill.getName());
					}
				}
				else
					getCSkillList().add(aString);
			}
			else if (choiceType.equals("CCSKILLLIST"))
			{
				String aString = (String)chooser.getSelectedList().get(i);
				if (rootArrayList.contains(aString))
				{
					Skill aSkill = null;
					for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
					{
						aSkill = (Skill)e2.next();
						if (aSkill.getRootName().equals(aString))
							getCcSkillList().add(aSkill.getName());
					}
				}
				else
					getCcSkillList().add(aString);
			}
			if (Globals.getWeaponTypes().contains(choiceType))
			{
				aPC.addWeaponProf(chooser.getSelectedList().get(i).toString());
			}
		}
		if (!choiceType.equals("SPELLLIST"))
			aPC.setFeats((int)((j - associatedList().size() + selectedList.size()) * cost));
	}

	// ??? related to associatedList
	// doesn't appear to be used anywhere!
	public int addStatBonuses(String aString)
	{
		int retVal = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		PCClass aClass = null;
		for (Iterator e = associatedList.iterator(); e.hasNext();)
		{
			aClass = (PCClass)aPC.getClassNamed((String)e.next());
			if (aClass != null && aClass.getSpellBaseStat().equals(aString))
				retVal++;
		}
		return retVal;
	}

	// ??? related to associatedList
	// used in Skill
	public boolean hasCSkill(String aName)
	{
		if (getCSkillList().contains(aName))
			return true;
		if (getCSkillList().contains("LIST"))
		{
			String aString = null;
			for (Iterator e = associatedList.iterator(); e.hasNext();)
			{
				aString = (String)e.next();
				if (aName.startsWith(aString) || aString.startsWith(aName))
					return true;
			}
		}
		String aString = null;
		for (Iterator e = getCSkillList().iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			if (aString.lastIndexOf("%") > -1)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
					return true;
			}
		}
		return false;
	}

	public int isVisible()
	{
		return visible;
	}


}