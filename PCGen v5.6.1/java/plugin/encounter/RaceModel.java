/**
 * Created on Mar 12, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package plugin.encounter;

import java.util.Iterator;
import javax.swing.DefaultListModel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Race;

/**
 * @author Jerril
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RaceModel extends DefaultListModel {

	/**
	 * Constructor for RaceModel.
	 */
	public RaceModel() {
		super();
	}

	public void update(){
		clear();

		for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
		{
			final Race aRace = (Race) it.next();

			if (!contains(aRace.toString()));
				this.addElement(aRace.toString());
				this.removeElement(Globals.getRaceMap().get(Constants.s_NONESELECTED).toString());
		}
	}

}
