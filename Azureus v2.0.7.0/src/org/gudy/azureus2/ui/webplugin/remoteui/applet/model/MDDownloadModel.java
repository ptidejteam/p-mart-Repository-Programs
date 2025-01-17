/*
 * File    : MDDownloadModel.java
 * Created : 29-Jan-2004
 * By      : parg
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.ui.webplugin.remoteui.applet.model;

/**
 * @author parg
 *
 */


import javax.swing.table.*;

import org.gudy.azureus2.plugins.download.*;
import org.gudy.azureus2.ui.webplugin.remoteui.plugins.RPException;

public class 
MDDownloadModel
	extends AbstractTableModel
{
	public static String[]	column_names = { "#", "Name", "Size", "Downloaded", "Done", "State", "Seeds", "Peers" };
	
	protected DownloadManager	download_manager;
	protected Download[]		downloads;
	
	public
	MDDownloadModel(
		DownloadManager		_download_manager )
	{
		download_manager	= _download_manager;
		
		loadData();
	}
	
	public void
	refresh()
	{
		loadData();
		
		fireTableDataChanged();
	}
	
	protected void
	loadData()
	{
		downloads = download_manager.getDownloads();
	}
	
	public int 
	getColumnCount() 
	{ 
		return( column_names.length );
	}

	public int 
	getRowCount() 
	{ 
		return( downloads.length );
	}
	
	public Object 
	getValueAt(
		int row, 
		int col ) 
	{
		Download				download	= downloads[row];
		DownloadAnnounceResult	announce	= download.getLastAnnounceResult();
		DownloadScrapeResult	scrape		= download.getLastScrapeResult();
		
		if ( col == 0 ){
			
			return( new Long(row));
			
		}else if ( col == 1 ){
				
			return( download.getTorrent().getName());
				
		}else if ( col == 2 ){
			
			return( new Long( download.getTorrent().getSize()));
			
		}else if ( col == 3 ){
			
			return( new Long( download.getStats().getDownloaded()));
			
		}else if ( col == 4 ){
			
			return(new Integer( download.getStats().getCompleted()));
			
		}else if ( col == 5 ){
			
			return( download.getStats().getStatus());
			
		}else if ( col == 6 ){
			
			return( announce.getSeedCount()+"("+(scrape.getSeedCount()==-1?0:scrape.getSeedCount())+")");
			
		}else if ( col == 7 ){
			
			return( announce.getNonSeedCount()+"("+(scrape.getNonSeedCount()==-1?0:scrape.getNonSeedCount())+")");
		}
		
		return( null );
	}

	public String 
	getColumnName(
		int column ) 
	{
		return(column_names[ column ]);
	}
	
	public Class 
	getColumnClass(
		int col ) 
	{
		return getValueAt(0,col).getClass();
	}
	
	public boolean 
	isCellEditable(
		int row, 
		int col )
	{
		return( false );
	}
	
	public void 
	setValueAt(
		Object 	aValue, 
		int 	row, 
		int 	column )
	{
		throw( new RuntimeException("not supported"));
	}	
	
	public void
	start(
		int[]		rows )
	{
		for (int i=0;i<rows.length;i++){
			
			try{
				downloads[rows[i]].restart();
				
			}catch( Throwable e ){
				
				throw( new RPException( "Start fails", e ));
			}
		}
		
		refresh();
	}
	
	public void
	stop(
			int[]		rows )
	{
		for (int i=0;i<rows.length;i++){
			
			try{
				downloads[rows[i]].stop();
				
			}catch( Throwable e ){
				
				throw( new RPException( "Stop fails", e ));
			}
		}
		
		refresh();
	}
	
	public void
	remove(
			int[]		rows )
	{
		for (int i=0;i<rows.length;i++){
			
			try{
				downloads[rows[i]].remove();
				
			}catch( Throwable e ){
				
				throw( new RPException( "Remove fails", e ));
			}
		}
		
		refresh();
	}
}
