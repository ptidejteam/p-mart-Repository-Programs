/*
 * TravelMethods.java
 *
 * Created on July 22, 2003, 10:17 PM
 */

package plugin.overland.util;


/** Class that holds a set of travel methods and speeds
 *
 * @author  Juliean Galak
 */
public class TravelMethods extends PairList {
	public TravelMethods() {
		super();
	}

	public void addTravelMethod(TravelMethod tm) {
		super.addPair(tm);
	}


	public TravelMethod getMethodAtI(int i) {
		return (TravelMethod) super.getElementAtI(i);
	}

}

