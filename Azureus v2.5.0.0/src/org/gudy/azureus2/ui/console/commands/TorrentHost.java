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

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.tracker.host.TRHost;
import org.gudy.azureus2.core3.tracker.host.TRHostException;
import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
import org.gudy.azureus2.ui.console.ConsoleInput;

/**
 * console command to host a torrent.
 * extracted from the Torrent class written by tobias
 */
public class TorrentHost extends TorrentCommand {

	public TorrentHost()
	{
		super(new String[] { "host"}, "Hosting");
	}

	protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) {
		TOTorrent torrent = dm.getTorrent();
        if (torrent != null) {
          try {
            	TRHost	host = ci.azureus_core.getTrackerHost();
            	
            	TRHostTorrent	existing = host.getHostTorrent( torrent );
            	
            	if ( existing == null ){
            		
            		 ci.azureus_core.getTrackerHost().hostTorrent(torrent,true, false);
            		 
            	}else{
            		try{
            			existing.remove();
            			
            		}catch( Throwable e ){
            			
            			e.printStackTrace();
            		}
            	}
           
          } catch (TRHostException e) {
            e.printStackTrace(ci.out);
            return false;
          }
          return true;
        }
        return false;
	}

	public String getCommandDescriptions() {
		return("host (<torrentoptions>)\t\th\tHost or stop hosting torrent(s).");
	}

}
