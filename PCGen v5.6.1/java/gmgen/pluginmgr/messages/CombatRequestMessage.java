/*
 *  GMBMessage.java - A GMBus message
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 1999
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.pluginmgr.messages;

import gmgen.plugin.InitHolderList;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;

/**
 *  Message sending out a whole combat
 *
 *@author     Soulcatcher
 *@created    May 23, 2003
 */
public class CombatRequestMessage extends GMBMessage {
	private InitHolderList combat;

	/**
	 *  Constructor for the CombatRequestMessage object
	 *
	 *@param  comp  Component requesting the list
	 *@param  cl    Combatant List to be sent
	 */
	public CombatRequestMessage(GMBComponent comp) {
		super(comp);
	}

	/**
	 *  Gets the party Combatnat List
	 *
	 *@return    The Combatant List
	 */
	public InitHolderList getCombat() {
		return combat;
	}

	/**
	 *  Sets the list for the message
	 *
	 *@param    The Combatant List
	 */
	public void setCombat(InitHolderList combat) {
		this.combat = combat;
	}
}

