/*
 * Created on 28-Nov-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pcgen.core.prereq;

import java.util.List;

import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;


public class PreReqTestsToUse
	implements PreReqTests
{
	public boolean passesTests(PlayerCharacter aPC, PObject aObj, List argList, PreReqState preReqState)
	{
		boolean flag = true;

		// Need to check this, but the code logic
		// implies that you need to computer PRESTATE
		// before checking RESTRICT since the latter
		// depends on the value of 'flag' which the
		// former can change.  XXX

		if (preReqState.getKind().equals("PRETYPE"))
		{
			preReqState.setHandled(true);
			flag = PrereqHandler.passesPreType(preReqState.getParameterString(), aObj);
		}

		if ("RESTRICT".equals(preReqState.getKind()))
		{
			preReqState.setHandled(true);
			return PrereqHandler.passesRestrict(preReqState.getParameterString(), flag, aPC);
		}

		if (!preReqState.getKind().startsWith("PRE"))
		{
			return true;
		}

		// Strip the "PRE"
		String match = preReqState.getKind().substring(3);

		// Assume matched since starts with "PRE" (but
		// see final else clause at bottom)
		preReqState.setHandled(true);

		
		PrerequisiteTest test = PrerequisiteTestFactory.getInstance().getTest(match);
		if (test != null )
		{
			// Remove the PRE before passing to new test
			((Prerequisite)test).setKind(match);
			((Prerequisite)test).setParameters( preReqState.getParameterString());
			((Prerequisite)test).setTheObj(aObj);
			flag = test.passes(aPC);
		}
		else if (match.equals("APPLY"))
		{
			flag = preReqState.getTheObj().passesPreApplied(aPC, aObj);
		}
		else if ("RESTRICT".equals(preReqState.getKind()))
		{
			flag = PrereqHandler.passesRestrict(preReqState.getParameterString(), flag, aPC);
		}
		else if (match.startsWith("VAR"))
		{
			if (aObj instanceof Equipment)
			{
				flag = PrereqHandler.passesPreVar(preReqState.getParameterString(), preReqState.getKind(), aPC, (Equipment) aObj);
			}
			else
			{
				flag = PrereqHandler.passesPreVar(preReqState.getParameterString(), preReqState.getKind(), aPC);
			}
		}
		else if (match.equals("MULT"))
		{
			flag = PrereqHandler.passesPreMult(preReqState.getTheObj(), aPC, aObj, preReqState.toString().substring(8));
		}
		else
		{
			preReqState.setHandled( false );
		}

		return flag;
	}
}
