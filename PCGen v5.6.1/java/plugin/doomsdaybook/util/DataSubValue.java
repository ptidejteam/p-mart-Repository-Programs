/* DataSubValue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package plugin.doomsdaybook.util;

public class DataSubValue {
	private String key;
	private String value;
	private DataSubValue next;

	public DataSubValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public void put(DataSubValue sub) {
		if(next == null) {
			next = sub;
		}
		else {
			next.put(sub);
		}
	}

	public String get(String searchKey) {
		if(key.equals(searchKey)) {
			return value;
		}
		if(next == null) {
			return null;
		}
		return next.get(searchKey);
	}
}
