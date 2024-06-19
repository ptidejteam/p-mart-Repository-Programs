/*
 * Created on 28-Nov-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pcgen.core.prereq;

import java.util.List;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;


public class PreReqTestsToGain
	implements PreReqTests
{
	private PreReqTests preReqTestsToUse;

	public PreReqTestsToGain()
	{
		this.preReqTestsToUse = new PreReqTestsToUse();
	}

	public boolean passesTests(PlayerCharacter aPC, PObject aObj, List argList, PreReqState preReqState)
	{
		if (!preReqState.getKind().startsWith("PRE"))
		{
			return preReqTestsToUse.passesTests(aPC, aObj, argList, preReqState);
		}

		String match = preReqState.getKind().substring(3);
		boolean flag = true;

		PrerequisiteTest test = PrerequisiteTestFactory.getInstance().getTest(match);
		if (test != null ) {
			preReqState.setHandled( true );
			// Remove the PRE before passing to new test
			((Prerequisite)test).setKind(match);
			((Prerequisite)test).setParameters(preReqState.getParameterString());
			((Prerequisite)test).setTheObj(aObj);
			flag = test.passes(aPC);
		}
		else if ("LEVELMAX".equals(preReqState.getKind()))
		{
			preReqState.setHandled( true );
			int preLevelMax;
			try
			{
				preLevelMax = Integer.parseInt(preReqState.getParameterString());
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Badly formed PRELEVELMAX attribute: " + preReqState.getParameterString() );
				preLevelMax = 0;
			}
			flag = (aPC.getTotalPlayerLevels() <= preLevelMax);
		}
		
		return flag ? preReqTestsToUse.passesTests(aPC, aObj, argList, preReqState) : false;
	}
}
