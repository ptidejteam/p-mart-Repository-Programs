/*
 * List.java
 *
 * Created on April 24, 2003, 4:30 PM
 */

package plugin.doomsdaybook.util;

import gmgen.plugin.Dice;
import java.util.ArrayList;
/**
 *
 * @author  devon
 */
public class List extends ArrayList implements DataElement {
	ArrayList retList = new ArrayList();
	VariableHashMap allVars;
	String title;
	String id;
	int weight;

	/** Creates a new instance of List */
	public List(VariableHashMap allVars) {
		this(allVars, "", "", 1);
	}

	public List(VariableHashMap allVars, String title, String id) {
		this(allVars, title, id, 1);
	}

	public List(VariableHashMap allVars, String title, String id, int weight) {
		this.allVars = allVars;
		this.title = title;
		this.id = id;
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getWeight() {
		return weight;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getRange() {
		int rangeTop = 0;
		for(int i = 0; i < this.size(); i++) {
			WeightedDataValue value = (WeightedDataValue)get(i);
			rangeTop = rangeTop + value.getWeight();
		}

		if(rangeTop <= 0) { //the die will nullpointer if it is not at least 1
			rangeTop = 1;
		}
		return rangeTop;
	}

	public ArrayList getData() {
		retList.clear();
		int rangeTop = getRange();
		int modifier;
		try {
			modifier = new Integer(allVars.getVal(getId() + "modifier")).intValue();
		}
		catch(Exception e) {
			modifier = 0;
		}

		// Determine which entry to choose
		Dice die = new Dice(1, rangeTop, 0);
		int choice = die.roll();
		choice = choice + modifier;
		choice = choice < 0 ? rangeTop : choice;

		//select the detail to return
		int weight = 0;
		//Iterate through the list of choices until the weights (from each DataValue) are greater the the num chosen as the 'choice'
		for(int i = 0; i < this.size(); i++) {
			WeightedDataValue chkValue = (WeightedDataValue)get(i);
			int valueWeight = chkValue.getWeight();
			if(valueWeight > 0) {
				weight = weight + valueWeight;
				if(weight >= choice) {
					retList.add(chkValue);
					break;
				}
			}
		}
		return retList;
	}

	public ArrayList getData(int choice) {
		retList.clear();
		//select the detail to return
		int weight = 0;
		//Iterate through the list of choices until the weights (from each DataValue) are greater the the num chosen as the 'choice'
		for(int i = 0; i < this.size(); i++) {
			WeightedDataValue chkValue = (WeightedDataValue)get(i);
			int valueWeight = chkValue.getWeight();
			if(valueWeight > 0) {
				weight = weight + valueWeight;
				if(weight >= choice) {
					retList.add(chkValue);
					break;
				}
			}
		}
		return retList;
	}

	public ArrayList getLastData() {
		return retList;
	}
}

