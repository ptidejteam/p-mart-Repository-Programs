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

import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import java.io.File;
import java.util.LinkedList;
import java.util.Vector;
import org.jdom.Element;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.StatList;
import pcgen.util.Logging;

/**
 *@author     devon
 *@created    March 20, 2003
 *@version
 */
public class PcgCombatant extends Combatant {

	protected PlayerCharacter pc;
	protected float crAdj = 0;

	/**
	 *  Creates new PcgCombatant
	 *
	 *@param  pc  PCGen pc that this combatant represents
	 */
	public PcgCombatant(PlayerCharacter pc) {
		this.pc = pc;
		Globals.setCurrentPC(pc);
		this.init = new PcgSystemInitiative(pc);
		StatList sl = pc.getStatList();
		this.hitPoints = new SystemHP(new SystemAttribute("Constitution", sl.getTotalStatFor("CON")), pc.hitPoints(), pc.hitPoints());
		setCombatantType("PC");
	}


	/**
	 *  Constructor for the PcgCombatant object
	 *
	 *@param  pc    PCGen pc that this combatant represents
	 *@param  type  PC/Enemy/Ally/Non Combatant
	 */
	public PcgCombatant(PlayerCharacter pc, String type) {
		this(pc);
		setCombatantType(type);
	}

	public PcgCombatant(Element combatant, GMBComponent comp) {
		try {
			File pcgFile = new File(combatant.getChild("PCG").getAttribute("file").getValue());
			OpenPCGRequestMessage msg = new OpenPCGRequestMessage(comp, pcgFile, true);
			GMBus.send(msg);
			this.pc = msg.getPlayerCharacter();
			Globals.setCurrentPC(pc);
			this.init = new PcgSystemInitiative(pc);
			StatList sl = pc.getStatList();
			this.hitPoints = new SystemHP(new SystemAttribute("Constitution", sl.getTotalStatFor("CON")), pc.hitPoints(), pc.hitPoints());

			setStatus(combatant.getAttribute("status").getValue());
			setCombatantType(combatant.getAttribute("type").getValue());

			init.setBonus(combatant.getChild("Initiative").getAttribute("bonus").getIntValue());
			try {
				init.setCurrentInitiative(combatant.getChild("Initiative").getAttribute("current").getIntValue());
			}
			catch(Exception e) {
				//Not necessarily set
			}

			hitPoints.setMax(combatant.getChild("HitPoints").getAttribute("max").getIntValue());
			hitPoints.setCurrent(combatant.getChild("HitPoints").getAttribute("current").getIntValue());
			hitPoints.setSubdual(combatant.getChild("HitPoints").getAttribute("subdual").getIntValue());
			hitPoints.setState(combatant.getChild("HitPoints").getAttribute("state").getValue());
		}
		catch (Exception e) {
			Logging.errorPrint("Initiative", e);
		}
	}

	public Element getSaveElement() {
		Element retElement = new Element("PcgCombatant");
		Element initiative = new Element("Initiative");
		Element hp = new Element("HitPoints");
		Element pcg = new Element("PCG");

		pcg.setAttribute("file", pc.getFileName() + "");
		retElement.addContent(pcg);

		initiative.setAttribute("bonus", init.getBonus() + "");
		if(init.getCurrentInitiative() > 0) {
			initiative.setAttribute("current", init.getCurrentInitiative() + "");
		}
		retElement.addContent(initiative);

		hp.setAttribute("current", hitPoints.getCurrent() + "");
		hp.setAttribute("subdual", hitPoints.getSubdual() + "");
		hp.setAttribute("max", hitPoints.getMax() + "");
		hp.setAttribute("state", hitPoints.getState() + "");
		retElement.addContent(hp);

		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("status", getStatus());
		retElement.setAttribute("type", getCombatantType());

		return retElement;
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
		if (columnName.equals("Name")) {
			// Character's Name
			setName(strData);
		} else if (columnName.equals("Player")) {
			// Player's Name
			setPlayer(strData);
		} else if (columnName.equals("Status")) {
			// XML Combatant's Status
			setStatus(strData);
		} else if (columnName.equals("+")) {
			// Initative bonus
			Integer intData = new Integer(strData);
			init.setBonus(intData.intValue());
		} else if (columnName.equals("Init")) {
			// Initative
			Integer intData = new Integer(strData);
			init.setCurrentInitiative(intData.intValue());
		} else if (columnName.equals("#")) {
			// Number (for tokens)
			Integer intData = new Integer(strData);
			setNumber(intData.intValue());
		} else if (columnName.equals("HP")) {
			// Current Hit Points
			Integer intData = new Integer(strData);
			hitPoints.setCurrent(intData.intValue());
		} else if (columnName.equals("HP Max")) {
			// Maximum Hit Points
			Integer intData = new Integer(strData);
			hitPoints.setMax(intData.intValue());
		} else if (columnName.equals("Dur")) {
			// Duration
			Integer intData = new Integer(strData);
			setDuration(intData.intValue());
		}
	}


	/**
	 *  Gets the CR for the character
	 *
	 *@return    CR
	 */
	public float getCR() {
		Globals.setCurrentPC(pc);
		return pc.calcCR() + crAdj;
	}


	/**
	 *  Gets the name of the PC
	 *
	 *@return    The name
	 */
	public String getName() {
		return pc.getName();
	}


	/**
	 *  Gets the PCGen PC of the PcgCombatant object
	 *
	 *@return    The PCGen PC
	 */
	public PlayerCharacter getPC() {
		return pc;
	}


	/**
	 *  Gets the experience value for the character
	 *
	 *@return    Experience value
	 */
	public int getXP() {
		return pc.getXP();
	}


	/**
	 *  Gets the player's name of the PcgCombatant object
	 *
	 *@return    The player's name
	 */
	public String getPlayer() {
		return pc.getPlayersName();
	}

	/**
	 *  Adjusts the CR for this combatant
	 *
	 *@param  cr  new CR value
	 */
	public void setCR(float cr) {
		Globals.setCurrentPC(pc);
		this.crAdj = cr - pc.calcCR();
	}

	/**
	 *  Sets the name of the character
	 *
	 *@param  name  The new name
	 */
	public void setName(String name) {
		pc.setName(name);
		pc.setDirty(true);
	}


	/**
	 *  Sets the player's name of the PcgCombatant object
	 *
	 *@param  player  The new player's name
	 */
	public void setPlayer(String player) {
		pc.setPlayersName(player);
		pc.setDirty(true);
	}


	/**
	 *  Set the experience value for this character
	 *
	 *@param  experience  Experience value
	 */
	public void setXP(int experience) {
		pc.setXP(experience);
		pc.setDirty(true);
	}
}

