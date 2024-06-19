/*
 * Created on 01-Dec-2003
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
public class PreCheck extends Prerequisite implements PrerequisiteTest {

	public PreCheck() {
		super();
	}
	
	
	public String[] kindsHandled() {
		return new String[]{"CHECK", "CHECKBASE"};
	}
	
	
	/* (non-Javadoc)
	 * @see pcgen.core.prereq.Prerequisite#passes(pcgen.core.PObject)
	 */
	public boolean passes(PObject object) {
		throw new UnsupportedOperationException("passes(PObject) is not supported for '"+this.getClass().getName()+"'");
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.Prerequisite#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		// PRECHECK:x,check.val,check.val
		// PRECHECKBASE:x,Fortitude.11
		boolean isBase = getKind().endsWith("BASE");
		final StringTokenizer tokeniser = new StringTokenizer(getParameters(), ",=.");
		String minMatchesStr = tokeniser.nextToken();
		int minMatches;
		try
		{
			minMatches = Integer.parseInt(minMatchesStr); // number we must match
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Badly formed PRECHECK: " + getParameters());
			minMatches = 0;
		}
		while (tokeniser.hasMoreTokens())
		{
			final String checkName = tokeniser.nextToken();
			final int val = character.getVariableValue(tokeniser.nextToken(), "").intValue();
			final int ci = SystemCollections.getIndexOfCheck(checkName);
			if (ci < 0)
			{
				continue;
			}
			if ((int) character.getBonus(ci + 1, !isBase) >= val)
			{
				--minMatches;
			}
		}
		return (minMatches <= 0);	}

}
