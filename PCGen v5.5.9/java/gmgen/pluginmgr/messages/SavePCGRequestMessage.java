/*
 *  PCLoadedMessage.java - A GMBus message
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

import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;
import pcgen.core.PlayerCharacter;


/**
 *  Message sending out a loaded PC
 *
 *@author     Soulcatcher
 *@created    May 23, 2003
 */
public class SavePCGRequestMessage extends GMBMessage {
	private PlayerCharacter pc;


	/**
	 *  Constructor for the PCLoadedMessage object
	 *
	 *@param  comp  Component requesting the list
	 *@param  cl    Combatant List to be sent
	 */
	public SavePCGRequestMessage(GMBComponent comp, PlayerCharacter pc) {
		super(comp);
		this.pc = pc;
	}


	/**
	 *  Gets the combatantList that has been sent in this message
	 *
	 *@return    The Combatant List
	 */
	public PlayerCharacter getPC() {
		return pc;
	}
}

