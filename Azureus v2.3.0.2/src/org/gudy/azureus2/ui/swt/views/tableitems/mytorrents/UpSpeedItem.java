/*
 * File    : UpSpeedItem.java
 * Created : 24 nov. 2003
 * By      : Olivier
 *
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
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
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
 
package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;

import org.eclipse.swt.graphics.Color;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.config.ParameterListener;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.DisplayFormatters;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.plugins.ui.tables.*;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;
import org.gudy.azureus2.ui.swt.views.table.TableCellCore;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;


/** Upload Speed column
 *
 * @author Olivier
 * @author TuxPaper (2004/Apr/17: modified to TableCellAdapter)
 */
public class UpSpeedItem
       extends CoreTableColumn 
       implements TableCellAddedListener, ParameterListener
{
  private final static String CONFIG_ID = "StartStopManager_iMinSpeedForActiveSeeding";
  private int iMinActiveSpeed;

  /** Default Constructor */
  public UpSpeedItem(String sTableID) {
    super("upspeed", ALIGN_TRAIL, POSITION_LAST, 70, sTableID);
    setRefreshInterval(INTERVAL_LIVE);

    iMinActiveSpeed = COConfigurationManager.getIntParameter(CONFIG_ID);
    COConfigurationManager.addParameterListener(CONFIG_ID, this);
  }

  protected void finalize() throws Throwable {
    super.finalize();
    COConfigurationManager.removeParameterListener(CONFIG_ID, this);
  }
  
  public void cellAdded(TableCell cell) {
    cell.addRefreshListener(new RefreshListener());
  }

  public class RefreshListener implements TableCellRefreshListener {
    private int iLastState;

    public void refresh(TableCell cell) {
      DownloadManager dm = (DownloadManager)cell.getDataSource();
      long value;
      int iState;
      if (dm == null) {
        iState = -1;
        value = 0;
      } else {
        iState = dm.getState();
        value = dm.getStats().getDataSendRate();
      }
      if (!cell.setSortValue(value) && cell.isValid() && (iState == iLastState))
        return;
  
      if (cell.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(value)) || 
          (iState != iLastState) || !cell.isValid()) {
        changeColor(cell, value, iState);
      }
    }

    private void changeColor(TableCell cell) {
      try {
        DownloadManager dm = (DownloadManager)cell.getDataSource();
        if (dm == null) {
          return;
        }
        changeColor(cell, dm.getStats().getDataReceiveRate(), dm.getState());
      } catch (Exception e) {
      	Debug.printStackTrace( e );
      }
    }
  
    private void changeColor(TableCell cell, long iSpeed, int iState) {
      try {
        Color newFG = (iSpeed < iMinActiveSpeed && 
                       iState == DownloadManager.STATE_SEEDING) ? Colors.colorWarning 
                                                                : null;
        ((TableCellCore)cell).setForeground(newFG);
        iLastState = iState;
      } catch (Exception e) {
      	Debug.printStackTrace( e );
      }
    }
  }


  public void parameterChanged(String parameterName) {
    iMinActiveSpeed = COConfigurationManager.getIntParameter(CONFIG_ID);
  }
}
