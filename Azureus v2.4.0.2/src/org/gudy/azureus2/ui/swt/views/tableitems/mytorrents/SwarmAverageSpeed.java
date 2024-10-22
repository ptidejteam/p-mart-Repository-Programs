/*
 * Created : 11 nov. 2004
 * By      : Alon Rohter
 *
 * Copyright (C) 2004, 2005, 2006 Aelitis SAS, All rights Reserved
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
 *
 * AELITIS, SAS au capital de 46,603.30 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
 
package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;

import org.gudy.azureus2.core3.util.DisplayFormatters;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.plugins.ui.tables.*;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;



public class SwarmAverageSpeed
       extends CoreTableColumn 
       implements TableCellRefreshListener
{

  public SwarmAverageSpeed(String sTableID) {
    super("swarm_average_speed", ALIGN_TRAIL, POSITION_INVISIBLE, 70, sTableID);
    setRefreshInterval(INTERVAL_LIVE);
  }

  public void refresh(TableCell cell) {
    long speed = -1;

    DownloadManager dm = (DownloadManager)cell.getDataSource();
    if( dm != null ) {
      int div = dm.getNbPeers() + (dm.isDownloadComplete() ? 0 : 1);  //since total speed includes our own speed when downloading   
      speed = div < 1 ? 0 : dm.getStats().getTotalAverage() / div;
    }

    if( !cell.setSortValue( speed ) && cell.isValid() ) {
      return;
    }
    
    if( speed < 0 ) {
      cell.setText( "" );
    }
    else {
      cell.setText( DisplayFormatters.formatByteCountToKiBEtcPerSec( speed ) );
    }
  }

}
