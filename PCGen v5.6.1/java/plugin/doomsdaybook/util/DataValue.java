/*
 * DataValue.java
 *
 * Created on April 24, 2003, 4:23 PM
 */

package plugin.doomsdaybook.util;

/**
 *
 * @author  devon
 */
public class DataValue {

	public String value;
	public DataSubValue subvalue;

	public DataValue() {
		value = "";
	}

	public DataValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void addSubValue(String key, String subValue) {
		if(subvalue != null) {
			subvalue.put(new DataSubValue(key, subValue));
		}
		else {
			subvalue = new DataSubValue(key, subValue);
		}
	}

	public String getSubValue(String key) {
		if(subvalue != null) {
			return subvalue.get(key);
		}
		else {
			return null;
		}
	}
}
