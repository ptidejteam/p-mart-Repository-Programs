/*
 * SpaceRule.java
 *
 * Created on April 25, 2003, 2:20 PM
 */

package plugin.doomsdaybook.util;

import java.util.ArrayList;
/**
 *
 * @author  devon
 */
public class SpaceRule implements DataElement {
	ArrayList retList = new ArrayList();

	/** Creates a new instance of SpaceRule */
	public SpaceRule() {
		retList.add(new DataValue(" "));
	}

	public String getId() {
		return " ";
	}

	public String getTitle() {
		return null;
	}

	public int getWeight() {
		return 1;
	}

	public ArrayList getData() {
		return retList;
	}

	public ArrayList getData(int choice) {
		return retList;
	}

	public ArrayList getLastData() {
		return retList;
	}

	public void trimToSize() {
	}
}
