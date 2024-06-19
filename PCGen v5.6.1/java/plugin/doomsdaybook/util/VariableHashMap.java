/*
 *  RPGeneration - A role playing utility generate interesting things
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 * VariableHashMap.java
 *
 * Created on November 1, 2002, 1:15 PM
 */

package plugin.doomsdaybook.util;

import java.util.ArrayList;
import java.util.HashMap;
//import org.jdom.*;
/**
 *
 * @author  devon
 */
public class VariableHashMap extends HashMap {
	private ArrayList initialize = new ArrayList();
	private HashMap dataElements;

	/** Creates a new instance of VariableHashMap */
	public VariableHashMap() {
		this.dataElements = new HashMap();
	}

	public VariableHashMap(HashMap dataElements) {
		this.dataElements = dataElements;
	}

	public void setVar(String key, String value) throws variableException {
		if(get(key) == null) {
			throw new variableException("Variable " + key + " does not exist, cannot set value");
		}
		put(key, value);
	}

	public String getVal(String key) throws variableException {
		if(get(key) == null) {
			throw new variableException("Variable " + key + " does not exist, cannot get value");
		}
		return (String)get(key);
	}

	public String addVar(String key, int add) throws variableException {
		if(get(key) == null) {
			throw new variableException("Variable " + key + " does not exist, cannot add to value");
		}
		if(get(key) == "") {
			put(key, "0");
		}
		int val = Integer.parseInt((String) get(key));
		val += add;
		put(key, val + "");
		return val + "";
	}

	public String subtractVar(String key, int subtract) throws variableException {
		if(get(key) == null) {
			throw new variableException("Variable " + key + " does not exist, cannot subtract from value");
		}
		if(get(key) == "") {
			put(key, "0");
		}
		int val = Integer.parseInt((String) get(key));
		val -= subtract;
		put(key, val + "");
		return val + "";
	}

	public String multiplyVar(String key, int multiply) throws variableException {
		if(get(key) == null) {
			throw new variableException("Variable " + key + " does not exist, cannot multiply by value");
		}
		if(get(key) == "") {
			put(key, "0");
		}
		int val = Integer.parseInt((String) get(key));
		val *= multiply;
		put(key, val + "");
		return val + "";
	}

	public String divideVar(String key, int divide) throws variableException {
		if(get(key) == null) {
			throw new variableException("Variable " + key + " does not exist, cannot divide by value");
		}
		if(get(key) == "") {
			put(key, "0");
		}
		int val = Integer.parseInt((String) get(key));
		val /= divide;
		put(key, val + "");
		return val + "";
	}

	public void addDataElement(DataElement dataElement) {
		String key = dataElement.getId();
		dataElement.trimToSize();
		dataElements.put(key, dataElement);
	}

	public DataElement getDataElement(String key) throws Exception {
		DataElement de = (DataElement)dataElements.get(key);
		if(de == null) {
			throw new Exception("Data Set " + key + " Does Not Exist");
		}
		return de;
	}

	public void addInitialOperations(Operation op) {
		initialize.add(op);
	}

	public void initialize() throws variableException {
		doOperation(initialize);
	}

	public void doOperation(ArrayList ops) throws variableException {
		for(int i = 0; i < ops.size(); i++) {
			doOperation((Operation)ops.get(i));
		}
	}

	public void doOperation(Operation op) throws variableException {
		String type = op.getType();
		String key = op.getKey();
		String value = op.getValue();

		if(type.equals("Set")) {
			value = parse(value);
			setVar(key, value);
		}
		else if(type.equals("Add")) {
			int val = Integer.parseInt(parse(value));
			addVar(key, val);
		}
		else if(type.equals("Subtract")) {
			int val = Integer.parseInt(parse(value));
			subtractVar(key, val);
		}
		else if(type.equals("Multiply")) {
			int val = Integer.parseInt(parse(value));
			multiplyVar(key, val);
		}
		else if(type.equals("Divide")) {
			int val = Integer.parseInt(parse(value));
			divideVar(key, val);
		}
	}

	public String parse(String val) {
		String retString = val;
		if(val.matches("\\$\\{.*?\\}.*")) {
			String var = val.substring(val.indexOf("${") + 2, val.indexOf("}"));
			String value = (String)get(var);
			if(value == null) {
				value = "";
			}
			retString = val.replaceFirst("\\$\\{.*?\\}", value) ;
		}
		return retString;
	}
}

