/*
 * @(#) $Id: Feat.java,v 1.1 2006/02/20 23:52:29 vauchers Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * Definition and games rules for a Feat.
 *
 * @version $Revision: 1.1 $
 */
public class Feat extends PObject
{
	///////////////////////////////////////
	// Static properties
	public static final int VISIBILITY_HIDDEN = 0;
	public static final int VISIBILITY_DEFAULT = 1;
	public static final int VISIBILITY_OUTPUT_ONLY = 2;
	public static final int VISIBILITY_DISPLAY_ONLY = 3;

	///////////////////////////////////////
	// Fields - Attributes

	private Integer levelsPerRepIncrease = new Integer(0);

	private int featVisible = 1; // Hidden Feats

	private String description = new String();
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

	public ArrayList getCSkillList()
	{
		return cSkillList;
	}

	public ArrayList getCcSkillList()
	{
		return ccSkillList;
	}
	///////////////////////////////////////
	// Methods - Mutator(s)

	public void setLevelsPerRepIncrease(Integer levelsPerRepIncrease)
	{
		this.levelsPerRepIncrease = levelsPerRepIncrease;
	}

	public void setAddSpellLevel(int addSpellLevel)
	{
		this.addSpellLevel = addSpellLevel;
	}

	public void setCost(double cost)
	{
		this.cost = cost;
	}

	public void setVisible(int visible)
	{
		this.featVisible = visible;
	}

	///////////////////////////////////////
	// Methods - Other

	public Object clone()
	{
		Feat aFeat = (Feat)super.clone();
		aFeat.visible = visible;
		aFeat.description = getDescription();
		aFeat.skillNameList = getSkillNameList();
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

	public boolean canBeSelectedBy(PlayerCharacter aPC)
	{
		return passesPreReqTests();
	}

	/**
	 * Bypass normal prerequisite checks for feats if
	 * Globals.boolBypassFeatPreReqs is true.  Otherwise,
	 * use the prerequisite checks from the parent object.
	 */
	public boolean passesPreReqTestsForList(PObject aObj, ArrayList anArrayList)
	{
		return (Globals.isBoolBypassFeatPreReqs() || super.passesPreReqTestsForList(aObj, anArrayList));
	}

	/**
	 * Returns true if the feat matches the given type (the type is
	 * contained in the type string of the feat).
	 */
	public boolean matchesType(String featType)
	{
		return isType(featType);
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
				for (Iterator e = associatedList.iterator(); e.hasNext();)
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
					aPC.removeFavoredClass(addSec);
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
		// This code needs to be made to add an assortment of special abilities
		else if (addType.equals("SPECIAL"))
		// Takes a \ deliminated list of special abilities and lets you choose one to add.
		// BUG: currently adds 2 items from the list. --- arcady 10/12/2001
		{
			if (addSec.equals("LIST"))
			{
				for (Iterator e = associatedList.iterator(); e.hasNext();)
				{
					if (addIt)
					{
						aPC.getSpecialAbilityList().add((String)e.next());
					}
					else
					{
						aPC.getSpecialAbilityList().remove((String)e.next());
					}

				}
			}
			else
			{
				if (addIt)
				{
					aPC.getSpecialAbilityList().add(addSec);
					while (aTok.countTokens() > 0)
						aPC.getSpecialAbilityList().add((String)aTok.nextToken());
				}
				else
				{
					aPC.getSpecialAbilityList().remove(addSec);
					while (aTok.countTokens() > 0)
						aPC.getSpecialAbilityList().remove((String)aTok.nextToken());
				}
			}
		}
	}

	public boolean hasCCSkill(String aName)
	{
		if (ccSkillList.contains(aName))
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

	public void setSkillNameList(String skillList)
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

	public void setDescription(String newString)
	{
		description = newString;
	}

	public void setMultiples(String aString)
	{
		if (aString.startsWith("Y"))
			multiples = true;
		else
			multiples = false;
	}

	public void setStacks(String aString)
	{
		if (aString.startsWith("Y"))
			stacks = true;
		else
			stacks = false;
	}

	public void setAddString(String aString)
	{
		addString = aString;
	}

	public void setChoiceString(String aString)
	{
		choiceString = aString;
	}

	public void setCSkillList(String aString)
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
					if (aSkill.isType(bString.substring(5)))
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

	public void setCCSkillList(String aString)
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
//					if (aSkill.getType().indexOf(bString.substring(5)) >= 0)
					if (aSkill.isType(bString.substring(5)))
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

//	private ArrayList getAssociatedList = new ArrayList(); // of String
//
//	public ArrayList associatedList
//	{
//		return getAssociatedList;
//	}

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
		if (associatedList.size() > 0 && !name.endsWith("Weapon Proficiency"))
		{
			// has a sub-detail
			aStrBuf.append(" (");
			int i = 0;
			if (getChoiceString().length() == 0 || (multiples && stacks))
			{
				// number of items only (ie stacking), e.g. " (1x)"
				aStrBuf.append(Integer.toString((int)(associatedList.size() * cost)));
				aStrBuf.append("x)");
			}
			else
			{
				// list of items in getAssociatedList, e.g. " (Sub1, Sub2, ...)"
				for (Iterator e = associatedList.iterator(); e.hasNext();)
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
	 * Enhanced getAssociatedList.contains(), which parses the input
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
		for (Iterator i = associatedList.iterator(); i.hasNext();)
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
	 * already made (getAssociatedList) are indicated in list B.
	 */
	public void modChoices(boolean addIt)
	{
		StringTokenizer aTok = new StringTokenizer(choiceString, "|", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aTok.countTokens() < 1 || aPC == null || aPC.isImporting())
			return;

		int i = 0;
		int j = (int)((aPC.getFeats() + associatedList.size()) / cost);
		int num = 0;
		ArrayList availableList = new ArrayList(); // available list of choices
		ArrayList selectedList = new ArrayList(); // selected list of choices
		ArrayList uniqueList = new ArrayList();
		ArrayList aBonusList = new ArrayList();
		final ArrayList rootArrayList = new ArrayList();
		String choiceType = aTok.nextToken();
		String choiceSec = getName();
		final Chooser chooser = new Chooser();
		chooser.setPoolFlag(false); // user is not required to make any changes
		chooser.setAllowsDups(isStacks()); // only stackable feats can be duped
		chooser.setVisible(false);
		Iterator iter = null;
		String title = "Choices";

		int maxSelections = (int)(aPC.getFeats() / cost);
		int requestedSelections = -1;
		if (choiceType.startsWith("COUNT="))
		{
			requestedSelections = Integer.parseInt(choiceType.substring(6));
			choiceType = aTok.nextToken();
		}

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
			if (aClass != null && aClass.getLevel().intValue() > 0 && aPC.adjStats(Constants.INTELLIGENCE) > 11)
			{
				for (iter = aClass.spellList().iterator(); iter.hasNext();)
				{
					Spell aSpell = (Spell)iter.next();
					if (!associatedList.contains(aSpell.getKeyName()))
						availableList.add(aSpell.getName());
				}
				selectedList = (ArrayList)associatedList.clone();
				num = selectedList.size();
				maxSelections = aPC.calcStatMod(Constants.INTELLIGENCE);
			}
		}
		else if (choiceType.equals("SALIST"))
		{
			// SALIST:Smite|VAR|%|1
			title = "Special Ability Choice";
			buildSALIST(choiceString, availableList, aBonusList);
			selectedList = (ArrayList)associatedList.clone();
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
		} // SKILLSNAMEDTOCSKILL --- Make one of the named skills a class skill.
		else if (choiceType.equals("SKILLSNAMED") || choiceType.equals("SKILLSNAMEDTOCSKILL") || choiceType.equals("SKILLSNAMEDTOCCSKILL"))
		{
			title = "Skill Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				boolean startsWith = false;
				/* TYPE in chooser
					--- arcady 10/21/2001
				*/
				if (aString.startsWith("TYPE."))
				{
					Skill aSkill = null;
					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						aSkill = (Skill)e1.next();
//						if (aSkill.getType().indexOf(aString.substring(5)) >= 0)
						if (aSkill.isType(aString.substring(5)))
						{
							availableList.add(aSkill.getName());
						}
					}
				}
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
			else  // if it was LIST
			{
				Skill aSkill = null;
				for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
				{
					aSkill = (Skill)iter.next();
					if (choiceType.equals("NONCLASSSKILLLIST") && (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1 ||
						aSkill.isExclusive().startsWith("Y")))
						continue; // builds a list of Cross class skills
					final int rootNameLength = aSkill.getRootName().length();
					if (rootNameLength == 0 || aSkill.getRootName().equals(aSkill.getName())) //all skills have ROOTs now, so go ahead and add it if the name and root are identical
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
			StringTokenizer cTok = new StringTokenizer(choiceString, "[]", false);
			String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
				aBonusList.add(cTok.nextToken());

			getSpellTypeChoices(choices, availableList, uniqueList); // get appropriate choices for chooser
			selectedList = (ArrayList)associatedList.clone();

			for (Iterator e1 = selectedList.iterator(); e1.hasNext();)
			{
				String aString = (String)e1.next();
				for (Iterator e2 = aBonusList.iterator(); e2.hasNext();)
				{
					String bString = (String)e2.next();
					removeBonus(bString, aString);
				}
			}
		}
		else if (choiceType.equals("WEAPONFOCUS"))
		{
			title = "WeaponFocus Choice";
			Feat aFeat = aPC.getFeatNamed("Weapon Focus");
			availableList = (ArrayList)aFeat.associatedList.clone();
			selectedList = (ArrayList)associatedList.clone();
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
					StringTokenizer bTok = new StringTokenizer(aString, ".", false);
					bTok.nextToken(); // should be WSize
					String sString = bTok.nextToken(); // should be Light, 1 handed, 2 handed choices above
					ArrayList typeList = new ArrayList();
					while (bTok.hasMoreTokens()) // any additional constraints
					{
						String dString = bTok.nextToken().toUpperCase();
						typeList.add(dString);
					}
					for (Iterator setIter = aPC.getWeaponProfList().iterator();
							 setIter.hasNext();)
					{
						bString = (String)setIter.next();
						wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
							continue;
						Equipment eq = null;
						boolean isValid = true; // assume we match unless...
						// search all the optional type strings, just one match passes the test
						for (Iterator wpi = typeList.iterator(); wpi.hasNext();)
						{
							isValid = false; // ... the typeList is non-empty, the assume we fail
							// get an Equipment object based on the named WeaponProf
							if (eq == null)
								eq = Globals.getEquipmentNamed(wp.getName());
							// if there is no eq then we can't determine if we match any of its TYPEs or not
							if (eq == null)
								break;
							String wpString = (String)wpi.next();
							if (eq.typeStringContains(wpString))
							{
								isValid = true; // if it contains even one of the TYPE strings, it passes
								break;
							}
						}
						if (!isValid)
							continue;
						final boolean availableListContainsBString = availableList.contains(bString);
						if (sString.equals("Light") && wp.isLight() && !availableListContainsBString)
						{
							availableList.add(bString);
						}
						if (sString.equals("1 handed") && wp.isOneHanded() && !availableListContainsBString)
							availableList.add(bString);
						if (sString.equals("2 handed") && wp.isTwoHanded() && !availableListContainsBString)
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

							Globals.debugPrint("Prof Name: -" + wp.getName() + "- " + adding);

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
				availableList = aFeat.associatedList;
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
						if (aFeat.isType(aString) && (stacks || (!stacks && !availableList.contains(aFeat.getName()))))
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
		else if (choiceType.equals("FEATSELECT"))
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
					for (int z = 0; z < Globals.getFeatList().size(); z++)
					{
						aFeat = Globals.getFeatListFeat(z);
						if (aFeat.isType(aString) && (stacks || (!stacks && !availableList.contains(aFeat.getName()))))
							availableList.add(aFeat.getName());
					}
				}
				//else if (aPC.getFeatNamed(aString) != null)
				//{
				//	if (stacks || (!stacks && !availableList.contains(aString)))
				//		availableList.add(aString);
				//}
			}
		}
		else if (choiceType.equals("SPELLCLASSES"))
		{
			title = "Spellcaster Classes";
			PCClass aClass = null;
			for (iter = aPC.getClassList().iterator(); iter.hasNext();)
			{
				aClass = (PCClass)iter.next();
				if (!aClass.getSpellBaseStat().equals(Constants.s_NONE))
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


		if (requestedSelections < 0)
		{
			requestedSelections = maxSelections;
		}
		else
		{
			requestedSelections -= selectedList.size();
			requestedSelections = Math.min(requestedSelections, maxSelections);
		}
		chooser.setPool(requestedSelections);

		title = title + " (" + name + ")";
		chooser.setTitle(title);
		Globals.sortChooserLists(availableList, selectedList);
		chooser.setAvailableList(availableList);
		chooser.setSelectedList(selectedList);
		chooser.show();

		if (choiceType.equals("SPELLLIST"))
		{
			int x = aPC.getFeats();
			if (chooser.getSelectedList().size() > num)
				aPC.setFeats(x - 1);
			if (chooser.getSelectedList().size() < num)
				aPC.setFeats(x + 1);
		}
		else if (choiceType.equals("SALIST"))
		{
			//
			// remove previous selections from special abilities
			// aBonusList contains all possible selections in form: <displayed info>|<special ability>
			//
			for (Iterator e = associatedList.iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				final String prefix = aString + "|";
				for (int x = 0; x < aBonusList.size(); x++)
				{
					final String bString = (String)aBonusList.get(x);
					if (bString.startsWith(prefix))
					{
						removeBonus(bString.substring(bString.indexOf('|') + 1), "");
						break;
					}
				}
			}
		}

		String tempString = null;
		for (iter = cSkillList.iterator(); iter.hasNext();)
		{
			tempString = (String)iter.next();
			if (!tempString.equals("LIST") && !Globals.getFeatNamed(name).cSkillList.contains(tempString))
				iter.remove();
		}

		getCcSkillList().clear(); // Why?
		associatedList.clear();
		for (i = 0; i < chooser.getSelectedList().size(); i++)
		{
			if (choiceType.equals("HP"))
			{
				associatedList.add("CURRENTMAX");
			}
			else if (choiceType.equals("SPELLLEVEL"))
			{
				final String aString = (String)chooser.getSelectedList().get(i);
				for (Iterator e = aBonusList.iterator(); e.hasNext();)
				{
					String bString = (String)e.next();
					associatedList.add(aString);
					applyBonus(bString, aString);
				}
			}
			else if (multiples && !stacks)
			{
				if (!associatedList.contains(chooser.getSelectedList().get(i)))
					associatedList.add(chooser.getSelectedList().get(i));
			}
			else
			{
				final String aString = (String)chooser.getSelectedList().get(i);
				final String prefix = aString + "|";
				associatedList.add(aString);
				// SALIST: aBonusList contains all possible selections in form: <displayed info>|<special ability>
				for (int x = 0; x < aBonusList.size(); x++)
				{
					final String bString = (String)aBonusList.get(x);
					if (bString.startsWith(prefix))
					{
						addBonusList(bString.substring(bString.indexOf('|') + 1));
						break;
					}
				}
			}

			if (choiceType.equals("SKILLLIST") || choiceType.equals("SKILLSNAMEDTOCSKILL") || choiceType.equals("NONCLASSSKILLLIST"))
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
				{
					getCSkillList().add(aString);
				}
			}
			else if (choiceType.equals("CCSKILLLIST") || choiceType.equals("SKILLSNAMEDTOCCSKILL"))
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
				{
					getCcSkillList().add(aString);
				}
			}
			if (Globals.getWeaponTypes().contains(choiceType))
			{
				aPC.addWeaponProf(chooser.getSelectedList().get(i).toString());
			}
		}
		if (!choiceType.equals("SPELLLIST"))
			aPC.setFeats((int)((j - associatedList.size() + selectedList.size()) * cost));
	}

	// ??? related to getAssociatedList
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
		return featVisible;
	}

	public void buildSALIST(String aChoice, ArrayList aAvailable, ArrayList aBonus)
	{
		// SALIST:Smite|VAR|%|1
		// SALIST:Turn ,Rebuke|VAR|%|1

		String aString;
		String aPost = "";
		int iOffs = aChoice.indexOf('|', 7);
		if (iOffs < 0)
		{
			aString = aChoice;
		}
		else
		{
			aString = aChoice.substring(7, iOffs);
			aPost = aChoice.substring(iOffs + 1);
		}

		ArrayList saNames = new ArrayList();
		StringTokenizer aTok = new StringTokenizer(aString, ",");
		while (aTok.hasMoreTokens())
		{
			saNames.add(aTok.nextToken());
		}


		final PlayerCharacter aPC = Globals.getCurrentPC();
		ArrayList aSAList = aPC.getSpecialAbilityList();

		//
		// Add special abilities due to templates
		//
		final ArrayList aTemplateList = aPC.getTemplateList();
		for (Iterator e1 = aTemplateList.iterator(); e1.hasNext();)
		{
			final PCTemplate aTempl = (PCTemplate)e1.next();
			final ArrayList SAs = (ArrayList)aTempl.getSAs(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize());
			for (Iterator e2 = SAs.iterator(); e2.hasNext();)
			{
				final String aSA = (String)e2.next();
				if (!aSAList.contains(aSA))
				{
					aSAList.add(aSA);
				}
			}
		}

		for (Iterator e2 = saNames.iterator(); e2.hasNext();)
		{
			aString = (String)e2.next();
			for (Iterator e1 = aSAList.iterator(); e1.hasNext();)
			{
				String aSA = (String)e1.next();
				if (aSA.startsWith(aString))
				{
					String aVar = "";
					//
					// Trim off variable portion of SA, and save variable name
					// (eg. "Smite Evil %/day|SmiteEvil" --> aSA = "Smite Evil", aVar = "SmiteEvil")
					//
					iOffs = aSA.indexOf('|');
					if (iOffs >= 0)
					{
						aVar = aSA.substring(iOffs + 1);
						iOffs = aSA.indexOf('%');
						if (iOffs >= 0)
						{
							aSA = aSA.substring(0, iOffs).trim();
						}
					}
					if (!aAvailable.contains(aSA))
					{
						aAvailable.add(aSA);
						//
						// Check for variable substitution
						//
						iOffs = aPost.indexOf('%');
						if (iOffs >= 0)
						{
							aVar = aPost.substring(0, iOffs) + aVar + aPost.substring(iOffs + 1);
						}
						aBonus.add(aSA + "|" + aVar);
					}
				}
			}
		}
	}

}
