/*
 * Pair.java
 *
 * Created on July 22, 2003, 10:22 PM
 */

package plugin.overland.util;


/** Class that holds a pair of values
 * Note: THE ITEMS PASSED TO THIS CLASS ARE NOT DUPLICATED
 *	IT OPERATES ON ORIGINAL INSTANCES!  BE CAREFUL!
 * @author  Juliean Galak
 */
public abstract class Pair {
	private Object Left;  //store one item here
	private Object Right;   //store other item here

	/** Creates a new instance of TravelMethod

	 * @param Left - Left Object to add
	 * @param Right - Right Object to add

	 */
	public Pair(Object Left, Object Right) {
		this.Left=Left;
		this.Right=Right;
	}

	public Pair() {
		this(null,null);
	}

	protected Object getLeft() {
		return Left;
	}

	protected Object getRight() {
		return Right;
	}

	protected void setLeft(Object Left) {
		this.Left=Left;
	}

	protected void setRight(Object Right) {
		this.Right=Right;
	}
}
