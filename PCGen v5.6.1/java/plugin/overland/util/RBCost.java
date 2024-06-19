/*
 * TravelMethod.java
 *
 * Created on July 22, 2003, 10:22 PM
 */

package plugin.overland.util;


/** Class that holds a single travel method and its speed.  Note: This is a wrapper for the Pair class
 *
 * @author  Juliean Galak
 */
public class RBCost extends Pair {

	/** Creates a new instance of TravelMethod

	 * @param Name - String containing name
	 * @param SPeed - Int containing speed in mpd

	 */
	public RBCost(String Name, float Cost) {
		super.setLeft(Name);
		super.setRight(new Float(Cost));
	}
	public RBCost() {
		this("", 0);
	}

	public String getName() {
		return (String) super.getLeft();
	}

	public float getCost() {
		return (((Float) super.getRight()).floatValue());
	}

	public void setName(String Name) {
		super.setLeft(Name);
	}

	public void setCost(float Cost) {
		super.setRight(new Float(Cost));
	}
}
