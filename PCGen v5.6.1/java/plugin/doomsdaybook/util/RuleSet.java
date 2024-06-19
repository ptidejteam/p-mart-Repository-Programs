/*
 * Rule.java
 *
 * Created on April 25, 2003, 1:51 PM
 */

package plugin.doomsdaybook.util;

import gmgen.plugin.Dice;
import java.util.ArrayList;
/**
 *
 * @author  devon
 */
public class RuleSet extends ArrayList implements DataElement {
	ArrayList retList = new ArrayList();
	Rule retRule;
	VariableHashMap allVars;
	String title;
	String id;
	int weight;
	String usage = "private";

	/** Creates a new instance of Rule */
	/** Creates a new instance of List */
	public RuleSet(VariableHashMap allVars) {
		this(allVars, "", "", 1);
	}

	public RuleSet(VariableHashMap allVars, String title, String id) {
		this(allVars, title, id, 1);
	}

	public RuleSet(VariableHashMap allVars, String title, String id, int weight) {
		this.allVars = allVars;
		this.title = title;
		this.id = id;
		this.weight = weight;
	}

	public RuleSet(VariableHashMap allVars, String title, String id, String usage) {
		this(allVars, title, id, 1, usage);
	}

	public RuleSet(VariableHashMap allVars, String title, String id, int weight, String usage) {
		this.allVars = allVars;
		this.title = title;
		this.id = id;
		this.weight = weight;
		this.usage = usage;
	}

	public int getRange() throws Exception {
		int rangeTop = 0;
		for(int i = 0; i < this.size(); i++) {
			String key = (String)this.get(i);
			Rule value = (Rule)allVars.getDataElement(key);
			rangeTop = rangeTop + value.getWeight();
		}

		if(rangeTop <= 0) { //the die will nullpointer if it is not at least 1
			rangeTop = 1;
		}
		return rangeTop;
	}

	public ArrayList getData() throws Exception {
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
			String key = (String)this.get(i);
			Rule chkValue = (Rule)allVars.getDataElement(key);
			int valueWeight = chkValue.getWeight();
			if(valueWeight > 0) {
				weight = weight + valueWeight;
				if(weight >= choice) {
					retList.addAll(chkValue.getData());
					break;
				}
			}
		}
		return retList;
	}

	public ArrayList getData(int choice) throws Exception {
		retList.clear();
		//select the detail to return
		int weight = 0;
		//Iterate through the list of choices until the weights (from each DataValue) are greater the the num chosen as the 'choice'
		for(int i = 0; i < this.size(); i++) {
			String key = (String)this.get(i);
			Rule chkValue = (Rule)allVars.getDataElement(key);
			int valueWeight = chkValue.getWeight();
			if(valueWeight > 0) {
				weight = weight + valueWeight;
				if(weight >= choice) {
					retList.addAll(chkValue.getData());
					break;
				}
			}
		}
		return retList;
	}

	public ArrayList getLastData() {
		return retList;
	}

	public Rule getRule() throws Exception {
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
			String key = (String)this.get(i);
			Rule chkValue = (Rule)allVars.getDataElement(key);
			int valueWeight = chkValue.getWeight();
			if(valueWeight > 0) {
				weight = weight + valueWeight;
				if(weight >= choice) {
					retRule = chkValue;
					return chkValue;
				}
			}
		}
		return retRule;
	}

	public Rule getRule(int choice) throws Exception {
		//select the detail to return
		int weight = 0;
		//Iterate through the list of choices until the weights (from each DataValue) are greater the the num chosen as the 'choice'
		for(int i = 0; i < this.size(); i++) {
			String key = (String)this.get(i);
			Rule chkValue = (Rule)allVars.getDataElement(key);
			int valueWeight = chkValue.getWeight();
			if(valueWeight > 0) {
				weight = weight + valueWeight;
				if(weight >= choice) {
					retRule = chkValue;
					break;
				}
			}
		}
		return retRule;
	}

	public Rule getLastRule() {
		return retRule;
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

	public String getUsage() {
		return usage;
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

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String toString() {
		return getTitle();
	}
}
