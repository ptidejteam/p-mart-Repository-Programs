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
 *  XMLCombatant.java
 *
 *  Created on January 24, 2002, 11:15 AM
 *
 *  This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;

import java.util.LinkedList;
import java.util.Vector;

/**
 *@author     devon
 *@created    March 20, 2003
 *@version
 */
public abstract class Combatant implements InitHolder {
	/*
	 *  History:
	 *  March 20, 2003: Cleanup for Version 1.0
	 */

	/**  The object that contains all initiative information */
	public SystemInitiative init;

	protected SystemHP hitPoints;
	protected String status = "";
	protected LinkedList effects = new LinkedList();
	protected int number;
	protected int duration;
	protected String comType = "Enemy";


	/**
	 *  Creates new Combatant
	 */
	public Combatant() {
	}

	public void endRound() {
		hitPoints.endRound();
	}

	/**
	 *  Gets the Combatant Type.
	 *
	 *@return    The status value
	 */
	public String getCombatantType() {
		return comType;
	}

	/**
	 *  Sets the Combatant Type.
	 *
	 */
	public void setCombatantType(String comType) {
		this.comType = comType;
	}

	public LinkedList getEffects() {
		return effects;
	}

	public void addEffect(Effect effect) {
		effects.add(effect);
	}

	/**
	 *  Gets the SystemInitiative of the Combatant
	 *
	 *@return    The SystemInitiative value
	 */
	public SystemInitiative getInitiative() {
		return init;
	}

	/**
	 *  Gets the SystemHP of the Combatant
	 *
	 *@return    The SystemHP value
	 */
	public SystemHP getHP() {
		return hitPoints;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 *  Decrements the duration
	 *
	 *@return    new duration
	 */
	public int decDuration() {
		if(duration > 0) {
			duration--;
			if(duration == 0) {
				setStatus(hitPoints.endDurationedStatus());
			}
		}

		return duration;
	}

	/**
	 *  Gets the number attribute of the XMLCombatant object
	 *
	 *@return    The number value
	 */
	public int getNumber() {
		return number;
	}


	/**
	 *  Sets the number attribute of the XMLCombatant object
	 *
	 *@param  number  The new number value
	 */
	public void setNumber(int number) {
		this.number = number;
	}


	/**
	 *  Creates a Vector intended for use as a row in a JTable
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
	public Vector getRowVector(LinkedList columnOrder) {
		Vector rowVector = new Vector();
		int columns = columnOrder.size();

		//Iterate through all the columns, and create the vector in that order
		for (int j = 0; j < columns; j++) {
			String columnName = (String) columnOrder.get(j);
			if (columnName.equals("Name")) { // Character's Name
				rowVector.add(getName());
			}
			else if (columnName.equals("Player")) { // Player's Name
				rowVector.add(getPlayer());
			}
			else if (columnName.equals("Status")) { // Status of XMLCombatant
				rowVector.add(getStatus());
			}
			else if (columnName.equals("+")) { // Initiative bonus
				rowVector.add("" + init.getModifier());
			}
			else if (columnName.equals("Init")) { // Initiative #
				rowVector.add("" + init.getCurrentInitiative());
			}
			else if (columnName.equals("Dur")) { // Duration
				if(duration == 0) {
					rowVector.add("");
				}
				else {
					rowVector.add("" + getDuration());
				}
			}
			else if (columnName.equals("#")) { // Number (for tokens)
				rowVector.add("" + number);
			}
			else if (columnName.equals("HP")) { // Current Hit Points
				int hp = hitPoints.getCurrent();
				int sub = hitPoints.getSubdual();
				if(sub == 0) {
					rowVector.add("" + hp);
				}
				else if(sub > 0) {
					rowVector.add(hp + "/" + sub + "s");
				}
			}
			else if (columnName.equals("HP Max")) { // Max Hit Points
				rowVector.add("" + hitPoints.getMax());
			}
			else if (columnName.equals("Type")) { //PC, Enemy, Ally, Non-Com
				rowVector.add(comType);
			}
		}
		return rowVector;
	}


	/**
	 *  Gets the status of the Combatant
	 *
	 *@return    The status value
	 */
	public String getStatus() {
		return status;
	}


	/**
	 *  Sets the status of the Combatant
	 *
	 *@param  status  The new status value
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**  Stabilizes the XMLCombatant */
	public void stabilize() {
		setStatus(hitPoints.stabilize());
	}


	/**  Raises a dead XMLCombatant */
	public void raise() {
		setStatus(hitPoints.raise());
	}


	/**  Kills the XMLCombatant */
	public void kill() {
		setStatus(hitPoints.kill());
	}


	/**  Causes the XMLCombatant to bleed for 1 point of damage */
	public void bleed() {
		setStatus(hitPoints.bleed());
	}


	/**
	 *  Does damage to the XMLCombatant
	 *
	 *@param  damage  number of points of damage to do
	 */
	public void damage(int damage) {
		setStatus(hitPoints.damage(damage));
	}


	/**
	 *  Does subdual damage to the Combatant
	 *
	 *@param  damage  number of points of damage to do
	 */
	public void subdualDamage(int damage) {
		setStatus(hitPoints.subdualDamage(damage));
	}

	/**
	 *  Does non lethal damage to the Combatant
	 *
	 *@param  damage  number of points of damage to do
	 */
	public void nonLethalDamage(boolean type) {
		setStatus(hitPoints.nonLethalDamage(type));
		if(type) {
			setDuration(new Dice(4,1).roll() + 1);
		}
		else {
			setDuration(1);
		}
	}

	/**
	 *  Heals the XMLCombatant
	 *
	 *@param  heal  amount of healing to do
	 */
	public void heal(int heal) {
		setStatus(hitPoints.heal(heal));
	}

	/**
	 * Returns a String representation of this combatant.
	 */
	public String toString()
	{
		return getName();
	}

	public abstract void setName(String name);
	public abstract int getXP();
	public abstract void setXP(int xp);
	public abstract float getCR();
	public abstract void setCR(float cr);
}

