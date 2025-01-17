/*
 * File    : NameItem.java
 * Created : 24 nov. 2003
 * By      : Olivier
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
 
package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;

import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;


/**
 * @author Olivier
 *
 */
public class PeersItem extends TorrentItem {

  
  
  public PeersItem(
    TorrentRow torrentRow,
    int position) {
    super(torrentRow, position);
  }

  public void refresh() {
    TRTrackerScraperResponse hd = torrentRow.getManager().getTrackerScrapeResponse();
    
    String tmp = "" + torrentRow.getManager().getNbPeers(); //$NON-NLS-1$
    if(hd!=null && hd.isValid())
      tmp += " (" + hd.getPeers() + ")";
    setText(tmp);
  }

}
