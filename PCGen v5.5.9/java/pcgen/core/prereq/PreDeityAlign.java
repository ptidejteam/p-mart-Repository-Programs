/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SystemCollections;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreDeityAlign extends Prerequisite implements PrerequisiteTest {

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

		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		flag = false;
		while (aTok.hasMoreTokens())
		{
			String aString;
			String tok = aTok.nextToken();
			try
			{
				aString = SystemCollections.getShortAlignmentAtIndex(Integer.parseInt(tok));
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed PRESR attribute: " + tok);
				aString = "";
			}
			if (character.getDeity() != null)
			{
				flag = (character.getDeity().getAlignment().equalsIgnoreCase(aString));
				if (flag)
				{
					break;
				}
			}
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"DEITYALIGN"};
	}

}
