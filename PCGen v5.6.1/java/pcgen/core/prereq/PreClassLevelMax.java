/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreClassLevelMax extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PObject)
	 */
	public boolean passes(PObject object) {
		throw new UnsupportedOperationException("passes(PObject) is not supported for '"+this.getClass().getName()+"'");
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		final int i = getParameters().lastIndexOf('=');
		boolean oneOver = false;
		int preClass = 0;
		try
		{
			preClass = Integer.parseInt(getParameters().substring(i + 1));
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreClassLevelMax attribute: " + getParameters().substring(i + 1));
			preClass = 0;
		}
		final StringTokenizer aTok = new StringTokenizer(getParameters().substring(0, i), ",");
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if ("SPELLCASTER".equals(aString))
			{
				oneOver = character.isSpellCastermax(preClass);
			}
			else
			{
				PCClass aClass = character.getClassNamed(aString);
				if ((aClass != null) && (aClass.getLevel() <= preClass))
				{
					oneOver = true;
				}
			}
		}
		return oneOver;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"CLASSLEVELMAX"};
	}

}
