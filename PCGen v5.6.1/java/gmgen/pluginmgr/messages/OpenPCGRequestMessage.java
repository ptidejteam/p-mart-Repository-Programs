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

import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;
import java.io.File;
import pcgen.core.PlayerCharacter;

/**
 *  Send this message to inform all components that a a call has taken
 *  place asking the current tab to open a file.
 *
 *@author     Soulcatcher
 *@created    May 23, 2003
 */
public class OpenPCGRequestMessage extends GMBMessage {
	private File pcg;
	private PlayerCharacter pc;
	private boolean blockLoadedMessage;

	/**
	 *  Constructor for the StateChangedMessage object
	 *
	 *@param  comp Component sending the state changed message
	 */
	public OpenPCGRequestMessage(GMBComponent comp, File pcg, boolean blockLoadedMessage) {
		super(comp);
		this.pcg = pcg;
		this.blockLoadedMessage = blockLoadedMessage;
	}

	/**
	 *  Gets the File that has been requested to be opened
	 *
	 *@return    The File
	 */
	public File getFile() {
		return pcg;
	}

	public boolean blockLoadedMessage() {
		return blockLoadedMessage;
	}

	public void setPlayerCharacter(PlayerCharacter pc) {
		this.pc = pc;
	}

	public PlayerCharacter getPlayerCharacter() {
		return pc;
	}
}

