/*
 * File    : AvailabilityItem.java
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

import org.gudy.azureus2.core3.peer.PEPeerManager;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.plugins.ui.tables.*;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;


/** Availability/"Seeing Copies" Column
 *
 * @author TuxPaper
 */
public class AvailabilityItem
       extends CoreTableColumn 
       implements TableCellRefreshListener
{
  // If you want more decimals, just add a zero
  private static final String zeros = "0000";
  // # decimal places == numZeros - 1
  private static final int numZeros = zeros.length();

  private int iTimesBy;
  
  /** Default Constructor */
  public AvailabilityItem(String sTableID) {
    super("availability", ALIGN_TRAIL, POSITION_INVISIBLE, 50, sTableID);
    setRefreshInterval(INTERVAL_LIVE);

    iTimesBy = 1;
    for (int i = 1; i < numZeros; i++)
      iTimesBy *= 10;
  }

  public void refresh(TableCell cell) {
    String sText = "";
    DownloadManager dm = (DownloadManager)cell.getDataSource();
    if (dm == null)
      return;

    PEPeerManager pm = dm.getPeerManager();
    if (pm != null) {
      float f = pm.getMinAvailability();
      if (!cell.setSortValue((long)(f * 1000)) && cell.isValid())
        return;
      if (f != 0) {
        int numZeros = zeros.length();
        sText = String.valueOf((int)(f * iTimesBy));
        if (numZeros - sText.length() > 0)
          sText = zeros.substring(0, numZeros - sText.length()) + sText;
        sText = sText.substring(0, sText.length() - numZeros + 1) + "." + 
                sText.substring(sText.length() - numZeros + 1);
      }
    } else {
      cell.setSortValue(0);
    }
    cell.setText(sText);
  }
}
