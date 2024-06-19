/*
 * InfoInventory.java
 * Copyright 2003 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 16, 2002, 3:30 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:55 $
 *
 */

package pcgen.core.prereq;


import java.util.*;

import pcgen.core.PObject;


/**
 * @author frugal@purplewombat.co.uk
 *
 */
public class Prerequisite {
	private String kind;
	private String key=null;
	private String subKey=null;
	private String logical=null;
	private java.util.List prerequisites = new java.util.ArrayList();
	private String operator=null;
	private String operand=null;
	private boolean countMultiples;
	
	private String parameters;
	private PObject theObj;
	
	public Prerequisite() {
	}

	
	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return Returns the parameters.
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @param parameters The parameters to set.
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}


	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLogical() {
		return logical;
	}
	public void setLogical(String logical) {
		this.logical = logical;
	}
	public java.util.List getPrerequisites() {
		return prerequisites;
	}
	public void setPrerequisites(java.util.List prerequisites) {
		this.prerequisites = prerequisites;
	}
	public void addPrerequisite(Prerequisite prerequisite) {
		this.prerequisites.add(prerequisite);
	}

	/**
	 * @return Returns the operand.
	 */
	public String getOperand() {
		return operand;
	}

	/**
	 * @param operand
	 *          The operand to set.
	 */
	public void setOperand(String operand) {
		this.operand = operand;
	}

	/**
	 * @return Returns the operator.
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *          The operator to set.
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("<prereq ");

		if (kind != null) {
			buf.append("kind=\"");
			buf.append(kind);
			buf.append("\" ");
		}
		if (countMultiples) {
			buf.append("count-multiples=\"true\" ");
		}

		if (key != null) {
			buf.append("key=\"");
			buf.append(key);
			buf.append("\" ");
		}

		if (subKey != null && !subKey.equals("")) {
			buf.append("sub-key=\"");
			buf.append(subKey);
			buf.append("\" ");
		}

		if (logical != null) {
			buf.append("logical=\"");
			buf.append(logical);
			buf.append("\" ");
		}
		if (operator != null) {
			buf.append("operator=\"");
			buf.append(operator);
			buf.append("\" ");
		}
		if (operand != null) {
			buf.append("operand=\"");
			buf.append(operand);
			buf.append("\" ");
		}

		buf.append(">\n");

		if (prerequisites.size() > 0) {
			for (Iterator iter = prerequisites.iterator(); iter.hasNext();) {
				Prerequisite element = (Prerequisite) iter.next();
				buf.append(element.toString());
			}
		}

		buf.append("</prereq>\n");
		return buf.toString();
	}

	/**
	 * @return Returns the countMultiples.
	 */
	public boolean isCountMultiples() {
		return countMultiples;
	}

	/**
	 * @param countMultiples The countMultiples to set.
	 */
	public void setCountMultiples(boolean countMultiples) {
		this.countMultiples = countMultiples;
	}

	/**
	 * @return Returns the subKey.
	 */
	public String getSubKey() {
		return subKey;
	}

	/**
	 * @param subKey The subKey to set.
	 */
	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}


	/**
	 * @return the PObject we are being called for
	 */
	public PObject getTheObj()
	{
		return theObj;
	}

	/**
	 * @param pObject the PObject we are being called for
	 */
	public void setTheObj(PObject object)
	{
		theObj = object;
	}


}
