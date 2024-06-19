/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreFeat extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		int number;
		final boolean flag;
		boolean countMults = false;
		StringTokenizer aTok = new StringTokenizer(getParameters(), "|");
		setParameters( aTok.nextToken() );
		if (aTok.hasMoreTokens())
		{
			countMults = "CHECKMULT".equals(aTok.nextToken());
		}
		aTok = new StringTokenizer(getParameters(), ",");
		try
		{
			number = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException exceptn)
		{
			Logging.errorPrint("Exception in PREFEAT:" + getParameters() + Constants.s_LINE_SEP + "Assuming 1 required", exceptn);
			number = 1;
			aTok = new StringTokenizer(getParameters(), ",");
		}
		while (aTok.hasMoreTokens() && number > 0)
		{
			String aString = aTok.nextToken();
			if ("CHECKMULT".equals(aString))
			{
				countMults = true;
				continue;
			}
			StringTokenizer bTok = new StringTokenizer(aString, "(");
			String pString = bTok.nextToken();
			int i = -1;
			if (pString.length() != aString.length())
			{
				i = pString.length(); // begin of subchoices
			}

			String featName;
			String subName = null;
			int j = -1;
			boolean isType = aString.startsWith("TYPE=") || aString.startsWith("TYPE.");
			if (i >= 0)
			{
				featName = aString.substring(0, i).trim();
				subName = aString.substring(i + 1, aString.length() - 1);
				j = subName.lastIndexOf('%');
				if (j >= 0)
				{
					subName = subName.substring(0, j);
				}
			}
			else
			{
				featName = aString;
			}

			boolean foundIt = false;
			List aFeatList = character != null ? character.aggregateFeatList() : null;
			if ((aFeatList != null) && !aFeatList.isEmpty())
			{
				for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
				{
					if (foundIt && !isType || (number <= 0))
					{
						break;
					}

					Feat aFeat = (Feat) e1.next();
					if ((!isType && (aFeat.getName().equalsIgnoreCase(featName) || aFeat.getName().equalsIgnoreCase(aString))) || (isType && aFeat.isType(featName.substring(5))))
					{
						if (subName != null)
						{
							if (subName.indexOf("TYPE=") >= 0 || subName.indexOf("TYPE.") >= 0) // TYPE syntax
							{
								final String cType = subName.substring(5);
								final List availableList = new ArrayList();
								final List selectedList = new ArrayList();
								final String aChoiceString = aFeat.getChoiceString();
								PObject.modChoices(aFeat, availableList, selectedList, false);
								availableList.clear();
								if (aChoiceString.startsWith("SKILL"))
								{
									for (Iterator e = selectedList.iterator(); e.hasNext();)
									{
										Object aObj = e.next();
										Skill sk;
										sk = Globals.getSkillNamed(aObj.toString());
										if (sk == null)
										{
											continue;
										}
										if (sk.isType(cType))
										{
											--number;
											foundIt = true;
											if (!countMults)
											{
												break;
											}
										}
									}
								}
								else if (aChoiceString.startsWith("WEAPONPROFS"))
								{
									for (Iterator e = selectedList.iterator(); e.hasNext();)
									{
										Object aObj = e.next();
										WeaponProf wp;
										wp = Globals.getWeaponProfNamed(aObj.toString());
										if (wp == null)
										{
											continue;
										}
										Equipment eq;
										eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());
										if (eq == null)
										{
											continue;
										}
										if (eq.isType(cType))
										{
											--number;
											foundIt = true;
											if (!countMults)
											{
												break;
											}
										}
									}
								}
								else if (aChoiceString.startsWith("DOMAIN"))
								{
									for (Iterator e = selectedList.iterator(); e.hasNext();)
									{
										Object aObj = e.next();
										Domain dom;
										dom = Globals.getDomainKeyed(aObj.toString());
										if (dom == null)
										{
											continue;
										}
										if (dom.isType(cType))
										{
											--number;
											foundIt = true;
											if (!countMults)
											{
												break;
											}
										}
									}
								}
								else if (aChoiceString.startsWith("SPELL"))
								{
									for (Iterator e = selectedList.iterator(); e.hasNext();)
									{
										Object aObj = e.next();
										Spell sp;
										sp = Globals.getSpellNamed(aObj.toString());
										if (sp == null)
										{
											continue;
										}
										if (sp.isType(cType))
										{
											--number;
											foundIt = true;
											if (!countMults)
											{
												break;
											}
										}
									}
								}
							}
							if (aFeat.getName().equalsIgnoreCase(aString) || aFeat.containsAssociated(subName))
							{
								--number;
								if (aFeat.isMultiples() && countMults)
								{
									number -= (aFeat.getAssociatedCount() - 1);
								}
								foundIt = true;
							}
							else if (j > 0)
							{
								for (int k = 0; k < aFeat.getAssociatedCount(); ++k)
								{

									String fString = aFeat.getAssociated(k).toUpperCase();
									if (fString.startsWith(subName.substring(0, j)))
									{
										--number;
										foundIt = true;
										if (!countMults)
										{
											break;
										}
									}
								}
							}
						}
						else
						{
							--number;
							if (aFeat.isMultiples() && countMults)
							{
								number -= (aFeat.getAssociatedCount() - 1);
							}
							foundIt = true;
						}
					}
				}
			}
		}
		flag = (number <= 0);
		return flag;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"FEAT"};
	}

}
