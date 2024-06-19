/*
 *  Initiative - A role playing utility to track turns
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
 *  Spell.java
 *
 *  Created on January 16, 2002, 12:27 PM
 */
package gmgen.plugin;

import java.util.LinkedList;
import java.util.Vector;
import org.jdom.Element;
import pcgen.util.Logging;

/**
 *@author     devon
 *@created    March 20, 2003
 *@version
 */
public class Event implements InitHolder {

	/**  Description of the Field */
	public SystemInitiative init;

	protected int duration;
	protected String name;
	protected String player;
	protected String effect;
	protected String status = "Active";
	protected boolean alert;


	/**
	 *  Creates new Spell
	 *
	 *@param  name      Description of the Parameter
	 *@param  player    Description of the Parameter
	 *@param  duration  Description of the Parameter
	 *@param  init      Description of the Parameter
	 */
	public Event(String name, String player, String effect, int duration, int init, boolean alert) {
		setValues(name, player, status, effect, duration, init, alert);
	}

	public Event(String name, String player, String status, String effect, int duration, int init, boolean alert) {
		setValues(name, player, status, effect, duration, init, alert);
	}

	public Event() {
	}

	public Event(Element event) {
		try {
			String name = event.getAttribute("name").getValue();
			String player = event.getAttribute("player").getValue();
			String status = event.getAttribute("status").getValue();
			String effect = event.getAttribute("effect").getValue();
			int duration = event.getChild("Initiative").getAttribute("duration").getIntValue();
			int init = event.getChild("Initiative").getAttribute("initiative").getIntValue();
			boolean alert = event.getChild("Initiative").getAttribute("alert").getBooleanValue();
			setValues(name, player, status, effect, duration, init, alert);
		}
		catch (Exception e) {
			Logging.errorPrint("Initiative", e);
		}
	}

	protected void setValues(String name, String player, String status, String effect, int duration, int init, boolean alert) {
		this.name = name;
		this.player = player;
		this.effect = effect;
		this.duration = duration;
		this.init = new SystemInitiative(0);
		this.init.setCurrentInitiative(init);
		this.alert = alert;
		this.status = status;
	}

	public Element getSaveElement() {
		Element retElement = new Element("Event");
		Element initiative = new Element("Initiative");

		initiative.setAttribute("initiative", init.getCurrentInitiative() + "");
		initiative.setAttribute("duration", getDuration() + "");
		initiative.setAttribute("alert", isAlert() + "");
		retElement.addContent(initiative);

		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("status", getStatus());
		retElement.setAttribute("effect", getEffect());
		return retElement;
	}

	/**
	 *  Gets the name attribute of the Spell object
	 *
	 *@return    The name value
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Gets the player attribute of the Spell object
	 *
	 *@return    The player value
	 */
	public String getPlayer() {
		return player;
	}

	public String getEffect() {
		return effect;
	}

	/**
	 *  Gets the status attribute of the Spell object
	 *
	 *@return    The status value
	 */
	public String getStatus() {
		return status;
	}


	/**
	 *  Gets the duration attribute of the Spell object
	 *
	 *@return    The duration value
	 */
	public int getDuration() {
		return duration;
	}


	/**
	 *  Sets the name attribute of the Spell object
	 *
	 *@param  name  The new name value
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 *  Sets the player attribute of the Spell object
	 *
	 *@param  player  The new player value
	 */
	public void setPlayer(String player) {
		this.player = player;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}
	/**
	 *  Sets the status attribute of the Spell object
	 *
	 *@param  status  The new status value
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 *  Sets the duration attribute of the Spell object
	 *
	 *@param  duration  The new duration value
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void endRound() {
	}

	/**
	 *  Decrements the duration
	 *
	 *@return    new duration
	 */
	public int decDuration() {
		duration--;
		return duration;
	}


	/**
	 *  Gets the initiative object of the Spell object
	 *
	 *@return    The initiative object
	 */
	public SystemInitiative getInitiative() {
		return init;
	}


	public boolean isAlert() {
		return alert;
	}

	public String getEndText() {
		return "Event " + getName() + " Completed or Occured";
	}

	/**
	 *  builds a vector that is intended to be turned into a table row that
	 *  contains all of this object's informaiton
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
	public Vector getRowVector(LinkedList columnOrder) {
		Vector rowVector = new Vector();
		int columns = columnOrder.size();
		for (int j = 0; j < columns; j++) {
			String columnName = (String) columnOrder.get(j);
			if (columnName.equals("Name")) { // Event's name
				rowVector.add(getName());
			}
			else if (columnName.equals("Player")) { // Player's Name who cast the spell
				rowVector.add("Owner: " + getPlayer());
			}
			else if (columnName.equals("Status")) { // Event's Status
				rowVector.add(getStatus());
			}
			else if (columnName.equals("+")) { // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("Init")) { // Event's Initiative
				rowVector.add("" + init.getCurrentInitiative());
			}
			else if (columnName.equals("Dur")) { // Event's Duration
				rowVector.add("" + getDuration());
			}
			else if (columnName.equals("#")) { // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("HP")) { // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("HP Max")) { // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("Type")) { //PC, Enemy, Ally, -
				rowVector.add("-");
			}
		}
		return rowVector;
	}


	/**
	 *  changes the value of a table field in the backend data set
	 *
	 *@param  columnOrder  A list of columns in order for the table
	 *@param  colNumber    What column number has been edited
	 *@param  data         The new value for the field
	 */
	public void editRow(LinkedList columnOrder, int colNumber, Object data) {
		Vector rowVector = new Vector();
		String columnName = (String) columnOrder.get(colNumber);
		String strData = String.valueOf(data);
		if (columnName.equals("Name")) { // Spell's Name
			setName(strData);
		}
		else if (columnName.equals("Player")) { // Name of the player who cast the spell
			setPlayer(strData);
		}
		else if (columnName.equals("Status")) { // SPell's status
			setStatus(strData);
		}
		else if (columnName.equals("Init")) { // Spell's Initiative
			Integer intData = new Integer(strData);
			init.setCurrentInitiative(intData.intValue());
		}
		else if (columnName.equals("Dur")) { // Spell's duration
			Integer intData = new Integer(strData);
			setDuration(intData.intValue());
		}
	}
}

