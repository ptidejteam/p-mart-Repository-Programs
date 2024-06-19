/*
 * WeightedDataValue.java
 *
 * Created on April 24, 2003, 4:35 PM
 */

package plugin.doomsdaybook.util;

/**
 *
 * @author  devon
 */
public class WeightedDataValue extends DataValue {
	private int weight;

	/** Creates a new instance of WeightedDataValue */
	public WeightedDataValue() {
		super();
		weight = 1;
	}

	public WeightedDataValue(String value, int weight) {
		super(value);
		this.weight = weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}
}
