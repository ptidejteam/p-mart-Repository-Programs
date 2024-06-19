/*
 * DataElement.java
 *
 * Created on April 24, 2003, 4:14 PM
 */
package plugin.doomsdaybook.util;

import java.util.ArrayList;
/**
 *
 * @author  devon
 */
public interface DataElement {
	public String getId();
	public String getTitle();
	public int getWeight();
	public ArrayList getData() throws Exception;
	public ArrayList getData(int choice) throws Exception;
	public ArrayList getLastData() throws Exception;
	public void trimToSize();
}

