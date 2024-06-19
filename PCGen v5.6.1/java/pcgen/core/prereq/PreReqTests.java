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


public interface PreReqTests
{
	boolean passesTests(PlayerCharacter aPC, PObject aObj, List argList, PreReqState preReqState);
}
