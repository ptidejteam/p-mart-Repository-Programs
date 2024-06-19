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

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreClass extends Prerequisite implements PrerequisiteTest {

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
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		int numberRequired = 1;
		String aString = aTok.nextToken();

		try
		{
			numberRequired = Integer.parseInt(aString);
		}
		catch (NumberFormatException nfe)
		{
			// Ignore, as its still a valid format
			numberRequired = 1;
		}

		List classesToTest = new ArrayList();

		for (; ;)
		{
			int i = aString.indexOf('=');
			if (i > 0)
			{
				classesToTest.add(aString.substring(0, i));
				int preClass = Integer.parseInt(aString.substring(i + 1));

				for (i = 0; i < classesToTest.size(); ++i)
				{
					boolean passes = false;
					aString = (String) classesToTest.get(i);
					if ("SPELLCASTER".equals(aString))
					{
						if (character.isSpellCaster(preClass))
						{
							passes = true;
						}
					}
					else if (aString.startsWith("SPELLCASTER."))
					{
						if (character.isSpellCaster(aString.substring(12), preClass))
						{
							passes = true;
						}
					}
					else
					{
						final PCClass aClass = character.getClassNamed(aString);
						if ((aClass != null) && (aClass.getLevel() >= preClass))
						{
							passes = true;
						}
					}

					if (passes)
					{
						if (--numberRequired == 0)
						{
							return true;
						}
					}
				}
				classesToTest.clear();
			}
			else
			{
				classesToTest.add(aString.toUpperCase());
			}

			if (!aTok.hasMoreTokens())
			{
				break;
			}
			aString = aTok.nextToken();
		}
		return false;	
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"CLASS"};
	}

}
