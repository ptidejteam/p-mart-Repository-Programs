/*
 * Created on 04/12/2004
 * Created by Paul Duran
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */
package org.gudy.azureus2.ui.console.commands;

import java.util.List;
import java.util.Vector;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.ui.console.ConsoleInput;

/**
 * console command to start a torrent.
 * extracted from the Torrent class written by tobias
 */
public class TorrentStart extends TorrentCommand {

	// we use this flag to effectively pass data between the
	// execute() and the performCommand() methods, since execute
	// will call performCommand
	private boolean startNow;
	
	public TorrentStart()
	{
		super( new String[] { "start" , "s" }, "Starting" );
	}
	public void execute(String commandName, ConsoleInput console, List args) {
		startNow = false;
		Vector newargs = new Vector(args);
		if (!newargs.isEmpty() && newargs.contains("now") ) {
			newargs.removeElement("now");
			startNow = true;
		}
		super.execute(commandName, console, args);
	}
	protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args)
	{
		try {
			dm.setStateWaiting();
			if( startNow )
			{
				dm.startDownloadInitialized(true);
			}
		} catch (Exception e) {
			e.printStackTrace(ci.out);
			return false;
		}
		return true;
	}

	public String getCommandDescriptions() {
		return "start (<torrentoptions>) [now]\ts\tStart torrent(s).";
	}
	
	public String getHelpExtra()
	{
		return ("<torrentoptions> can be one of:\n"+
		"<#>\t\tNumber of a torrent. You have to use 'show torrents' first. as the number is taken from there.\n"+
		"all\t\tCommand is applied to all torrents\n"+
		"hash <hash>\tApplied to torrent with the hash <hash> as given in the xml output or extended torrent info ('show <#>').");
	}
}
