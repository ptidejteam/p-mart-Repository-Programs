/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.Logging;
/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreSkillTotal extends Prerequisite implements PrerequisiteTest {


	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		boolean flag;
		int i = getParameters().lastIndexOf('=');
		int ranks;
		try
		{
			ranks = Integer.parseInt(getParameters().substring(i + 1));
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreSkillTot attribute: " + getParameters().substring(i + 1));
			ranks = 0;
		}
		final StringTokenizer aTok = new StringTokenizer(getParameters().substring(0, i), ",");

		// the number of feats which must match
		// number = Integer.parseInt(aTok.nextToken());
		final List sList = (ArrayList) character.getSkillList().clone();
		final List tList = new ArrayList();
		while (aTok.hasMoreTokens() && ranks > 0)
		{

			String aString = aTok.nextToken().toUpperCase();
			StringTokenizer bTok = new StringTokenizer(aString, "(");
			String pString = bTok.nextToken();
			i = -1;
			if (pString.length() != aString.length())
			{
				i = pString.length();
			}

			String skillName;
			String subName;
			boolean isType = (aString.startsWith("TYPE.") || aString.startsWith("TYPE="));
			int j;
			if (i >= 0)
			{
				skillName = aString.substring(0, i);
				subName = aString.substring(i + 1, aString.length() - 1);
				j = subName.lastIndexOf('%');
				if (j >= 0)
				{
					//subName = subName.substring(0, j);
				}
			}
			else
			{
				skillName = aString;
				j = aString.lastIndexOf('%');
			}

			boolean foundIt = false;
			Skill aSkill;
			for (int si = 0; si < sList.size(); ++si)
			{
				if ((foundIt && !isType && (j < 0)) || (ranks <= 0))
				{
					break;
				}
				aSkill = (Skill) sList.get(si);
				if (!isType && (aSkill.getName().equalsIgnoreCase(skillName) || aSkill.getName().equalsIgnoreCase(aString) || (j >= 0 && aSkill.getName().toUpperCase().startsWith(aString.substring(0, j)))))
				{
					if (tList.contains(aSkill.getName().toUpperCase()))
					{
						continue;
					}
					if ((j >= 0) && aSkill.getName().toUpperCase().startsWith(aString.substring(0, j)))
					{
						foundIt = true;
					}
					else if ((j < 0) && aSkill.getName().equalsIgnoreCase(aString))
					{
						foundIt = true;
					}
					if (!foundIt)
					{
						aSkill = null;
					}
				}
				else if ((isType && (aSkill.getType().indexOf(skillName.substring(5)) >= 0)))
				{
					foundIt = false;
					if (tList.contains(aSkill.getName().toUpperCase()))
					{
						continue;
					}
					if ((j >= 0) && aSkill.getType().startsWith(aString.substring(5, j)))
					{
						foundIt = true;
					}
					else if ((j < 0) && (aSkill.getType().indexOf(aString.substring(5)) >= 0))
					{
						foundIt = true;
					}
					if (!foundIt)
					{
						aSkill = null;
					}
				}
				if ((aSkill != null) && (j >= 0))
				{
					sList.remove(aSkill);
					--si; // to adjust for incrementer
				}
				flag = (aSkill != null);
				if (flag)
				{
					if (aSkill != null) //Only here to shut jlint up
					{
						tList.add(aSkill.getName().toUpperCase());
						ranks -= aSkill.getTotalRank().intValue();
					}
				}
			}
		}
		flag = (ranks <= 0);
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"SKILLTOT"};
	}

}
