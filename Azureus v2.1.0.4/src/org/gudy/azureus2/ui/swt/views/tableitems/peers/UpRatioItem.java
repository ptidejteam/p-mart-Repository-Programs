/*
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
 
package org.gudy.azureus2.ui.swt.views.tableitems.peers;

import org.gudy.azureus2.core3.util.DisplayFormatters;
import org.gudy.azureus2.core3.util.Constants;

import org.gudy.azureus2.core3.peer.PEPeer;
import org.gudy.azureus2.plugins.ui.tables.*;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;

/**
 *
 * @author TuxPaper
 */
public class UpRatioItem
       extends CoreTableColumn 
       implements TableCellRefreshListener
{
  /** Default Constructor */
  public UpRatioItem() {
    super("UpRatio", ALIGN_TRAIL, POSITION_INVISIBLE, 70, TableManager.TABLE_TORRENT_PEERS);
    setRefreshInterval(INTERVAL_LIVE);
  }

  public void refresh(TableCell cell) {
    PEPeer peer = (PEPeer)cell.getDataSource();
    float value = 0;
    long lDivisor = 0;
    long lDivident = 0;
    if (peer != null) {
      lDivisor = peer.getStats().getBytesDone() - peer.getStats().getTotalSent();
      lDivident = peer.getStats().getTotalSent();
      // skip if divisor is small (most likely handshake) or 0 (DivisionByZero)
      if (lDivisor > 1024) {
        value = lDivident / (float)lDivisor;
        if (value == 0)
          value = -1;
      } else if (lDivident > 0)
        value = Constants.INFINITY_AS_INT;
    }

    if (!cell.setSortValue((long)(value * 1000)) && cell.isValid())
      return;

    String s;
    if (lDivisor <= 0) 
      s = "";
    else if (value == Constants.INFINITY_AS_INT)
      s = Constants.INFINITY_STRING + ":1";
    else if (value == -1)
      s = "1:" + Constants.INFINITY_STRING;
    else
      s = DisplayFormatters.formatDecimal(value, "0.00") + ":1";

    cell.setText(s);
  }
}
