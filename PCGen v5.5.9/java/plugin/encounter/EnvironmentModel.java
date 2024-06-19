/**
 * Created on Mar 16, 2003
 */
package plugin.encounter;

import gmgen.io.ReadXML;
import gmgen.io.VectorTable;
import java.io.File;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import pcgen.util.Logging;

/**
 * @author Jerril
 *
 */
public class EnvironmentModel extends DefaultComboBoxModel {

	private String dir;

	/**
	 * Constructor for EnvironmentModel.
	 */
	public EnvironmentModel() {
		this("");
	}

	public EnvironmentModel(String parentDir) {
		super();
		dir = parentDir;
	}

	public void update() {
		VectorTable table;
		Vector row;
		ReadXML reader;
		File f = new File(dir + File.separator + "encounter_tables/environments.xml");

		this.removeAllElements();

		if (f == null) {
			Logging.errorPrint("Eek! environments.xml is missing!");
			return;
		}
		reader = new ReadXML(f);
		table = reader.getTable();

		this.addElement( new String("Generic"));

		for(int x=1; x< table.sizeY();x++) {
			try {
				this.addElement(((Vector) table.elementAt(x)).firstElement());
			} catch(NoSuchElementException e) {
				break;
			}
		}
	}
}
