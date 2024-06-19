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
public class TravelMethod extends Pair {

	/** Creates a new instance of TravelMethod

	 * @param Name - String containing name
	 * @param SPeed - Int containing speed in mpd

	 */
	public TravelMethod(String Name, int Speed) {
		super.setLeft(Name);
		super.setRight(new Integer(Speed));
	}

	public TravelMethod() {
		this("", 0);
	}

	public String getName() {
		return (String) super.getLeft();
	}

	public int getSpeed() {
		return (((Integer) super.getRight()).intValue());
	}

	public void setName(String Name) {
		super.setLeft(Name);
	}

	public void setSpeed(int Speed) {
		super.setRight(new Integer(Speed));
	}

}
