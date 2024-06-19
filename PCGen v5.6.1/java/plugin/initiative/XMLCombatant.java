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
package plugin.initiative;

import gmgen.plugin.Combatant;
import gmgen.plugin.SystemAttribute;
import gmgen.plugin.SystemHP;
import gmgen.plugin.SystemInitiative;
import java.util.LinkedList;
import java.util.Vector;
import org.jdom.Element;
import pcgen.util.Logging;

/**
 *@author     devon
 *@created    March 20, 2003
 *@version
 */
public class XMLCombatant extends Combatant {
	/*
	 *  History:
	 *  March 20, 2003: Cleanup for Version 1.0
	 */

	private Element combatant;
	protected int xp;
	protected float cr;

	/**
	 *  Creates new XMLCombatant
	 *
	 *@param  combatant  XML Element containing one combatant.
	 */
	public XMLCombatant(Element combatant) {
		this.combatant = combatant;
		try {
			int strVal = combatant.getChild("Attributes").getChild("Strength").getAttribute("value").getIntValue();
			int dexVal = combatant.getChild("Attributes").getChild("Dexterity").getAttribute("value").getIntValue();
			int conVal = combatant.getChild("Attributes").getChild("Constitution").getAttribute("value").getIntValue();
			int intVal = combatant.getChild("Attributes").getChild("Intelligence").getAttribute("value").getIntValue();
			int wisVal = combatant.getChild("Attributes").getChild("Wisdom").getAttribute("value").getIntValue();
			int chaVal = combatant.getChild("Attributes").getChild("Charisma").getAttribute("value").getIntValue();

			int xp = combatant.getChild("General").getChild("Experience").getAttribute("total").getIntValue();
			int cr = combatant.getChild("General").getChild("CR").getAttribute("value").getIntValue();

			int hpVal = combatant.getChild("Combat").getChild("HitPoints").getAttribute("max").getIntValue();
			int hpCurrVal = combatant.getChild("Combat").getChild("HitPoints").getAttribute("current").getIntValue();
			int hpSubdual = 0;
			try {
				hpSubdual = combatant.getChild("Combat").getChild("HitPoints").getAttribute("subdual").getIntValue();
			}
			catch (Exception e) {
				//Subdual is not set, we will jsut use 0
			}

			try {
				setCombatantType(combatant.getAttribute("type").getValue());
			}
			catch (Exception e) {
			}

			String sInitBonus = combatant.getChild("Combat").getChild("Initiative").getAttribute("Misc").getValue();
			int initBonus = 0;
			if (sInitBonus.startsWith("+")) {
				sInitBonus = sInitBonus.substring(1);
				initBonus = Integer.parseInt(sInitBonus);
			}
			else {
				initBonus = Integer.parseInt(sInitBonus);
			}
			createSystemVals(dexVal, conVal, hpVal, hpCurrVal, hpSubdual, initBonus);

			try {
				init.setCurrentInitiative(combatant.getChild("Combat").getChild("Initiative").getAttribute("current").getIntValue());
			}
			catch (Exception e) {
				//Current initiative not necessarily set
			}

			try {
				hitPoints.setState(combatant.getChild("Combat").getChild("HitPoints").getAttribute("subdual").getValue());
			}
			catch (Exception e) {
				//Hit Point state not necessarily set
			}
		}
		catch (Exception e) {
			Logging.errorPrint(e.getMessage(), e);
		}
	}


	/**
	 *  Constructor for the XMLCombatant object.  Used for creation without an
	 *  existing XML representation of the combatant
	 *
	 *@param  name       Name of the combatant
	 *@param  player     Name of the Player
	 *@param  dexVal     Value of the Dexterity Attribute (used for the Initiative bonus)
	 *@param  conVal     Value of the Constitution Attribute (used for the determining when a person dies)
	 *@param  hpVal      Combatant's maximum hit points
	 *@param  hpCurrVal  Combatant's current hit points
	 *@param  initBonus  Combatant's initiative bonus (excluding dexterity)
	 */
	public XMLCombatant(String name, String player, int dexVal, int conVal, int hpVal, int hpCurrVal, int hpSubdual, int initBonus, String type, float cr) {
		this(name, player, 10, dexVal, conVal, 10, 10, 10, 0, 0, 0, hpVal, hpCurrVal, hpSubdual, initBonus, type, cr);
	}

	public XMLCombatant(String name, String player, int strVal, int dexVal, int conVal, int intVal, int wisVal, int chaVal, int fortSave, int refSave, int willSave, int hpVal, int hpCurrVal, int hpSubdual, int initBonus, String type, float cr) {
		this.comType = type;
		createSystemVals(dexVal, conVal, hpVal, hpCurrVal, hpSubdual, initBonus);
		int dexMod = init.getAttribute().getModifier();
		int miscMod = initBonus - dexMod;
		combatant = new Element("Character");
		combatant.setAttribute("name", name);
		combatant.setAttribute("player", player);
		Element attributes = new Element("Attributes");
		attributes.addContent(new Element("Strength").setAttribute("value", strVal + ""));
		attributes.addContent(new Element("Dexterity").setAttribute("value", dexVal + ""));
		attributes.addContent(new Element("Constitution").setAttribute("value", conVal + ""));
		attributes.addContent(new Element("Intelligence").setAttribute("value", intVal + ""));
		attributes.addContent(new Element("Wisdom").setAttribute("value", wisVal + ""));
		attributes.addContent(new Element("Charisma").setAttribute("value", chaVal + ""));
		combatant.addContent(attributes);
		Element saves = new Element("Saves");
		saves.addContent(new Element("Fortitude").setAttribute("total", fortSave + ""));
		saves.addContent(new Element("Reflex").setAttribute("total", refSave + ""));
		saves.addContent(new Element("Will").setAttribute("total", willSave + ""));
		combatant.addContent(saves);

		Element general = new Element("General");
		general.addContent(new Element("Experience").setAttribute("total", 0 + ""));
		general.addContent(new Element("CR").setAttribute("value", cr + ""));
		combatant.addContent(general);
		this.cr = cr;

		Element combat = new Element("Combat");
		combat.addContent(new Element("Initiative").setAttribute("mod", formatBonus(initBonus)).setAttribute("Dex", formatBonus(dexMod)).setAttribute("Misc", formatBonus(miscMod)));
		combat.addContent(new Element("HitPoints").setAttribute("max", hpVal + "").setAttribute("current", hpCurrVal + ""));
		combatant.addContent(combat);
	}

	//Creates the SystemInitiative and SystemHP objects
	private void createSystemVals(int dexVal, int conVal, int hpVal, int hpCurrVal, int subdual, int initBonus) {
		init = new SystemInitiative(new SystemAttribute("Dexterity", dexVal), initBonus);
		hitPoints = new SystemHP(new SystemAttribute("Constitution", conVal), hpVal, hpCurrVal);
		hitPoints.setSubdual(subdual);
	}

	//Returns a string of a number with a + or - before it
	private String formatBonus(int bonus) {
		if (bonus >= 0) {
			return "+" + bonus;
		}
		else {
			return "-" + bonus;
		}
	}

	/**
	 *  Gets the player attribute of the XMLCombatant
	 *
	 *@return    The player value
	 */
	public String getPlayer() {
		return combatant.getAttribute("player").getValue();
	}

	/**
	 *  Gets the name attribute of the XMLCombatant
	 *
	 *@return    The name value
	 */
	public String getName() {
		return combatant.getAttribute("name").getValue();
	}


	/**
	 *  Sets the Initative Bonues of the XMLCombatant
	 *
	 *@param  initBonus  The new Init Bonus value
	 */
	public void setInitBonus(int initBonus) {
		init.setBonus(initBonus);
	}


	/**
	 *  Sets the name attribute of the XMLCombatant
	 *
	 *@param  name  The new name value
	 */
	public void setName(String name) {
		combatant.getAttribute("player").setValue(name);
	}


	/**
	 *  Sets the player attribute of the XMLCombatant
	 *
	 *@param  player  The new player value
	 */
	public void setPlayer(String player) {
		combatant.getAttribute("player").setValue(player);
	}

	public int getSave(String Name) {
		try {
			String saveBonus = combatant.getChild("Saves").getChild(Name).getAttribute("total").getValue();
			if (saveBonus.startsWith("+")) {
				saveBonus = saveBonus.substring(1);
			}
			return Integer.parseInt(saveBonus);
		}
		catch (Exception e) {
			Logging.errorPrint(e.getMessage(), e);
			return 0;
		}
	}

	public void setSave(String Name, int value) {
		try {
			combatant.getChild("Saves").getChild(Name).setAttribute("total", value + "");
		}
		catch (Exception e) {
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	public int getAttribute(String Name) {
		try {
			return combatant.getChild("Attributes").getChild(Name).getAttribute("value").getIntValue();
		}
		catch (Exception e) {
			Logging.errorPrint(e.getMessage(), e);
			return 10;
		}
	}

	/**
	 *  Gets the XML element attribute of the XMLCombatant
	 *
	 *@return    The XML element
	 */
	public Element getElement() {
		return combatant;
	}

	/** Gets the experience value for the character
	 * @return Experience value
	 */
	public int getXP() {
		return xp;
	}

	/** Sets the CR value for the character
	 */
	public void setXP(int xp) {
		this.xp = xp;
	}

	/** Gets the CR value for the character
	 * @return CR value
	 */
	public float getCR() {
		return cr;
	}

	public void setCR(float cr) {
		this.cr = cr;
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

		//Determine which row was edited
		if (columnName.equals("Name")) { // Character's Name
			setName(strData);
		}
		else if (columnName.equals("Player")) { // Player's Name
			setPlayer(strData);
		}
		else if (columnName.equals("Status")) { // XML Combatant's Status
			setStatus(strData);
		}
		else if (columnName.equals("+")) { // Initative bonus
			Integer intData = new Integer(strData);
			setInitBonus(intData.intValue());
		}
		else if (columnName.equals("Init")) { // Initative
			Integer intData = new Integer(strData);
			init.setCurrentInitiative(intData.intValue());
		}
		else if (columnName.equals("#")) { // Number (for tokens)
			Integer intData = new Integer(strData);
			setNumber(intData.intValue());
		}
		else if (columnName.equals("HP")) { // Current Hit Points
			Integer intData = new Integer(strData);
			hitPoints.setCurrent(intData.intValue());
		}
		else if (columnName.equals("HP Max")) { // Maximum Hit Points
			Integer intData = new Integer(strData);
			hitPoints.setMax(intData.intValue());
		}
		else if (columnName.equals("Dur")) { // Duration
			Integer intData = new Integer(strData);
			setDuration(intData.intValue());
		}
	}

	/**
	 *  gets a new XML Element from the XMLCombatant, intended to be added to a new XML Document.
	 *  Used for saving out to file.
	 *
	 *@return    The New Element
	 */
	public Element getSaveElement() {
		Element retElement = (Element) combatant.clone();
		retElement.detach();
		int dexMod = init.getAttribute().getModifier();
		int initBonus = init.getModifier();
		int miscMod = init.getBonus();
		retElement.getChild("Attributes").getChild("Dexterity").setAttribute("value", init.getAttribute().getValue() + "");
		retElement.getChild("Attributes").getChild("Constitution").setAttribute("value", hitPoints.getAttribute().getValue() + "");
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("max", hitPoints.getMax() + "");
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("current", hitPoints.getCurrent() + "");
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("subdual", hitPoints.getSubdual() + "");
		retElement.getChild("Combat").getChild("HitPoints").setAttribute("state", hitPoints.getState() + "");
		retElement.getChild("Combat").getChild("Initiative").setAttribute("mod", formatBonus(initBonus));
		retElement.getChild("Combat").getChild("Initiative").setAttribute("Dex", formatBonus(dexMod));
		retElement.getChild("Combat").getChild("Initiative").setAttribute("Misc", formatBonus(miscMod));
		if(init.getCurrentInitiative() > 0) {
			retElement.getChild("Combat").getChild("Initiative").setAttribute("current", init.getCurrentInitiative() + "");
		}
		retElement.getChild("General").getChild("CR").setAttribute("value", cr + "");
		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("type", getCombatantType());
		return retElement;
	}
}

