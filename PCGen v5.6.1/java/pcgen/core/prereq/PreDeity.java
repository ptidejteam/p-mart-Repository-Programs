/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import java.util.*;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreDeity extends Prerequisite implements PrerequisiteTest {

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
		boolean flag;

		//
		// PREDEITY:[Y|N|deity1,deity2,...,dietyn]
		//
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		flag = false;
		while (aTok.hasMoreTokens())
		{
			final String yesNoString = aTok.nextToken();
			flag = (("Y".equals(yesNoString) && (character.getDeity() != null)) || 
					("N".equals(yesNoString) && (character.getDeity() == null)) || 
					( (character.getDeity() != null) && character.getDeity().getName().equalsIgnoreCase(yesNoString))
			       );
			if (flag)
			{
				break;
			}
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"DEITY"};
	}

}
